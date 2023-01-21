package io.paper.uhcmeetup.gamestate;

public abstract class GameState {
    public static final int LOBBY = 0;
    public static final int INGAME = 1;
    public static final int ENDING = 2;

    public abstract void start();

    public abstract void stop();
}
