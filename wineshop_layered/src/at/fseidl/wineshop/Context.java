package at.fseidl.wineshop;

import at.fseidl.wineshop.business.WineMatcher;
import at.fseidl.wineshop.db.Db;
import at.fseidl.wineshop.persistence.WineDbConfig;
import at.fseidl.wineshop.persistence.WineRepository;
import at.fseidl.wineshop.service.WineService;

public class Context {
    public final Db db = WineDbConfig.createWineDb();
    public final WineRepository wineRepository = new WineRepository(db);
    public final WineMatcher wineMatcher = new WineMatcher(wineRepository);
    public final WineService wineService = new WineService(wineRepository, wineMatcher);
}
