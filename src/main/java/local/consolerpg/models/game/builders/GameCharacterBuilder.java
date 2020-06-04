package local.consolerpg.models.game.builders;

import local.consolerpg.models.game.Companion;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;

import java.util.List;

public class GameCharacterBuilder {

    private GameCharacter gameCharacter;

    public GameCharacterBuilder() {
        gameCharacter = new GameCharacter();
    }

    public GameCharacterBuilder withId(long id) {
        gameCharacter.setId(id);
        return this;
    }

    public GameCharacterBuilder withUserId(long userId) {
        gameCharacter.setUserId(userId);
        return this;
    }

    public GameCharacterBuilder withName(String name) {
        gameCharacter.setName(name);
        return this;
    }

    public GameCharacterBuilder withHeroClass(String heroClass) {
        gameCharacter.setHeroClass(heroClass);
        return this;
    }

    public GameCharacterBuilder withLevel(int level) {
        gameCharacter.setLevel(level);
        return this;
    }

    public GameCharacterBuilder withStrength(int strength) {
        gameCharacter.setStrength(strength);
        return this;
    }

    public GameCharacterBuilder withAgility(int agility) {
        gameCharacter.setAgility(agility);
        return this;
    }

    public GameCharacterBuilder withIntelligence(int intelligence) {
        gameCharacter.setIntelligence(intelligence);
        return this;
    }

    public GameCharacterBuilder withEquipments(List<Equipment> equipments) {
        gameCharacter.setEquipments(equipments);
        return this;
    }

    public GameCharacterBuilder withBag(List<Item> bag) {
        gameCharacter.setBag(bag);
        return this;
    }

    public GameCharacterBuilder withEnemiesKill(long enemiesKill) {
        gameCharacter.setEnemiesKill(enemiesKill);
        return this;
    }

    public GameCharacterBuilder withQuestsDone(long questsDone) {
        gameCharacter.setQuestsDone(questsDone);
        return this;
    }

    public GameCharacterBuilder withGameCount(int gameCount) {
        gameCharacter.setGameCount(gameCount);
        return this;
    }

    public GameCharacterBuilder withCompanion(Companion companion) {
        gameCharacter.setCompanion(companion);
        return this;
    }

    public GameCharacterBuilder withGold(int gold) {
        gameCharacter.setGold(gold);
        return this;
    }

    public GameCharacterBuilder withStatPoints(int statPoints) {
        gameCharacter.setStatPoints(statPoints);
        return this;
    }

    public GameCharacter build() {
        return gameCharacter;
    }
}
