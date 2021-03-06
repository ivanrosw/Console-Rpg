package local.consolerpg.database.dao;

import local.consolerpg.models.game.GameCharacter;

import java.util.List;

public interface GameCharacterDao {
    void add(GameCharacter character);

    GameCharacter getById(long id);

    List<GameCharacter> getAll(long userId);

    void update(GameCharacter gameCharacter);

    List<GameCharacter> getFirstByLevel();

    List<GameCharacter> getFirstByKills();

    List<GameCharacter> getFirstByQuests();

    List<GameCharacter> getFirstByLevel(int gameCount);

    List<GameCharacter> getFirstByKills(int gameCount);

    List<GameCharacter> getFirstByQuests(int gameCount);
}
