package io.paper.uhcmeetup.listener;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.PlayerState;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.EndingState;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import io.paper.uhcmeetup.gamestate.states.LobbyState;
import io.paper.uhcmeetup.handler.ItemHandler;
import org.bukkit.event.Listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class ConnectionListener
        implements Listener {
    private Game game = Game.getInstance();
    private int minimalPlayers = this.game.getConfig().getInt("GAME.MIN-PLAYERS");

    @EventHandler
    public void handlePlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        if (this.game.isDatabaseActive()) {
            this.game.getDatabaseManager().registerPlayer(player);
        }
        this.game.getPlayerKills().putIfAbsent(player.getUniqueId(), 0);
        if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            this.game.getGameManager().setPlayerState(player, PlayerState.PLAYER);
            event.setJoinMessage(this.game.getPrefix() + ChatColor.GREEN + player.getName() + " è entrato in game " + ChatColor.GRAY + "(" + this.game.getPlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
            try {
                player.teleport(this.game.getLocationManager().getLocation("Lobby-Spawn"));
            }
            catch (Exception exception) {
                player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Lobby-Spawn non settato");
            }
            this.game.getGameManager().resetPlayer(player);
            if (this.game.getPlayers().size() >= this.minimalPlayers && !this.game.getStartingTask().isRunning()) {
                this.game.getStartingTask().startTask();
            }
            player.getInventory().setItem(4, new ItemHandler(Material.PAPER).setDisplayName(this.game.getmColor() + "§lScenario Voting").build());
            player.sendMessage(this.game.getPrefix() + this.game.getsColor() + "Use " + this.game.getmColor() + "/vote " + this.game.getsColor() + "per votare lo scenario del voto");
        } else if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            this.game.getGameManager().setPlayerState(player, PlayerState.SPECTATOR);
            event.setJoinMessage("");
        }
    }

    @EventHandler
    public void handlePlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.game.getPlayers().remove(player);
        this.game.getSpectators().remove(player);
        if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            event.setQuitMessage(this.game.getPrefix() + ChatColor.RED + player.getName() + " è uscito dal game " + ChatColor.GRAY + "(" + this.game.getPlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
            if (this.game.getVoted().containsKey(player.getUniqueId())) {
                Scenarios votedScenario = this.game.getVoted().get(player.getUniqueId());
                votedScenario.removeVote();
                this.game.getVoted().remove(player.getUniqueId());
            }
            if (this.game.getPlayers().size() < this.minimalPlayers && this.game.getStartingTask().isRunning()) {
                this.game.getStartingTask().stopTask();
            }
        } else if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            event.setQuitMessage("");
            if (this.game.getLoggedPlayers().contains(player.getUniqueId())) {
                player.damage(24.0);
                this.game.getLoggedPlayers().remove(player.getUniqueId());
            }
            this.game.getGameManager().checkWinner();
        }
    }

    @EventHandler
    public void handlePlayerLoginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (this.game.getGameStateManager().getCurrentGameState() instanceof EndingState) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Il gioco sta già per finire");
        } else if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
            if (!player.hasPermission("meetup.staff")) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Il gioco sta già per iniziare");
            }
        } else if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState && this.game.isPreparing()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Il game e già in preparazione");
        }
    }
}
