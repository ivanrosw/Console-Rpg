package local.consolerpg.managers.game;

import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;
import local.consolerpg.models.game.Usable;
import local.consolerpg.models.game.concepts.Usables;
import local.consolerpg.models.game.generators.EquipmentGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TraderManager {

    private final static int BAG_SIZE = 10;

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    private long generateTrigger = 0;
    private List<Item> traderItems = new ArrayList<>();

    public TraderManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getTraderMenu() {
        if (gameCharacter.getEnemiesKill() == 0 && traderItems.isEmpty()) {
            generateItems();
        }
        if (generateTrigger != gameCharacter.getEnemiesKill()) {
            generateItems();
            generateTrigger = gameCharacter.getEnemiesKill();
        }

        boolean inMenu = true;
        while (inMenu) {
            try {
                System.out.println("Trader:");
                System.out.println("1: Buy  2: Sell  3: Close menu");
                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("3")) {
                    inMenu = false;
                } else if (userAnswer.equals("1")) {
                    getBuyingMenu();
                } else if (userAnswer.equals("2")) {
                    getSellingMenu();
                } else {
                    System.out.println("Entered wrong symbol");
                    System.out.println();
                }

            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            }

        }
    }

    private void getSellingMenu() {
        boolean inMenu = true;
        while (inMenu) {
            try {
                List<Item> bag = gameCharacter.getBag();
                System.out.println("Sell:");
                printItems(bag);
                System.out.println("Enter number of item that you want to sell or \"E\" to cancel");
                String userAnswer = consoleReader.readLine();

                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else {
                    int itemIndex = Integer.parseInt(userAnswer) - 1;
                    if (itemIndex >= bag.size()) {
                        System.out.println("Entered wrong number");
                        System.out.println();
                        continue;
                    }

                    Item sellingItem = bag.get(itemIndex);

                    if (sellingItem instanceof Usable) {
                        Usable sellingUsable = (Usable) sellingItem;

                        System.out.println("Enter how much do you want to sell or \"E\" to cancel");
                        userAnswer = consoleReader.readLine();

                        if (userAnswer.equals("E")) {
                            System.out.println("Selling canceled");
                            System.out.println();
                            continue;

                        } else {
                            int sellingCount = Integer.parseInt(userAnswer);

                            if (sellingCount > sellingUsable.getCount()) {
                                System.out.println("Entered wrong count");
                                System.out.println();
                                continue;
                            }

                            int totalGold = sellingUsable.getGold() * sellingCount;

                            if (sellingCount == sellingUsable.getCount()) {
                                bag.remove(sellingUsable);
                            } else {
                                sellingUsable.setCount(sellingUsable.getCount() - sellingCount);
                                bag.set(itemIndex, sellingUsable);
                            }

                            gameCharacter.setBag(bag);
                            gameCharacter.setGold(gameCharacter.getGold() + totalGold);
                            System.out.println("Sold: " + sellingUsable.getName() + " x" + sellingCount + " : " + totalGold + " gold");
                            System.out.println();
                        }
                    } else if (sellingItem instanceof Equipment) {
                        Equipment sellingEquipment = (Equipment) sellingItem;

                        System.out.println("Are you sure want to sell? y\\n");
                        userAnswer = consoleReader.readLine();

                        if (userAnswer.equals("n")) {
                            System.out.println("Selling canceled");
                            System.out.println();
                            continue;

                        } else if (userAnswer.equals("y")) {
                            bag.remove(sellingEquipment);
                            gameCharacter.setBag(bag);
                            gameCharacter.setGold(gameCharacter.getGold() + sellingEquipment.getGold());
                            System.out.println("Sold: " + sellingEquipment.getName() + " : " + sellingEquipment.getGold() + " gold" );
                            System.out.println();
                        } else {
                            System.out.println("Entered wrong symbol");
                        }
                    }
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }
    }

    private void getBuyingMenu() {
        boolean inMenu = true;
        while (inMenu) {
            try {
                System.out.println("Buy:");
                printItems(traderItems);
                System.out.println();
                System.out.println("Gold in bag: " + gameCharacter.getGold());
                System.out.println();
                System.out.println("Enter number to buy item or \"E\" to cancel");

                String userAnswer = consoleReader.readLine();
                if (userAnswer.equals("E")) {
                    inMenu = false;
                } else {
                    int itemIndex = Integer.parseInt(userAnswer) - 1;
                    if (itemIndex >= traderItems.size()) {
                        System.out.println("Entered wrong number");
                        continue;
                    }

                    if (gameCharacter.getBag().size() >= BAG_SIZE) {
                        System.out.println("No empty slot in bag");
                    }

                    if (traderItems.get(itemIndex).getGold() > gameCharacter.getGold()) {
                        System.out.println("Not enough gold to buy item");
                        continue;
                    }

                    System.out.println("Are you sure want to buy? y\\n");
                    userAnswer = consoleReader.readLine();

                    if (userAnswer.equals("y")) {
                        Item boughtItem = traderItems.get(itemIndex);

                        if (boughtItem instanceof Usable) {
                            Usable usable = (Usable) boughtItem;
                            usable.setCount(usable.getCount() - 1);

                            if (usable.getCount() <= 0) {
                                traderItems.remove(itemIndex);
                            } else {
                                traderItems.set(itemIndex, usable);
                            }

                            Usable inBagUsable = getUsableFromBag(usable.getName());
                            if (inBagUsable != null) {
                                inBagUsable.setCount(inBagUsable.getCount() + 1);
                                setUsableInBag(inBagUsable);
                            } else {
                                List<Item> bag = gameCharacter.getBag();
                                usable.setCount(1);
                                usable.setGold(10);

                                bag.add(usable);
                                gameCharacter.setBag(bag);
                            }

                            gameCharacter.setGold(gameCharacter.getGold() - usable.getGold());

                        } else if (boughtItem instanceof Equipment) {
                            traderItems.remove(boughtItem);
                            List<Item> bag = gameCharacter.getBag();

                            gameCharacter.setGold(gameCharacter.getGold() - boughtItem.getGold());

                            Equipment equipment = (Equipment) boughtItem;
                            equipment.setGold(getCharacterEquipmentPrice(equipment));
                            bag.add(equipment);
                            gameCharacter.setBag(bag);
                        }

                        System.out.println("Bought " + boughtItem.getName());
                        System.out.println();

                    } else if (userAnswer.equals("n")) {
                        System.out.println("Buying canceled");
                        System.out.println();
                    } else {
                        System.out.println("Entered wrong symbol");
                        System.out.println();
                    }
                }
            } catch (IOException e) {
                throw new ManagerException("Internal consoleReader error", e);
            } catch (NumberFormatException e) {
                System.out.println("Entered wrong symbol");
            }
        }
    }

    private void generateItems() {
        Usable usable = new Usable();
        usable.setName(Usables.HP_potion.toString().replaceAll("_", " "));
        usable.setGold(20);
        usable.setCount(5);
        traderItems.add(usable);

        usable.setName(Usables.MP_potion.toString().replaceAll("_", " "));
        traderItems.add(usable);

        EquipmentGenerator generator = new EquipmentGenerator();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            Equipment equipment = generator.getRandom(gameCharacter.getLevel());
            int equipmentPrice = equipment.getLevel() * 4
                    + Math.round(equipment.getLevel() * 4 / 100 * (random.nextInt(20)-10));
            equipment.setGold(equipmentPrice);
            traderItems.add(equipment);
        }
    }

    private void printItems(List<Item> itemList) {
        int index = 1;
        for (Item item : itemList) {
            System.out.println(index);
            System.out.println(item);
            index++;
        }
    }

    private void setUsableInBag(Usable usable) {
        List<Item> bag = gameCharacter.getBag();

        for (int i = 0; i < bag.size(); i++) {
            Item item = bag.get(i);
            if (item instanceof Usable && ((Usable) item).getName().equals(usable.getName())) {
                bag.set(i, usable);
                gameCharacter.setBag(bag);
                break;
            }
        }
    }

    private Usable getUsableFromBag(String usableName) {
        List<Item> bag = gameCharacter.getBag();

        for (Item item : bag) {
            if (item instanceof Usable && ((Usable) item).getName().equals(usableName)) {
                return (Usable) item;
            }
        }
        return null;
    }

    private int getCharacterEquipmentPrice(Equipment equipment) {
        Random random = new Random();
        return (int) (Math.round(equipment.getLevel() * 1.25) + Math.round(equipment.getLevel() * 1.25 / 100 * (random.nextInt(20) - 10)));
    }
}
