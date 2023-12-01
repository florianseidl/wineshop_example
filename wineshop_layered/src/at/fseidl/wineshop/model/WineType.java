package at.fseidl.wineshop.model;

import java.awt.*;

public record WineType(int id, String name, Color color) implements Entity {
    public enum Color { RED, WHITE, ROSE, UNDEFINED; }
    public WineType(String name, Color color) {
        this(ID_UNDEFINED, name, color);
    }
}
