package io.paper.uhcmeetup.listener.scenarios;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import io.paper.uhcmeetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeBombListener implements Listener {
    private Game game;
    private String prefix;
    private String mColor;
    private String sColor;

    public TimeBombListener() {
        this.game = Game.getInstance();
        this.prefix = this.game.getPrefix();
        this.mColor = this.game.getmColor();
        this.sColor = this.game.getsColor();
    }

    @EventHandler
    public void handlePlayerDeathEvent(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Location location = player.getLocation();
        final Inventory inventory = (Inventory)player.getInventory();
        if (!Scenarios.TimeBomb.isEnabled()) {
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemHandler(Material.GOLDEN_APPLE).setDisplayName(ChatColor.GOLD + "Golden Head").build());
        }
        else if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            event.getDrops().clear();
            player.getLocation().getBlock().breakNaturally();
            player.getLocation().getBlock().setType(Material.CHEST);
            player.getLocation().add(1.0, 0.0, 0.0).getBlock().breakNaturally();
            player.getLocation().add(1.0, 0.0, 0.0).getBlock().setType(Material.CHEST);
            player.getLocation().add(0.0, 1.0, 0.0).getBlock().setType(Material.AIR);
            player.getLocation().add(1.0, 1.0, 0.0).getBlock().setType(Material.AIR);
            final Chest chest = (Chest)player.getLocation().getBlock().getState();
            chest.getInventory().setContents(inventory.getContents());
            chest.getInventory().addItem(player.getInventory().getArmorContents());
            chest.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.GOLDEN_APPLE).setDisplayName(ChatColor.GOLD + "Golden Head").build() });
            new BukkitRunnable() {
                public void run() {
                    location.getWorld().createExplosion(location, 6.0f);
                    location.getWorld().strikeLightning(location);
                    Bukkit.broadcastMessage(TimeBombListener.this.prefix + TimeBombListener.this.mColor + "[TimeBomb] " + player.getName() + "'s " + TimeBombListener.this.sColor + "il cadavere è esploso!");
                }
            }.runTaskLater((Plugin)this.game, 600L);
        }
    }
}
