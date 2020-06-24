package local.consolerpg.models.game.builders;

import local.consolerpg.models.game.Companion;
import local.consolerpg.models.game.Equipment;

import java.util.List;

public class CompanionBuilder {

    Companion companion;

    public CompanionBuilder() {
        companion = new Companion();
    }

    public CompanionBuilder withId(long id) {
        companion.setId(id);
        return this;
    }

    public CompanionBuilder withName(String name) {
        companion.setName(name);
        return this;
    }

    public CompanionBuilder withLevel(int level) {
        companion.setLevel(level);
        return this;
    }

    public CompanionBuilder withHeroClass(String heroClass) {
        companion.setHeroClass(heroClass);
        return this;
    }

    public CompanionBuilder withStrength(int strength) {
        companion.setStrength(strength);
        return this;
    }

    public CompanionBuilder withAgility(int agility) {
        companion.setAgility(agility);
        return this;
    }

    public CompanionBuilder withIntelligence(int intelligence) {
        companion.setIntelligence(intelligence);
        return this;
    }

    public CompanionBuilder withEquipments(List<Equipment> equipments) {
        companion.setEquipments(equipments);
        return this;
    }

    public CompanionBuilder withOriginalEquipments(List<Equipment> originalEquipments) {
        companion.setOriginalEquipments(originalEquipments);
        return this;
    }

    public CompanionBuilder withUserEquipments(List<Equipment> userEquipments) {
        companion.setUserEquipments(userEquipments);
        return this;
    }

    public Companion build() {
        return companion;
    }
}
