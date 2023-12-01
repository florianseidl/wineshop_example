package at.fseidl.wineshop.service;

import at.fseidl.wineshop.business.WineMatcher;
import at.fseidl.wineshop.model.Origin;
import at.fseidl.wineshop.model.Wine;
import at.fseidl.wineshop.model.WineType;
import at.fseidl.wineshop.persistence.WineRepository;

import java.util.List;

public class WineService {

    public WineService(WineRepository wineRepository, WineMatcher wineMatcher) {
        this.wineRepository = wineRepository;
        this.wineMatcher = wineMatcher;
    }

    private final WineRepository wineRepository;
    private final WineMatcher wineMatcher;

    public List<WineDTO> listWines() {
        return wineRepository.findAll().stream()
                .map(WineService::toWineDTO)
                .toList();
    }

    public WineDTO addWine(WineDTO wineDTO) {
        return toWineDTO(wineRepository.save(toWine(wineDTO)));
    }

    public WineDTO matchingWineFor(String name, int age, String tastePreference) {
        return toWineDTO(wineMatcher.findMatchingWine(name, age, tastePreference));
    }

    public WineDTO matchingWineFor(String name, int age, String tastePreference, String color) {
        return toWineDTO(wineMatcher.findMatchingWine(name, age, tastePreference, WineType.Color.valueOf(color)));
    }

    private static WineDTO toWineDTO(Wine wine) {
        return new WineDTO(wine.name(),
                wine.origin().country(),
                wine.origin().region(),
                wine.type().name(),
                wine.type().color().name(),
                wine.price());
    }

    private static Wine toWine(WineDTO wineDTO) {
        return new Wine(
                wineDTO.name(),
                new Origin(wineDTO.orignCountry(), wineDTO.originRegion()),
                new WineType(wineDTO.wineType(), WineType.Color.valueOf(wineDTO.wineTypeColor())),
                wineDTO.price());
    }

}
