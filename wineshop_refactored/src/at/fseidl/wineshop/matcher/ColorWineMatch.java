package at.fseidl.wineshop.matcher;

import at.fseidl.wineshop.shared.Wine;
import at.fseidl.wineshop.shared.WineType;
import at.fseidl.wineshop.shared.persistence.WineRepository;


public class ColorWineMatch {
    private final double priceClass;

    private final WineType.Color color;

    public ColorWineMatch(String name, int age, String tastePreference, WineType.Color color) {
        this.priceClass = new PriceClassCalculator(name, age, tastePreference).calculate();
        this.color = color;
    }

    public Wine find(WineRepository wineRepository) {
        return Wine.findMostExpensiveWithPriceLowerThan(wineRepository, priceClass, color).orElseGet(() -> Wine.findTheCheapest(wineRepository, color));
    }

}
