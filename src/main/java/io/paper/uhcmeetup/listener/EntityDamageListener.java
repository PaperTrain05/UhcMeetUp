package io.paper.uhcmeetup.listener;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.PlayerState;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import io.paper.uhcmeetup.handler.ItemHandler;
import net.minecraft.server.v1_8_R1.PacketPlayOutRespawn;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class EntityDamageListener implements Listener {
    private Game game = Game.getInstance();

    @EventHandler
    public void handleEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (this.game.getSpectators().contains(player)) {
                event.setCancelled(true);
            }
            if (!(this.game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
                event.setCancelled(true);
            } else if (!this.game.getTimeTask().isRunning()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacker = (Player)event.getDamager();
            if (this.game.getSpectators().contains(attacker)) {
                event.setCancelled(true);
            }
        }
        if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState && event.getEntity() instanceof Player && event.getDamager() instanceof Arrow && ((Arrow)((Object)event.getDamager())).getShooter() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (((Player)event.getEntity()).getHealth() - event.getFinalDamage() > 0.0) {
                ((Player)((Object)((Arrow)((Object)event.getDamager())).getShooter())).sendMessage(this.game.getPrefix() + this.game.getmColor() + player.getName() + "'s" + this.game.getsColor() + " cuori a " + ChatColor.RED + Math.round(player.getHealth() - 1.0) + "❤!");
            }
        }
    }

    @EventHandler
    public void handlePlayerDeathEvent(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (player.getKiller() != null) {
            this.game.getPlayerKills().put(player.getKiller().getUniqueId(), this.game.getPlayerKills().get(player.getKiller().getUniqueId()) + 1);
            if (this.game.isDatabaseActive()) {
                this.game.getDatabaseManager().addKills(player.getKiller(), 1);
            }
            Player killer = player.getKiller();
            event.setDeathMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + "[" + this.game.getPlayerKills().get(player.getUniqueId()) + "] " + ChatColor.YELLOW + "è stato ucciso da " + ChatColor.RED + killer.getName() + ChatColor.GRAY + "[" + this.game.getPlayerKills().get(killer.getUniqueId()) + "]");
            player.getKiller().getWorld().dropItemNaturally(player.getLocation(), new ItemHandler(Material.EXP_BOTTLE).setAmount(32).build());
            player.getKiller().getWorld().dropItemNaturally(player.getLocation(), new ItemHandler(Material.GOLDEN_APPLE).setDisplayName(ChatColor.GOLD + "Golden Head").build());
        } else {
            event.setDeathMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + "[" + this.game.getPlayerKills().get(player.getUniqueId()) + "] " + ChatColor.YELLOW + "è morto misteriosamente");
        }
        new BukkitRunnable(){

            public void run() {
                player.spigot().respawn();
                PacketPlayOutRespawn packet = new PacketPlayOutRespawn();
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                EntityDamageListener.this.game.getGameManager().setPlayerState(player, PlayerState.SPECTATOR);
                EntityDamageListener.this.game.getGameManager().checkWinner();
                if (EntityDamageListener.this.game.isDatabaseActive()) {
                    EntityDamageListener.this.game.getDatabaseManager().addDeaths(player, 1);
                }
            }
        }.runTask(this.game);
    }

    @EventHandler
    public void handleFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        Player player = (Player)((Object)event.getEntity());
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (!(this.game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else if (event.getFoodLevel() < player.getFoodLevel() && new Random().nextInt(100) > 4) {
            event.setCancelled(true);
        }
    }
}

