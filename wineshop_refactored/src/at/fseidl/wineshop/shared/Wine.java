package at.fseidl.wineshop.shared;

import at.fseidl.wineshop.db.Db;
import at.fseidl.wineshop.shared.persistence.WineDbConfig;
import at.fseidl.wineshop.shared.persistence.WineRepository;
import at.fseidl.wineshop.shared.service.WineDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Wine {
    private static final int ID_UNDEFINED = -1;

    private final int id;
    private final String name;
    private final Origin origin;
    private final WineType type;
    private final double price;

    public Wine(int id, String name, Origin origin, WineType type, double price) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.type = type;
        this.price = price;
    }

    public Wine(String name, Origin origin, WineType type, double price) {
        this(ID_UNDEFINED, name, origin, type, price);
    }

    public static Wine ofRecord(Map<String, Object> record, WineRepository wineRepository) {
        return ofRecord(record, wineRepository.selectOrigin((int) record.get(WineDbConfig.FIELD_ORIGIN)), wineRepository.selectWineType((int) record.get(WineDbConfig.FIELD_TYPE)));
    }

    public static Wine ofRecord(Map<String, Object> record, Map<String, Object> originRecord, Map<String, Object> wineTypeRecord) {
        return new Wine(
                (int) record.get(Db.FIELD_ID),
                (String) record.get(WineDbConfig.FIELD_NAME),
                Origin.ofRecord(originRecord),
                WineType.ofRecord(wineTypeRecord),
                (double) record.get(WineDbConfig.FIELD_PRICE));
    }

    public static Optional<Wine> findMostExpensiveWithPriceLowerThan(WineRepository wineRepository, double priceClass, WineType.Color color) {
        return wineRepository.findMostExpensiveWithPriceLowerThan(priceClass, color.name()).map(record -> Wine.ofRecord(record, wineRepository));
    }

    public static Optional<Wine> findMostExpensiveWithPriceLowerThan(WineRepository wineRepository, double priceClass) {
        return wineRepository.findMostExpensiveWithPriceLowerThan(priceClass).map(record -> Wine.ofRecord(record, wineRepository));
    }

    public static Wine findTheCheapest(WineRepository wineRepository, WineType.Color color) {
        return ofRecord(wineRepository.findTheCheapest(color.name()), wineRepository);
    }

    public static Wine findTheCheapest(WineRepository wineRepository) {
        return ofRecord(wineRepository.findTheCheapest(), wineRepository);
    }

    public WineDTO toDTO() {
        WineDTO.WineDTOBuilder builder = new WineDTO.WineDTOBuilder(name);
        builder.setPrice(price);
        origin.addToWineDTOBuilder(builder);
        type.addToWineDTOBuilder(builder);
        return builder.build();
    }

    public static Wine ofDTO(WineDTO wineDTO) {
        return new Wine(wineDTO.name(), Origin.ofDTO(wineDTO), WineType.ofDTO(wineDTO), wineDTO.price());
    }

    public Map<String, Object> toRecord() {
        Map<String, Object> record = new HashMap<>();
        record.put(WineDbConfig.FIELD_NAME, name);
        record.put(WineDbConfig.FIELD_PRICE, price);
        origin.addRefToRecord(record, WineDbConfig.FIELD_ORIGIN);
        type.addRefToRecord(record, WineDbConfig.FIELD_TYPE);
        if (id != ID_UNDEFINED) {
            record.put(Db.FIELD_ID, id);
        }
        return record;
    }

    public Wine save(WineRepository wineRepository) {
        Map<String, Object> wineRecordToInsert = new HashMap<>(toRecord());
        if (origin.idUndefined()) {
            Map<String, Object> originWithId = wineRepository.saveOrigin(origin.toRecord());
            wineRecordToInsert.put(WineDbConfig.FIELD_ORIGIN, originWithId.get(Db.FIELD_ID));
        }
        if (type.idUndefined()) {
            Map<String, Object> typeWithId = wineRepository.saveTypeRecord(origin.toRecord());
            wineRecordToInsert.put(WineDbConfig.FIELD_TYPE, typeWithId.get(Db.FIELD_ID));
        }
        return ofRecord(wineRepository.save(wineRecordToInsert), wineRepository);
    }

    public static List<Wine> findAll(WineRepository wineRepository) {
        return wineRepository.findAll().stream().map(record -> Wine.ofRecord(record, wineRepository))
                .toList();

    }
}
