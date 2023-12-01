package at.fseidl.wineshop.db;

import at.fseidl.wineshop.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DbTest {

    private Db db;

    @BeforeEach
    void screateDb() {
        db = new Db();
        db.createTable("foo", Set.of(
                Db.Field.idField(),
                new Db.Field("field1", true, String.class),
                new Db.Field("field2", false, Double.class)));
        db.createTable("bar", Set.of(Db.Field.idField(), new Db.Field("ref1", true, "foo")));
    }

    @Test
    void createTable_selectAll_exists() {
        db.selectAll("foo");
    }

    @Test
    void insert_selectAll_exists() {
        db.insert("foo", Map.of("field1", "hotzenplotz"));
        assertEquals(1, db.selectAll("foo").size());
    }

    @Test
    void insert_selectAll_equal() {
        Map<String, Object> fooRecord = db.insert("foo", Map.of("field1", "hotzenplotz"));
        assertEquals(fooRecord, db.selectAll("foo").get(0));
    }

    @Test
    void insertWithForeignKey_selectAll_exists() {
        Map<String, Object> fooRecord = db.insert("foo", Map.of("field1", "hotzenplotz"));
        db.insert("bar", Map.of("ref1", fooRecord.get(Db.FIELD_ID)));
        assertEquals(1, db.selectAll("bar").size());
    }

    @Test
    void insert_update_changed() {
        Map<String, Object> fooRecord = new HashMap<>(db.insert("foo", Map.of("field1", "hotzenplotz")));
        fooRecord.put("field1", "changed");
        db.update("foo", fooRecord);
        assertEquals( "changed", db.selectById("foo", (int) fooRecord.get(Db.FIELD_ID)).get("field1"));
    }

    @Test
    void insert_delete_removed() {
        Map<String, Object> fooRecord = new HashMap<>(db.insert("foo", Map.of("field1", "hotzenplotz")));
        db.delete("foo", (int) fooRecord.get(Db.FIELD_ID));
        assertEquals( 0, db.selectAll("foo").size());
    }

    @Test
    void insert_selectById_equal() {
        Map<String, Object> fooRecord = db.insert("foo", Map.of("field1", "hotzenplotz"));
        assertEquals(fooRecord, db.selectById("foo", (int) fooRecord.get(Db.FIELD_ID)));
    }

    @Test
    void insert_selectMaxAndFilter_equal() {
        db.insert("foo", Map.of("field1", "hotzenplotz"));
        db.insert("foo", Map.of("field1", "xyz"));
        Map<String, Object> fooRecord = db.insert("foo", Map.of("field1", "hotzenplotz"));
        Optional<Map<String, Object>> found = db.selectMaxFilterBy("foo",
                record -> record.get("field1").equals("hotzenplotz"),
                Comparator.comparing((Map<String, Object> record) -> (int) record.get(Db.FIELD_ID)));
        assertTrue(found.isPresent());
        assertEquals(fooRecord, found.get());
    }

    @Test
    void insertWithExistingId_exception() {
        assertThrows(PersistenceException.class, () -> db.insert("foo", Map.of(Db.FIELD_ID, 42, "field1", "hotzenplotz")));
    }

    @Test
    void insertWithMissingForeignKey_exception() {
        assertThrows(PersistenceException.class, () -> db.insert("bar", Map.of("ref1", 42)));
    }

    @Test
    void updateMissing_exception() {
        Map<String, Object> fooRecord = new HashMap<>();
        fooRecord.put(Db.FIELD_ID, 42);
        fooRecord.put("field1", "hotzenplotz");
        assertThrows(PersistenceException.class, () -> db.update("foo", fooRecord));
    }

    @Test
    void updateWithMissingForeignKey_exception() {
        Map<String, Object> fooRecord = db.insert("foo", Map.of("field1", "hotzenplotz"));
        Map<String, Object> barRecord = new HashMap<String, Object>(db.insert("bar", Map.of("ref1", fooRecord.get(Db.FIELD_ID))));
        barRecord.put("ref1", 42);
        assertThrows(PersistenceException.class, () -> db.update("bar", barRecord));
    }

    @Test
    void insertInvalidType_exception() {
        assertThrows(PersistenceException.class, () -> db.update("foo", Map.of("field1", 42)));
    }

    @Test
    void insertMissingMandatoryField_exception() {
        assertThrows(PersistenceException.class, () -> db.update("foo", Map.of()));
    }

    @Test
    void insertInvalidOptionalType_exception() {
        assertThrows(PersistenceException.class, () -> db.update("foo", Map.of("field1", "bla", "field2", "blo")));
    }

    @Test
    void deleteMissing_exception() {
        db.insert("foo", Map.of("field1", "hotzenplotz"));
        assertThrows(PersistenceException.class, () -> db.delete("foo", 42));
    }

    @Test
    void selectByIdMissing_exception() {
        db.insert("foo", Map.of("field1", "hotzenplotz"));
        assertThrows(PersistenceException.class, () -> db.selectById("foo", 42));
    }

    @Test
    void insertMissingTable_exception() {
        assertThrows(PersistenceException.class, () -> db.insert("bla", Map.of()));
    }

    @Test
    void insertWithInvalidField_exception() {
        assertThrows(PersistenceException.class, () -> db.insert("foo", Map.of("field1", "hotzenplotz", "gibtsnicht", 42)));
    }

    @Test
    void insertWithInvalidId_exception() {
        assertThrows(PersistenceException.class, () -> db.insert("foo", Map.of(Db.FIELD_ID, "bla", "field1", "hotzenplotz")));
    }

    @Test
    void updateWithoutId_exception() {
        Map<String, Object> fooRecord = new HashMap<>(db.insert("foo", Map.of("field1", "hotzenplotz")));
        fooRecord.remove(Db.FIELD_ID);
        fooRecord.put("field1", "hotzenplotz");
        assertThrows(PersistenceException.class, () -> db.update("foo", fooRecord));
    }
}