package at.fseidl.wineshop.db;

import at.fseidl.wineshop.persistence.PersistenceException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Db {
    public static String FIELD_ID = "id";

    private static class Table {
        public Table(String name, Set<Field> fields) {
            this.fields = fields.stream().collect(Collectors.toMap(field -> field.name, field -> field));
            if (!this.fields.containsKey(FIELD_ID)) {
                throw new IllegalArgumentException(String.format("Invalid Table definition %s, has to contain an ID field named %s", name, FIELD_ID));
            }
            this.name = name;
        }

        private boolean containsId(int id) {
            return records.containsKey(id);
        }

        private final Map<Integer, Map<String, Object>> records = new HashMap<>();

        private final String name;
        private final Map<String, Field> fields;

        private void checkIdExists(Map<String, Object> value) {
            fields.get(FIELD_ID).checkType(value.get(FIELD_ID));
            checkIdExists((int) value.get(FIELD_ID));
        }

        private void checkIdExists(int id) {
            if (!records.containsKey(id)) {
                throw new PersistenceException(String.format("Record with id %d does not exist in %s", id, name));
            }
        }

        private void checkFieldTypes(Map<String, Object> value) {
            if (!fields.keySet().containsAll(value.keySet())) {
                throw new PersistenceException(String.format("Undefined field in %s (defined fields are %s", value, fields));
            }
            fields.values().stream()
                    .filter(field -> !field.name.equals(FIELD_ID))
                    .forEach(field -> field.checkType(value.get(field.name)));
        }

        private void checkForeignKeys(Map<String, Object> value, Map<String, Table> tables) {
            fields.values().forEach(field -> field.checkForeignKeys(tables, value.get(field.name)));
        }

        private Map<String, Object> insertRecord(Map<String, Object> values) {
            int id = nextId(records);
            Map<String, Object> record = createRecordWithId(values, id);
            records.put(id, record);
            return Map.copyOf(record);
        }

        private Map<String, Object> updateRecord(Map<String, Object> values) {
            Map<String, Object> record = records.get((int) values.get(FIELD_ID));
            record.putAll(values);
            return Map.copyOf(record);
        }

        private Map<String, Object> deleteRecord(int id) {
            Map<String, Object> record = records.remove(id);
            return Map.copyOf(record);
        }

        private Map<String, Object> createRecordWithId(Map<String, Object> values, int id) {
            Map<String, Object> record = new HashMap<>(values);
            record.put(FIELD_ID, id);
            return record;
        }

        private Map<String, Object> selectRecordById(int id) {
            return Map.copyOf(records.get(id));
        }

        private List<Map<String, Object>> selectAllRecords() {
            return records.values().stream().map(Map::copyOf).toList();
        }

        private Optional<Map<String, Object>> selectMaxRecordFilterBy(Function<Map<String, Object>, Boolean> filterFunction, Comparator<Map<String, Object>> maxOrder) {
            return records.values().stream()
                    .filter(filterFunction::apply)
                    .max(maxOrder);
        }

        private Optional<Map<String, Object>> selectMinRecordFilterBy(Function<Map<String, Object>, Boolean> filterFunction, Comparator<Map<String, Object>> minOrder) {
            return records.values().stream()
                    .filter(filterFunction::apply)
                    .min(minOrder);
        }

        @Override
        public String toString() {
            return name;
        }
    }


    public static final class Field {
        public Field(String name, boolean mandatory, Class<?> type) {
            this.name = name;
            this.type = type;
            this.mandatory = mandatory;
            this.foreignKeyRelationship = Optional.empty();
        }

        public Field(String name, boolean mandatory, String foreignKeyRelationship) {
            this.name = name;
            this.type = Integer.class;
            this.mandatory = mandatory;
            this.foreignKeyRelationship = Optional.of(foreignKeyRelationship);
        }

        public static Field idField() {
            return new Field(FIELD_ID, true, Integer.class);
        }

        private final String name;
        private final Class<?> type;
        private final boolean mandatory;

        private final Optional<String> foreignKeyRelationship;

        private void checkType(Object value) {
            if (mandatory && value == null) {
                throw new PersistenceException(String.format("Missing or null value in mandatory field %s", name));
            }
            if (value != null && !value.getClass().isAssignableFrom(value.getClass())) {
                throw new PersistenceException(String.format("Invalid wineType %s for value %s in field %s", value, value.getClass(), name));
            }
        }

        private void checkForeignKeys(Map<String, Table> tables, Object value) {
            if (value != null) {
                foreignKeyRelationship.ifPresent(foreignKeyToTable -> checkForeignKey(getTableForForeignKey(tables, foreignKeyToTable), (Integer) value));
            }
        }

        private Table getTableForForeignKey(Map<String, Table> tables, String foreignKeyToTable) {
            if (!tables.containsKey(foreignKeyToTable)) {
                throw new PersistenceException(String.format("Invalid foreign key relationship in field %s (table %s does not exist)", name, foreignKeyToTable));
            }
            return tables.get(foreignKeyToTable);
        }

        private void checkForeignKey(Table table, int value) {
            if (!table.containsId(value)) {
                throw new PersistenceException(String.format("Invalid foreign key relationship in field %s (id %d not found in %s)", name, value, table));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Field field = (Field) o;
            return Objects.equals(name, field.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final Map<String, Table> tables = new HashMap<>();

    public synchronized void createTable(String tableName, Set<Field> fields) {
        tables.put(tableName, new Table(tableName, fields));
    }

    public synchronized Map<String, Object> insert(String tableName, Map<String, Object> values) {
        if (values.containsKey(FIELD_ID) && !values.get(FIELD_ID).equals(-1)) {
            throw new PersistenceException(String.format("Can only insert new record into table %s with ID -1 (or no id), but ID was already defined in %s", tableName, values));
        }
        Table table = getTable(tableName);
        table.checkFieldTypes(values);
        table.checkForeignKeys(values, tables);
        return table.insertRecord(values);
    }

    public synchronized Map<String, Object> update(String tableName, Map<String, Object> values) {
        Table table = getTable(tableName);
        table.checkIdExists(values);
        table.checkFieldTypes(values);
        table.checkForeignKeys(values, tables);
        return table.updateRecord(values);
    }

    public synchronized Map<String, Object> delete(String tableName, int id) {
        Table table = getTable(tableName);
        table.checkIdExists(id);
        return table.deleteRecord(id);
    }

    private Table getTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            throw new PersistenceException(String.format("Undefined table %s", tableName));
        }
        return tables.get(tableName);
    }

    public synchronized Map<String, Object> selectById(String tableName, int id) {
        Table table = getTable(tableName);
        table.checkIdExists(id);
        return table.selectRecordById(id);
    }

    public synchronized List<Map<String, Object>> selectAll(String tableName) {
        return getTable(tableName).selectAllRecords();
    }

    public synchronized Optional<Map<String, Object>> selectMaxFilterBy(String tableName, Function<Map<String, Object>, Boolean> filterFunction, Comparator<Map<String, Object>> maxOrder) {
        return tables.get(tableName).selectMaxRecordFilterBy(filterFunction, maxOrder);
    }

    public Optional<Map<String, Object>> selectMinRecordFilterBy(String tableName, Function<Map<String, Object>, Boolean> filterFunction, Comparator<Map<String, Object>> minOrder) {
        return tables.get(tableName).selectMinRecordFilterBy(filterFunction, minOrder);
    }

    private static int nextId(Map<Integer, Map<String, Object>> targetMap) {
        return targetMap.values().stream()
                .mapToInt(entry -> (Integer) entry.get(FIELD_ID))
                .max()
                .orElse(-1)
                + 1;
    }
}
