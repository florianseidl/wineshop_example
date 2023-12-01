package at.fseidl.wineshop.shared;

import at.fseidl.wineshop.db.Db;
import at.fseidl.wineshop.shared.persistence.WineDbConfig;
import at.fseidl.wineshop.shared.service.WineDTO;

import java.util.HashMap;
import java.util.Map;

public class Origin {
    private static final int ID_UNDEFINED = -1;

    private final int id;
    private final String country;
    private final String region;

    public Origin(int id, String country, String region) {
        this.id = id;
        this.country = country;
        this.region = region;
    }

    public Origin(String country, String region) {
        this(ID_UNDEFINED, country, region);
    }

    public boolean idUndefined() {
        return id == ID_UNDEFINED;
    }

    public Map<String, Object> toRecord() {
        Map<String, Object> record = new HashMap<>();
        record.put(WineDbConfig.FIELD_COUNTRY, country);
        record.put(WineDbConfig.FIELD_REGION, region);
        if (id != ID_UNDEFINED) {
            record.put(Db.FIELD_ID, id);
        }
        return Map.copyOf(record);
    }

    public static Origin ofRecord(Map<String, Object> record) {
        return new Origin(
                (int) record.get(Db.FIELD_ID),
                (String) record.get(WineDbConfig.FIELD_COUNTRY),
                (String) record.get(WineDbConfig.FIELD_REGION));
    }

    public WineDTO.WineDTOBuilder addToWineDTOBuilder(WineDTO.WineDTOBuilder builder) {
        builder.setOrignCountry(country);
        builder.setOriginRegion(country);
        return builder;
    }

    public static Origin ofDTO(WineDTO wineDTO) {
        return new Origin(wineDTO.orignCountry(), wineDTO.originRegion());
    }

    public boolean addRefToRecord(Map<String, Object> record, String fieldName) {
        if (id == ID_UNDEFINED) {
            return false;
        }
        record.put(fieldName, id);
        return true;
    }
}
