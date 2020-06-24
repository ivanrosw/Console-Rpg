package local.consolerpg.managers.game;

import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;
import local.consolerpg.models.game.Usable;
import local.consolerpg.models.game.concepts.EquipmentParts;
import local.consolerpg.models.game.concepts.HeroClasses;
import local.consolerpg.models.game.concepts.Usables;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class GameCharacterManager {

    private static final String EMPTY_SLOT = "----------------Empty----------------";
    private static final int BAG_SIZE = 10;

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    public GameCharacterManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getCharacterMenu() {
        try {
            boolean inMenu = true;
            while (inMenu) {
                System.out.println("Character Menu");
                System.out.println("Name: " + gameCharacter.getName());
                System.out.println("Class: " + gameCharacter.getHeroClass());
                System.out.println("Level: " + gameCharacter.getLevel() + " Exp: " + gameCharacter.getCurrentExp() + "\\" + gameCharacter.getExp());
                System.out.println("Hp: " + gameCharacter.getHp() + "  Mp: " + gameCharacter.getMp());
                System.out.println("Strength: " + gameCharacter.getStrength() + "  Agility: " + gameCharacter.getAgility());
                System.out.println("Intelligence: " + gameCharacter.getIntelligence());
                System.out.println("Stat points: " + gameCharacter.getStatPoints());
                System.out.println();

                printAllEquipments();

                System.out.println("1: Spend stat points  2: Reset stat points");
                System.out.println("3: Change class  4: Take off equipment");
                System.out.println("5: Close menu");
                System.out.println("Enter number to select option");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("5")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    getSpendPointsMenu();
                } else if (userAnswer.equals("2")) {
                    getResetPointsMenu();
                } else if (userAnswer.equals("3")) {
                    getChangeClassMenu();
                } else if (userAnswer.equals("4")) {
                    getTakeOffEquipmentMenu();
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void getSpendPointsMenu() {
        int statPoints = gameCharacter.getStatPoints();
        if (statPoints < 1) {
            System.out.println("Nothing to spend");
            System.out.println();
            return;
        }

        while (statPoints > 0) {
            try {
                System.out.println("Stats: ");
                System.out.println("1: Strength: " + gameCharacter.getStrength());
                System.out.println("2: Agility: " + gameCharacter.getAgility());
                System.out.println("3: Intelligence: " + gameCharacter.getIntelligence());
                System.out.println("Stat points: " + gameCharacter.getStatPoints());

                boolean isChosen = false;
                String userAnswer;
                int statPointsCount = 0;
                while (!isChosen) {
                    System.out.println("Enter how much points you want to spend or \"E\" to cancel spending");
                    userAnswer = consoleReader.readLine();
                    System.out.println();
                    if (userAnswer.equals("E")) {
                        return;
                    }
                    statPointsCount = Integer.parseInt(userAnswer);
                    if (statPointsCount > 0 && statPointsCount <= statPoints) {
                        isChosen = true;
                    } else {
                        System.out.println("Entered wrong number of count");
                    }
                    System.out.println();
                }

                isChosen = false;
                while (!isChosen) {
                    System.out.println("Enter number where you want to spend points or \"E\" to cancel spending");
                    userAnswer = consoleReader.readLine();
                    System.out.println();
                    if (userAnswer.equals("E")) {
                        return;
                    } else if (userAnswer.equals("1")) {
                        int points = gameCharacter.getStrength() + statPointsCount;
                        statPoints -= statPointsCount;
                        gameCharacter.setStatPoints(statPoints);
                        gameCharacter.setStrength(points);
                        System.out.println("Spent " + statPointsCount + " points to Strength");
                        System.out.println();
                        isChosen = true;
                    } else if (userAnswer.equals("2")) {
                        int points = gameCharacter.getAgility() + statPointsCount;
                        statPoints -= statPointsCount;
                        gameCharacter.setStatPoints(statPoints);
                        gameCharacter.setAgility(points);
                        System.out.println("Spent " + statPointsCount + " points to Agility");
                        System.out.println();
                        isChosen = true;
                    } else if (userAnswer.equals("3")) {
                        int points = gameCharacter.getIntelligence() + statPointsCount;
                        statPoints -= statPointsCount;
                        gameCharacter.setStatPoints(statPoints);
                        gameCharacter.setIntelligence(points);
                        System.out.println("Spent " + statPointsCount + " points to Intelligence");
                        System.out.println();
                        isChosen = true;
                    } else {
                        System.out.println("Entered wrong symbol");
                        System.out.println();
                    }
                }

            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong number");
            }
        }
    }

    private void getResetPointsMenu() {
        try {
            System.out.println("Are you sure want to reset stat points? y\\n");
            String userAnswer = consoleReader.readLine();

            if (userAnswer.equals("n")) {
                System.out.println("Resetting canceled");
                System.out.println();
                return;
            } else if (userAnswer.equals("y")) {
                resetStatPoints();
            } else {
                System.out.println("Entered wrong symbol");
                System.out.println();
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void getChangeClassMenu() {
        try {
            System.out.println("You need " + Usables.Book_of_fate.toString().replaceAll("_", " ") +
                    " to change your class");
            System.out.println("Do you want to change class? y\\n");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("n")) {
                System.out.println("Changing hero class canceled");
                System.out.println();
                return;

            } else if (userAnswer.equals("y")) {
                int bookIndex = 0;
                Usable book = null;
                List<Item> bag = gameCharacter.getBag();

                for (int i = 0; i < gameCharacter.getBag().size(); i++) {
                    if (bag.get(i) instanceof Usable) {
                        Usable usable = (Usable) bag.get(i);
                        if (usable.getName().equals(Usables.Book_of_fate.toString().replace("_", " "))) {
                            book = usable;
                            bookIndex = i;
                            break;
                        }
                    }
                }

                if (book == null) {
                    System.out.println("You dont have " + Usables.Book_of_fate.toString().replaceAll("_", " "));
                    System.out.println();
                    return;
                }

                System.out.println("Choose class 1: " + HeroClasses.Warriror.toString() + " 2: " + HeroClasses.Rogue.toString() +
                        " 3: " + HeroClasses.Mage.toString());
                boolean isChosen = false;
                while (!isChosen) {
                    System.out.println("Enter number or \"E\" to cancel class changing");
                    userAnswer = consoleReader.readLine();
                    System.out.println();

                    if (userAnswer.equals("E")) {
                        System.out.println("Changing hero class canceled");
                        System.out.println();
                        return;

                    } else if (userAnswer.equals("1")) {
                        if (gameCharacter.getHeroClass().equals(HeroClasses.Warriror.toString())) {
                            System.out.println("Your character already " + HeroClasses.Warriror.toString());
                            System.out.println();
                        } else {
                            book.setCount(book.getCount() - 1);
                            if (book.getCount() == 0) {
                                bag.remove(bookIndex);
                            } else {
                                bag.set(bookIndex, book);
                            }
                            gameCharacter.setBag(bag);

                            gameCharacter.setHeroClass(HeroClasses.Warriror.toString());
                            System.out.println("Hero class changed to " + HeroClasses.Warriror.toString());
                            System.out.println();
                            isChosen = true;
                        }
                    } else if (userAnswer.equals("2")) {
                        if (gameCharacter.getHeroClass().equals(HeroClasses.Rogue.toString())) {
                            System.out.println("Your character already " + HeroClasses.Rogue.toString());
                            System.out.println();
                        } else {
                            book.setCount(book.getCount() - 1);
                            if (book.getCount() == 0) {
                                bag.remove(bookIndex);
                            } else {
                                bag.set(bookIndex, book);
                            }
                            gameCharacter.setBag(bag);

                            gameCharacter.setHeroClass(HeroClasses.Rogue.toString());
                            System.out.println("Hero class changed to " + HeroClasses.Rogue.toString());
                            System.out.println();
                            isChosen = true;
                        }
                    } else if (userAnswer.equals("3")) {
                        if (gameCharacter.getHeroClass().equals(HeroClasses.Mage.toString())) {
                            System.out.println("Your character already " + HeroClasses.Mage.toString());
                            System.out.println();
                        } else {
                            book.setCount(book.getCount() - 1);
                            if (book.getCount() == 0) {
                                bag.remove(bookIndex);
                            } else {
                                bag.set(bookIndex, book);
                            }
                            gameCharacter.setBag(bag);

                            gameCharacter.setHeroClass(HeroClasses.Mage.toString());
                            System.out.println("Hero class changed to " + HeroClasses.Mage.toString());
                            System.out.println();
                            isChosen = true;
                        }
                    } else {
                        System.out.println("Entered wrong symbol");
                        System.out.println();
                    }
                }
            } else {
                System.out.println("Entered wrong symbol");
                System.out.println();
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void getTakeOffEquipmentMenu() {
        try {
            List<Equipment> equipments = gameCharacter.getEquipments();
            List<Item> bag = gameCharacter.getBag();

            if (equipments.isEmpty()) {
                System.out.println("Equipments are empty");
                System.out.println();
            }

            while (!equipments.isEmpty()) {
                if (bag.size() >= BAG_SIZE) {
                    System.out.println("Not enough space in bag to take off equipment");
                    System.out.println();
                    return;
                }

                printAllEquipments();

                System.out.println("Enter number what equipment do you want to take off or \"E\" to cancel");
                System.out.println("1: " + EquipmentParts.Head.toString() + " 2: " + EquipmentParts.Body.toString() +
                        " 3: " + EquipmentParts.Hands.toString() + " 4: " + EquipmentParts.Legs.toString() + "  5: " +
                        EquipmentParts.Feet.toString() + " 6: " + EquipmentParts.Weapon.toString());
                String userAnswer = consoleReader.readLine();
                System.out.println();

                InventoryManager inventoryManager = new InventoryManager(gameCharacter);
                if (userAnswer.equals("E")) {
                    System.out.println("Taking off equipment canceled");
                    System.out.println();
                    return;

                } else if (userAnswer.equals("1")) {
                    Equipment head = inventoryManager.getUsingEquipment(EquipmentParts.Head);
                    if (head == null) {
                        System.out.println(EquipmentParts.Head.toString() + " slot is empty");
                        System.out.println();
                    } else {
                        equipments.remove(head);
                        bag.add(head);
                        gameCharacter.setEquipments(equipments);
                        gameCharacter.setBag(bag);
                        System.out.println(EquipmentParts.Head.toString() + " taken off");
                        System.out.println();
                    }
                } else if (userAnswer.equals("2")) {
                    Equipment body = inventoryManager.getUsingEquipment(EquipmentParts.Body);
                    if (body == null) {
                        System.out.println(EquipmentParts.Body.toString() + " slot is empty");
                        System.out.println();
                    } else {
                        equipments.remove(body);
                        bag.add(body);
                        gameCharacter.setEquipments(equipments);
                        gameCharacter.setBag(bag);
                        System.out.println(EquipmentParts.Body.toString() + " taken off");
                        System.out.println();
                    }
                } else if (userAnswer.equals("3")) {
                    Equipment hands = inventoryManager.getUsingEquipment(EquipmentParts.Hands);
                    if (hands == null) {
                        System.out.println(EquipmentParts.Hands.toString() + " slot is empty");
                        System.out.println();
                    } else {
                        equipments.remove(hands);
                        bag.add(hands);
                        gameCharacter.setEquipments(equipments);
                        gameCharacter.setBag(bag);
                        System.out.println(EquipmentParts.Hands.toString() + " taken off");
                        System.out.println();
                    }
                } else if (userAnswer.equals("4")) {
                    Equipment legs = inventoryManager.getUsingEquipment(EquipmentParts.Legs);
                    if (legs == null) {
                        System.out.println(EquipmentParts.Legs.toString() + " slot is empty");
                        System.out.println();
                    } else {
                        equipments.remove(legs);
                        bag.add(legs);
                        gameCharacter.setEquipments(equipments);
                        gameCharacter.setBag(bag);
                        System.out.println(EquipmentParts.Legs.toString() + " taken off");
                        System.out.println();
                    }
                } else if (userAnswer.equals("5")) {
                    Equipment feet = inventoryManager.getUsingEquipment(EquipmentParts.Feet);
                    if (feet == null) {
                        System.out.println(EquipmentParts.Feet.toString() + " slot is empty");
                        System.out.println();
                    } else {
                        equipments.remove(feet);
                        bag.add(feet);
                        gameCharacter.setEquipments(equipments);
                        gameCharacter.setBag(bag);
                        System.out.println(EquipmentParts.Feet.toString() + " taken off");
                        System.out.println();
                    }
                } else if (userAnswer.equals("6")) {
                    Equipment weapon = inventoryManager.getUsingEquipment(EquipmentParts.Weapon);
                    if (weapon == null) {
                        System.out.println(EquipmentParts.Weapon.toString() + " slot is empty");
                        System.out.println();
                    } else {
                        equipments.remove(weapon);
                        bag.add(weapon);
                        gameCharacter.setEquipments(equipments);
                        gameCharacter.setBag(bag);
                        System.out.println(EquipmentParts.Weapon.toString() + " taken off");
                        System.out.println();
                    }
                } else {
                    System.out.println("Entered wrong symbol");
                    System.out.println();
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private void resetStatPoints() {
        int totalPoints = gameCharacter.getStatPoints();
        if (gameCharacter.getHeroClass().equals(HeroClasses.Warriror.toString())) {
            int addingPoints = gameCharacter.getStrength() - 2;
            gameCharacter.setStrength(2);
            addingPoints += gameCharacter.getAgility() - 1;
            gameCharacter.setAgility(1);
            addingPoints += gameCharacter.getIntelligence() - 1;
            gameCharacter.setIntelligence(1);
            totalPoints += addingPoints;
        } else if (gameCharacter.getHeroClass().equals(HeroClasses.Rogue.toString())) {
            int addingPoints = gameCharacter.getStrength() - 1;
            gameCharacter.setStrength(1);
            addingPoints += gameCharacter.getAgility() - 2;
            gameCharacter.setAgility(2);
            addingPoints += gameCharacter.getIntelligence() - 1;
            gameCharacter.setIntelligence(1);
            totalPoints += addingPoints;
        } else if (gameCharacter.getHeroClass().equals(HeroClasses.Mage.toString())) {
            int addingPoints = gameCharacter.getStrength() - 1;
            gameCharacter.setStrength(1);
            addingPoints += gameCharacter.getAgility() - 1;
            gameCharacter.setAgility(1);
            addingPoints += gameCharacter.getIntelligence() - 2;
            gameCharacter.setIntelligence(2);
            totalPoints += addingPoints;
        } else {
            int addingPoints = gameCharacter.getStrength() - 1;
            gameCharacter.setStrength(1);
            addingPoints += gameCharacter.getAgility() - 1;
            gameCharacter.setAgility(1);
            addingPoints += gameCharacter.getIntelligence() - 1;
            gameCharacter.setIntelligence(1);
            totalPoints += addingPoints;
        }

        gameCharacter.setStatPoints(totalPoints);
    }

    private void printEquipment(Equipment equipment) {
        if (equipment == null) {
            System.out.println(EMPTY_SLOT);
        } else {
            System.out.println("Name: " + equipment.getName() + "  Level: " + equipment.getLevel());
            System.out.println("Strength: " + equipment.getStrength() + "  Agility: " + equipment.getAgility());
            System.out.println("Intelligence: " + equipment.getIntelligence() + "  Gold: " + equipment.getGold());
        }
    }

    private void printAllEquipments() {
        InventoryManager inventoryManager = new InventoryManager(gameCharacter);

        System.out.println("Head:");
        Equipment equipment = inventoryManager.getUsingEquipment(EquipmentParts.Head);
        printEquipment(equipment);

        System.out.println("Body:");
        equipment = inventoryManager.getUsingEquipment(EquipmentParts.Body);
        printEquipment(equipment);

        System.out.println("Hands:");
        equipment = inventoryManager.getUsingEquipment(EquipmentParts.Hands);
        printEquipment(equipment);

        System.out.println("Legs:");
        equipment = inventoryManager.getUsingEquipment(EquipmentParts.Legs);
        printEquipment(equipment);

        System.out.println("Feet:");
        equipment = inventoryManager.getUsingEquipment(EquipmentParts.Feet);
        printEquipment(equipment);

        System.out.println("Weapon:");
        equipment = inventoryManager.getUsingEquipment(EquipmentParts.Weapon);
        printEquipment(equipment);
        System.out.println();
    }
}
