package local.consolerpg.models.game.generators;

import local.consolerpg.models.game.Enemy;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.concepts.EquipmentParts;
import local.consolerpg.models.game.concepts.HeroClasses;
import local.consolerpg.models.game.concepts.names.EnemiesNouns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EnemyGenerator {

    private static final int STATS_PER_LEVEL = 5;

    public Enemy getRandom(int questLevel) {
        Random random = new Random();

        List<EnemiesNouns> enemiesNouns = Arrays.asList(EnemiesNouns.values());
        String name = enemiesNouns.get(random.nextInt(enemiesNouns.size())).toString().replaceAll("_"," ");

        int level = questLevel + random.nextInt(7) - 2;
        if (level <= 0) {
            level = 1;
        }

        List<HeroClasses> classes = Arrays.asList(HeroClasses.values());
        String enemyClass = classes.get(random.nextInt(classes.size())).toString();

        List<Equipment> equipments = new ArrayList<>();
        if (questLevel > 1) {
            generateEquipments(equipments, enemyClass, level);
        }

        int strength = 1;
        int agility = 1;
        int intelligence = 1;

        if (enemyClass.equals(HeroClasses.Warriror.toString())) {
            strength = 2;
        } else if (enemyClass.equals(HeroClasses.Rogue.toString())) {
            agility = 2;
        } else if (enemyClass.equals(HeroClasses.Mage.toString())) {
            intelligence = 2;
        }

        int stats = (level - 1) * STATS_PER_LEVEL;
        for (int i = 0; i < stats; i++) {
            int statSelect = random.nextInt(3);

            if (statSelect == 0) {
                strength++;
            } else if (statSelect == 1) {
                agility++;
            } else if (statSelect == 2) {
                intelligence++;
            }
        }

        int totalStrength = strength;
        int totalAgility = agility;
        int totalIntelligence = intelligence;

        for (Equipment equipment : equipments) {
            totalStrength += equipment.getStrength();
            totalAgility += equipment.getAgility();
            totalIntelligence += equipment.getIntelligence();
        }

        int totalStats = totalStrength + totalAgility + totalIntelligence;

        int gold = (int) Math.round(1.15 * level + random.nextInt((int) (Math.round(level * 1.15) + 1)));
        int experience = (int) Math.round(1.15 * level + random.nextInt((int) (Math.round(level * 1.15) + 1)));

        int hp = totalStrength * 10;
        int mp = totalIntelligence * 10;

        Enemy enemy = new Enemy();
        enemy.setName(name);
        enemy.setHeroClass(enemyClass);
        enemy.setLevel(level);
        enemy.setStrength(strength);
        enemy.setAgility(agility);
        enemy.setIntelligence(intelligence);
        enemy.setEquipments(equipments);
        enemy.setTotalStrength(totalStrength);
        enemy.setTotalAgility(totalAgility);
        enemy.setTotalIntelligence(totalIntelligence);
        enemy.setTotalStats(totalStats);
        enemy.setGold(gold);
        enemy.setExperience(experience);
        enemy.setCurrentHp(hp);
        enemy.setHp(hp);
        enemy.setCurrentMp(mp);
        enemy.setMp(mp);
        return enemy;
    }

    public Enemy getLastBoss(int level) {
        Random random = new Random();

        String name = "Old hero";

        List<HeroClasses> classes = Arrays.asList(HeroClasses.values());
        String bossClass = classes.get(random.nextInt(classes.size())).toString();

        List<Equipment> equipments = new ArrayList<>();
        generateEquipments(equipments, bossClass, level);

        int strength = 1;
        int agility = 1;
        int intelligence = 1;

        int statPoints = level * 8;

        for (int i = 0; i < statPoints; i++) {
            int statSelect = random.nextInt(3);

            if (statSelect == 0) {
                strength++;
            } else if (statSelect == 1) {
                agility++;
            } else if (statSelect == 2) {
                intelligence++;
            }
        }

        int totalStrength = strength;
        int totalAgility = agility;
        int totalIntelligence = intelligence;

        for (Equipment equipment : equipments) {
            totalStrength += equipment.getStrength();
            totalAgility += equipment.getAgility();
            totalIntelligence += equipment.getIntelligence();
        }

        int totalStats = totalStrength + totalAgility + totalIntelligence;

        int gold = (int) Math.round(1.15 * level + random.nextInt((int) (level * 1.15)));
        int experience = (int) Math.round(1.15 * level + random.nextInt((int) (level * 1.15)));

        Enemy enemy = new Enemy();
        enemy.setName(name);
        enemy.setHeroClass(bossClass);
        enemy.setLevel(level);
        enemy.setStrength(strength);
        enemy.setAgility(agility);
        enemy.setIntelligence(intelligence);
        enemy.setEquipments(equipments);
        enemy.setTotalStrength(totalStrength);
        enemy.setTotalAgility(totalAgility);
        enemy.setTotalIntelligence(totalIntelligence);
        enemy.setTotalStats(totalStats);
        enemy.setGold(gold);
        enemy.setExperience(experience);
        return enemy;
    }

    private void generateEquipments(List<Equipment> equipments, String enemyClass, int level) {
        EquipmentGenerator generator = new EquipmentGenerator();
        if (enemyClass.equals(HeroClasses.Warriror.toString())) {
            equipments.add(generator.getWarriorEquipment(level, EquipmentParts.Head));
            equipments.add(generator.getWarriorEquipment(level, EquipmentParts.Body));
            equipments.add(generator.getWarriorEquipment(level, EquipmentParts.Legs));
            equipments.add(generator.getWarriorEquipment(level, EquipmentParts.Hands));
            equipments.add(generator.getWarriorEquipment(level, EquipmentParts.Feet));
            equipments.add(generator.getWarriorEquipment(level, EquipmentParts.Weapon));
        } else if (enemyClass.equals(HeroClasses.Rogue.toString())) {
            equipments.add(generator.getRogueEquipment(level, EquipmentParts.Head));
            equipments.add(generator.getRogueEquipment(level, EquipmentParts.Body));
            equipments.add(generator.getRogueEquipment(level, EquipmentParts.Legs));
            equipments.add(generator.getRogueEquipment(level, EquipmentParts.Hands));
            equipments.add(generator.getRogueEquipment(level, EquipmentParts.Feet));
            equipments.add(generator.getRogueEquipment(level, EquipmentParts.Weapon));
        } else if (enemyClass.equals(HeroClasses.Mage.toString())) {
            equipments.add(generator.getMageEquipment(level, EquipmentParts.Head));
            equipments.add(generator.getMageEquipment(level, EquipmentParts.Body));
            equipments.add(generator.getMageEquipment(level, EquipmentParts.Legs));
            equipments.add(generator.getMageEquipment(level, EquipmentParts.Hands));
            equipments.add(generator.getMageEquipment(level, EquipmentParts.Feet));
            equipments.add(generator.getMageEquipment(level, EquipmentParts.Weapon));
        }
    }
}
