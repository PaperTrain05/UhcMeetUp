package io.paper.uhcmeetup.listener.scenarios;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import org.bukkit.*;
import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import java.util.*;

public class NoCleanListener implements Listener {
    private Game game;
    private String prefix;
    private ArrayList<UUID> noCleanPlayers;

    public NoCleanListener() {
        this.game = Game.getInstance();
        this.prefix = this.game.getPrefix();
        this.noCleanPlayers = new ArrayList<UUID>();
    }

    @EventHandler
    public void handlePlayerDeathEvent(final PlayerDeathEvent event) {
        final Player death = event.getEntity();
        final Player killer = death.getKiller();
        if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState && Scenarios.NoClean.isEnabled() && killer != null) {
            this.noCleanPlayers.add(killer.getUniqueId());
            killer.sendMessage(this.prefix + ChatColor.GREEN + "[NoClean] Ora sei protetto da qualsiasi danno per 20 secondi");
            new BukkitRunnable() {
                public void run() {
                    if (NoCleanListener.this.noCleanPlayers.contains(killer.getUniqueId())) {
                        NoCleanListener.this.noCleanPlayers.remove(killer.getUniqueId());
                        killer.sendMessage(NoCleanListener.this.prefix + ChatColor.RED + "[NoClean] Non sei più protetto da eventuali danni");
                    }
                }
            }.runTaskLater((Plugin)this.game, 400L);
        }
    }

    @EventHandler
    public void handleEntityDamageEvent(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player damaged = (Player)event.getEntity();
            if (this.noCleanPlayers.contains(damaged.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleEntityDamageByEntityEvent(final EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            final Player damager = (Player)event.getDamager();
            final Player damaged = (Player)event.getEntity();
            if (this.noCleanPlayers.contains(damaged.getUniqueId())) {
                event.setCancelled(true);
                damager.sendMessage(this.prefix + ChatColor.RED + "[NoClean] Questo giocatore è protetto da qualsiasi danno!");
            }
            if (this.noCleanPlayers.contains(damager.getUniqueId())) {
                event.setCancelled(true);
                this.noCleanPlayers.remove(damager.getUniqueId());
                damager.sendMessage(this.prefix + ChatColor.RED + "[NoClean] Non sei più protetto da eventuali danni!");
            }
        }
    }
}
