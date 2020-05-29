package local.consolerpg.models.game;

import java.util.List;

public class Character extends BasicHero{

    private long id;
    private long userId;
    private long enemiesKill;
    private long questsDone;
    private int gameCount;
    private Companion companion;
    private List<Item> bag;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getEnemiesKill() {
        return enemiesKill;
    }

    public void setEnemiesKill(long enemiesKill) {
        this.enemiesKill = enemiesKill;
    }

    public long getQuestsDone() {
        return questsDone;
    }

    public void setQuestsDone(long questsDone) {
        this.questsDone = questsDone;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public Companion getCompanion() {
        return companion;
    }

    public void setCompanion(Companion companion) {
        this.companion = companion;
    }

    public List<Item> getBag() {
        return bag;
    }

    public void setBag(List<Item> bag) {
        this.bag = bag;
    }
}
