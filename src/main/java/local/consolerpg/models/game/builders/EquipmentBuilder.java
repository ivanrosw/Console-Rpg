package local.consolerpg.models.game.builders;

import local.consolerpg.models.game.Equipment;

public class EquipmentBuilder {

    private Equipment equipment;

    public EquipmentBuilder() {
        equipment = new Equipment();
    }

    public EquipmentBuilder withName(String name) {
        equipment.setName(name);
        return this;
    }

    public EquipmentBuilder withHeroClass(String heroClass) {
        equipment.setHeroClass(heroClass);
        return this;
    }

    public EquipmentBuilder withBodyPart(String bodyPart) {
        equipment.setBodyPart(bodyPart);
        return this;
    }

    public EquipmentBuilder withLevel(int level) {
        equipment.setLevel(level);
        return this;
    }

    public EquipmentBuilder withAgility(int agility) {
        equipment.setAgility(agility);
        return this;
    }

    public EquipmentBuilder withStrength(int strength) {
        equipment.setStrength(strength);
        return this;
    }

    public EquipmentBuilder withIntelligence(int intelligence) {
        equipment.setIntelligence(intelligence);
        return this;
    }

    public EquipmentBuilder withGold(int gold) {
        equipment.setGold(gold);
        return this;
    }

    public Equipment build() {
        return equipment;
    }
}
