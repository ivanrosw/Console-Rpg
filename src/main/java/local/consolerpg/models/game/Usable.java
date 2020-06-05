package local.consolerpg.models.game;

public class Usable implements Item {

    private String name;
    private int count;
    private int gold;

    public Usable() {
    }


    public Usable(String name, int count, int gold) {
        this.name = name;
        this.count = count;
        this.gold = gold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    @Override
    public String toString() {
        return "Name: " + getName() +
                " Count: " + getCount() +
                " Gold: " + getGold();
    }
}
