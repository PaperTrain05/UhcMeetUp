package io.paper.uhcmeetup.gamestate.states;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.gamestate.GameState;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingState extends GameState {
    private Game game;

    public EndingState() {
        this.game = Game.getInstance();
    }

    @Override
    public void start() {
        new BukkitRunnable() {
            public void run() {
                EndingState.this.game.getTimeTask().stopTask();
            }
        }.runTaskLater((Plugin)this.game, 20L);
    }

    @Override
    public void stop() {
    }
}
