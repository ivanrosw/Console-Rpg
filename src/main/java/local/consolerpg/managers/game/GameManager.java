package local.consolerpg.managers.game;

import local.consolerpg.database.dao.DaoFactory;
import local.consolerpg.database.dao.GameCharacterDao;
import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;
import local.consolerpg.models.game.builders.EquipmentBuilder;
import local.consolerpg.models.game.concepts.EquipmentParts;
import local.consolerpg.models.game.concepts.HeroClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class GameManager {

    private GameCharacterDao gameCharacterDao = DaoFactory.getGameCharacterDao();

    private GameCharacterManager gameCharacterManager;
    private InventoryManager inventoryManager;
    private CompanionManager companionManager;

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    public GameManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
        gameCharacterManager = new GameCharacterManager(consoleReader, gameCharacter);
        inventoryManager = new InventoryManager(consoleReader, gameCharacter);
        companionManager = new CompanionManager(consoleReader, gameCharacter);
    }

    public void getTavernMenu() {
        boolean inGame = true;
        try {
            while (inGame) {
                System.out.println("1: Character  2: Inventory  3: Trader  4: Quests  5: Companion  6: Leaderboard  7: Exit");
                System.out.println("Enter number to choose option");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if(userAnswer.equals("7")) {
                    gameCharacterDao.update(gameCharacter);
                    System.out.println("Progress saved");
                    System.out.println();
                    inGame = false;

                } else if (userAnswer.equals("1")) {
                    gameCharacterManager.getCharacterMenu();
                } else if (userAnswer.equals("2")) {
                    inventoryManager.getInventoryMenu();
                } else if (userAnswer.equals("3")) {

                } else if (userAnswer.equals("4")) {

                } else if (userAnswer.equals("5")) {
                    companionManager.getCompanionMenu();
                } else if (userAnswer.equals("6")) {

                } else if (userAnswer.equals("statsCheat")) {
                    gameCharacter.setStatPoints(5);
                } else if (userAnswer.equals("itemCheat")) {
                    List<Item> bag = gameCharacter.getBag();
                    bag.add(new EquipmentBuilder()
                            .withName("Test item")
                            .withHeroClass(HeroClasses.Rogue.toString())
                            .withLevel(1)
                            .withStrength(10)
                            .withAgility(11)
                            .withIntelligence(12)
                            .withGold(100)
                            .withBodyPart(EquipmentParts.Body.toString())
                            .build());
                    gameCharacter.setBag(bag);
                } else if (userAnswer.equals("itemCheat2")) {
                    List<Item> bag = gameCharacter.getBag();
                    bag.add(new EquipmentBuilder()
                            .withName("Test item2")
                            .withHeroClass(HeroClasses.Rogue.toString())
                            .withLevel(1)
                            .withStrength(1)
                            .withAgility(2)
                            .withIntelligence(3)
                            .withGold(2)
                            .withBodyPart(EquipmentParts.Body.toString())
                            .build());
                    gameCharacter.setBag(bag);
                } else {
                    System.out.println("Entered wrong symbol");
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }
}
