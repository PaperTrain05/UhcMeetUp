package io.paper.uhcmeetup.commands.admin.gamemdoes;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gms implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(p.hasPermission("uhcmeetup.admin.gamemdoes")){
            if(args.length == 0){
                p.sendMessage("§fGms attivata");
                p.setGameMode(GameMode.SURVIVAL);
            }
        }else {
            p.sendMessage("§cNon hai permesso per utilizzare questo comando");
        }
        return true;
    }
}