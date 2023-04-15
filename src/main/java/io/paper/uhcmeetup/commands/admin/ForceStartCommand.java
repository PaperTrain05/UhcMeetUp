package io.paper.uhcmeetup.commands.admin;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.gamestate.states.LobbyState;
import org.bukkit.command.CommandExecutor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceStartCommand
        implements CommandExecutor {
    private Game game = Game.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("forcestart") && sender instanceof Player) {
            Player player = (Player)sender;
            if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                if (this.game.getStartingTask().isRunning()) {
                    if (this.game.getStartingTask().getStartingTime() > 20) {
                        this.game.getStartingTask().setStartingTime(10);
                        player.sendMessage(this.game.getPrefix() + ChatColor.GREEN + "Hai startato il game forzatamente");
                    } else {
                        player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Il game sta gia startando");
                    }
                } else {
                    player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Non ci sono tanti player per startare forzatamente");
                }
            } else {
                player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Il gioco e gia iniziato!");
            }
        }
        return false;
    }
}

