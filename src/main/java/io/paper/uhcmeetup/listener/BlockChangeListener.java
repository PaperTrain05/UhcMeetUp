package io.paper.uhcmeetup.listener;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockChangeListener implements Listener {
    private Game game = Game.getInstance();

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (!(this.game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else if (event.getBlock().getType() == Material.GRASS || event.getBlock().getType() == Material.STONE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (!(this.game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        }
    }
}
