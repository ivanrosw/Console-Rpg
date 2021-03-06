package local.consolerpg.models.game;

import java.util.List;

public class GameCharacter extends BasicHero{

    private long id;
    private long userId;
    private int statPoints;
    private int gold;
    private int exp;
    private int currentExp;
    private long enemiesKill;
    private long questsDone;
    private int gameCount;
    private int useCooldown;
    private boolean gameComplete;
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

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
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

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
    }

    public int getUseCooldown() {
        return useCooldown;
    }

    public void setUseCooldown(int useCooldown) {
        this.useCooldown = useCooldown;
    }

    public boolean isGameComplete() {
        return gameComplete;
    }

    public void setGameComplete(boolean gameComplete) {
        this.gameComplete = gameComplete;
    }

    @Override
    public String toString() {
        return "Name: " + this.getName() +
                " Class: " + this.getHeroClass() +
                " Level: " + this.getLevel() +
                " Enemies: " + this.getEnemiesKill() +
                " Quests: " + this.getQuestsDone() +
                " League: " + this.getGameCount() +
                " Game complete: " + this.isGameComplete();
    }
}
