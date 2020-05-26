package local.database.dao.implementations;

import local.database.ConnectionGetter;
import local.database.dao.UserDao;
import local.database.exceptions.DaoException;
import local.services.exceptions.ManagerException;
import local.models.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final String encryptKey = "J@NcRfUjXn2r5u8x";

    private static final String SQL_ADD_QUERY = "INSERT INTO registered_users(username, password) VALUES(?,?)";
    private static final String SQL_CHECK_USERNAME_QUERY = "SELECT COUNT(*) FROM registered_users WHERE username = ?";
    private static final String SQL_CHECK_USERNAME_AND_PASSWORD_QUERY = "SELECT COUNT(*) FROM registered_users " +
            "WHERE username = ? and password = ?";
    private static final String SQL_GET_ID_QUERY = "SELECT id FROM registered_users WHERE username = ?";
    private static final String SQL_GET_CHARACTERS_ID_QUERY = "SELECT id FROM users_characters WHERE user_id = ?";

    @Override
    public void add(User user) {
        try (Connection connection = ConnectionGetter.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_ADD_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, getEncryptPassword(user.getPassword()));
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
            statement.setString(2, getEncryptPassword(user.getPassword()));

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) == 1;
            }

        } catch (SQLException e) {
            throw new DaoException("Check user password failed", e);
        }
    }

    @Override
    public long getIdByName(String username) {
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

    @Override
    public List<Long> getCharactersId(long userId) {
        try(Connection connection = ConnectionGetter.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_GET_CHARACTERS_ID_QUERY)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Long> charactersId = new ArrayList<>();
                while(resultSet.next()) {
                    charactersId.add(resultSet.getLong("id"));
                }
                return charactersId;
            }

        } catch (SQLException e) {
            throw new DaoException("Get characters id failed", e);
        }
    }

    private String getEncryptPassword(String password) {

        Key aesKey = new SecretKeySpec(encryptKey.getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedPassword = cipher.doFinal(password.getBytes());

            return new String(encryptedPassword);

        } catch (NoSuchPaddingException e) {
            throw new ManagerException("Internal error", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ManagerException("Internal error", e);
        } catch (InvalidKeyException e) {
            throw new ManagerException("Internal error", e);
        } catch (BadPaddingException e) {
            throw new ManagerException("Internal error", e);
        } catch (IllegalBlockSizeException e) {
            throw new ManagerException("Internal error", e);
        }
    }
}
