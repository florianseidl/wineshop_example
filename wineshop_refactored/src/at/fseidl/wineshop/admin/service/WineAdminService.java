package at.fseidl.wineshop.admin.service;

import at.fseidl.wineshop.shared.Wine;
import at.fseidl.wineshop.shared.persistence.WineRepository;
import at.fseidl.wineshop.shared.service.WineDTO;

import java.util.List;

public class WineAdminService {

    public WineAdminService(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    private final WineRepository wineRepository;

    public List<WineDTO> listWines() {
        return Wine.findAll(wineRepository).stream()
                .map(Wine::toDTO)
                .toList();
    }

    public WineDTO addWine(WineDTO wineDTO) {
        return Wine.ofDTO(wineDTO).save(wineRepository).toDTO();
    }
}
