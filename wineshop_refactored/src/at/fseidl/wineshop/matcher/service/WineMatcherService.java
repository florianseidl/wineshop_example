package at.fseidl.wineshop.matcher.service;

import at.fseidl.wineshop.matcher.ColorWineMatch;
import at.fseidl.wineshop.matcher.ColorblindWineMatch;
import at.fseidl.wineshop.shared.WineType;
import at.fseidl.wineshop.shared.persistence.WineRepository;
import at.fseidl.wineshop.shared.service.WineDTO;

public class WineMatcherService {

    public WineMatcherService(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    private final WineRepository wineRepository;

    public WineDTO matchingWineFor(String name, int age, String tastePreference) {
        return new ColorblindWineMatch(name, age, tastePreference).find(wineRepository).toDTO();
    }

    public WineDTO matchingWineFor(String name, int age, String tastePreference, String color) {
        return new ColorWineMatch(name, age, tastePreference, WineType.Color.valueOf(color)).find(wineRepository).toDTO();
    }
}
