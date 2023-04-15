package io.paper.uhcmeetup.commands;

import io.paper.uhcmeetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StatsCommand implements CommandExecutor, Listener {
    private Game game = Game.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("stats") && sender instanceof Player) {
            Player player = (Player)sender;
            if (this.game.isDatabaseActive()) {
                if (args.length == 1) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                    if (this.game.getDatabaseManager().isPlayerRegistered(target)) {
                        this.game.getInventoryHandler().handleStatsInventory(player, target);
                    } else {
                        player.sendMessage(this.game.getPrefix() + ChatColor.RED + target.getName() + " non e stato registrato nel database!");
                    }
                } else if (args.length == 0) {
                    this.game.getInventoryHandler().handleStatsInventory(player, player);
                } else {
                    player.sendMessage(ChatColor.RED + "Usa: /stats (player)");
                }
            } else {
                player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Stats sono disattivate");
            }
        }
        return false;
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getClickedInventory() != null && event.getInventory().getName().contains(this.game.getsColor() + "Stats") && (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE || event.getCurrentItem().getType() == Material.IRON_SWORD || event.getCurrentItem().getType() == Material.FIREBALL || event.getCurrentItem().getType() == Material.NETHER_STAR)) {
            event.setCancelled(true);
        }
    }
}
