package local.consolerpg.models.game;

import java.util.List;

public class Companion extends BasicHero{

    private long id;
    private List<Equipment> originalEquipments;
    private List<Equipment> userEquipments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Equipment> getOriginalEquipments() {
        return originalEquipments;
    }

    public void setOriginalEquipments(List<Equipment> originalEquipments) {
        this.originalEquipments = originalEquipments;
    }

    public List<Equipment> getUserEquipments() {
        return userEquipments;
    }

    public void setUserEquipments(List<Equipment> userEquipments) {
        this.userEquipments = userEquipments;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Name: " + getName()
                        + "  Hero class: " + getHeroClass()
                        + "  Level: " + getLevel()
                        + "  Strength: " + getStrength()
                        + "  Agility: " + getAgility()
                        + "  Intelligence: " + getIntelligence()
                        + "\nEquipments:\n");
        getEquipments().forEach(equipment -> {
            result.append(equipment.toString() + "\n");
        });
        return result.toString();
    }
}
