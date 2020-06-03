package local.consolerpg.models.game;

public class Equipment extends BasicStats implements Item {

    private String name;
    private String heroClass;
    private String bodyPart;
    private int gold;

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

    public String getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
