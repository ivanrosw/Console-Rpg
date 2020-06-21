package local.consolerpg.models.game;

public class Quest {

    private String name;
    private int level;
    private int gold;
    private long experience;
    private int stages;

    public Quest() {
    }

    public Quest(String name, int level, int gold, long experience, int stages) {
        this.name = name;
        this.level = level;
        this.gold = gold;
        this.experience = experience;
        this.stages = stages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public int getStages() {
        return stages;
    }

    public void setStages(int stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        return "Location: " + name +
                "  Level: " + level +
                "  Experience: " + experience +
                "  Gold: " + gold;
    }
}
