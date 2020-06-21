package local.consolerpg.models.game;

import java.util.List;

public abstract class BasicHero extends BasicStats {

    private String name;
    private String heroClass;
    private int totalStrength;
    private int totalAgility;
    private int totalIntelligence;
    private int totalStats;
    private List<Equipment> equipments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeroClass() {
        return heroClass;
    }

    public void setHeroClass(String heroClass) {
        this.heroClass = heroClass;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Equipment> equipments) {
        this.equipments = equipments;
    }

    public int getTotalStrength() {
        return totalStrength;
    }

    public void setTotalStrength(int totalStrength) {
        this.totalStrength = totalStrength;
    }

    public int getTotalAgility() {
        return totalAgility;
    }

    public void setTotalAgility(int totalAgility) {
        this.totalAgility = totalAgility;
    }

    public int getTotalIntelligence() {
        return totalIntelligence;
    }

    public void setTotalIntelligence(int totalIntelligence) {
        this.totalIntelligence = totalIntelligence;
    }

    public int getTotalStats() {
        return totalStats;
    }

    public void setTotalStats(int totalStats) {
        this.totalStats = totalStats;
    }
}
