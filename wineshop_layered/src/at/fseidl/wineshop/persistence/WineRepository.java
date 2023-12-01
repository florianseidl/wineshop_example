package at.fseidl.wineshop.persistence;

import at.fseidl.wineshop.db.Db;
import at.fseidl.wineshop.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class WineRepository {
    private final Db db;

    public WineRepository(Db db) {
        this.db = db;
    }

    public Wine save(Wine wine) {
        Map<String, Object> wineRecordToInsert = toWineRecord(wine);
        if (wine.origin().idUndefined()) {
            Map<String, Object> originWithId = db.insert(WineDbConfig.TABLE_ORIGIN, toOriginRecord(wine.origin()));
            wineRecordToInsert.put(WineDbConfig.FIELD_ORIGIN, originWithId.get(Db.FIELD_ID));
        }
        if (wine.type().idUndefined()) {
            Map<String, Object> typeWithId = db.insert(WineDbConfig.TABLE_WINE_TYPE, toWineTypeRecord(wine.type()));
            wineRecordToInsert.put(WineDbConfig.FIELD_TYPE, typeWithId.get(Db.FIELD_ID));
        }
        return toWine(db.insert(WineDbConfig.TABLE_WINE, wineRecordToInsert));
    }

    private Wine toWine(Map<String, Object> record) {
        return new Wine(
                (int) record.get(Db.FIELD_ID),
                (String) record.get(WineDbConfig.FIELD_NAME),
                toOrigin(db.selectById(WineDbConfig.TABLE_ORIGIN, (int) record.get(WineDbConfig.FIELD_ORIGIN))),
                toWineType(db.selectById(WineDbConfig.TABLE_WINE_TYPE, (int) record.get(WineDbConfig.FIELD_TYPE))),
                (double) record.get(WineDbConfig.FIELD_PRICE));
    }

    private Origin toOrigin(Map<String, Object> record) {
        return new Origin(
                (int) record.get(Db.FIELD_ID),
                (String) record.get(WineDbConfig.FIELD_COUNTRY),
                (String) record.get(WineDbConfig.FIELD_REGION));
    }

    private WineType toWineType(Map<String, Object> record) {
        return new WineType(
                (int) record.get(Db.FIELD_ID),
                (String) record.get(WineDbConfig.FIELD_NAME),
                record.containsKey(WineDbConfig.FIELD_COLOR) ? WineType.Color.valueOf((String) record.get(WineDbConfig.FIELD_COLOR)) : WineType.Color.UNDEFINED);
    }

    private static Map<String, Object> toWineRecord(Wine wine) {
        Map<String, Object> record = new HashMap<>();
        record.put(WineDbConfig.FIELD_NAME, wine.name());
        record.put(WineDbConfig.FIELD_ORIGIN, wine.origin().id());
        record.put(WineDbConfig.FIELD_TYPE, wine.type().id());
        record.put(WineDbConfig.FIELD_PRICE, wine.price());
        putId(record, wine);
        return record;
    }

    private static Map<String, Object> toWineTypeRecord(WineType wineType) {
        Map<String, Object> record = new HashMap<>();
        record.put(WineDbConfig.FIELD_NAME, wineType.name());
        record.put(WineDbConfig.FIELD_COLOR, wineType.color().name());
        putId(record, wineType);
        return Map.copyOf(record);
    }

    private static Map<String, Object> toOriginRecord(Origin origin) {
        Map<String, Object> record = new HashMap<>();
        record.put(WineDbConfig.FIELD_COUNTRY, origin.country());
        record.put(WineDbConfig.FIELD_REGION, origin.region());
        putId(record, origin);
        return Map.copyOf(record);
    }

    private static void putId(Map<String, Object> record, Entity entity) {
        if (entity.id() != -1) {
            record = new HashMap<>(record);
            record.put(Db.FIELD_ID, entity.id());
        }
    }

    public List<Wine> findAll() {
        return db.selectAll(WineDbConfig.TABLE_WINE).stream().map(this::toWine).toList();
    }


    public Optional<Wine> findMostExpensiveWithPriceLowerThan(WineType.Color color, double maxPrice) {
        Set<Integer> wineTypes = selectAllWineTypeIdsForColor(color);
        return findMostExpensiveWithPriceLowerThan(wineTypes, maxPrice);
    }

    public Optional<Wine> findMostExpensiveWithPriceLowerThan(double maxPrice) {
        Set<Integer> allWineTypes = selectAllWineTypeIds();
        return findMostExpensiveWithPriceLowerThan(allWineTypes, maxPrice);
    }

    private Optional<Wine> findMostExpensiveWithPriceLowerThan(Set<Integer> wineTypes, double maxPrice) {
        return db.selectMaxFilterBy(
                        WineDbConfig.TABLE_WINE, record -> (double) record.get(WineDbConfig.FIELD_PRICE) <= maxPrice && wineTypes.contains((int) record.get(WineDbConfig.FIELD_TYPE)),
                        Comparator.comparing((Map<String, Object> record) -> (double) record.get(WineDbConfig.FIELD_PRICE)))
                .map(this::toWine);
    }

    public Wine findTheCheapest() {
        Set<Integer> allWineTypes = selectAllWineTypeIds();
        return findTheCheapest(allWineTypes);
    }

    public Wine findTheCheapest(WineType.Color color) {
        Set<Integer> wineTypes = selectAllWineTypeIdsForColor(color);
        return findTheCheapest(wineTypes);
    }

    private Wine findTheCheapest(Set<Integer> wineTypes) {
        return db.selectMinRecordFilterBy(
                        WineDbConfig.TABLE_WINE,
                        record -> wineTypes.contains((int) record.get(WineDbConfig.FIELD_TYPE)),
                        Comparator.comparing((Map<String, Object> record) -> (double) record.get(WineDbConfig.FIELD_PRICE)))
                .map(this::toWine)
                .orElseThrow(() -> new IllegalStateException("No Wine in Repository"));
    }

    private Set<Integer> selectAllWineTypeIds() {
        return db.selectAll(WineDbConfig.TABLE_WINE_TYPE).stream()
                .map(record -> (Integer) record.get(Db.FIELD_ID))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Integer> selectAllWineTypeIdsForColor(WineType.Color color) {
        return db.selectAll(WineDbConfig.TABLE_WINE_TYPE).stream()
                .filter(record -> color.name().equals((String) record.get(WineDbConfig.FIELD_COLOR)))
                .map(record -> (Integer) record.get(Db.FIELD_ID))
                .collect(Collectors.toUnmodifiableSet());
    }
}
