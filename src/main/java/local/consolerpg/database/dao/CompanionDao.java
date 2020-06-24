package local.consolerpg.database.dao;

import local.consolerpg.models.game.Companion;

import java.util.List;

public interface CompanionDao {

    List<Companion> getBetweenLevels(int minLevel, int maxLevel);
    List<Companion> getLessLevel(int level);

}
