package at.fseidl.wineshop.shared.persistence;

import at.fseidl.wineshop.db.Db;

import java.util.*;
import java.util.stream.Collectors;

public class WineRepository {
    private final Db db;

    public WineRepository(Db db) {
        this.db = db;
    }

    public Map<String, Object> save(Map<String, Object> record) {
        return db.insert(WineDbConfig.TABLE_WINE, record);
    }

    public Map<String, Object> saveOrigin(Map<String, Object> originRecord) {
        return db.insert(WineDbConfig.TABLE_ORIGIN, originRecord);
    }

    public Map<String, Object> saveTypeRecord(Map<String, Object> wineTypeRecord) {
        return db.insert(WineDbConfig.TABLE_WINE_TYPE, wineTypeRecord);
    }

    public List<Map<String, Object>> findAll() {
        return db.selectAll(WineDbConfig.TABLE_WINE);
    }

    public Optional<Map<String, Object>> findMostExpensiveWithPriceLowerThan(double maxPrice, String color) {
        Set<Integer> wineTypes = selectAllWineTypeIdsForColor(color);
        return findMostExpensiveWithPriceLowerThan(wineTypes, maxPrice);
    }

    public Optional<Map<String, Object>> findMostExpensiveWithPriceLowerThan(double maxPrice) {
        Set<Integer> allWineTypes = selectAllWineTypeIds();
        return findMostExpensiveWithPriceLowerThan(allWineTypes, maxPrice);
    }

    private Optional<Map<String, Object>> findMostExpensiveWithPriceLowerThan(Set<Integer> wineTypes, double maxPrice) {
        return db.selectMaxFilterBy(
                WineDbConfig.TABLE_WINE, record -> (double) record.get(WineDbConfig.FIELD_PRICE) <= maxPrice && wineTypes.contains((int) record.get(WineDbConfig.FIELD_TYPE)),
                Comparator.comparing((Map<String, Object> record) -> (double) record.get(WineDbConfig.FIELD_PRICE)));
    }

    public Map<String, Object> findTheCheapest() {
        Set<Integer> allWineTypes = selectAllWineTypeIds();
        return findTheCheapest(allWineTypes);
    }

    public Map<String, Object> findTheCheapest(String color) {
        Set<Integer> wineTypes = selectAllWineTypeIdsForColor(color);
        return findTheCheapest(wineTypes);
    }

    private Map<String, Object> findTheCheapest(Set<Integer> wineTypes) {
        return db.selectMinRecordFilterBy(
                        WineDbConfig.TABLE_WINE,
                        record -> wineTypes.contains((int) record.get(WineDbConfig.FIELD_TYPE)),
                        Comparator.comparing((Map<String, Object> record) -> (double) record.get(WineDbConfig.FIELD_PRICE)))
                .orElseThrow(() -> new IllegalStateException("No Wine in Repository"));
    }

    private Set<Integer> selectAllWineTypeIds() {
        return db.selectAll(WineDbConfig.TABLE_WINE_TYPE).stream()
                .map(record -> (Integer) record.get(Db.FIELD_ID))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<Integer> selectAllWineTypeIdsForColor(String color) {
        return db.selectAll(WineDbConfig.TABLE_WINE_TYPE).stream()
                .filter(record -> color.equals(record.get(WineDbConfig.FIELD_COLOR)))
                .map(record -> (Integer) record.get(Db.FIELD_ID))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Map<String, Object> selectOrigin(int id) {
        return db.selectById(WineDbConfig.TABLE_ORIGIN, id);
    }

    public Map<String, Object> selectWineType(int id) {
        return db.selectById(WineDbConfig.TABLE_WINE_TYPE, id);
    }
}
