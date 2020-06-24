package local.consolerpg.managers;

import local.consolerpg.database.dao.DaoFactory;
import local.consolerpg.database.dao.UserDao;
import local.consolerpg.database.exceptions.DaoException;
import local.consolerpg.database.exceptions.DatabaseException;
import local.consolerpg.database.exceptions.PasswordEncryptionException;
import local.consolerpg.managers.exceptions.AuthorizationException;
import local.consolerpg.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

class AuthorizationManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationManager.class);

    private UserDao userDao = DaoFactory.getUserDao();

    User login(BufferedReader consoleReader) {
        logger.debug("Start logging in");
        try {
            System.out.println("Enter username to login or \"E\" to cancel authorization");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return null;
            }

            logger.debug("Checking userName: {} existence in database", userAnswer);
            if (!userDao.isExistsUsername(userAnswer)) {
                System.out.println("User with username \"" + userAnswer + "\" not found");
                logger.info("User {} not found", userAnswer);
                System.out.println();
                return null;
            }

            String username = userAnswer;

            while (true) {
                System.out.println("Enter password or \"E\" to cancel authorization");
                userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    return null;
                }

                String password = userAnswer;
                User user = new User(username, password);

                logger.debug("Checking password to userName: {}", username);
                if (userDao.isCorrectUserPassword(user)) {
                    user.setId(userDao.getIdByName(user.getName()));
                    System.out.println("Login successful");
                    System.out.println();
                    logger.info("Logging userName: {} in complete", user.getName());
                    return user;
                } else {
                    System.out.println("Entered wrong password");
                    logger.info("Entered wrong password to userName: {}", username);
                    System.out.println();
                }
            }

        } catch (IOException e) {
            logger.error("Error with consoleReader", e);
            throw new AuthorizationException("Internal error", e);
        } catch (DaoException e) {
            logger.warn("Logging in failed", e);
            throw new AuthorizationException("Logging in failed", e);
        } catch (DatabaseException e) {
            logger.warn("Logging in failed", e);
            throw new AuthorizationException("Connection failed", e);
        } catch (PasswordEncryptionException e) {
            logger.error("Problem with encryption password", e);
            throw new AuthorizationException("Problem with encryption password", e);
        }
    }

    User register(BufferedReader consoleReader) {
        logger.debug("Start registration");
        try {
            System.out.println("Enter username to register or \"E\" to cancel registration");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return null;
            }

            logger.debug("Checking userName: {} existence in database", userAnswer);
            if (userDao.isExistsUsername(userAnswer)) {
                System.out.println("User with username \"" + userAnswer + "\" already exist");
                logger.info("User {} already exist", userAnswer);
                System.out.println();
                return null;
            }

            String username = userAnswer;

            System.out.println("Enter password or \"E\" to cancel registration");
            userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return null;
            }

            String password = userAnswer;
            User user = new User(username, password);

            userDao.add(user);
            System.out.println("Registration successful");
            System.out.println();
            logger.info("Registration userName: {} complete", user.getName());
            return user;

        } catch (IOException e) {
            logger.error("ConsoleReader error", e);
            throw new AuthorizationException("Internal error", e);
        } catch (DaoException e) {
            logger.warn("Registration failed", e);
            throw new AuthorizationException("Registration failed", e);
        } catch (DatabaseException e) {
            logger.warn("Registration failed", e);
            throw new AuthorizationException("Connection failed", e);
        } catch (PasswordEncryptionException e) {
            logger.error("Problem with encryption password", e);
            throw new AuthorizationException("Problem with encryption password", e);
        }
    }
}
