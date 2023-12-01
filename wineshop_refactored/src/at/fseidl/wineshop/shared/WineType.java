package at.fseidl.wineshop.shared;

import at.fseidl.wineshop.db.Db;
import at.fseidl.wineshop.shared.persistence.WineDbConfig;
import at.fseidl.wineshop.shared.service.WineDTO;

import java.util.HashMap;
import java.util.Map;

public class WineType {
    private static final int ID_UNDEFINED = -1;

    public enum Color {RED, WHITE, ROSE, UNKNOWN}

    private final int id;
    private final String name;

    private final Color color;

    public WineType(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public WineType(String name, Color color) {
        this(ID_UNDEFINED, name, color);
    }

    public boolean idUndefined() {
        return id == ID_UNDEFINED;
    }

    public Map<String, Object> toRecord() {
        Map<String, Object> record = new HashMap<>();
        record.put(WineDbConfig.FIELD_NAME, name);
        record.put(WineDbConfig.FIELD_COLOR, color.name());
        if (id != ID_UNDEFINED) {
            record.put(Db.FIELD_ID, id);
        }
        return Map.copyOf(record);
    }

    public static WineType ofRecord(Map<String, Object> record) {
        return new WineType(
                (int) record.get(Db.FIELD_ID),
                (String) record.get(WineDbConfig.FIELD_NAME),
                record.containsKey(WineDbConfig.FIELD_COLOR) ? Color.valueOf((String) record.get(WineDbConfig.FIELD_COLOR)) : Color.UNKNOWN);
    }

    public void addToWineDTOBuilder(WineDTO.WineDTOBuilder builder) {
        builder.setWineType(name);
        builder.setWineTypeColor(color.name());
    }

    public static WineType ofDTO(WineDTO wineDTO) {
        return new WineType(wineDTO.wineType(), wineDTO.wineTypeColor() != null ? Color.valueOf(wineDTO.wineTypeColor()) : Color.UNKNOWN);
    }

    public boolean addRefToRecord(Map<String, Object> record, String fieldName) {
        if (id == ID_UNDEFINED) {
            return false;
        }
        record.put(fieldName, id);
        return true;
    }
}
