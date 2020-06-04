package local.consolerpg.managers;

import local.consolerpg.database.dao.DaoFactory;
import local.consolerpg.database.dao.GameCharacterDao;
import local.consolerpg.models.game.concepts.HeroClasses;
import local.consolerpg.managers.exceptions.AuthorizationException;
import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.managers.game.GameManager;
import local.consolerpg.models.User;
import local.consolerpg.models.game.GameCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ApplicationManager {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationManager.class);
    private static final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

    private static final int MAX_SAVES_COUNT = 3;

    private GameCharacterDao gameCharacterDao = DaoFactory.getGameCharacterDao();

    private User user;
    private GameCharacter gameCharacter;

    public void getMainMenu() {
        logger.debug("Getting main menu");
        getLoginMenu();

        try {
            boolean working = true;
            while (working) {
                System.out.println("1: New game");
                System.out.println("2: Load game");
                System.out.println("3: Change user");
                System.out.println("4: Exit");
                System.out.println();
                System.out.println("Enter a number to choose option");

                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("1")) {
                    newGame();
                    if (gameCharacter != null) {
                        startGame();
                    }

                } else if (userAnswer.equals("2")) {
                    getLoadMenu();
                    if (gameCharacter != null) {
                        startGame();
                    }

                } else if (userAnswer.equals("3")) {
                    user = null;
                    getLoginMenu();

                } else if (userAnswer.equals("4")) {
                    logger.debug("Exiting game");
                    System.out.println("See you next time " + user.getName());
                    working = false;

                    try {
                        consoleReader.close();
                    } catch (IOException e) {
                        logger.error("Internal consoleReader problem", e);
                    }

                } else {
                    System.out.println("Entered wrong symbol");
                    System.out.println();
                }
            }
        } catch (IOException e) {
            logger.error("Internal consoleReader problem", e);
            throw new ManagerException("Internal consoleReader problem", e);
        }
    }

    private void getLoginMenu() {
        logger.debug("Starting authorization");
        AuthorizationManager authorizationManager = new AuthorizationManager();

        while (user == null) {
            try {
                System.out.println("1: Login");
                System.out.println("2: Register");
                System.out.println("Enter a number to choose option or \"E\" to exit");

                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    System.exit(0);

                } else if (userAnswer.equals("1")) {
                    user = authorizationManager.login(consoleReader);

                } else if (userAnswer.equals("2")) {
                    user = authorizationManager.register(consoleReader);

                } else {
                    System.out.println("Entered wrong symbol");
                }

            } catch (AuthorizationException e) {
                System.out.println("Some problem with logging in. Try again later \nError:");
                System.out.println(e.getMessage());
                logger.warn("Authorization userName: {} failed", user.getName());

            } catch (IOException e) {
                logger.error("Internal consoleReader problem", e);
                throw new ManagerException("Internal consoleReader problem", e);
            }
        }

        logger.debug("Authorized as {}", user.getName());
        System.out.println("Welcome " + user.getName());
    }

    private void getLoadMenu() {
        logger.debug("Getting load menu");
        gameCharacter = null;
        List<GameCharacter> characters = gameCharacterDao.getAllByUserId(user.getId());

        printSaves(characters);

        boolean isChosen = false;
        try {
            while (!isChosen) {
                System.out.println("Enter number of save to load hero or \"E\" to cancel load");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    logger.debug("Cancel load game character");
                    return;
                }

                try {
                    int saveNumber = Integer.parseInt(userAnswer);
                    if (saveNumber > characters.size() && saveNumber <= MAX_SAVES_COUNT) {
                        System.out.println("Entered empty slot");

                    } else if (saveNumber <= characters.size() && saveNumber >= 1) {
                        gameCharacter = characters.get(saveNumber - 1);
                        logger.debug("Loaded {}", gameCharacter);
                        System.out.println("Loaded " + gameCharacter);
                        System.out.println();
                        isChosen = true;

                    } else {
                        System.out.println("Entered wrong number");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entered wrong symbols");
                }
            }
        } catch (IOException e) {
            logger.error("Internal consoleReader problem", e);
            throw new ManagerException("Internal consoleReader problem", e);
        }
    }

    private void printSaves (List<GameCharacter> characters) {
        System.out.println("Saves:");
        for (int i = 1; i <= MAX_SAVES_COUNT; i++) {
            int characterIndex = i - 1;
            System.out.println(i + ":");

            if (characters.size() >= i) {
                System.out.println(characters.get(characterIndex).toString());
            } else {
                System.out.println("---------------------Empty---------------------");
            }
        }
        System.out.println();
    }

    private void newGame() {
        logger.debug("Starting new game");
        gameCharacter = null;
        try {
            List<GameCharacter> characters = gameCharacterDao.getAllByUserId(user.getId());

            logger.debug("Check saves count");
            long newGameCharacterId = 0;
            if (characters.size() >= MAX_SAVES_COUNT) {
                System.out.println("You have max saves count");
                printSaves(characters);

                boolean isChosen = false;
                while (!isChosen) {
                    try {
                        logger.debug("Choosing save to new game");
                        System.out.println("Enter number of save what you want to use to new game or \"E\" to cancel");
                        String userAnswer = consoleReader.readLine();
                        System.out.println();

                        if (userAnswer.equals("E")) {
                            logger.debug("Canceled new game");
                            return;
                        }

                        int saveNumber = Integer.parseInt(userAnswer);
                        if (saveNumber <= characters.size() && saveNumber >= 1) {
                            newGameCharacterId = characters.get(saveNumber - 1).getId();
                            logger.debug("Chosen save with id: {}", newGameCharacterId);
                            System.out.println("Chosen " + saveNumber);
                            System.out.println();
                            isChosen = true;

                        } else {
                            System.out.println("Entered wrong number");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Entered wrong symbols");
                    }
                }
            }

            System.out.println("Enter heroes name");
            String gameCharacterName = consoleReader.readLine();
            System.out.println();

            System.out.println("Choose begin class for your hero:");
            System.out.println("1: " + HeroClasses.Warriror);
            System.out.println("2: " + HeroClasses.Rogue);
            System.out.println("3: " + HeroClasses.Mage);

            String gameCharacterClass = null;
            while (gameCharacterClass == null) {
                System.out.println("Enter number of hero class to choose class");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                try {
                    int classNumber = Integer.parseInt(userAnswer);
                    if (classNumber == 1) {
                        gameCharacterClass = HeroClasses.Warriror.toString();

                    } else if (classNumber == 2) {
                        gameCharacterClass = HeroClasses.Rogue.toString();

                    } else if(classNumber == 3) {
                        gameCharacterClass = HeroClasses.Mage.toString();

                    } else {
                        System.out.println("Entered wrong number");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entered wrong symbols");
                }
            }

            GameCharacter newGameCharacter = new GameCharacter();
            newGameCharacter.setUserId(user.getId());
            newGameCharacter.setName(gameCharacterName);
            newGameCharacter.setHeroClass(gameCharacterClass);
            generateNewHero(newGameCharacter);
            if (newGameCharacterId > 0) {
                newGameCharacter.setId(newGameCharacterId);
            }
            gameCharacter = newGameCharacter;
            logger.debug("Created new game character {}", gameCharacter);

        } catch (IOException e) {
            logger.error("Internal consoleReader problem", e);
            throw new ManagerException("Internal consoleReader problem", e);
        }
    }

    private void generateNewHero(GameCharacter gameCharacter) {
        logger.debug("Generating stats to new game character: {}", gameCharacter);
        gameCharacter.setLevel(1);
        gameCharacter.setGameCount(1);
        gameCharacter.setBag(new ArrayList<>());
        gameCharacter.setEquipments(new ArrayList<>());
        gameCharacter.setGold(15);

        if(gameCharacter.getHeroClass().equals(HeroClasses.Warriror.toString())) {
            gameCharacter.setStrength(2);
            gameCharacter.setAgility(1);
            gameCharacter.setIntelligence(1);
        } else if (gameCharacter.getHeroClass().equals(HeroClasses.Rogue.toString())) {
            gameCharacter.setStrength(1);
            gameCharacter.setAgility(2);
            gameCharacter.setIntelligence(1);
        } else if (gameCharacter.getHeroClass().equals(HeroClasses.Mage.toString())) {
            gameCharacter.setStrength(1);
            gameCharacter.setAgility(1);
            gameCharacter.setIntelligence(2);
        }
        logger.debug("Generated stats to new game character {}", gameCharacter);
    }

    private void startGame() {
        logger.debug("Starting game");
        if (gameCharacter != null) {
            System.out.println("Game using autosave system. Use game buttons to exit. Dont close console.");
            GameManager gameManager = new GameManager(consoleReader, gameCharacter);
            gameManager.getTavernMenu();
        } else {
            throw new ManagerException("Game character not loaded");
        }
    }
}
