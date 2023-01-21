package io.paper.uhcmeetup.commands;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.LobbyState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VoteCommand implements CommandExecutor, Listener {
    private Game game = Game.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("vote") && sender instanceof Player) {
            Player player = (Player)((Object)sender);
            if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                this.game.getInventoryHandler().handleVotingInventory(player);
            } else {
                player.sendMessage(this.game.getPrefix() + ChatColor.RED + "The game has already started!");
            }
        }
        return false;
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player)((Object)event.getWhoClicked());
        if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState && event.getClickedInventory() != null && event.getCurrentItem() != null && event.getInventory().getName().contains("Scenario Voting")) {
            event.setCancelled(true);
            if (!this.game.getVoted().containsKey(player.getUniqueId())) {
                if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE || event.getCurrentItem().getType() == Material.PAPER) {
                    return;
                }
                for (Scenarios scenarios : Scenarios.values()) {
                    if (event.getCurrentItem().getType() != scenarios.getScenarioItem()) continue;
                    event.setCancelled(true);
                    Scenarios votedScenario = scenarios;
                    this.game.getVoted().put(player.getUniqueId(), votedScenario);
                    votedScenario.addVote();
                }
                player.closeInventory();
                player.sendMessage(this.game.getPrefix() + ChatColor.GRAY + "Hai votato per: " + ChatColor.YELLOW + (Object)((Object)this.game.getVoted().get(player.getUniqueId())) + " " + ChatColor.GRAY + "(Voti totali: " + this.game.getVoted().get(player.getUniqueId()).getVotes() + ")");
            } else {
                player.closeInventory();
                player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Hai gia votato per: " + ChatColor.YELLOW + (Object)((Object)this.game.getVoted().get(player.getUniqueId())));
            }
        }
    }
}
