package local.services;

import local.database.dao.DaoFactory;
import local.database.dao.UserDao;
import local.database.exceptions.DaoException;
import local.services.exceptions.ManagerException;
import local.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class AuthorizationManager {

    User login() {

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter username to login or \"E\" to cancel authorization");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return null;
            }

            UserDao userDao = DaoFactory.getUserDao();
            if (!userDao.isExistsUsername(userAnswer)) {
                System.out.println("User with username \"" + userAnswer + "\" not found");
                System.out.println();
                return null;
            }

            String username = userAnswer;

            while(true) {
                System.out.println("Enter password or \"E\" to cancel authorization");
                userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    return null;
                }

                String password = userAnswer;
                User user = new User(username, password);

                if(userDao.isCorrectUserPassword(user)) {
                    user.setId(userDao.getIdByName(user.getName()));
                    user.setCharactersId(userDao.getCharactersId(user.getId()));
                    System.out.println("Login successful");
                    System.out.println();
                    return user;
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

    User register() {

        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter username to register or \"E\" to cancel registration");
            String userAnswer = consoleReader.readLine();
            System.out.println();

            if (userAnswer.equals("E")) {
                return null;
            }

            UserDao userDao = DaoFactory.getUserDao();
            if (userDao.isExistsUsername(userAnswer)) {
                System.out.println("User with username \"" + userAnswer + "\" already exist");
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
            return user;

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        } catch (DaoException e) {
            throw new ManagerException("Exception in DAO layer", e);
        }
    }
}
