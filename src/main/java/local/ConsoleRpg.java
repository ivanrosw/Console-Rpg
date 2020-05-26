package local;

import local.services.ApplicationManager;

public class ConsoleRpg {

    public static void main(String[] args) {
        ApplicationManager applicationManager = new ApplicationManager();
        applicationManager.getLoginMenu();
    }
}
