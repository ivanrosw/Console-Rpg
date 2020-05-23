package local.managers;

import local.managers.exceptions.ManagerException;
import local.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationManager {

    private User user;

    public void getLoginMenu() {
        boolean login = false;
        AuthorizationManager authorizationManager = new AuthorizationManager();

        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            while (!login) {
                System.out.println("1: Login");
                System.out.println("2: Register");
                System.out.println("Enter a number to choose option or \"E\" to exit");

                String userAnswer = consoleReader.readLine();
                System.out.println();

                if (userAnswer.equals("E")) {
                    System.exit(0);

                } else if(userAnswer.equals("1")) {
                    if(authorizationManager.login()) {
                        user = authorizationManager.getUser();
                        login = true;
                    }

                } else if (userAnswer.equals("2")) {
                    if(authorizationManager.register()) {
                        user = authorizationManager.getUser();
                        login = true;
                    }

                } else {
                    System.out.println("Entered wrong symbol");
                }
            }

            System.out.println("Welcome " + user.getName());

        } catch (IOException e) {
            throw new ManagerException("Internal error", e);
        }
    }
}
