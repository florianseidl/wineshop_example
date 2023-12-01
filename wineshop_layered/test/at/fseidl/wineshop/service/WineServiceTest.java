package at.fseidl.wineshop.service;


import at.fseidl.wineshop.Context;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WineServiceTest {
    private final Context context = new Context();

    @Test
    void empty_findAll_empty() {
        assertEquals(0, context.wineService.listWines().size());
    }

    @Test
    void  add1Wine_findAll_1wines() {
        WineDTO wine = createFederspiel();
        context.wineService.addWine(wine);
        List<WineDTO> queryResult = context.wineService.listWines();
        assertEquals(1, queryResult.size());
        assertEquals(queryResult.get(0).name(), wine.name());
    }

    @Test
    void  add3Wines_findAll_3wines() {
        context.wineService.addWine(createFederspiel());
        context.wineService.addWine(createSkoff());
        context.wineService.addWine(createLutzmannsburg());

        List<WineDTO> queryResult = context.wineService.listWines();
        assertEquals(3, queryResult.size());
    }

    @Test
    void  add3Wines_matchWineForHans_skoff() {
        context.wineService.addWine(createFederspiel());
        WineDTO skoff = context.wineService.addWine(createSkoff());
        context.wineService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineService.matchingWineFor("Hans", 45, "not too sour, maybe a bit sweet");
        assertEquals(skoff, matchingWine);
    }

    @Test
    void  add3Wines_matchWineForHansRed_lutzmannsburg() {
        context.wineService.addWine(createFederspiel());
        context.wineService.addWine(createSkoff());
        WineDTO lutzmansburg = context.wineService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineService.matchingWineFor("Hans", 45, "not too sour, maybe a bit sweet", "RED");
        assertEquals(lutzmansburg, matchingWine);
    }

    @Test
    void  add3Wines_matchWineForMax_federspiel() {
        WineDTO federspiel  = context.wineService.addWine(createFederspiel());
        context.wineService.addWine(createSkoff());
        context.wineService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineService.matchingWineFor("Max", 20, "none");
        assertEquals(federspiel, matchingWine);
    }

    @Test
    void  add3Wines_matchWineForMariaLutzmansburg() {
        context.wineService.addWine(createFederspiel());
        context.wineService.addWine(createSkoff());
        WineDTO lutzmansburg = context.wineService.addWine(createLutzmannsburg());

        WineDTO matchingWine = context.wineService.matchingWineFor("Maria", 61, "ausgeprägtes Bouquet, angenehm im Abgang, eher Fruchtig");
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