package local.consolerpg.models.game;

import java.util.List;
import java.util.Map;

public class BasicHero extends BasicStats {

    private String name;
    private String heroClass;
    private int totalStrength;
    private int totalAgility;
    private int totalIntelligence;
    private int totalStats;
    private int hp;
    private int currentHp;
    private int mp;
    private int currentMp;
    private int spellCooldown;
    private boolean isBlocked;
    private Map<String, Integer> bonus;
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

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(int currentMp) {
        this.currentMp = currentMp;
    }

    public int getSpellCooldown() {
        return spellCooldown;
    }

    public void setSpellCooldown(int spellCooldown) {
        this.spellCooldown = spellCooldown;
    }

    public Map<String, Integer> getBonus() {
        return bonus;
    }

    public void setBonus(Map<String, Integer> bonus) {
        this.bonus = bonus;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
