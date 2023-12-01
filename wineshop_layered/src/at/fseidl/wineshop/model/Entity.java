package at.fseidl.wineshop.model;

public interface Entity {
    public static final int ID_UNDEFINED = -1;

    default boolean idUndefined() {
        return id() == ID_UNDEFINED;
    }

    int id();
}
