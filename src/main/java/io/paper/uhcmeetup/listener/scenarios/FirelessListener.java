package io.paper.uhcmeetup.listener.scenarios;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class FirelessListener implements Listener
{
    private Game game;

    public FirelessListener() {
        this.game = Game.getInstance();
    }

    @EventHandler
    public void handleEntityDamageEvent(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && this.game.getGameStateManager().getCurrentGameState() instanceof IngameState && Scenarios.Fireless.isEnabled() && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            event.setCancelled(true);
        }
    }
}

