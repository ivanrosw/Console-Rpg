package local.database.dao.implementations;

import local.database.ConnectionGetter;
import local.database.dao.UserDao;
import local.database.exceptions.DaoException;
import local.models.User;

import java.sql.*;

public class UserDaoImpl implements UserDao {

    private static final String SQL_ADD_QUERY = "INSERT INTO registered_users(username, password) VALUES(?,?)";
    private static final String SQL_CHECK_USERNAME_QUERY = "SELECT COUNT(*) FROM registered_users WHERE username = ?";
    private static final String SQL_CHECK_USERNAME_AND_PASSWORD_QUERY = "SELECT COUNT(*) FROM registered_users " +
            "WHERE username = ? and password = ?";
    private static final String SQL_GET_ID_QUERY = "SELECT id FROM registered_users WHERE username = ?";

    @Override
    public void add(User user) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_ADD_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEncryptedPassword());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                user.setId(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            throw new DaoException("Add user to database failed", e);
        }
    }

    @Override
    public boolean isExistsUsername(String username) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_CHECK_USERNAME_QUERY)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) == 1;
            }

        } catch (SQLException e) {
            throw new DaoException("Check username failed", e);
        }
    }

    @Override
    public boolean isCorrectUserPassword(User user) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_CHECK_USERNAME_AND_PASSWORD_QUERY)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEncryptedPassword());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) == 1;
            }

        } catch (SQLException e) {
            throw new DaoException("Check user password failed", e);
        }
    }

    @Override
    public long getUserIdByName(String username) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_ID_QUERY)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }

        } catch (SQLException e) {
            throw new DaoException("Get user id failed", e);
        }
    }
}
