package local.consolerpg.managers;

import local.consolerpg.database.exceptions.PasswordEncryptionException;
import local.consolerpg.managers.exceptions.AuthorizationException;
import local.consolerpg.managers.exceptions.ManagerException;
import local.consolerpg.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationManager {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationManager.class);

    private User user;

    public void getLoginMenu() {
        AuthorizationManager authorizationManager = new AuthorizationManager();

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            while (user == null) {
                try {
                    System.out.println("1: Login");
                    System.out.println("2: Register");
                    System.out.println("Enter a number to choose option or \"E\" to exit");

                    String userAnswer = consoleReader.readLine();
                    System.out.println();

                    if (userAnswer.equals("E")) {
                        System.exit(0);

                    } else if (userAnswer.equals("1")) {
                        user = authorizationManager.login();

                    } else if (userAnswer.equals("2")) {
                        user = authorizationManager.register();

                    } else {
                        System.out.println("Entered wrong symbol");
                    }

                } catch (AuthorizationException e) {
                    System.out.println("Some problem with logging in. Try again later \nError:");
                    System.out.println(e.getMessage());
                    logger.warn("Authorization userName: {} failed", user.getName());

                } catch (PasswordEncryptionException e) {
                    System.out.println("Error in program. Contact with technical support");
                    logger.error("Problem with encryption password", e);
                }
            }

            System.out.println("Welcome " + user.getName());

        } catch (IOException e) {
            logger.error("ConsoleReader error", e);
            throw new ManagerException("Internal error", e);
        }
    }

    public void getMainMenu() {
        if (user == null) {
            throw new ManagerException("User is not logged in");
        }


    }
}
