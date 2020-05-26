package local.services;

import local.services.exceptions.ManagerException;
import local.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationManager {

    private User user;

    public void getLoginMenu() {
        AuthorizationManager authorizationManager = new AuthorizationManager();

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            while (user == null) {
                System.out.println("1: Login");
                System.out.println("2: Register");
                System.out.println("Enter a number to choose option or \"E\" to exit");

                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    System.exit(0);

                } else if(userAnswer.equals("1")) {
                    user = authorizationManager.login();

                } else if (userAnswer.equals("2")) {
                    user = authorizationManager.register();

                } else {
                    System.out.println("Entered wrong symbol");
                }
            }

            System.out.println("Welcome " + user.getName());

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        }
    }

    public void getMainMenu() {

    }
}
