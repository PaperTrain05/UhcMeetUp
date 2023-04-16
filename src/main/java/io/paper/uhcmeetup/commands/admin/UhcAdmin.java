package io.paper.uhcmeetup.commands.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UhcAdmin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){

            Player p = (Player) sender;
            if(p.hasPermission("uhcmeetup.admin")){
                p.sendMessage("§7UhcMeetup by PaperTrain");
                p.sendMessage("");
                p.sendMessage("§cAdmin commands:");
                p.sendMessage("§e/setspawn §7- Setta lo lobby del server per lo spawn dei player");
                p.sendMessage("§e/forcestart §7- Starta forzatamente il game diminuendo il tempo a 10 secondi");
                p.sendMessage("§e/gmc §7- Attiva la gamemode creative");
                p.sendMessage("§e/gms §7- Attiva la gamemode survival");
                p.sendMessage("");
            }
            else{
                p.sendMessage("§cNon hai accesso a questo comando");
            }

        }else {
            sender.sendMessage("§cNon puoi utilizzare questo comando nella console");
        }
        return true;
    }
}
