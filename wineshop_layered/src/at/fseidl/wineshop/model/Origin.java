package at.fseidl.wineshop.model;

public record Origin(int id, String country, String region) implements Entity {
    public Origin(String country, String region) {
        this(ID_UNDEFINED, country, region);
    }
}
