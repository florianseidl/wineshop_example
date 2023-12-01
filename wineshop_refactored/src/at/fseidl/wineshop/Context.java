package at.fseidl.wineshop;

import at.fseidl.wineshop.admin.service.WineAdminService;
import at.fseidl.wineshop.db.Db;
import at.fseidl.wineshop.matcher.service.WineMatcherService;
import at.fseidl.wineshop.shared.persistence.WineDbConfig;
import at.fseidl.wineshop.shared.persistence.WineRepository;

public class Context {
    public final Db db = WineDbConfig.createWineDb();
    public final WineRepository wineRepository = new WineRepository(db);
    public final WineAdminService wineAdminService = new WineAdminService(wineRepository);
    public final WineMatcherService wineMatcherService = new WineMatcherService(wineRepository);
}
