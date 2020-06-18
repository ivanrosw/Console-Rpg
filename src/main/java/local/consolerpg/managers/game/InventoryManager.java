package local.consolerpg.managers.game;

import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;
import local.consolerpg.models.game.concepts.BodyParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class InventoryManager {

    private static final String EMPTY_SLOT = "--------------------Empty--------------------";
    private static final int BAG_SIZE = 10;

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    public InventoryManager(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
    }

    public InventoryManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getInventoryMenu() {
        try {
            boolean inMenu = true;
            while (inMenu) {
                try {
                    printInventory();
                    System.out.println("1: Equip  2: Drop  3: Compare  4: Close menu");
                    System.out.println("Enter number to choose option");
                    String userAnswer = consoleReader.readLine();
                    System.out.println();

                    if (userAnswer.equals("4")) {
                        System.out.println("Inventory closed");
                        inMenu = false;

                    } else if (userAnswer.equals("1")) {
                        System.out.println("Enter number of item what you want to equip");
                        userAnswer = consoleReader.readLine();
                        System.out.println();

                        List<Item> bag = gameCharacter.getBag();
                        int itemIndex = Integer.parseInt(userAnswer) - 1;
                        if (isValidToEquipIndex(itemIndex)) {
                            equip((Equipment) bag.get(itemIndex));
                        }

                    } else if (userAnswer.equals("2")) {
                        System.out.println("Enter number of item what you want to drop");
                        userAnswer = consoleReader.readLine();
                        System.out.println();

                        List<Item> bag = gameCharacter.getBag();
                        int itemIndex = Integer.parseInt(userAnswer) - 1;
                        if (isValidToDropIndex(itemIndex)) {
                            bag.remove(itemIndex);
                            gameCharacter.setBag(bag);
                        }

                    } else if (userAnswer.equals("3")) {
                        getCompareMenu();
                    } else {
                        System.out.println("Entered wrong symbol");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entered wrong symbol");
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    public Equipment getUsingEquipment(BodyParts bodyPart) {
        List<Equipment> equipments = gameCharacter.getEquipments();
        for (int i = 0; i < equipments.size(); i++) {
            Equipment equipment = equipments.get(i);
            if (equipment.getBodyPart().equals(bodyPart.toString())) {
                return equipment;
            }
        }
        return null;
    }

    private void getCompareMenu() {
        try {
            System.out.println("Enter number of item what you want to compare with equipped item or \"E\" to cancel");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return;
            }

            List<Item> bag = gameCharacter.getBag();
            int itemIndex = Integer.parseInt(userAnswer) - 1;
            if (isValidToEquipIndex(itemIndex)) {
                Equipment comparing = (Equipment) bag.get(itemIndex);
                Equipment equipped = getUsingEquipment(BodyParts.valueOf(comparing.getBodyPart()));

                int compareStrength = comparing.getStrength() - equipped.getStrength();
                int compareAgility = comparing.getAgility() - equipped.getAgility();
                int compareIntelligence = comparing.getIntelligence() - equipped.getIntelligence();

                StringBuilder compareStats = new StringBuilder();
                compareStats.append("Strength: ");
                if (compareStrength < 0) {
                    compareStats.append("-");
                } else if (compareStrength > 0) {
                    compareStats.append("+");
                }
                compareStats.append(compareStrength);
                compareStats.append("  Agility: ");
                if (compareAgility < 0) {
                    compareStats.append("-");
                } else if (compareAgility > 0) {
                    compareStats.append("+");
                }
                compareStats.append(compareAgility);
                compareStats.append("  Intelligence: ");
                if (compareIntelligence < 0) {
                    compareStats.append("-");
                } else if (compareIntelligence > 0) {
                    compareStats.append("+");
                }
                compareStats.append(compareIntelligence);

                System.out.println("Equipped:");
                System.out.println(equipped);
                System.out.println("Compare:");
                System.out.println(comparing);
                System.out.println(compareStats);
            }
        } catch (IOException e) {
            throw new ManagerException("Internal consoleReader error", e);
        }
    }

    private boolean isValidToDropIndex(int itemIndex) {
        List<Item> bag = gameCharacter.getBag();
        if (itemIndex < 0 || itemIndex >= BAG_SIZE) {
            System.out.println("Entered wrong number");
            System.out.println();
            return false;
        } else if (itemIndex >= bag.size()) {
            System.out.println("Chosen empty slot");
            System.out.println();
            return false;
        }
        return true;
    }

    private boolean isValidToEquipIndex(int itemIndex) {
        List<Item> bag = gameCharacter.getBag();
        if (itemIndex < 0 || itemIndex >= BAG_SIZE) {
            System.out.println("Entered wrong number");
            System.out.println();
            return false;
        } else if (itemIndex < bag.size() && !(bag.get(itemIndex) instanceof Equipment)) {
            System.out.println("Chosen not equipment");
            System.out.println();
            return false;
        } else if (itemIndex >= bag.size()) {
            System.out.println("Chosen empty slot");
            System.out.println();
            return false;
        }
        return true;
    }

    private void equip(Equipment equipment) {
        List<Equipment> equipments = gameCharacter.getEquipments();
        List<Item> bag = gameCharacter.getBag();

        if (gameCharacter.getLevel() < equipment.getLevel() || !gameCharacter.getHeroClass().equals(equipment.getHeroClass())) {
            System.out.println("Cant equip this item");
            System.out.println();
        } else {
            Equipment equipped = getUsingEquipment(BodyParts.valueOf(equipment.getBodyPart()));
            if (equipped != null) {
                equipments.remove(equipped);
                bag.add(equipped);
            }
            equipments.add(equipment);
            bag.remove(equipment);
            gameCharacter.setEquipments(equipments);
            gameCharacter.setBag(bag);
        }

        System.out.println("Equipped " + equipment.getName());
    }

    private void printInventory() {
        List<Item> bag = gameCharacter.getBag();
        System.out.println("Inventory:");
        for (int i = 1; i <= 10; i++) {
            int itemIndex = i - 1;
            System.out.println(i + ":");
            if (bag.size() >= i && bag.get(itemIndex) instanceof Equipment) {
                System.out.println(bag.get(itemIndex));
            } else {
                System.out.println(EMPTY_SLOT);
            }
        }
    }
}
