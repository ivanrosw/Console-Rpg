package local.consolerpg.database.dao.implementations;

import local.consolerpg.database.ConnectionGetter;
import local.consolerpg.database.dao.GameCharacterDao;
import local.consolerpg.database.exceptions.DaoException;
import local.consolerpg.models.game.Equipment;
import local.consolerpg.models.game.GameCharacter;
import local.consolerpg.models.game.Item;
import local.consolerpg.models.game.Usable;
import local.consolerpg.models.game.builders.EquipmentBuilder;
import local.consolerpg.models.game.builders.GameCharacterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameCharacterDaoImpl implements GameCharacterDao {

    private static final Logger logger = LoggerFactory.getLogger(GameCharacterDaoImpl.class);

    private static final String SQL_ADD_QUERY = "INSERT INTO users_characters(user_id, name, level, strength, agility, " +
            "intelligence, hero_class, enemies_kill, quests_done, game_count, gold, stat_points) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_ADD_CHARACTER_EQUIPMENT_QUERY = "INSERT INTO characters_equipments(character_id, name, " +
            "hero_class, body_part, level, strength, agility, intelligence, gold) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_ADD_CHARACTER_BAG_USABLE_QUERY = "INSERT INTO characters_bag_usable(character_id, name, " +
            "count, gold) VALUES (?,?,?,?)";
    private static final String SQL_ADD_CHARACTER_BAG_EQUIPMENT_QUERY = "INSERT INTO characters_bag_equipments(character_id, name, " +
            "hero_class, body_part, level, strength, agility, intelligence, gold) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String SQL_GET_ALL_USER_CHARACTERS_QUERY = "SELECT * FROM users_characters WHERE user_id = ?";
    private static final String SQL_GET_CHARACTER_QUERY = "SELECT * FROM users_characters WHERE id = ?";
    private static final String SQL_GET_ALL_CHARACTERS_EQUIPMENTS_QUERY = "SELECT * FROM characters_equipments WHERE character_id = ?";
    private static final String SQL_GET_ALL_CHARACTERS_BAG_USABLE_QUERY = "SELECT * FROM characters_bag_usable WHERE character_id = ?";
    private static final String SQL_GET_ALL_CHARACTERS_BAG_EQUIPMENTS_QUERY = "SELECT * FROM characters_bag_equipments WHERE character_id = ?";
    private static final String SQL_UPDATE_QUERY = "UPDATE users_characters SET name=?, level=?, strength=?, agility=?, intelligence=?, " +
            "hero_class=?, enemies_kill=?, quests_done=?, game_count=?, gold=?, stat_points=? WHERE id = ?";
    private static final String SQL_DELETE_CHARACTER_EQUIPMENT_QUERY = "DELETE FROM characters_equipments WHERE character_id = ?";
    private static final String SQL_DELETE_CHARACTER_BAG_USABLE_QUERY = "DELETE FROM characters_bag_usable WHERE character_id = ?";
    private static final String SQL_DELETE_CHARACTER_BAG_EQUIPMENT_QUERY = "DELETE FROM characters_bag_equipments WHERE character_id = ?";

    @Override
    public void add(GameCharacter gameCharacter) {
        logger.debug("Adding new {}", gameCharacter);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_ADD_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, gameCharacter.getUserId());
            statement.setString(2, gameCharacter.getName());
            statement.setInt(3, gameCharacter.getLevel());
            statement.setInt(4, gameCharacter.getStrength());
            statement.setInt(5, gameCharacter.getAgility());
            statement.setInt(6, gameCharacter.getIntelligence());
            statement.setString(7, gameCharacter.getHeroClass());
            statement.setLong(8, gameCharacter.getEnemiesKill());
            statement.setLong(9, gameCharacter.getQuestsDone());
            statement.setInt(10, gameCharacter.getGameCount());
            statement.setLong(11, gameCharacter.getGold());
            statement.setInt(12, gameCharacter.getStatPoints());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                gameCharacter.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            logger.warn("Adding new {} failed", gameCharacter, e);
            throw new DaoException("Adding new game character failed", e);
        }

        addCharacterEquipments(gameCharacter);
        addCharacterBag(gameCharacter);
        logger.debug("Added new {}", gameCharacter);
    }

    @Override
    public GameCharacter getById(long id) {
        logger.debug("Getting game character with id: {}", id);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_CHARACTER_QUERY)) {

            statement.setLong(1, id);

            GameCharacter gameCharacter;
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                gameCharacter = getGameCharacter(resultSet);
            }

            getAllCharactersEquipments(gameCharacter);
            gameCharacter.setBag(new ArrayList<>());
            getAllCharactersBagUsable(gameCharacter);
            getAllCharactersBagEquipments(gameCharacter);

            logger.debug("Got {}", gameCharacter);
            return gameCharacter;

        } catch (SQLException e) {
            logger.warn("Getting game character with id: {} failed", id);
            throw new DaoException("Getting game character failed", e);
        }
    }

    @Override
    public List<GameCharacter> getAllByUserId(long userId) {
        logger.debug("Getting all users's with id: {} game characters", userId);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_USER_CHARACTERS_QUERY)) {

            statement.setLong(1, userId);

            List<GameCharacter> gameCharacters = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    GameCharacter gameCharacter = getGameCharacter(resultSet);

                    getAllCharactersEquipments(gameCharacter);
                    gameCharacter.setBag(new ArrayList<>());
                    getAllCharactersBagUsable(gameCharacter);
                    getAllCharactersBagEquipments(gameCharacter);

                    gameCharacters.add(gameCharacter);
                }
            }
            logger.debug("Got {} users's with id: {} game characters", gameCharacters.size(), userId);
            return gameCharacters;

        } catch (SQLException e) {
            logger.warn("Getting all game characters by user id : {} failed", userId);
            throw new DaoException("Getting all game characters by user id failed", e);
        }
    }

    @Override
    public void update(GameCharacter gameCharacter) {
        logger.debug("Updating {} in database", gameCharacter);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_QUERY)) {

            statement.setString(1, gameCharacter.getName());
            statement.setInt(2, gameCharacter.getLevel());
            statement.setInt(3, gameCharacter.getStrength());
            statement.setInt(4, gameCharacter.getAgility());
            statement.setInt(5, gameCharacter.getIntelligence());
            statement.setString(6, gameCharacter.getHeroClass());
            statement.setLong(7, gameCharacter.getEnemiesKill());
            statement.setLong(8, gameCharacter.getQuestsDone());
            statement.setInt(9, gameCharacter.getGameCount());
            statement.setInt(10, gameCharacter.getGold());
            statement.setInt(11, gameCharacter.getStatPoints());
            statement.setLong(12, gameCharacter.getId());

            statement.execute();

        } catch (SQLException e) {
            logger.warn("Updating {} in database failed", gameCharacter, e);
            throw new DaoException("Updating game character in database failed", e);
        }

        deleteCharacterEquipments(gameCharacter.getId());
        deleteCharacterBagEquipments(gameCharacter.getId());
        deleteCharacterBagUsables(gameCharacter.getId());

        addCharacterEquipments(gameCharacter);
        addCharacterBag(gameCharacter);
        logger.debug("Updated {}", gameCharacter);
    }

    private void getAllCharactersEquipments(GameCharacter gameCharacter) {
        logger.debug("Getting game character's with id: {} equipments", gameCharacter.getId());
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_CHARACTERS_EQUIPMENTS_QUERY)) {

            statement.setLong(1, gameCharacter.getId());

            List<Equipment> equipments = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Equipment equipment = getEquipment(resultSet);
                    equipments.add(equipment);
                }
            }
            gameCharacter.setEquipments(equipments);
            logger.debug("Got {} game character's with id: {} equipments", equipments.size(), gameCharacter.getId());

        } catch (SQLException e) {
            logger.warn("Getting game character's with id: {} equipments failed", gameCharacter.getId());
            throw new DaoException("Getting game character's equipments failed", e);
        }
    }

    private void getAllCharactersBagUsable(GameCharacter gameCharacter) {
        logger.debug("Getting game character's with id: {} bag usable", gameCharacter.getId());
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_CHARACTERS_BAG_USABLE_QUERY)) {

            statement.setLong(1, gameCharacter.getId());

            List<Item> bagUsable = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bagUsable.add(new Usable(
                                    resultSet.getString("name"),
                                    resultSet.getInt("count"),
                                    resultSet.getInt("gold")));
                }
            }
            List<Item> bag = gameCharacter.getBag();
            bag.addAll(bagUsable);
            gameCharacter.setBag(bag);
            logger.debug("Got {} game character's with id: {} bag usable", bagUsable.size(), gameCharacter.getId());

        } catch (SQLException e) {
            logger.warn("Getting game character's with id : {} bag usable failed", gameCharacter.getId());
            throw new DaoException("Getting game character's bag usable failed", e);
        }
    }

    private void getAllCharactersBagEquipments(GameCharacter gameCharacter) {
        logger.debug("Getting game character's with id: {} bag equipments", gameCharacter.getId());
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_CHARACTERS_BAG_EQUIPMENTS_QUERY)) {

            statement.setLong(1, gameCharacter.getId());

            List<Item> bagEquipments = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Equipment equipment = getEquipment(resultSet);
                    bagEquipments.add(equipment);
                }
            }
            List<Item> bag = gameCharacter.getBag();
            bag.addAll(bagEquipments);
            gameCharacter.setBag(bag);
            logger.debug("Got {} game character's with id: {} bag equipments", bagEquipments.size(), gameCharacter.getId());

        } catch (SQLException e) {
            logger.warn("Getting game character's with id : {} bag equipments failed", gameCharacter.getId());
            throw new DaoException("Getting game character's bag equipments failed", e);
        }
    }

    private Equipment getEquipment(ResultSet resultSet) {
        logger.debug("Getting character's equipment from ResultSet");
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
            logger.warn("Getting character's equipment from ResultSet failed", e);
            throw new DaoException("Getting character's equipment from ResultSet failed", e);
        }
    }

    private GameCharacter getGameCharacter(ResultSet resultSet) {
        logger.debug("Getting character from ResultSet");
        try {
            return new GameCharacterBuilder()
                    .withId(resultSet.getLong("id"))
                    .withUserId(resultSet.getLong("user_id"))
                    .withName(resultSet.getString("name"))
                    .withLevel(resultSet.getInt("level"))
                    .withStrength(resultSet.getInt("strength"))
                    .withAgility(resultSet.getInt("agility"))
                    .withIntelligence(resultSet.getInt("intelligence"))
                    .withHeroClass(resultSet.getString("hero_class"))
                    .withEnemiesKill(resultSet.getLong("enemies_kill"))
                    .withQuestsDone(resultSet.getLong("quests_done"))
                    .withGameCount(resultSet.getInt("game_count"))
                    .withGold(resultSet.getInt("gold"))
                    .withStatPoints(resultSet.getInt("stat_points"))
                    .build();
        } catch (SQLException e) {
            logger.warn("Getting character from ResultSet failed", e);
            throw new DaoException("Getting character from ResultSet failed", e);
        }
    }

    private void addCharacterEquipments(GameCharacter gameCharacter) {
        logger.debug("Adding character's with id: {} equipments to database", gameCharacter.getId());
        gameCharacter.getEquipments().forEach(equipment -> {
            try (Connection connection = ConnectionGetter.getConnection();
                 PreparedStatement statement = connection.prepareStatement(SQL_ADD_CHARACTER_EQUIPMENT_QUERY)) {

                logger.debug("Adding {}", equipment);
                statement.setLong(1, gameCharacter.getId());
                statement.setString(2, equipment.getName());
                statement.setString(3, equipment.getHeroClass());
                statement.setString(4, equipment.getBodyPart());
                statement.setInt(5, equipment.getLevel());
                statement.setInt(6, equipment.getStrength());
                statement.setInt(7, equipment.getAgility());
                statement.setInt(8, equipment.getIntelligence());
                statement.setInt(9, equipment.getGold());

                statement.executeUpdate();
                logger.debug("Added {}", equipment);

            } catch (SQLException e) {
                logger.warn("Adding character with id: {} {} failed", gameCharacter.getId(), equipment, e);
                throw new DaoException("Adding character equipment failed", e);
            }
        });
    }

    private void addCharacterBag(GameCharacter gameCharacter) {
        logger.debug("Adding character's with id: {} bag to database", gameCharacter.getId());
        gameCharacter.getBag().forEach(item -> {

            if (item instanceof Usable) {
                Usable usable = (Usable) item;
                logger.debug("Adding character's with id: {} {}", gameCharacter.getId(), usable);

                try (Connection connection = ConnectionGetter.getConnection();
                     PreparedStatement statement = connection.prepareStatement(SQL_ADD_CHARACTER_BAG_USABLE_QUERY)) {

                    statement.setLong(1, gameCharacter.getId());
                    statement.setString(2, usable.getName());
                    statement.setInt(3, usable.getCount());
                    statement.setInt(4, usable.getGold());

                    statement.executeUpdate();
                    logger.debug("Added character's with id: {} {}", gameCharacter.getId(), usable);

                } catch (SQLException e) {
                    logger.warn("Adding character's with id: {} {} failed", gameCharacter.getId(), usable, e);
                    throw new DaoException("Adding character bag usable failed", e);
                }

            } else if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                logger.debug("Adding character's with id: {} {}", gameCharacter.getId(), equipment);

                try (Connection connection = ConnectionGetter.getConnection();
                     PreparedStatement statement = connection.prepareStatement(SQL_ADD_CHARACTER_BAG_EQUIPMENT_QUERY)) {

                    statement.setLong(1, gameCharacter.getId());
                    statement.setString(2, equipment.getName());
                    statement.setString(3, equipment.getHeroClass());
                    statement.setString(4, equipment.getBodyPart());
                    statement.setInt(5, equipment.getLevel());
                    statement.setInt(6, equipment.getStrength());
                    statement.setInt(7, equipment.getAgility());
                    statement.setInt(8, equipment.getIntelligence());
                    statement.setInt(9, equipment.getGold());

                    statement.executeUpdate();
                    logger.debug("Added character's with id: {} {}", gameCharacter.getId(), equipment);

                } catch (SQLException e) {
                    logger.warn("Adding character's with id: {} {} failed", gameCharacter.getId(), equipment, e);
                    throw new DaoException("Adding character bag equipment failed", e);
                }
            }
        });
        logger.debug("Added character's with id: {} bag to database", gameCharacter.getId());
    }

    private void deleteCharacterEquipments(long gameCharacterId) {
        logger.debug("Deleting character's with id: {} equipments from database", gameCharacterId);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_CHARACTER_EQUIPMENT_QUERY)) {

            statement.setLong(1, gameCharacterId);
            statement.executeUpdate();
            logger.debug("Deleted {} character's with id: {} equipments from database", statement.getUpdateCount(), gameCharacterId);

        } catch (SQLException e) {
            logger.warn("Deleting character's with id: {} equipments from database failed", gameCharacterId, e);
            throw new DaoException("Adding character's equipments from database failed", e);
        }
    }

    private void deleteCharacterBagUsables(long gameCharacterId) {
        logger.debug("Deleting character's with id: {} bag usables from database", gameCharacterId);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_CHARACTER_BAG_USABLE_QUERY)) {

            statement.setLong(1, gameCharacterId);
            statement.executeUpdate();
            logger.debug("Deleted {} character's with id: {} bag usables from database", statement.getUpdateCount(), gameCharacterId);

        } catch (SQLException e) {
            logger.warn("Deleting character's with id: {} bag usables from database failed", gameCharacterId, e);
            throw new DaoException("Adding character's bag usables from database failed", e);
        }
    }

    private void deleteCharacterBagEquipments(long gameCharacterId) {
        logger.debug("Deleting character's with id: {} bag equipments from database", gameCharacterId);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_CHARACTER_BAG_EQUIPMENT_QUERY)) {

            statement.setLong(1, gameCharacterId);
            statement.executeUpdate();
            logger.debug("Deleted {} character's with id: {} bag equipments from database", statement.getUpdateCount(), gameCharacterId);

        } catch (SQLException e) {
            logger.warn("Deleting character's with id: {} bag equipments from database failed", gameCharacterId, e);
            throw new DaoException("Adding character's bag equipments from database failed", e);
        }
    }
}
