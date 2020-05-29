package local.consolerpg;

import local.consolerpg.managers.ApplicationManager;

public class ConsoleRpg {

    public static void main(String[] args) {
        ApplicationManager applicationManager = new ApplicationManager();
        applicationManager.getLoginMenu();
    }
}
