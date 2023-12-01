package at.fseidl.wineshop.persistence;

import at.fseidl.wineshop.db.Db;

import java.util.Set;

public class WineDbConfig {
    public static final String TABLE_WINE = "Wine";

    public static final String TABLE_ORIGIN = "Origin";
    public static final String TABLE_WINE_TYPE = "WineType";

    public static String FIELD_NAME = "name";
    public static String FIELD_ORIGIN = "origin";
    public static String FIELD_TYPE = "type";
    public static String FIELD_PRICE = "price";
    public static String FIELD_COUNTRY = "country";
    public static String FIELD_REGION = "region";
    public static String FIELD_COLOR = "color";

    public static Db createWineDb() {
        Db db = new Db();
        db.createTable(TABLE_WINE_TYPE, Set.of(
                Db.Field.idField(),
                new Db.Field(FIELD_NAME, true, String.class),
                new Db.Field(FIELD_COLOR, false, String.class)
        ));
        db.createTable(TABLE_ORIGIN, Set.of(
                Db.Field.idField(),
                new Db.Field(FIELD_COUNTRY, true, String.class),
                new Db.Field(FIELD_REGION, false, String.class)
        ));
        db.createTable(TABLE_WINE, Set.of(
                Db.Field.idField(),
                new Db.Field(FIELD_NAME, true, String.class),
                new Db.Field(FIELD_ORIGIN, true, TABLE_ORIGIN),
                new Db.Field(FIELD_TYPE, true, TABLE_WINE_TYPE),
                new Db.Field(FIELD_PRICE, true, Double.class)
        ));
        return db;
    }
}
