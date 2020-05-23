package local.managers;

import local.database.dao.DaoFactory;
import local.database.dao.UserDao;
import local.database.exceptions.DaoException;
import local.managers.exceptions.ManagerException;
import local.models.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

class AuthorizationManager {

    private User user;

    private static final String encryptKey = "J@NcRfUjXn2r5u8x";

    User getUser() {
        return user;
    }

    boolean login() {

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {

            String username;
            String password;

            System.out.println("Enter username to login or \"E\" to cancel authorization");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return false;
            }

            UserDao userDao = DaoFactory.getUserDao();
            if (!userDao.isExistsUsername(userAnswer)) {
                System.out.println("User with username \"" + userAnswer + "\" not found");
                System.out.println();
                return false;
            }

            username = userAnswer;

            while(true) {
                System.out.println("Enter password or \"E\" to cancel authorization");
                userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    return false;
                }

                password = encryptPassword(userAnswer);
                user = new User(username, password);

                if(userDao.isCorrectUserPassword(user)) {
                    user.setId(userDao.getUserIdByName(user.getName()));
                    System.out.println("Login successful");
                    System.out.println();
                    return true;
                } else {
                    System.out.println("Entered wrong password");
                    System.out.println();
                }
            }

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in DAO layer", e);
        }
    }

    boolean register() {

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {

            String username;
            String password;

            System.out.println("Enter username to register or \"E\" to cancel registration");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return false;
            }

            UserDao userDao = DaoFactory.getUserDao();
            if (userDao.isExistsUsername(userAnswer)) {
                System.out.println("User with username \"" + userAnswer + "\" already exist");
                System.out.println();
                return false;
            }

            username = userAnswer;

            System.out.println("Enter password or \"E\" to cancel registration");
            userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return false;
            }

            password = encryptPassword(userAnswer);
            user = new User(username, password);

            userDao.add(user);
            System.out.println("Registration successful");
            System.out.println();
            return true;

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in DAO layer", e);
        }
    }

    private String encryptPassword(String password) {

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
