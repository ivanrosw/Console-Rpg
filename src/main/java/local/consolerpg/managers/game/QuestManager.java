package local.consolerpg.managers.game;

import local.consolerpg.database.dao.DaoFactory;
import local.consolerpg.database.dao.GameCharacterDao;
import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.*;
import local.consolerpg.models.game.concepts.HeroClasses;
import local.consolerpg.models.game.concepts.Usables;
import local.consolerpg.models.game.concepts.names.LocationsNouns;
import local.consolerpg.models.game.concepts.names.MageSpells;
import local.consolerpg.models.game.concepts.names.RogueSpells;
import local.consolerpg.models.game.concepts.names.WarriorSpells;
import local.consolerpg.models.game.generators.EnemyGenerator;
import local.consolerpg.models.game.generators.EquipmentGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class QuestManager {

    private static final int STATS_PER_LEVEL = 5;
    private static final int BAG_SIZE = 10;

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    private List<Quest> quests;

    public QuestManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getQuestMenu() {
        boolean inMune = true;
        while (inMune) {
            try {
                generateQuests();
                printQuests();
                System.out.println("Enter number of quest where you want to go or \"E\" to close menu");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMune = false;
                } else {
                    int questIndex = Integer.parseInt(userAnswer) - 1;

                    if (questIndex >= quests.size() || questIndex < 0) {
                        System.out.println("Entered wrong number");
                        continue;
                    }

                    System.out.println("Are you sure want to enter quest? y\\n");
                    userAnswer = consoleReader.readLine();
                    System.out.println();

                    if (userAnswer.equals("n")) {
                        System.out.println("Entering quest canceled");
                        System.out.println();
                    } else if (userAnswer.equals("y")) {
                        if (questIndex == 5) {
                            getLastQuestMenu(quests.get(questIndex));
                        } else {
                            getEnterQuestMenu(quests.get(questIndex));
                        }
                        inMune = false;
                    } else {
                        System.out.println("Entered wrong symbol");

                    }
                }

            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }

        gameCharacter.setCurrentHp(gameCharacter.getHp());
        gameCharacter.setCurrentMp(gameCharacter.getCurrentMp());
        if (gameCharacter.getCompanion() != null) {
            Companion companion = gameCharacter.getCompanion();
            companion.setCurrentHp(companion.getHp());
            companion.setCurrentMp(companion.getCurrentMp());
            gameCharacter.setCompanion(companion);
        }

        GameCharacterDao characterDao = DaoFactory.getGameCharacterDao();
        characterDao.update(gameCharacter);
        System.out.println("Progress saved");
        System.out.println();
    }

    private void printQuests() {
        int index = 1;
        for (Quest quest : quests) {
            System.out.println(index + ":");
            System.out.println(quest);
            index++;
        }
    }

    private void generateQuests() {
        Random random = new Random();
        quests = new ArrayList<>();
        List<LocationsNouns> questsNames = Arrays.asList(LocationsNouns.values());

        for (int i = 0; i < 5; i++) {
            int level = gameCharacter.getLevel() + random.nextInt(11) - 5;
            if (level < 1) {
                level = 1;
            }

            String name = questsNames.get(random.nextInt(questsNames.size())).toString();
            int stages = random.nextInt(6) + 5;

            long experience = level * 20 + Math.round(level * 20 / 100.0 * (random.nextInt(40) - 20));
            int gold = (int) (level * 4 + Math.round(level * 4 / 100.0 * (random.nextInt(40) - 20)));

            Quest quest = new Quest(name, level, gold, experience, stages);
            quests.add(quest);
        }

        int lastBossLevel = 50 * gameCharacter.getGameCount();
        if (gameCharacter.getLevel() == lastBossLevel) {
            Quest quest = new Quest();
            quest.setName("Broken tavern");
            quest.setLevel(lastBossLevel);
            quest.setStages(1);
            int gold = lastBossLevel * 8;
            quest.setGold(gold);
            int exp = lastBossLevel * 40;
            quest.setExperience(exp);
            quests.add(quest);
        }
    }

    private void getEnterQuestMenu(Quest quest) {
        Random random = new Random();
        int stage = 1;
        boolean inQuest = true;
        while (inQuest && stage <= quest.getStages()) {
            try {
                System.out.println(quest.getName());
                System.out.println("1: Next  2: Camp  3: Run away");
                System.out.println("Enter number to choose option");
                String userAnswer = consoleReader.readLine();

                if (userAnswer.equals("1")) {
                    int enemiesCount = random.nextInt(4) + 1;

                    List<Enemy> enemies = new ArrayList<>();
                    EnemyGenerator generator = new EnemyGenerator();
                    for (int i = 0; i < enemiesCount; i++) {
                        enemies.add(generator.getRandom(quest.getLevel()));
                    }

                    getFightMenu(enemies);

                    if (!isWon(enemies)) {
                        System.out.println("Quest failed");
                        inQuest = false;
                    } else {
                        calculateKills(enemies);
                        getRewardMenu(enemies);
                        calculateLevelling();
                        stage++;
                    }

                } else if (userAnswer.equals("2")) {
                    getCampMenu();

                } else if (userAnswer.equals("3")) {
                    System.out.println("Are you sure want to run away? y\\n");
                    userAnswer = consoleReader.readLine();
                    if (userAnswer.equals("y")) {
                        System.out.println("Running away...");
                        inQuest = false;
                    }

                } else {
                    System.out.println("Entered wrong symbol");
                }

                if (stage > quest.getStages()) {
                    System.out.println("Quest complete!");
                    System.out.println("Exp: " + quest.getExperience() + "  Gold: " + quest.getGold());
                    System.out.println();
                    gameCharacter.setQuestsDone(gameCharacter.getQuestsDone() + 1);
                    gameCharacter.setCurrentExp((int) (gameCharacter.getCurrentExp() + quest.getExperience()));
                    gameCharacter.setGold(gameCharacter.getGold() + quest.getGold());
                    calculateLevelling();
                    inQuest = false;
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            }
        }
    }

    private void calculateLevelling() {
        int currentExp = gameCharacter.getCurrentExp();
        int exp = gameCharacter.getExp();

        if (currentExp >= exp) {
            System.out.println("Level up!");
            System.out.println();
            currentExp -= exp;
            exp = (int) Math.round(exp * 1.75);
            int level = gameCharacter.getLevel() + 1;

            gameCharacter.setLevel(level);
            gameCharacter.setCurrentExp(currentExp);
            gameCharacter.setExp(exp);
            gameCharacter.setStatPoints(gameCharacter.getStatPoints() + STATS_PER_LEVEL);
        }
    }

    private boolean isWon(List<Enemy> enemies) {
        boolean allDead = true;
        for (Enemy enemy : enemies) {
            if (enemy.getCurrentHp() > 0) {
                allDead = false;
                break;
            }
        }
        return allDead;
    }

    private void calculateKills(List<Enemy> enemies) {
        long kills = gameCharacter.getEnemiesKill();
        kills += enemies.size();
        gameCharacter.setEnemiesKill(kills);
    }

    private void getRewardMenu(List<Enemy> enemies) {
        Random random = new Random();

        int totalExp = 0;
        int totalGold = 0;

        for (Enemy enemy : enemies) {
            totalExp += enemy.getExperience();
            totalGold += enemy.getGold();
        }

        gameCharacter.setCurrentExp(gameCharacter.getCurrentExp() + totalExp);
        gameCharacter.setGold(gameCharacter.getGold() + totalGold);

        Enemy enemy = enemies.get(random.nextInt(enemies.size()));
        EquipmentGenerator generator = new EquipmentGenerator();
        Equipment equipment = generator.getRandom(enemy.getLevel());

        boolean isChosen = false;
        while (!isChosen) {
            try {
                System.out.println("You find: ");
                System.out.println(equipment);
                System.out.println("Do you want to peack up? y\\n");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("n")) {
                    isChosen = true;
                } else if (userAnswer.equals("y")) {
                    List<Item> bag = gameCharacter.getBag();
                    if (bag.size() <= BAG_SIZE) {
                        bag.add(equipment);
                        gameCharacter.setBag(bag);
                        System.out.println(equipment.getName() + " picked up");
                        System.out.println();
                        isChosen = true;
                    } else {
                        System.out.println("Not enough space in bag");
                        printBag();
                        System.out.println("Enter number of item that you want to throw or \"E\" to cancel");
                        userAnswer = consoleReader.readLine();
                        System.out.println();

                        if (consoleReader.equals("E")) {
                            continue;
                        } else {
                            int itemIndex = Integer.parseInt(userAnswer) - 1;
                            System.out.println("Are you sure? y\\n");
                            userAnswer = consoleReader.readLine();

                            if (userAnswer.equals("n")) {
                                continue;
                            } else if (userAnswer.equals("y")) {
                                Item item = bag.get(itemIndex);
                                bag.set(itemIndex, equipment);
                                gameCharacter.setBag(bag);
                                System.out.println(item.getName() + " throwed");
                                System.out.println(equipment.getName() + " picked up");
                                isChosen = true;
                            } else {
                                System.out.println("Entered wrong symbol");
                            }
                        }
                    }

                } else {
                    System.out.println("Entered wrong symbol");
                    System.out.println();
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong number");
            }
        }
    }

    private void printBag() {
        int index = 1;
        for (Item item : gameCharacter.getBag()) {
            System.out.println(index + ":");
            System.out.println(item);
        }
    }

    private void getCampMenu() {
        boolean inMenu = true;
        while (inMenu) {
            try {
                System.out.println("1: Rest  2: Use  3: Get out");
                String userAnswer = consoleReader.readLine();

                if (userAnswer.equals("3")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    restInCamp();
                } else if (userAnswer.equals("2")) {
                    getUseMenu();
                } else {
                    System.out.println("Entered wrong symbol");
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            }
        }
    }

    private void restInCamp() {
        int healHp = gameCharacter.getHp() - gameCharacter.getCurrentHp();
        int healMp = gameCharacter.getMp() - gameCharacter.getCurrentMp();

        int time = 0;
        if (healHp == 0 && healMp == 0) {

            if (gameCharacter.getCompanion() != null && gameCharacter.getCompanion().getCurrentHp() < gameCharacter.getCompanion().getHp()) {
                System.out.println("Companion healing...");
                time = 6;
            } else {
                System.out.println("You have full HP and MP");
                System.out.println();
            }
            return;
        } else if (healHp > healMp) {
            int percent = (int) Math.round(gameCharacter.getHp() / 100.0);
            time = (int) Math.round(healHp / percent / 2.0);
        } else {
            int percent = (int) Math.round(gameCharacter.getMp() / 100.0);
            time = (int) Math.round(healMp / percent / 2.0);
        }

        try {
            while (time > 0) {
                System.out.println(time + "...");
                Thread.sleep(1000);
                time--;
            }
        } catch (InterruptedException e) {
            throw new ManagerException("Rest error", e);
        }

        gameCharacter.setCurrentHp(gameCharacter.getHp());
        gameCharacter.setCurrentMp(gameCharacter.getMp());

        if (gameCharacter.getCompanion() != null) {
            Companion companion = gameCharacter.getCompanion();
            companion.setCurrentHp(companion.getHp());
            companion.setCurrentMp(companion.getMp());
            gameCharacter.setCompanion(companion);
        }

        System.out.println("Resting over...");
        System.out.println("Restored full HP and MP");
        System.out.println();
    }

    private boolean getUseMenu() {
        List<Item> bag = gameCharacter.getBag();
        List<Usable> usables = new ArrayList<>();

        for (Item item : bag) {
            if (item instanceof Usable) {
                usables.add((Usable) item);
            }
        }

        boolean isUsed = false;
        boolean inMenu = true;
        while (inMenu) {
            try {
                System.out.println("Use:");
                printUsables(usables);
                System.out.println("Enter number of usables that you want to use or \"E\" to close menu");
                String userAnswer = consoleReader.readLine();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else {
                    int usableIndex = Integer.parseInt(userAnswer);
                    Usable usable = usables.get(usableIndex);
                    usable.setCount(usable.getCount() - 1);

                    if (usable.getName().equals(Usables.HP_potion.toString().replaceAll("_", " "))) {
                        int percentHp = (int) Math.round(gameCharacter.getHp() / 100.0);
                        int healHp = percentHp * 10;
                        int currentHp = gameCharacter.getCurrentHp() + healHp;

                        if (currentHp > gameCharacter.getHp()) {
                            currentHp = gameCharacter.getHp();
                        }

                        gameCharacter.setCurrentHp(currentHp);
                        gameCharacter.setUseCooldown(4);
                        isUsed = true;
                        inMenu = false;

                    } else if (usable.getName().equals(Usables.MP_potion.toString().replaceAll("_", " "))) {
                        int percentMp = (int) Math.round(gameCharacter.getMp() / 100.0);
                        int healMp = percentMp * 10;
                        int currentMp = gameCharacter.getCurrentMp() + healMp;

                        if (currentMp > gameCharacter.getMp()) {
                            currentMp = gameCharacter.getMp();
                        }

                        gameCharacter.setCurrentMp(currentMp);
                        gameCharacter.setUseCooldown(4);
                        isUsed = true;
                        inMenu = false;

                    } else {
                        System.out.println("Cant use it now");
                    }

                    if (!inMenu) {
                        for (int i = 0; i < bag.size(); i++) {
                            Item item = bag.get(i);

                            if (item.getName().equals(usable.getName())) {
                                if (usable.getCount() <= 0) {
                                    bag.remove(i);
                                    break;
                                } else {
                                    bag.set(i, usable);
                                    break;
                                }
                            }
                        }
                        gameCharacter.setBag(bag);
                    }
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }

        return isUsed;
    }

    private void printUsables(List<Usable> usables) {
        for (int i = 1; i <= usables.size(); i++) {
            int index = i - 1;
            System.out.println(i + ":");
            System.out.println(usables.get(index));
        }
    }

    private void getFightMenu(List<Enemy> enemies) {
        gameCharacter.setUseCooldown(0);
        gameCharacter.setSpellCooldown(0);
        boolean isRunnigAway = false;
        try {
            while (!isWon(enemies) && gameCharacter.getCurrentHp() > 0) {
                boolean isChosen = false;
                while (!isChosen) {
                    System.out.println("1: Attack  2: Use  3: Spell  4: Block  5: Run away");
                    System.out.println();
                    printBattle(enemies);
                    System.out.println();
                    System.out.println("Enter number to choose option");
                    String userAnswer = consoleReader.readLine();

                    if (userAnswer.equals("1")) {
                        if (getAttackMenu(enemies)) {
                            isChosen = true;
                        }

                    } else if (userAnswer.equals("2")) {
                        if (gameCharacter.getUseCooldown() > 0) {
                            System.out.println("Cant use items now, cd:" + gameCharacter.getUseCooldown());
                        } else {
                            if (getUseMenu()) {
                                isChosen = true;
                            }
                        }
                    } else if (userAnswer.equals("3")) {
                        if (gameCharacter.getSpellCooldown() > 0) {
                            System.out.println("Cant use spells now, cd:" + gameCharacter.getSpellCooldown());
                        } else {
                            if (getSpellMenu(enemies)) {
                                isChosen = true;
                            }
                        }
                    } else if (userAnswer.equals("4")) {
                        System.out.println(gameCharacter.getName() + " block");
                        System.out.println();
                        gameCharacter.setBlocked(true);
                        isChosen = true;

                    } else if (userAnswer.equals("5")) {
                        System.out.println("Running away...");
                        System.out.println();
                        isRunnigAway = true;
                        isChosen = true;

                    } else {
                        System.out.println("Entered wrong symbol");
                    }
                }

                if(isRunnigAway) {
                    break;
                }

                uncheckEnemiesBlock(enemies);
                aiMove(enemies);
                uncheckHeroesBlock();
                calculateCooldowns(enemies);
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }

        if (gameCharacter.getCurrentHp() <= 0) {
            System.out.println("Some stranger take you to tavern...");
            System.out.println();
        }
    }

    private void uncheckHeroesBlock() {
        gameCharacter.setBlocked(false);
        if (gameCharacter.getCompanion() != null) {
            Companion companion = gameCharacter.getCompanion();
            companion.setBlocked(false);
            gameCharacter.setCompanion(companion);
        }
    }

    private void uncheckEnemiesBlock(List<Enemy> enemies) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            enemy.setBlocked(false);
            enemies.set(i, enemy);
        }
    }

    private void calculateCooldowns(List<Enemy> enemies) {
        if (gameCharacter.getUseCooldown() > 0) {
            gameCharacter.setUseCooldown(gameCharacter.getUseCooldown() - 1);
        }
        if (gameCharacter.getSpellCooldown() > 0) {
            gameCharacter.setSpellCooldown(gameCharacter.getSpellCooldown() - 1);
        }
        if (gameCharacter.getBonus() != null && !gameCharacter.getBonus().isEmpty()) {
            Map<String, Integer> bonus = gameCharacter.getBonus();
            if (bonus.containsKey(WarriorSpells.Fenrir_Rage.toString())) {
                int cooldown = bonus.get(WarriorSpells.Fenrir_Rage.toString()) - 1;
                if (cooldown <= 0) {
                    bonus.remove(WarriorSpells.Fenrir_Rage.toString());
                } else {
                    bonus.replace(WarriorSpells.Fenrir_Rage.toString(), cooldown);
                }
                gameCharacter.setBonus(bonus);
            }
            if (bonus.containsKey(RogueSpells.Poison.toString())) {
                int cooldown = bonus.get(RogueSpells.Poison.toString()) - 1;
                if (cooldown <= 0) {
                    bonus.remove(RogueSpells.Poison.toString());
                } else {
                    bonus.replace(RogueSpells.Poison.toString(), cooldown);
                }
                gameCharacter.setBonus(bonus);
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.getSpellCooldown() > 0) {
                enemy.setSpellCooldown(enemy.getSpellCooldown() - 1);
                enemies.set(i, enemy);
            }
            if (enemy.getBonus() != null && !enemy.getBonus().isEmpty()) {
                Map<String, Integer> bonus = enemy.getBonus();
                if (bonus.containsKey(WarriorSpells.Fenrir_Rage.toString())) {
                    int cooldown = bonus.get(WarriorSpells.Fenrir_Rage.toString()) - 1;
                    if (cooldown <= 0) {
                        bonus.remove(WarriorSpells.Fenrir_Rage.toString());
                    } else {
                        bonus.replace(WarriorSpells.Fenrir_Rage.toString(), cooldown);
                    }
                    enemy.setBonus(bonus);
                }
                if (bonus.containsKey(RogueSpells.Poison.toString())) {
                    int cooldown = bonus.get(RogueSpells.Poison.toString()) - 1;
                    if (cooldown <= 0) {
                        bonus.remove(RogueSpells.Poison.toString());
                    } else {
                        bonus.replace(RogueSpells.Poison.toString(), cooldown);
                    }
                    enemy.setBonus(bonus);
                }
            }
        }
    }

    private boolean getAttackMenu(List<Enemy> enemies) {
        Random random = new Random();
        boolean isAttacked = false;
        boolean inMenu = true;

        while (inMenu) {
            try {
                printEnemies(enemies);
                System.out.println();
                System.out.println("Enter number of enemy that you want to attack or \"E\" to cancel");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else {
                    int enemyIndex = Integer.parseInt(userAnswer) - 1;

                    if (enemyIndex >= enemies.size() || enemyIndex < 0) {
                        System.out.println("Entered wrong number");
                        System.out.println();
                        continue;
                    }

                    Enemy enemy = enemies.get(enemyIndex);
                    if (enemy.getCurrentHp() <= 0) {
                        System.out.println("Enemy already dead");
                        System.out.println();
                        continue;
                    }

                    int totalAgilityPercent = enemy.getTotalAgility() / 100;
                    if (totalAgilityPercent <= 0) {
                        totalAgilityPercent = 1;
                    }
                    int chanceMissPercent = (gameCharacter.getTotalAgility() - enemy.getTotalAgility()) / totalAgilityPercent + 10;
                    if (chanceMissPercent < 10) {
                        chanceMissPercent = 10;
                    } else if (chanceMissPercent > 50) {
                        chanceMissPercent = 50;
                    }
                    int chanceMiss = random.nextInt(101);
                    if (chanceMiss <= chanceMissPercent) {
                        System.out.println(gameCharacter.getName() + " Miss");
                        System.out.println();
                        isAttacked = true;
                        inMenu = false;
                        continue;
                    }

                    int bonus = 0;
                    int totalDamage = 0;
                    int damage = (int) (Math.round(gameCharacter.getTotalStrength() * 1.25)
                            + Math.round(gameCharacter.getTotalStrength() * 1.25 / 100 * (random.nextInt(10)-5)));

                    if (gameCharacter.getBonus() != null && gameCharacter.getBonus().containsKey(WarriorSpells.Fenrir_Rage.toString())) {
                        bonus = damage;
                    }
                    if (gameCharacter.getBonus() != null && gameCharacter.getBonus().containsKey(RogueSpells.Poison.toString())) {
                        bonus += (int) Math.round(damage * 0.5);
                    }

                    totalDamage += bonus + damage;

                    int totalStatsPercent = (int) Math.round(gameCharacter.getTotalStats()/100.0);
                    if (totalStatsPercent <= 0) {
                        totalStatsPercent = 1;
                    }
                    int chanceCritPercent = gameCharacter.getTotalAgility() / totalStatsPercent + 20;
                    int chanceCrit = random.nextInt(101);
                    if (chanceCritPercent < 20) {
                        chanceCritPercent = 20;
                    } else if (chanceCritPercent > 75) {
                        chanceCritPercent = 75;
                    }
                    if (chanceCrit <= chanceCritPercent) {
                        totalDamage += damage;
                    }

                    if (calculateDamage(totalDamage, enemy)) {
                        enemies.set(enemyIndex, enemy);
                        isAttacked = true;
                        inMenu = false;
                    }
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }

        }

        return isAttacked;
    }

    private boolean getSpellMenu(List<Enemy> enemies) {
        boolean isSpellUsed = false;

        if (gameCharacter.getHeroClass().equals(HeroClasses.Warriror.toString())) {
            if (getWarriorSpells(enemies)) {
                isSpellUsed = true;
            }
        } else if (gameCharacter.getHeroClass().equals(HeroClasses.Rogue.toString())) {
            if (getRogueSpells(enemies)) {
                isSpellUsed = true;
            }
        } else if (gameCharacter.getHeroClass().equals(HeroClasses.Mage.toString())) {
            if (getMageSpells(enemies)) {
                isSpellUsed = true;
            }
        } else {
            System.out.println("Its looks like you are cant use spells");
            System.out.println();
        }

        return isSpellUsed;
    }

    private boolean getWarriorSpells(List<Enemy> enemies) {
        boolean isSpellUsed = false;
        boolean inMenu = true;

        while (inMenu) {
            try {
                System.out.println("1: " + WarriorSpells.Heavy_Strike.toString().replaceAll("_", " "));
                int strengthPercents = (int) Math.round(gameCharacter.getTotalStrength() / 100.0 * 20);
                if (strengthPercents <= 0) {
                    strengthPercents = 1;
                }
                int damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                int manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 15);
                System.out.println("Enemies: 1  Damage: " + damage + "  Mana: " + manaCost + "  Cd: 3");

                System.out.println("2: " + WarriorSpells.Sweeping_Blow.toString().replaceAll("_", " "));
                damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 10);
                System.out.println("Enemies: All  Damage: " + damage + "  Mana: " + manaCost + "  Cd: 2");

                System.out.println("3: " + WarriorSpells.Fenrir_Rage.toString().replaceAll("_", " "));
                manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 20);
                System.out.println("Gain 100% damage increase to 3 moves  Mana: " + manaCost + "  Cd: 3");

                System.out.println("Enter number of spell or \"E\" to close menu");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 15);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    }

                    printEnemies(enemies);
                    System.out.println();
                    System.out.println("Enter number of enemy to use spell");
                    int enemyIndex = Integer.parseInt(consoleReader.readLine()) - 1;

                    Enemy enemy = enemies.get(enemyIndex);
                    damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                    if (calculateDamage(damage, enemy)) {
                        enemies.set(enemyIndex, enemy);
                        gameCharacter.setSpellCooldown(3);
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);

                        isSpellUsed = true;
                        inMenu = false;
                    } else {
                        System.out.println("Chosen dead enemy");
                        System.out.println();
                    }

                } else if (userAnswer.equals("2")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 10);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    } else {
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);
                    }

                    for (int i = 0; i < enemies.size(); i++) {
                        damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                        Enemy enemy = enemies.get(i);
                        calculateDamage(damage, enemy);
                        enemies.set(i, enemy);
                    }
                    gameCharacter.setSpellCooldown(2);
                    isSpellUsed = true;
                    inMenu = false;

                } else if (userAnswer.equals("3")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 20);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    } else {
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);
                    }

                    Map<String, Integer> bonus = gameCharacter.getBonus();
                    if (bonus == null) {
                        bonus = new HashMap<>();
                    }

                    bonus.put(WarriorSpells.Fenrir_Rage.toString(), 3);
                    gameCharacter.setSpellCooldown(3);
                    isSpellUsed = true;
                    inMenu = false;
                } else {
                    System.out.println("Entered wrong symbol");
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Enter wrong number;");
            }
        }

        return isSpellUsed;
    }

    private boolean getRogueSpells(List<Enemy> enemies) {
        boolean isSpellUsed = false;
        boolean inMenu = true;

        while (inMenu) {
            try {
                System.out.println("1: " + RogueSpells.Rapid_Strike.toString().replaceAll("_", " "));
                int strengthPercents = (int) Math.round(gameCharacter.getTotalStrength() / 100.0 * 20.0);
                if (strengthPercents == 0) {
                    strengthPercents = 1;
                }
                int damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                int manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 15);
                System.out.println("Enemies: 1  Damage: " + damage + "  Mana: " + manaCost + "  Cd: 3");

                System.out.println("2: " + RogueSpells.Blades_Throw.toString().replaceAll("_", " "));
                damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 10);
                System.out.println("Enemies: All  Damage: " + damage + "  Mana: " + manaCost + "  Cd: 2");

                System.out.println("3: " + RogueSpells.Poison.toString().replaceAll("_", " "));
                manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 20);
                System.out.println("Gain 50% damage increase to 5 moves  Mana: " + manaCost + "  Cd: 3");

                System.out.println("Enter number of spell or \"E\" to close menu");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 15);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    }

                    printEnemies(enemies);
                    System.out.println();
                    System.out.println("Enter number of enemy to use spell");
                    int enemyIndex = Integer.parseInt(consoleReader.readLine()) - 1;

                    Enemy enemy = enemies.get(enemyIndex);
                    damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                    if (calculateDamage(damage, enemy)) {
                        enemies.set(enemyIndex, enemy);
                        gameCharacter.setSpellCooldown(3);
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);

                        isSpellUsed = true;
                        inMenu = false;
                    } else {
                        System.out.println("Chosen dead enemy");
                        System.out.println();
                    }

                } else if (userAnswer.equals("2")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 10);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    } else {
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);
                    }

                    for (int i = 0; i < enemies.size(); i++) {
                        damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                        Enemy enemy = enemies.get(i);
                        calculateDamage(damage, enemy);
                        enemies.set(i, enemy);
                    }
                    gameCharacter.setSpellCooldown(2);
                    isSpellUsed = true;
                    inMenu = false;

                } else if (userAnswer.equals("3")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 20);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    } else {
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);
                    }

                    Map<String, Integer> bonus = gameCharacter.getBonus();
                    if (bonus == null) {
                        bonus = new HashMap<>();
                    }

                    bonus.put(RogueSpells.Poison.toString(), 5);
                    gameCharacter.setSpellCooldown(3);
                    isSpellUsed = true;
                    inMenu = false;
                } else {
                    System.out.println("Entered wrong symbol");
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Enter wrong number;");
            }
        }

        return isSpellUsed;
    }

    private boolean getMageSpells(List<Enemy> enemies) {
        boolean isSpellUsed = false;
        boolean inMenu = true;

        while (inMenu) {
            try {
                System.out.println("1: " + MageSpells.Frozen_Stream.toString().replaceAll("_", " "));
                int damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.15  * 2.75);
                int manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 15);
                System.out.println("Enemies: 1  Damage: " + damage + "  Mana: " + manaCost + "  Cd: 3");

                System.out.println("2: " + MageSpells.Fire_Wall.toString().replaceAll("_", " "));
                damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.05 * 1.5);
                manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 10);
                System.out.println("Enemies: All  Damage: " + damage + "  Mana: " + manaCost + "  Cd: 2");

                System.out.println("3: " + MageSpells.Eir_Blessing.toString().replaceAll("_", " "));
                manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 20);
                System.out.println("Heal 20% HP  Mana: " + manaCost + "  Cd: 4");

                System.out.println("Enter number of spell or \"E\" to close menu");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 15);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    }

                    printEnemies(enemies);
                    System.out.println();
                    System.out.println("Enter number of enemy to use spell");
                    int enemyIndex = Integer.parseInt(consoleReader.readLine()) - 1;

                    Enemy enemy = enemies.get(enemyIndex);
                    damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.15  * 2.75);
                    if (calculateDamage(damage, enemy)) {
                        enemies.set(enemyIndex, enemy);
                        gameCharacter.setSpellCooldown(3);
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);

                        isSpellUsed = true;
                        inMenu = false;
                    } else {
                        System.out.println("Chosen dead enemy");
                        System.out.println();
                    }

                } else if (userAnswer.equals("2")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 10);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    } else {
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);
                    }

                    for (int i = 0; i < enemies.size(); i++) {
                        damage = (int) Math.round(gameCharacter.getTotalIntelligence() * 1.05 * 1.5);
                        Enemy enemy = enemies.get(i);
                        calculateDamage(damage, enemy);
                        enemies.set(i, enemy);
                    }
                    gameCharacter.setSpellCooldown(2);
                    isSpellUsed = true;
                    inMenu = false;

                } else if (userAnswer.equals("3")) {
                    manaCost = (int) Math.round(gameCharacter.getMp() / 100.0 * 20);
                    int currentMp = gameCharacter.getCurrentMp();
                    if (currentMp < manaCost) {
                        System.out.println("Not enough mana");
                        continue;
                    } else {
                        currentMp -= manaCost;
                        gameCharacter.setCurrentMp(currentMp);
                    }

                    int hpPercent = (int) Math.round(gameCharacter.getHp() / 100.0 * 20);
                    int currentHp = gameCharacter.getCurrentHp() + hpPercent;

                    if (currentHp > gameCharacter.getHp()) {
                        currentHp = gameCharacter.getHp();
                    }

                    System.out.println(gameCharacter.getName() + " healed by " + hpPercent);
                    gameCharacter.setCurrentHp(currentHp);

                    gameCharacter.setSpellCooldown(4);
                    isSpellUsed = true;
                    inMenu = false;
                } else {
                    System.out.println("Entered wrong symbol");
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Enter wrong number;");
            }
        }

        return isSpellUsed;
    }

    private boolean calculateDamage(int damage, BasicHero target) {
        if (target.getCurrentHp() <= 0) {
            return false;
        }

        if (target.isBlocked()) {
            damage = (int) (damage * 0.8);
        }

        int currentHp = target.getCurrentHp() - damage;
        if (currentHp < 0) {
            currentHp = 0;
        }
        target.setCurrentHp(currentHp);
        System.out.println(damage + " damage to " + target.getName());
        System.out.println();
        return true;
    }

    private void printEnemies(List<Enemy> enemies) {
        int index = 1;
        for (Enemy enemy : enemies) {
            System.out.println(index + ":");
            System.out.println(enemy);
            index++;
        }
    }

    private void printBattle(List<Enemy> enemies) {
        System.out.println("Heroes:");
        System.out.println("Hero: " + gameCharacter.getName() + "  Level: " + gameCharacter.getLevel() +
                "  HP: " + gameCharacter.getCurrentHp() + "\\" + gameCharacter.getHp() +
                "  MP: " + gameCharacter.getCurrentMp() + "\\" + gameCharacter.getMp());
        System.out.println();

        if (gameCharacter.getCompanion() != null) {
            Companion companion = gameCharacter.getCompanion();
            System.out.println("Companion: " + companion.getName() + "  Level: " + companion.getLevel() +
                    "  HP: " + companion.getCurrentHp() + "\\" + companion.getHp() +
                    "  MP: " + companion.getCurrentMp() + "\\" + companion.getMp());
            System.out.println();
        }

        System.out.println("Enemies:");
        printEnemies(enemies);
    }

    private void aiMove(List<Enemy> enemies) {
        if (gameCharacter.getCompanion() != null && !isWon(enemies)) {
            companionMove(enemies);
        }
        if (!isWon(enemies)) {
            enemiesMove(enemies);
        }
    }

    private void companionMove(List<Enemy> enemies) {
        if (gameCharacter.getCompanion() != null && gameCharacter.getCompanion().getCurrentHp() > 0) {
            Random random = new Random();

            Companion companion = gameCharacter.getCompanion();

            boolean isChosen = false;
            int enemyIndex = 0;
            Enemy enemy = new Enemy();

            while (!isChosen) {
                enemyIndex = random.nextInt(enemies.size());
                enemy = enemies.get(enemyIndex);

                if (enemy.getCurrentHp() > 0) {
                    isChosen = true;
                }
            }

            isChosen = false;
            int moveIndex = 0;
            while (!isChosen) {
                moveIndex = random.nextInt(3);

                if (moveIndex == 1) {
                    int mpPercents = (int) Math.round(companion.getMp() / 100.0 * 10);

                    if (companion.getSpellCooldown() > 0) {
                        continue;
                    } else if (companion.getCurrentMp() < mpPercents) {
                        continue;
                    } else {
                        isChosen = true;
                    }
                } else {
                    isChosen = true;
                }
            }

            if (moveIndex == 0) {
                int totalAgilityPercent = enemy.getTotalAgility() / 100;
                if (totalAgilityPercent <= 0) {
                    totalAgilityPercent = 1;
                }
                int chanceMissPercent = (companion.getTotalAgility() - enemy.getTotalAgility()) / totalAgilityPercent + 10;
                if (chanceMissPercent < 10) {
                    chanceMissPercent = 10;
                } else if (chanceMissPercent > 50) {
                    chanceMissPercent = 50;
                }
                int chanceMiss = random.nextInt(101);
                if (chanceMiss <= chanceMissPercent) {
                    System.out.println(companion.getName() + " Miss");
                    System.out.println();
                    return;
                }

                int bonus = 0;
                int totalDamage = 0;
                int damage = (int) (Math.round(companion.getStrength() * 1.25)
                        + Math.round(companion.getStrength() * 1.25 / 100 * (random.nextInt(10)-5)));

                if (companion.getBonus() != null && companion.getBonus().containsKey(WarriorSpells.Fenrir_Rage.toString())) {
                    bonus = damage;
                }
                if (companion.getBonus() != null && companion.getBonus().containsKey(RogueSpells.Poison.toString())) {
                    bonus += (int) Math.round(damage * 0.5);
                }

                totalDamage += bonus + damage;

                int totalStatsPercent = (int) Math.round(companion.getTotalStats()/100.0);
                if (totalStatsPercent <= 0) {
                    totalStatsPercent = 1;
                }
                int chanceCritPercent = companion.getTotalAgility() / totalStatsPercent + 20;
                int chanceCrit = random.nextInt(101);
                if (chanceCritPercent < 20) {
                    chanceCritPercent = 20;
                } else if (chanceCritPercent > 75) {
                    chanceCritPercent = 75;
                }
                if (chanceCrit <= chanceCritPercent) {
                    totalDamage += damage;
                }

                System.out.println(companion.getName() + " attacking " + enemy.getName());
                if (calculateDamage(totalDamage, enemy)) {
                    enemies.set(enemyIndex, enemy);
                }

            } else if (moveIndex == 1) {
                isChosen = false;
                int spellIndex = 0;
                int loopCount = 0;

                while (!isChosen) {
                    spellIndex = random.nextInt(3);
                    if (spellIndex == 0) {
                        int manaCost = (int) Math.round(companion.getMp() / 100.0 * 15);
                        if (companion.getCurrentMp() >= manaCost) {
                            isChosen = true;
                        }
                    } else if (spellIndex == 1) {
                        int manaCost = (int) Math.round(companion.getMp() / 100.0 * 10);
                        if (companion.getCurrentMp() >= manaCost) {
                            isChosen = true;
                        }
                    } else if (spellIndex == 2) {
                        int manaCost = (int) Math.round(companion.getMp() / 100.0 * 20);
                        if (companion.getCurrentMp() >= manaCost) {
                            isChosen = true;
                        }
                    }
                    loopCount++;

                    if (loopCount >= 50) {
                        spellIndex = 0;
                        isChosen = true;
                    }
                }

                int strengthPercents = (int) Math.round(companion.getTotalStrength() / 100.0 * 20);
                if (strengthPercents <= 0) {
                    strengthPercents = 1;
                }

                int manaCost = 0;

                if (companion.getHeroClass().equals(HeroClasses.Warriror.toString())) {
                    if (spellIndex == 0) {
                        System.out.println(companion.getName() + " cast " + WarriorSpells.Heavy_Strike.toString().replaceAll("_"," "));
                        int damage = (int) Math.round(companion.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 15);
                        companion.setSpellCooldown(3);
                        calculateDamage(damage, enemy);
                        enemies.set(enemyIndex, enemy);

                    } else if (spellIndex == 1) {
                        System.out.println(companion.getName() + " cast " + WarriorSpells.Sweeping_Blow.toString().replaceAll("_"," "));
                        int damage = (int) Math.round(companion.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 10);
                        companion.setSpellCooldown(2);

                        for (int i = 0; i < enemies.size(); i++) {
                            enemy = enemies.get(i);
                            calculateDamage(damage, enemy);
                            enemies.set(i, enemy);
                        }

                    } else if (spellIndex == 2) {
                        System.out.println(companion.getName() + " cast " + WarriorSpells.Fenrir_Rage.toString().replaceAll("_"," "));
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 20);
                        Map<String, Integer> bonus = companion.getBonus();
                        if (bonus == null) {
                            bonus = new HashMap<>();
                        }
                        bonus.put(WarriorSpells.Fenrir_Rage.toString(), 3);
                        companion.setBonus(bonus);
                        companion.setSpellCooldown(3);
                    }
                } else if (companion.getHeroClass().equals(HeroClasses.Rogue.toString())) {
                    if (spellIndex == 0) {
                        System.out.println(companion.getName() + " cast " + RogueSpells.Rapid_Strike.toString().replaceAll("_"," "));
                        int damage = (int) Math.round(companion.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 15);
                        companion.setSpellCooldown(3);
                        calculateDamage(damage, enemy);
                        enemies.set(enemyIndex, enemy);

                    } else if (spellIndex == 1) {
                        System.out.println(companion.getName() + " cast " + RogueSpells.Blades_Throw.toString().replaceAll("_"," "));
                        int damage = (int) Math.round(companion.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 10);
                        companion.setSpellCooldown(2);

                        for (int i = 0; i < enemies.size(); i++) {
                            enemy = enemies.get(i);
                            calculateDamage(damage, enemy);
                            enemies.set(i, enemy);
                        }

                    } else if (spellIndex == 2) {
                        System.out.println(companion.getName() + " cast " + RogueSpells.Poison.toString().replaceAll("_"," "));
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 20);
                        Map<String, Integer> bonus = companion.getBonus();
                        if (bonus == null) {
                            bonus = new HashMap<>();
                        }
                        bonus.put(RogueSpells.Poison.toString(), 5);
                        companion.setBonus(bonus);
                        companion.setSpellCooldown(3);
                    }
                } else if (companion.getHeroClass().equals(HeroClasses.Mage.toString())) {
                    if (spellIndex == 0) {
                        System.out.println(companion.getName() + " cast " + MageSpells.Frozen_Stream.toString().replaceAll("_"," "));
                        int damage = (int) Math.round(companion.getTotalIntelligence() * 1.15  * 2.75);
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 15);
                        companion.setSpellCooldown(3);
                        calculateDamage(damage, enemy);
                        enemies.set(enemyIndex, enemy);

                    } else if (spellIndex == 1) {
                        System.out.println(companion.getName() + " cast " + MageSpells.Fire_Wall.toString().replaceAll("_"," "));
                        int damage = (int) Math.round(companion.getTotalIntelligence() * 1.05 * 1.5);
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 10);
                        companion.setSpellCooldown(2);

                        for (int i = 0; i < enemies.size(); i++) {
                            enemy = enemies.get(i);
                            calculateDamage(damage, enemy);
                            enemies.set(i, enemy);
                        }

                    } else if (spellIndex == 2) {
                        System.out.println(companion.getName() + " cast " + MageSpells.Eir_Blessing.toString().replaceAll("_"," "));
                        manaCost = (int) Math.round(companion.getMp() / 100.0 * 20);

                        int hpPercent = (int) Math.round(companion.getHp() / 100.0 * 20);
                        int currentHp = companion.getCurrentHp() + hpPercent;

                        if (currentHp > companion.getHp()) {
                            currentHp = companion.getHp();
                        }

                        System.out.println(companion.getName() + " healed by " + hpPercent);
                        companion.setCurrentHp(currentHp);

                        companion.setSpellCooldown(4);
                    }
                }

                int currentMp =  companion.getCurrentMp() - manaCost;
                if (currentMp < 0) {
                    currentMp = 0;
                }
                companion.setCurrentMp(currentMp);
                gameCharacter.setCompanion(companion);

            } else if (moveIndex == 2) {
                companion.setBlocked(true);
                System.out.println(companion.getName() + " block");
                System.out.println();
                gameCharacter.setCompanion(companion);
            }
        }
    }

    private void enemiesMove(List<Enemy> enemies) {
        Random random = new Random();
        List<BasicHero> targets = new ArrayList<>();
        targets.add(gameCharacter);

        if (gameCharacter.getCompanion() != null) {
            targets.add(gameCharacter.getCompanion());
        }

        for (int i = 0; i < enemies.size(); i++) {
            if (gameCharacter.getCurrentHp() > 0 || gameCharacter.getCompanion() != null && gameCharacter.getCompanion().getCurrentHp() > 0) {
                Enemy enemy = enemies.get(i);

                if (enemy.getCurrentHp() > 0) {
                    boolean isChosen = false;
                    int targetIndex = 0;
                    BasicHero target = new BasicHero();

                    while (!isChosen) {
                        targetIndex = random.nextInt(targets.size());
                        target = targets.get(targetIndex);

                        if (target.getCurrentHp() > 0) {
                            isChosen = true;
                        }
                    }

                    isChosen = false;
                    int moveIndex = 0;
                    while (!isChosen) {
                        moveIndex = random.nextInt(3);

                        if (moveIndex == 1) {
                            int mpPercents = (int) Math.round(enemy.getMp() / 100.0 * 10);

                            if (enemy.getSpellCooldown() > 0) {
                                continue;
                            } else if (enemy.getCurrentMp() < mpPercents) {
                                continue;
                            } else {
                                isChosen = true;
                            }
                        } else {
                            isChosen = true;
                        }
                    }

                    if (moveIndex == 0) {
                        int totalAgilityPercent = target.getTotalAgility() / 100;
                        if (totalAgilityPercent <= 0) {
                            totalAgilityPercent = 1;
                        }
                        int chanceMissPercent = (enemy.getTotalAgility() - target.getTotalAgility()) / totalAgilityPercent + 10;
                        if (chanceMissPercent < 10) {
                            chanceMissPercent = 10;
                        } else if (chanceMissPercent > 50) {
                            chanceMissPercent = 50;
                        }
                        int chanceMiss = random.nextInt(101);
                        if (chanceMiss <= chanceMissPercent) {
                            System.out.println(enemy.getName() + " Miss");
                            System.out.println();
                            continue;
                        }

                        int bonus = 0;
                        int totalDamage = 0;
                        int damage = (int) (Math.round(enemy.getStrength() * 1.25)
                                + Math.round(enemy.getStrength() * 1.25 / 100 * (random.nextInt(10)-5)));

                        if (enemy.getBonus() != null && enemy.getBonus().containsKey(WarriorSpells.Fenrir_Rage.toString())) {
                            bonus = damage;
                        }
                        if (enemy.getBonus() != null && enemy.getBonus().containsKey(RogueSpells.Poison.toString())) {
                            bonus += (int) Math.round(damage * 0.5);
                        }

                        totalDamage += bonus + damage;

                        int totalStatsPercent = (int) Math.round(enemy.getTotalStats()/100.0);
                        if (totalStatsPercent <= 0) {
                            totalStatsPercent = 1;
                        }
                        int chanceCritPercent = enemy.getTotalAgility() / totalStatsPercent + 20;
                        int chanceCrit = random.nextInt(101);
                        if (chanceCritPercent < 20) {
                            chanceCritPercent = 20;
                        } else if (chanceCritPercent > 75) {
                            chanceCritPercent = 75;
                        }
                        if (chanceCrit <= chanceCritPercent) {
                            totalDamage += damage;
                        }

                        System.out.println(enemy.getName() + " attacking " + target.getName());
                        if (calculateDamage(totalDamage, target)) {
                            targets.set(targetIndex, target);
                        }

                    } else if (moveIndex == 1) {
                        isChosen = false;
                        int spellIndex = 0;
                        int loopCount = 0;

                        while (!isChosen) {
                            spellIndex = random.nextInt(3);
                            if (spellIndex == 0) {
                                int manaCost = (int) Math.round(enemy.getMp() / 100.0 * 15);
                                if (enemy.getCurrentMp() >= manaCost) {
                                    isChosen = true;
                                }
                            } else if (spellIndex == 1) {
                                int manaCost = (int) Math.round(enemy.getMp() / 100.0 * 10);
                                if (enemy.getCurrentMp() >= manaCost) {
                                    isChosen = true;
                                }
                            } else if (spellIndex == 2) {
                                int manaCost = (int) Math.round(enemy.getMp() / 100.0 * 20);
                                if (enemy.getCurrentMp() >= manaCost) {
                                    isChosen = true;
                                }
                            }
                            loopCount++;

                            if (loopCount >= 50) {
                                spellIndex = 0;
                                isChosen = true;
                            }
                        }

                        int strengthPercents = (int) Math.round(enemy.getTotalStrength() / 100.0 * 20);
                        if (strengthPercents <= 0) {
                            strengthPercents = 1;
                        }

                        int manaCost = 0;

                        if (enemy.getHeroClass().equals(HeroClasses.Warriror.toString())) {
                            if (spellIndex == 0) {
                                System.out.println(enemy.getName() + " cast " + WarriorSpells.Heavy_Strike.toString().replaceAll("_"," "));
                                int damage = (int) Math.round(enemy.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 15);
                                enemy.setSpellCooldown(3);
                                calculateDamage(damage, target);
                                targets.set(targetIndex, target);

                            } else if (spellIndex == 1) {
                                System.out.println(enemy.getName() + " cast " + WarriorSpells.Sweeping_Blow.toString().replaceAll("_"," "));
                                int damage = (int) Math.round(enemy.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 10);
                                enemy.setSpellCooldown(2);

                                for (int j = 0; j < targets.size(); j++) {
                                    target = targets.get(j);
                                    calculateDamage(damage, target);
                                    targets.set(j, target);
                                }

                            } else if (spellIndex == 2) {
                                System.out.println(enemy.getName() + " cast " + WarriorSpells.Fenrir_Rage.toString().replaceAll("_"," "));
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 20);
                                Map<String, Integer> bonus = enemy.getBonus();
                                if (bonus == null) {
                                    bonus = new HashMap<>();
                                }
                                bonus.put(WarriorSpells.Fenrir_Rage.toString(), 3);
                                enemy.setBonus(bonus);
                                enemy.setSpellCooldown(3);
                            }
                        } else if (enemy.getHeroClass().equals(HeroClasses.Rogue.toString())) {
                            if (spellIndex == 0) {
                                System.out.println(enemy.getName() + " cast " + RogueSpells.Rapid_Strike.toString().replaceAll("_"," "));
                                int damage = (int) Math.round(enemy.getTotalIntelligence() * 1.15 * 1.75 * strengthPercents);
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 15);
                                enemy.setSpellCooldown(3);
                                calculateDamage(damage, target);
                                targets.set(targetIndex, target);

                            } else if (spellIndex == 1) {
                                System.out.println(enemy.getName() + " cast " + RogueSpells.Blades_Throw.toString().replaceAll("_"," "));
                                int damage = (int) Math.round(enemy.getTotalIntelligence() * 1.05 * 1.75 * strengthPercents);
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 10);
                                enemy.setSpellCooldown(2);

                                for (int j = 0; j < targets.size(); j++) {
                                    target = targets.get(j);
                                    calculateDamage(damage, target);
                                    targets.set(j, target);
                                }

                            } else if (spellIndex == 2) {
                                System.out.println(enemy.getName() + " cast " + RogueSpells.Poison.toString().replaceAll("_"," "));
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 20);
                                Map<String, Integer> bonus = enemy.getBonus();
                                if (bonus == null) {
                                    bonus = new HashMap<>();
                                }
                                bonus.put(RogueSpells.Poison.toString(), 5);
                                enemy.setBonus(bonus);
                                enemy.setSpellCooldown(3);
                            }
                        } else if (enemy.getHeroClass().equals(HeroClasses.Mage.toString())) {
                            if (spellIndex == 0) {
                                System.out.println(enemy.getName() + " cast " + MageSpells.Frozen_Stream.toString().replaceAll("_"," "));
                                int damage = (int) Math.round(enemy.getTotalIntelligence() * 1.15  * 2.75);
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 15);
                                enemy.setSpellCooldown(3);
                                calculateDamage(damage, target);
                                targets.set(targetIndex, target);

                            } else if (spellIndex == 1) {
                                System.out.println(enemy.getName() + " cast " + MageSpells.Fire_Wall.toString().replaceAll("_"," "));
                                int damage = (int) Math.round(enemy.getTotalIntelligence() * 1.05 * 1.5);
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 10);
                                enemy.setSpellCooldown(2);

                                for (int j = 0; j < targets.size(); j++) {
                                    target = targets.get(j);
                                    calculateDamage(damage, target);
                                    targets.set(j, target);
                                }

                            } else if (spellIndex == 2) {
                                System.out.println(enemy.getName() + " cast " + MageSpells.Eir_Blessing.toString().replaceAll("_"," "));
                                manaCost = (int) Math.round(enemy.getMp() / 100.0 * 20);

                                int hpPercent = (int) Math.round(enemy.getHp() / 100.0 * 20);
                                int currentHp = enemy.getCurrentHp() + hpPercent;

                                if (currentHp > enemy.getHp()) {
                                    currentHp = enemy.getHp();
                                }

                                System.out.println(enemy.getName() + " healed by " + hpPercent);
                                enemy.setCurrentHp(currentHp);

                                enemy.setSpellCooldown(4);
                            }
                        }

                        int currentMp =  enemy.getCurrentMp() - manaCost;
                        if (currentMp < 0) {
                            currentMp = 0;
                        }
                        enemy.setCurrentMp(currentMp);
                        enemies.set(i, enemy);

                    } else if (moveIndex == 2) {
                        enemy.setBlocked(true);
                        System.out.println(enemy.getName() + " block");
                        System.out.println();
                        enemies.set(i, enemy);
                    }
                }
            }
        }

        for (BasicHero hero : targets) {
            if (hero instanceof GameCharacter) {
                gameCharacter = (GameCharacter) hero;
                break;
            }
        }

        for (BasicHero hero : targets) {
            if (hero instanceof Companion) {
                gameCharacter.setCompanion((Companion) hero);
            }
        }
    }

    private void getLastQuestMenu(Quest quest) {
        try {
            List<Enemy> enemies = new ArrayList<>();
            EnemyGenerator generator = new EnemyGenerator();

            enemies.add(generator.getLastBoss(quest.getLevel()));

            System.out.println(quest.getName());

            getFightMenu(enemies);

            if(isWon(enemies)) {
                System.out.println("You finished the game!");
                System.out.println("Thank you for playing");
                System.out.println("If you will load the character you will start new game+");
                System.out.println("Your character will move to new league");
                gameCharacter.setGameComplete(true);
                GameCharacterDao characterDao = DaoFactory.getGameCharacterDao();
                characterDao.update(gameCharacter);
                Thread.sleep(20000);
                System.exit(0);

            } else {
                System.out.println("Quest failed");
                System.out.println();
            }
        } catch (InterruptedException e) {
            throw new ManagerException("Internal sleep error", e);
        }

    }

}
