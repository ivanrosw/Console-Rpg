package local.consolerpg.database.dao;

import local.consolerpg.models.game.GameCharacter;

import java.util.List;

public interface GameCharacterDao {
    void add(GameCharacter character);
    GameCharacter getById(long id);
    List<GameCharacter> getAllByUserId(long userId);
    void update(GameCharacter gameCharacter);
}
