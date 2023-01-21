package io.paper.uhcmeetup.commands;

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
        if (cmd.getName().equalsIgnoreCase("setup") && sender instanceof Player) {
            Player player = (Player) ((Object) sender);
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("setspawn") && this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    this.game.getLocationManager().setLocation("Lobby-Spawn", player.getLocation());
                    player.sendMessage(this.game.getPrefix() + ChatColor.GREEN + "La lobby spawn e stato settato");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usa: /setup (setspawn)");
            }
        }
        return false;
    }
}

