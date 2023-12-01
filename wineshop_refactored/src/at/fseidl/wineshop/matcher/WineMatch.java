package at.fseidl.wineshop.matcher;

import at.fseidl.wineshop.shared.Wine;
import at.fseidl.wineshop.shared.persistence.WineRepository;

public interface WineMatch {
    Wine find(WineRepository wineRepository);
}
