package local.consolerpg.managers.game;

import local.consolerpg.database.dao.DaoFactory;
import local.consolerpg.database.dao.GameCharacterDao;
import local.consolerpg.database.dao.UserDao;
import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.GameCharacter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class LeaderboardManager {

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    public LeaderboardManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getLeaderboardMenu() {
        boolean inMenu = true;
        GameCharacterDao characterDao = DaoFactory.getGameCharacterDao();
        UserDao userDao = DaoFactory.getUserDao();

        while (inMenu) {
            try {
                System.out.println("Leaderboard:");
                System.out.println("1: Level  2: Enemies  3: Quests  4: League  5: Close menu");
                System.out.println("Enter number to choose option");
                String userAnswer = consoleReader.readLine();

                if (userAnswer.equals("5")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    List<GameCharacter> characters = characterDao.getFirstByLevel();
                    int index = 1;
                    for (GameCharacter gameCharacter : characters) {
                        String userName = userDao.getNameById(gameCharacter.getUserId());
                        System.out.println(index + ": " + "Username: " + userName + "  " + gameCharacter);
                        System.out.println();
                        index++;
                    }
                } else if (userAnswer.equals("2")) {
                    List<GameCharacter> characters = characterDao.getFirstByKills();
                    int index = 1;
                    for (GameCharacter gameCharacter : characters) {
                        String userName = userDao.getNameById(gameCharacter.getUserId());
                        System.out.println(index + ": " + "Username: " + userName + "  " + gameCharacter);
                        System.out.println();
                        index++;
                    }
                } else if (userAnswer.equals("3")) {
                    List<GameCharacter> characters = characterDao.getFirstByQuests();
                    int index = 1;
                    for (GameCharacter gameCharacter : characters) {
                        String userName = userDao.getNameById(gameCharacter.getUserId());
                        System.out.println(index + ": " + "Username: " + userName + "  " + gameCharacter);
                        System.out.println();
                        index++;
                    }
                } else if (userAnswer.equals("4")) {
                    System.out.println("Enter number of league (1-100)");
                    int gameCount = Integer.parseInt(consoleReader.readLine());
                    System.out.println();

                    if (gameCount < 1) {
                        System.out.println("Entered wrong number");
                        System.out.println();
                        continue;
                    }

                    System.out.println("1: Level  2: Enemies  3: Quests  4: Close league menu");
                    System.out.println("Enter number to choose option");
                    userAnswer = consoleReader.readLine();
                    System.out.println();

                    if (userAnswer.equals("4")) {
                        continue;
                    } else if (userAnswer.equals("1")) {
                        System.out.println("League: " + gameCount);
                        List<GameCharacter> characters = characterDao.getFirstByLevel(gameCount);
                        int index = 1;
                        for (GameCharacter gameCharacter : characters) {
                            String userName = userDao.getNameById(gameCharacter.getUserId());
                            System.out.println(index + ": " + "Username: " + userName + "  " + gameCharacter);
                            System.out.println();
                            index++;
                        }
                    } else if (userAnswer.equals("2")) {
                        System.out.println("League: " + gameCount);
                        List<GameCharacter> characters = characterDao.getFirstByKills(gameCount);
                        int index = 1;
                        for (GameCharacter gameCharacter : characters) {
                            String userName = userDao.getNameById(gameCharacter.getUserId());
                            System.out.println(index + ": " + "User1name: " + userName + "  " + gameCharacter);
                            System.out.println();
                            index++;
                        }
                    } else if (userAnswer.equals("3")) {
                        System.out.println("League: " + gameCount);
                        List<GameCharacter> characters = characterDao.getFirstByQuests(gameCount);
                        int index = 1;
                        for (GameCharacter gameCharacter : characters) {
                            String userName = userDao.getNameById(gameCharacter.getUserId());
                            System.out.println(index + ": " + "Username: " + userName + "  " + gameCharacter);
                            System.out.println();
                            index++;
                        }
                    }
                } else {
                    System.out.println("Entered wrong symbol");
                }


            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }
    }
}
