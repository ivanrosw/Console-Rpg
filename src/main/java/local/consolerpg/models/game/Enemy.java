package local.consolerpg.models.game;

public class Enemy extends BasicHero{
    private int gold;
    private int experience;

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "Enemy: " + getName() +
                "  Level: " + getLevel() +
                "  HP: " + getCurrentHp() + "\\" + getHp() +
                "  MP: " + getCurrentMp() + "\\" + getMp();
    }
}
