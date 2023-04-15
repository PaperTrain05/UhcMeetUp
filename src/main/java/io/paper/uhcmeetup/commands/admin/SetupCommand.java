package io.paper.uhcmeetup.commands.admin;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.gamestate.states.LobbyState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {
    private Game game = Game.getInstance();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setspawn") && sender instanceof Player) {
            Player player = (Player)sender;
                 if(this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    this.game.getLocationManager().setLocation("Lobby-Spawn", player.getLocation());
                    player.sendMessage(this.game.getPrefix() + ChatColor.GREEN + "La lobby spawn e stato settato");
                } else {
                player.sendMessage(ChatColor.RED + "Usa: /setspawn");
            }
        }
        return false;
    }
}

