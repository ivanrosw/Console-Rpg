package local.consolerpg.models.game.generators;

import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.builders.EquipmentBuilder;
import local.consolerpg.models.game.concepts.EquipmentParts;
import local.consolerpg.models.game.concepts.HeroClasses;
import local.consolerpg.models.game.concepts.names.*;

import java.util.Random;

public class EquipmentGenerator {

    public Equipment getRandom(int level) {
        Random random = new Random();

        EquipmentParts[] equipmentParts = EquipmentParts.values();
        EquipmentParts equipmentPart = equipmentParts[random.nextInt(equipmentParts.length)];

        HeroClasses[] heroClasses = HeroClasses.values();
        HeroClasses heroClass = heroClasses[random.nextInt(heroClasses.length)];

        int equipmentLevel = level + (random.nextInt(10) - 5);
        if (equipmentLevel <= 0) {
            equipmentLevel = 1;
        }

        return generateEquipment(equipmentLevel, equipmentPart, heroClass);
    }

    public Equipment getWarriorEquipment(int level, EquipmentParts equipmentPart) {
        Random random = new Random();
        int equipmentLevel = level - random.nextInt(5);

        return generateEquipment(equipmentLevel, equipmentPart, HeroClasses.Warriror);
    }

    public Equipment getRogueEquipment(int level, EquipmentParts equipmentPart) {
        Random random = new Random();
        int equipmentLevel = level - random.nextInt(5);

        return generateEquipment(equipmentLevel, equipmentPart, HeroClasses.Rogue);
    }

    public Equipment getMageEquipment(int level, EquipmentParts equipmentPart) {
        Random random = new Random();
        int equipmentLevel = level - random.nextInt(5);

        return generateEquipment(equipmentLevel, equipmentPart, HeroClasses.Mage);
    }

    private Equipment generateEquipment(int level, EquipmentParts equipmentPart, HeroClasses heroClass) {
        Random random = new Random();

        StringBuilder name = new StringBuilder();
        int adjectivesCount = random.nextInt(1) + 1;

        EquipmentAdjectives[] equipmentAdjectives = EquipmentAdjectives.values();
        for (int i = 0; i < adjectivesCount; i++) {
            name.append(equipmentAdjectives[random.nextInt(equipmentAdjectives.length)] + " ");
        }

        if (equipmentPart.equals(EquipmentParts.Body)) {
            BodyNouns[] bodyNouns = BodyNouns.values();
            name.append(bodyNouns[random.nextInt(bodyNouns.length)]);

        } else if (equipmentPart.equals(EquipmentParts.Head)) {
            HeadNouns[] headNouns = HeadNouns.values();
            name.append(headNouns[random.nextInt(headNouns.length)]);

        } else if (equipmentPart.equals(EquipmentParts.Legs)) {
            LegsNouns[] legsNouns = LegsNouns.values();
            name.append(legsNouns[random.nextInt(legsNouns.length)]);

        } else if (equipmentPart.equals(EquipmentParts.Feet)) {
            FeetNouns[] feetNouns = FeetNouns.values();
            name.append(feetNouns[random.nextInt(feetNouns.length)]);

        } else if (equipmentPart.equals(EquipmentParts.Hands)) {
            HandsNouns[] handsNouns = HandsNouns.values();
            name.append(handsNouns[random.nextInt(handsNouns.length)]);

        } else if (equipmentPart.equals(EquipmentParts.Weapon) && heroClass.equals(HeroClasses.Warriror)) {
            WarriorWeaponNouns[] warriorWeaponNouns = WarriorWeaponNouns.values();
            name.append(warriorWeaponNouns[random.nextInt(warriorWeaponNouns.length)]
                    .toString().replaceAll("_", " "));

        } else if (equipmentPart.equals(EquipmentParts.Weapon) && heroClass.equals(HeroClasses.Rogue)) {
            RogueWeaponNouns[] rogueWeaponNouns = RogueWeaponNouns.values();
            name.append(rogueWeaponNouns[random.nextInt(rogueWeaponNouns.length)]
                    .toString().replaceAll("_", " "));

        } else if (equipmentPart.equals(EquipmentParts.Weapon) && heroClass.equals(HeroClasses.Mage)) {
            MageWeaponNouns[] mageWeaponNouns = MageWeaponNouns.values();
            name.append(mageWeaponNouns[random.nextInt(mageWeaponNouns.length)]
                    .toString().replaceAll("_", " "));
        }

        int gold =(int)(Math.round(level * 1.25) + Math.round(level * 1.25 / 100 * (random.nextInt(20)-10)));

        Equipment equipment = new EquipmentBuilder().withName(name.toString())
                .withBodyPart(equipmentPart.toString())
                .withLevel(level)
                .withHeroClass(heroClass.toString())
                .withGold(gold)
                .build();

        generateStats(equipment);

        return equipment;
    }

    private void generateStats(Equipment equipment) {
        Random random = new Random();
        int totalStats = equipment.getLevel() * 5 + Math.round(equipment.getLevel() * 5 / 100 * (random.nextInt(20)-10));

        for (int i = 0; i < totalStats; i++) {
            int statIndex = random.nextInt(3);

            if (statIndex == 0) {
                equipment.setStrength(equipment.getStrength() + 1);
            } else if (statIndex == 1) {
                equipment.setAgility(equipment.getAgility() + 1);
            } else if (statIndex == 2) {
                equipment.setIntelligence(equipment.getIntelligence() + 1);
            }
        }
    }
}
