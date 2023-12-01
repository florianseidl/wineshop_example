package at.fseidl.wineshop.business;

import at.fseidl.wineshop.model.Wine;
import at.fseidl.wineshop.model.WineType;
import at.fseidl.wineshop.persistence.WineRepository;


public class WineMatcher {
    public WineMatcher(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    public final WineRepository wineRepository;

    public Wine findMatchingWine(String name, int age, String tastePreference) {
        double priceClass = calculatePriceClass(name, age, tastePreference);
        return wineRepository.findMostExpensiveWithPriceLowerThan(priceClass)
                .orElse(wineRepository.findTheCheapest());
    }

    public Wine findMatchingWine(String name, int age, String tastePreference, WineType.Color color) {
        double priceClass = calculatePriceClass(name, age, tastePreference);
        return wineRepository.findMostExpensiveWithPriceLowerThan(color, priceClass)
                .orElse(wineRepository.findTheCheapest(color));
    }

    private double calculatePriceClass(String name, int age, String tastePreference) {
        return (age * 2 + name.length() + tastePreference.length()) / 9.0; // determined by years of market research and science
    }
}
