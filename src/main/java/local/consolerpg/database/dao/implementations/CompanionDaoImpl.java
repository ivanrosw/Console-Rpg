package local.consolerpg.database.dao.implementations;

import local.consolerpg.database.ConnectionGetter;
import local.consolerpg.database.dao.CompanionDao;
import local.consolerpg.database.exceptions.DaoException;
import local.consolerpg.models.game.Companion;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.builders.CompanionBuilder;
import local.consolerpg.models.game.builders.EquipmentBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanionDaoImpl implements CompanionDao {

    private static final String SQL_GET_BETWEEN_LEVELS_QUERY = "SELECT * FROM users_characters WHERE level >= ? AND level <= ?";
    private static final String SQL_GET_LESS_LEVEL_QUERY = "SELECT * FROM users_characters WHERE level <= ?";
    private static final String SQL_GET_ALL_CHARACTERS_EQUIPMENTS_QUERY = "SELECT * FROM characters_equipments WHERE character_id = ?";

    @Override
    public List<Companion> getBetweenLevels(int minLevel, int maxLevel) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_BETWEEN_LEVELS_QUERY)) {

            statement.setInt(1, minLevel);
            statement.setInt(2, maxLevel);

            List<Companion> companions = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Companion companion = getCompanion(resultSet);
                    companion.setEquipments(getCompanionEquipments(resultSet.getLong("id")));
                    List<Equipment> originalEquipments = new ArrayList<>();
                    originalEquipments.addAll(companion.getEquipments());
                    companion.setOriginalEquipments(originalEquipments);
                    companions.add(companion);
                }
            }
            return companions;
        } catch (SQLException e) {
            throw new DaoException("Getting companions between levels failed", e);
        }
    }

    @Override
    public List<Companion> getLessLevel(int level) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_LESS_LEVEL_QUERY)) {

            statement.setInt(1, level);

            List<Companion> companions = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Companion companion = getCompanion(resultSet);
                    companion.setEquipments(getCompanionEquipments(resultSet.getLong("id")));
                    List<Equipment> originalEquipments = new ArrayList<>();
                    originalEquipments.addAll(companion.getEquipments());
                    companion.setOriginalEquipments(originalEquipments);
                    companions.add(companion);
                }
            }
            return companions;
        } catch (SQLException e) {
            throw new DaoException("Getting companions less levels failed", e);
        }
    }

    private Companion getCompanion(ResultSet resultSet) {
        try {
            return new CompanionBuilder()
                    .withId(resultSet.getLong("id"))
                    .withName(resultSet.getString("name"))
                    .withHeroClass(resultSet.getString("hero_class"))
                    .withLevel(resultSet.getInt("level"))
                    .withStrength(resultSet.getInt("strength"))
                    .withAgility(resultSet.getInt("agility"))
                    .withIntelligence(resultSet.getInt("intelligence"))
                    .withEquipments(new ArrayList<>())
                    .withOriginalEquipments(new ArrayList<>())
                    .withUserEquipments(new ArrayList<>())
                    .build();
        } catch (SQLException e) {
            throw new DaoException("Getting companion from ResultSet failed", e);
        }
    }

    private List<Equipment> getCompanionEquipments(long companionId) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_CHARACTERS_EQUIPMENTS_QUERY)) {

            statement.setLong(1, companionId);

            List<Equipment> equipments = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    equipments.add(getEquipment(resultSet));
                }
            }
            return equipments;
        } catch (SQLException e) {
            throw new DaoException("Getting companions between levels failed", e);
        }
    }

    private Equipment getEquipment(ResultSet resultSet) {
        try {
            return new EquipmentBuilder()
                    .withName(resultSet.getString("name"))
                    .withHeroClass(resultSet.getString("hero_class"))
                    .withBodyPart(resultSet.getString("body_part"))
                    .withLevel(resultSet.getInt("level"))
                    .withStrength(resultSet.getInt("strength"))
                    .withAgility(resultSet.getInt("agility"))
                    .withIntelligence(resultSet.getInt("intelligence"))
                    .withGold(resultSet.getInt("gold"))
                    .build();
        } catch (SQLException e) {
            throw new DaoException("Getting companion equipment from ResultSet failed", e);
        }
    }
}
