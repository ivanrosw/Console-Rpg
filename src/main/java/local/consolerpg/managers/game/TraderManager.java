package local.consolerpg.managers.game;

import local.consolerpg.models.game.GameCharacter;

import java.io.BufferedReader;

public class TraderManager {

    private BufferedReader consoleReader;
    private GameCharacter gameCharacter;

    public TraderManager(BufferedReader consoleReader, GameCharacter gameCharacter) {
        this.consoleReader = consoleReader;
        this.gameCharacter = gameCharacter;
    }

    public void getTraderMenu() {

    }
}
