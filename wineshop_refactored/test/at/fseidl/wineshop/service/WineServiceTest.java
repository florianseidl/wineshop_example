package at.fseidl.wineshop.service;


import at.fseidl.wineshop.Context;
import at.fseidl.wineshop.shared.WineType;
import at.fseidl.wineshop.shared.service.WineDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WineServiceTest {
    private final Context context = new Context();

    @Test
    void empty_findAll_empty() {
        assertEquals(0, context.wineAdminService.listWines().size());
    }

    @Test
    void  add1Wine_findAll_1wines() {
        WineDTO wine = createFederspiel();
        context.wineAdminService.addWine(wine);
        List<WineDTO> queryResult = context.wineAdminService.listWines();
        assertEquals(1, queryResult.size());
        assertEquals(queryResult.get(0).name(), wine.name());
    }

    @Test
    void  add3Wines_findAll_3wines() {
        context.wineAdminService.addWine(createFederspiel());
        context.wineAdminService.addWine(createSkoff());
        context.wineAdminService.addWine(createLutzmannsburg());

        List<WineDTO> queryResult = context.wineAdminService.listWines();
        assertEquals(3, queryResult.size());
    }

    @Test
    void  add3Wines_matchWineForHans_skoff() {
        context.wineAdminService.addWine(createFederspiel());
        WineDTO skoff = context.wineAdminService.addWine(createSkoff());
        context.wineAdminService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineMatcherService.matchingWineFor("Hans", 45, "not too sour, maybe a bit sweet");
        assertEquals(skoff, matchingWine);
    }

    @Test
    void  add3Wines_matchWineForHansRed_lutzmannsburg() {
        context.wineAdminService.addWine(createFederspiel());
        context.wineAdminService.addWine(createSkoff());
        WineDTO lutzmansburg = context.wineAdminService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineMatcherService.matchingWineFor("Hans", 45, "not too sour, maybe a bit sweet", "RED");
        assertEquals(lutzmansburg, matchingWine);
    }

    @Test
    void  add3Wines_matchWineForMax_federspiel() {
        WineDTO federspiel  = context.wineAdminService.addWine(createFederspiel());
        context.wineAdminService.addWine(createSkoff());
        context.wineAdminService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineMatcherService.matchingWineFor("Max", 20, "none");
        assertEquals(federspiel, matchingWine);
    }

    @Test
    void  add3Wines_matchWineForMariaLutzmansburg() {
        context.wineAdminService.addWine(createFederspiel());
        context.wineAdminService.addWine(createSkoff());
        WineDTO lutzmansburg = context.wineAdminService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineMatcherService.matchingWineFor("Maria", 61, "ausgeprägtes Bouquet, angenehm im Abgang, eher Fruchtig");
        assertEquals(lutzmansburg, matchingWine);
    }

    private static WineDTO createFederspiel()  {
        return new WineDTO(
                "Federspiel",
                "AT", "Wachau",
                "Grüner Veltliner",
                "WHITE",
                10.3);
    }

    private static WineDTO createSkoff()  {
        return new WineDTO(
                "Skoff",
                "AT", "Wachau",
                "Savingion Blanc",
                "WHITE",
                12.5);
    }

    private static WineDTO createLutzmannsburg()  {
        return new WineDTO(
                "Lutzmannsburg",
                "AT", "Wachau",
                "Blaufränkisch",
                "RED",
                15.9);
    }
}