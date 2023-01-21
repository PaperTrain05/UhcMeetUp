package io.paper.uhcmeetup.listener;

import io.paper.uhcmeetup.Game;
import me.uhc.worldborder.Events.WorldBorderFillFinishedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChunkListener implements Listener {
    private Game game = Game.getInstance();

    @EventHandler
    public void handleChunkLoadingEvent(WorldBorderFillFinishedEvent event) {
        this.game.setPreparing(false);
    }
}
