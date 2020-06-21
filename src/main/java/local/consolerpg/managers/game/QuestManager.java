package local.consolerpg.managers.game;

import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Quest;
import local.consolerpg.models.game.concepts.names.LocationsNouns;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestManager {

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
                    System.out.println("Are you sure want to enter quest? y\\n");
                    userAnswer = consoleReader.readLine();
                    System.out.println();

                    if (userAnswer.equals("n")) {
                        System.out.println("Entering quest canceled");
                        System.out.println();
                    } else if (userAnswer.equals("y")) {
                        getEnterQuestMenu(quests.get(questIndex));
                        return;
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
    }

    private void getEnterQuestMenu(Quest quest) {

    }
}
