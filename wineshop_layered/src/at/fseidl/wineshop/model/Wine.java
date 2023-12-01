package at.fseidl.wineshop.model;

public record Wine(int id, String name, Origin origin, WineType type, double price) implements Entity {
    public Wine(String name, Origin origin, WineType type, double price) {
        this(ID_UNDEFINED, name, origin, type, price);
    }
}
