package io.paper.uhcmeetup.manager;


import io.paper.uhcmeetup.gamestate.GameState;
import io.paper.uhcmeetup.gamestate.states.EndingState;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import io.paper.uhcmeetup.gamestate.states.LobbyState;

public class GameStateManager {
    private GameState[] gameStates;
    private GameState currentGameState;

    public GameStateManager() {
        (this.gameStates = new GameState[3])[0] = new LobbyState();
        this.gameStates[1] = new IngameState();
        this.gameStates[2] = new EndingState();
    }

    public void setGameState(final int gameStateIndex) {
        if (this.currentGameState != null) {
            this.currentGameState.stop();
        }
        (this.currentGameState = this.gameStates[gameStateIndex]).start();
    }

    public void stopCurrentGameState() {
        this.currentGameState.stop();
        this.currentGameState = null;
    }

    public GameState getCurrentGameState() {
        return this.currentGameState;
    }
}

