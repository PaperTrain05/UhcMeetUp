package io.paper.uhcmeetup.handler;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.Scenarios;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryHandler {
    private Game game = Game.getInstance();

    private void fillEmptySlots(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); ++slot) {
            if (inventory.getItem(slot) != null) continue;
            inventory.setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE));
        }
    }

    public void handleVotingInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Scenario Voting");
        inventory.setItem(1, new ItemHandler(Scenarios.Default.getScenarioItem()).setDisplayName(this.game.getmColor() + "No Gamemode").setLore("§7§m------------", ChatColor.GRAY + "Voti: " + Scenarios.Default.getVotes(), "§7§m------------").build());
        inventory.setItem(2, new ItemHandler(Scenarios.Rodless.getScenarioItem()).setDisplayName(this.game.getmColor() + "Rodless").setLore("§7§m------------", ChatColor.GRAY + "Voti: " + Scenarios.Rodless.getVotes(), "§7§m------------").build());
        inventory.setItem(3, new ItemHandler(Scenarios.Bowless.getScenarioItem()).setDisplayName(this.game.getmColor() + "Bowless").setLore("§7§m------------", ChatColor.GRAY + "Voti " + Scenarios.Bowless.getVotes(), "§7§m------------").build());
        inventory.setItem(4, new ItemHandler(Scenarios.NoClean.getScenarioItem()).setDisplayName(this.game.getmColor() + "NoClean").setLore("§7§m------------", ChatColor.GRAY + "Voti: " + Scenarios.NoClean.getVotes(), "§7§m------------").build());
        inventory.setItem(5, new ItemHandler(Scenarios.Fireless.getScenarioItem()).setDisplayName(this.game.getmColor() + "Fireless").setLore("§7§m------------", ChatColor.GRAY + "Voti: " + Scenarios.Fireless.getVotes(), "§7§m------------").build());
        inventory.setItem(6, new ItemHandler(Scenarios.TimeBomb.getScenarioItem()).setDisplayName(this.game.getmColor() + "TimeBomb").setLore("§7§m------------", ChatColor.GRAY + "Voti: " + Scenarios.TimeBomb.getVotes(), "§7§m------------").build());
        inventory.setItem(7, new ItemHandler(Scenarios.Soup.getScenarioItem()).setDisplayName(this.game.getmColor() + "Soup").setLore("§7§m------------", ChatColor.GRAY + "Votes: " + Scenarios.Soup.getVotes(), "§7§m------------").build());
        this.fillEmptySlots(inventory);
        player.openInventory(inventory);
    }

    public void handleStatsInventory(Player player, OfflinePlayer toWatch) {
        Inventory inventory = Bukkit.createInventory(null, 9, this.game.getsColor() + "Stats: " + this.game.getmColor() + toWatch.getName());
        inventory.setItem(2, new ItemHandler(Material.IRON_SWORD).setDisplayName(this.game.getsColor() + "Ucciosioni: " + this.game.getmColor() + this.game.getDatabaseManager().getKills(toWatch)).build());
        inventory.setItem(4, new ItemHandler(Material.FIREBALL).setDisplayName(this.game.getsColor() + "Morti: " + this.game.getmColor() + this.game.getDatabaseManager().getDeaths(toWatch)).build());
        inventory.setItem(6, new ItemHandler(Material.NETHER_STAR).setDisplayName(this.game.getsColor() + "Vittorie: " + this.game.getmColor() + this.game.getDatabaseManager().getWins(toWatch)).build());
        this.fillEmptySlots(inventory);
        player.openInventory(inventory);
    }

    public void handleStaffInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        inventory.setItem(0, new ItemHandler(Material.WATCH).setDisplayName(this.game.getmColor() + "Player").build());
        inventory.setItem(1, new ItemHandler(Material.BEACON).setDisplayName(this.game.getmColor() + "Random Player").build());
        inventory.setItem(4, new ItemHandler(Material.NETHER_STAR).setDisplayName(this.game.getmColor() + "Teleportati al centro").build());
        inventory.setItem(8, new ItemHandler(Material.BOOK).setDisplayName(this.game.getmColor() + "Ispezionare l'inventario").build());
    }

    public void handlePlayersInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, this.game.getmColor() + "Players Vivi");
        for (Player allPlayers : this.game.getPlayers()) {
            ItemStack playerStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta playersMeta = (SkullMeta)playerStack.getItemMeta();
            playersMeta.setOwner(allPlayers.getName());
            playersMeta.setDisplayName(allPlayers.getName());
            playerStack.setItemMeta(playersMeta);
            inventory.addItem(new ItemStack[]{playerStack});
        }
        player.openInventory(inventory);
    }
}
