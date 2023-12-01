package at.fseidl.wineshop.matcher;

import at.fseidl.wineshop.shared.Wine;
import at.fseidl.wineshop.shared.persistence.WineRepository;


public class ColorblindWineMatch implements WineMatch {
    private final double priceClass;

    public ColorblindWineMatch(String name, int age, String tastePreference) {
        this.priceClass = new PriceClassCalculator(name, age, tastePreference).calculate();
    }

    @Override
    public Wine find(WineRepository wineRepository) {
        return Wine.findMostExpensiveWithPriceLowerThan(wineRepository, priceClass)
                .orElseGet(() -> Wine.findTheCheapest(wineRepository));
    }
}
