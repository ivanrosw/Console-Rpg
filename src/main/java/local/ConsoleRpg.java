package local;

import local.managers.ApplicationManager;

public class ConsoleRpg {

    public static void main(String[] args) {
        ApplicationManager applicationManager = new ApplicationManager();
        applicationManager.getLoginMenu();
    }
}
