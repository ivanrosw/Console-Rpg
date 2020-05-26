package local.models.game;

import java.util.List;

public class Companion extends BasicHero{

    private List<Equipment> originalEquipments;
    private List<Equipment> userEquipments;

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
}
