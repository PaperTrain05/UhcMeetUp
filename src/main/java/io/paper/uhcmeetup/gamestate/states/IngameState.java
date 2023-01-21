package io.paper.uhcmeetup.gamestate.states;

import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.GameState;
import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class IngameState extends GameState {
    private Game game;

    public IngameState() {
        this.game = Game.getInstance();
    }

    @Override
    public void start() {
        for (final Player allPlayers : Bukkit.getOnlinePlayers()) {
            allPlayers.getInventory().clear();
            if (this.game.getPlayers().contains(allPlayers)) {
                allPlayers.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 127));
                allPlayers.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, -5));
                this.game.getLoggedPlayers().add(allPlayers.getUniqueId());
            }
        }
        this.game.getGameManager().scatterPlayers();
        Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getsColor() + "Tutti i giocatori sono stati dispersi");
        Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getsColor() + "Il gioco inizia tra " + this.game.getmColor() + "10 " + this.game.getsColor() + "secondi");
        for (final Player allPlayers : this.game.getPlayers()) {
            this.game.getGameManager().equipPlayerRandomly(allPlayers);
        }
        this.game.setStartedWith(this.game.getPlayers().size());
        new BukkitRunnable() {
            public void run() {
                IngameState.this.game.getTimeTask().startTask();
                Bukkit.broadcastMessage(IngameState.this.game.getPrefix() + ChatColor.GREEN + "Game iniziato buona fortuna!");
                IngameState.this.game.getGameManager().activateScenarios();
                if (Scenarios.Soup.isEnabled()) {
                    for (final Player allPlayers : IngameState.this.game.getPlayers()) {
                        allPlayers.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.BROWN_MUSHROOM).setAmount(32).build() });
                        allPlayers.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.RED_MUSHROOM).setAmount(32).build() });
                        allPlayers.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.BOWL).setAmount(32).build() });
                    }
                }
                if (IngameState.this.game.getGameManager().getWonScenarios().contains(Scenarios.Bowless)) {
                    for (final Player allPlayers : IngameState.this.game.getPlayers()) {
                        allPlayers.getInventory().remove(Material.BOW);
                    }
                }
                else if (IngameState.this.game.getGameManager().getWonScenarios().contains(Scenarios.Rodless)) {
                    for (final Player allPlayers : IngameState.this.game.getPlayers()) {
                        allPlayers.getInventory().remove(Material.FISHING_ROD);
                    }
                }
                Bukkit.broadcastMessage(IngameState.this.game.getPrefix() + IngameState.this.game.getsColor() + "Il confine sta per restringersi " + IngameState.this.game.getmColor() + IngameState.this.getNextBorder() + " blocchi" + IngameState.this.game.getsColor() + " in " + IngameState.this.game.getmColor() + IngameState.this.game.getTimeTask().getFirstShrink() + " minuti");
            }
        }.runTaskLater((Plugin)this.game, 200L);
    }

    private int getNextBorder() {
        if (this.game.getGameManager().getBorderSize() > 100) {
            return 100;
        }
        if (this.game.getGameManager().getBorderSize() == 100) {
            return 75;
        }
        return 0;
    }

    @Override
    public void stop() {
    }
}
