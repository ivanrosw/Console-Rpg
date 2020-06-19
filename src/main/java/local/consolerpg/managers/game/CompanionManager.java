package local.consolerpg.managers.game;

import local.consolerpg.database.dao.CompanionDao;
import local.consolerpg.database.dao.DaoFactory;
import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.Companion;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;
import local.consolerpg.models.game.concepts.EquipmentParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CompanionManager {

    private static final String EMPTY_COMPANION = "------------------Empty------------------";
    private static final int BAG_SIZE = 10;
    private static final int LEVEL_DIFFERENCE = 2;

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    private CompanionDao companionDao = DaoFactory.getCompanionDao();

    public CompanionManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getCompanionMenu() {
        try {
            boolean inMenu = true;
            while (inMenu) {
                System.out.println("Companion:");
                if (gameCharacter.getCompanion() != null) {
                    System.out.println(gameCharacter.getCompanion());
                } else {
                    System.out.println(EMPTY_COMPANION);
                }
                System.out.println("1: Find  2: Dismiss  3: Give equipment  4: Take equipment  5: Close menu");
                System.out.println("Enter number to choose option");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("5")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    if (gameCharacter.getCompanion() != null) {
                        System.out.println("You already have a companion. Dismiss your companion to  find new? y\\n");
                        userAnswer = consoleReader.readLine();
                        System.out.println();

                        if (userAnswer.equals("y")) {
                            getDismissCompanionMenu();
                            if (gameCharacter.getCompanion() == null) {
                                getFindCompanionMenu();
                            }
                        } else if (userAnswer.equals("n")) {
                            System.out.println("Find new companion canceled");
                            System.out.println();
                        } else {
                            System.out.println("Entered wrong symbol");
                        }
                    } else {
                        getFindCompanionMenu();
                    }

                } else if (userAnswer.equals("2")) {
                    getDismissCompanionMenu();
                } else if (userAnswer.equals("3")) {
                    getGiveEquipmentMenu();
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void getDismissCompanionMenu() {
        try {
            if (gameCharacter.getCompanion() == null) {
                System.out.println("You dont have a companion");
                return;
            }

            System.out.println("Are you sure want to dismiss the companion? y\\n");
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("n")) {
                System.out.println("Dismissing companion canceled");
                System.out.println();
                return;
            } else if (userAnswer.equals("y")) {
                if (gameCharacter.getCompanion() == null) {
                    System.out.println("You haven't companion");
                    return;
                } else if (gameCharacter.getCompanion().getUserEquipments().isEmpty()) {
                    gameCharacter.setCompanion(null);
                    System.out.println("Companion dismissed");
                    return;
                }

                int needFreeSpace = BAG_SIZE - gameCharacter.getCompanion().getUserEquipments().size();
                if (BAG_SIZE - gameCharacter.getBag().size() < needFreeSpace) {
                    System.out.println("Not enough space in bag to take back your's equipments");
                    System.out.println("You can sell items from character inventory to free space or Dont take equipments back from companion");
                    System.out.println("Dont take equipments back? y\\n");
                    userAnswer = consoleReader.readLine();
                    System.out.println();

                    if(userAnswer.equals("y")) {
                        gameCharacter.setCompanion(null);
                        System.out.println("Companion dismissed");
                        System.out.println();
                    } else if (userAnswer.equals("n")) {
                        System.out.println("You should sell items from character inventory");
                        System.out.println();
                    } else {
                        System.out.println("Entered wrong symbol");
                        System.out.println();
                    }
                } else {
                    List<Item> bag = gameCharacter.getBag();
                    bag.addAll(gameCharacter.getCompanion().getUserEquipments());
                    gameCharacter.setBag(bag);
                    gameCharacter.setCompanion(null);
                    System.out.println("Equipments from companion taken back");
                    System.out.println("Companion dismissed");
                    System.out.println();
                }
            } else {
                System.out.println("Entered wrong symbol");
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void getFindCompanionMenu() {
        try {
            int minLevel = gameCharacter.getLevel() - LEVEL_DIFFERENCE;
            int maxLevel = gameCharacter.getLevel() + LEVEL_DIFFERENCE;
            List<Companion> companions = companionDao.getBetweenLevels(minLevel, maxLevel);
            if (companions.isEmpty()) {
                companions = companionDao.getLessLevel(gameCharacter.getLevel());
            }

            List<Companion> resultCompanions = new ArrayList<>();
            if (companions.size() <= 3) {
                companions.forEach(companion -> {
                    if (companion.getId() != gameCharacter.getId()) {
                        resultCompanions.add(companion);
                    }
                });
            } else {
                Random random = new Random();
                for (int i = 0; i < 3; i++) {
                    Companion companion = companions.get(random.nextInt(companions.size()));
                    if (companion.getId() != gameCharacter.getId()) {
                        resultCompanions.add(companion);
                    }
                }
            }

            System.out.println("Companions:");
            for (int i = 1; i <= 3; i++) {
                System.out.println(i + ":");
                int companionIndex = i - 1;
                if (i > resultCompanions.size()) {
                    System.out.println(EMPTY_COMPANION);
                } else {
                    System.out.println(resultCompanions.get(companionIndex));
                }
            }

            while (gameCharacter.getCompanion() == null) {
                System.out.println("Enter number of companion that you want to hire or \"E\" to cancel hiring");
                String userAnswer = consoleReader.readLine();

                if (userAnswer.equals("E")) {
                    System.out.println("Hiring companion canceled");
                    System.out.println();
                    return;
                } else if (userAnswer.equals("1")) {
                    if (resultCompanions.size() > 0) {
                        gameCharacter.setCompanion(resultCompanions.get(0));
                        System.out.println("Companion hired");
                    } else {
                        System.out.println("Chosen empty slot");
                    }
                } else if (userAnswer.equals("2")) {
                    if (resultCompanions.size() > 1) {
                        gameCharacter.setCompanion(resultCompanions.get(1));
                        System.out.println("Companion hired");
                    } else {
                        System.out.println("Chosen empty slot");
                    }
                } else if (userAnswer.equals(3)) {
                    if (resultCompanions.size() > 2) {
                        gameCharacter.setCompanion(resultCompanions.get(2));
                        System.out.println("Companion hired");
                    } else {
                        System.out.println("Chosen empty slot");
                    }
                } else {
                    System.out.println("Entered wrong symbol");
                }
            }
        }catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void getGiveEquipmentMenu() {
        Companion companion = gameCharacter.getCompanion();
        if (companion == null) {
            System.out.println("You haven't companion");
            System.out.println();
            return;
        }

        List<Item> bag = gameCharacter.getBag();
        if (bag.isEmpty()) {
            System.out.println("Bag is empty");
            System.out.println();
            return;

        } else {
            boolean hasEquipment = false;

            for (Item item : bag) {
                if (item instanceof Equipment) {
                    hasEquipment = true;
                    break;
                }
            }

            if (!hasEquipment) {
                System.out.println("No equipments in bag");
                System.out.println();
                return;
            }
        }

        List<Equipment> bagEquipments = new ArrayList<>();
        for (Item item : bag) {
            if (item instanceof Equipment) {
                bagEquipments.add((Equipment) item);
            }
        }

        boolean inMenu = true;
        while (inMenu) {
            try {
                if (bagEquipments.isEmpty()) {
                    System.out.println("Nothing to give to companion");
                    System.out.println();
                    return;
                }

                printBagEquipments(bagEquipments);
                System.out.println("Enter number of equipment that you want to give to your companion or \"E\" to cancel");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMenu = false;

                } else {
                    int equipmentIndex = Integer.parseInt(userAnswer) - 1;

                    if (equipmentIndex >= bagEquipments.size() || equipmentIndex < 0) {
                        System.out.println("Entered wrong number");
                        System.out.println();
                        continue;
                    }

                    Equipment givenEquipment = bagEquipments.get(equipmentIndex);
                    if (companion.getLevel() < givenEquipment.getLevel()) {
                        System.out.println("Companion cant equip this item. Companion's level too low");
                        System.out.println();
                        continue;
                    }
                    if (!companion.getHeroClass().equals(givenEquipment.getHeroClass())) {
                        System.out.println("Companion cant equip this item. Companion's class is " + companion.getHeroClass());
                        System.out.println();
                        continue;
                    }

                    bagEquipments.remove(givenEquipment);
                    bag.remove(givenEquipment);

                    List<Equipment> companionUsersEquipments = companion.getUserEquipments();
                    List<Equipment> companionEquipments = companion.getEquipments();

                    if (hasBodyPart(companionUsersEquipments, EquipmentParts.valueOf(givenEquipment.getBodyPart()))) {
                        for (Equipment equipment : companionUsersEquipments) {
                            if (equipment.getBodyPart().equals(givenEquipment.getBodyPart())) {
                                companionUsersEquipments.remove(equipment);
                                companionEquipments.remove(equipment);
                                bag.add(equipment);
                                bagEquipments.add(equipment);
                                break;
                            }
                        }
                    }

                    if (hasBodyPart(companionEquipments, EquipmentParts.valueOf(givenEquipment.getBodyPart()))) {
                        for (Equipment equipment : companionEquipments) {
                            if (equipment.getBodyPart().equals(givenEquipment.getBodyPart())) {
                                companionEquipments.remove(equipment);
                                break;
                            }
                        }
                    }

                    companionUsersEquipments.add(givenEquipment);
                    companionEquipments.add(givenEquipment);
                    companion.setUserEquipments(companionUsersEquipments);
                    companion.setEquipments(companionEquipments);

                    System.out.println("Equipment given to companion");
                }

                gameCharacter.setCompanion(companion);
                gameCharacter.setBag(bag);

            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }
    }

    private void getTakeEquipmentMenu() {
        Companion companion = gameCharacter.getCompanion();
        if (companion == null) {
            System.out.println("You haven't companion");
            System.out.println();
            return;
        }

        List<Equipment> companionUserEquipments = companion.getUserEquipments();
        boolean inMenu = true;
        while (inMenu) {
            try {
                if (companionUserEquipments.isEmpty()) {
                    System.out.println("Nothing to take back");
                    System.out.println();
                    return;
                }
                if (gameCharacter.getBag().size() >= BAG_SIZE) {
                    System.out.println("Bag is full");
                    System.out.println();
                    return;
                }

                printBagEquipments(companionUserEquipments);
                System.out.println();
                System.out.println("Enter number of equipment that you want to take back or \"E\" to cancel");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else {
                    int equipmentIndex = Integer.parseInt(userAnswer) - 1;

                    if (equipmentIndex >= companionUserEquipments.size() || equipmentIndex < 0) {
                        System.out.println("Entered wrong number");
                        System.out.println();
                        continue;
                    }

                    Equipment takenEquipment = companionUserEquipments.get(equipmentIndex);
                    companionUserEquipments.remove(takenEquipment);

                    List<Item> bag = gameCharacter.getBag();
                    bag.add(takenEquipment);

                    List<Equipment> companionEquipments = companion.getEquipments();
                    List<Equipment> companionOriginalEquipments = companion.getOriginalEquipments();
                    companionEquipments.remove(takenEquipment);

                    for (Equipment equipment : companionOriginalEquipments) {
                        if (equipment.getBodyPart().equals(takenEquipment.getBodyPart())) {
                            companionEquipments.add(equipment);
                            break;
                        }
                    }

                    companion.setEquipments(companionEquipments);
                    companion.setUserEquipments(companionUserEquipments);
                    gameCharacter.setCompanion(companion);
                    gameCharacter.setBag(bag);
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }
    }

    private void printBagEquipments(List<Equipment> bagEquipments) {
        int index = 1;
        for (Equipment equipment : bagEquipments) {
            System.out.println(index + ":");
            System.out.println(equipment);
            index++;
        }
    }

    private boolean hasBodyPart(List<Equipment> equipments, EquipmentParts bodyPart) {
        boolean hasPart = false;
        for (Equipment equipment : equipments) {
            if (equipment.getBodyPart().equals(bodyPart.toString())) {
                hasPart = true;
                break;
            }
        }
        return hasPart;
    }


}
