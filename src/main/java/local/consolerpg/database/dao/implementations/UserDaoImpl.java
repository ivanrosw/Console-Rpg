package local.consolerpg.database.dao.implementations;

import local.consolerpg.database.ConnectionGetter;
import local.consolerpg.database.PasswordEncryptor;
import local.consolerpg.database.dao.UserDao;
import local.consolerpg.database.exceptions.DaoException;
import local.consolerpg.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private static final String SQL_ADD_QUERY = "INSERT INTO registered_users(username, password) VALUES(?,?)";
    private static final String SQL_CHECK_USERNAME_QUERY = "SELECT COUNT(*) FROM registered_users WHERE username = ?";
    private static final String SQL_CHECK_USERNAME_AND_PASSWORD_QUERY = "SELECT COUNT(*) FROM registered_users " +
            "WHERE username = ? and password = ?";
    private static final String SQL_GET_ID_QUERY = "SELECT id FROM registered_users WHERE username = ?";
    private static final String SQL_GET_CHARACTERS_ID_QUERY = "SELECT id FROM users_characters WHERE user_id = ?";

    @Override
    public void add(User user) {
        logger.debug("Adding user: {} to database", user.getName());
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_ADD_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, PasswordEncryptor.getEncryptedPassword(user.getPassword()));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                user.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            logger.warn("Add user: {} to database failed", user.getName(), e);
            throw new DaoException("Add user to database failed", e);
        }
    }

    @Override
    public boolean isExistsUsername(String username) {
        logger.debug("Checking userName: {} existence in database", username);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_CHECK_USERNAME_QUERY)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) == 1;
            }

        } catch (SQLException e) {
            logger.warn("Check username: {} failed", username, e);
            throw new DaoException("Check username failed", e);
        }
    }

    @Override
    public boolean isCorrectUserPassword(User user) {
        logger.debug("Checking userName: {} password", user.getName());
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_CHECK_USERNAME_AND_PASSWORD_QUERY)) {

            statement.setString(1, user.getName());
            statement.setString(2, PasswordEncryptor.getEncryptedPassword(user.getPassword()));

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) == 1;
            }

        } catch (SQLException e) {
            logger.warn("Check userName: {} password failed", user.getName(), e);
            throw new DaoException("Check user password failed", e);
        }
    }

    @Override
    public long getIdByName(String username) {
        logger.debug("Getting id by userName: {}", username);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ID_QUERY)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }

        } catch (SQLException e) {
            logger.warn("Get userName: {} id failed", username, e);
            throw new DaoException("Get user id failed", e);
        }
    }

    @Override
    public List<Long> getCharactersId(long userId) {
        logger.debug("Getting characters id by userId: {}", userId);
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_CHARACTERS_ID_QUERY)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Long> charactersId = new ArrayList<>();
                while (resultSet.next()) {
                    charactersId.add(resultSet.getLong("id"));
                }
                return charactersId;
            }

        } catch (SQLException e) {
            logger.warn("Get characters id by userId: {} failed", userId, e);
            throw new DaoException("Get characters id failed", e);
        }
    }
}
