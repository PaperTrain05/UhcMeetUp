package io.paper.uhcmeetup.board;

import io.paper.uhcmeetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SimpleBoardManager extends BukkitRunnable implements Listener
{
    private Game game;
    private Map<UUID, SimpleBoard> boards;
    private BoardProvider boardProvider;

    public SimpleBoardManager(final Game game, final BoardProvider boardProvider) {
        this.game = game;
        this.boards = new HashMap<UUID, SimpleBoard>();
        this.boardProvider = boardProvider;
        this.runTaskTimerAsynchronously((Plugin)game, 10L, 10L);
    }

    public BoardProvider getBoardProvider() {
        return this.boardProvider;
    }

    public void setBoardProvider(final BoardProvider boardProvider) {
        this.boardProvider = boardProvider;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        SimpleBoard simpleBoard = this.boards.get(player.getUniqueId());
        if (simpleBoard == null) {
            simpleBoard = new SimpleBoard(event.getPlayer());
            this.boards.put(player.getUniqueId(), simpleBoard);
        }
        simpleBoard.updateTitle(this.boardProvider.getTitle(player));
        simpleBoard.show();
    }

    @EventHandler
    public void onPlayerQuiteEvent(final PlayerQuitEvent event) {
        this.boards.remove(event.getPlayer().getUniqueId());
    }

    public void run() {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getScoreboard().getObjectives().stream().noneMatch(objective -> objective.getName().equals("list"))) {
                final Objective healthList = player.getScoreboard().registerNewObjective("list", "health");
                healthList.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            }
            if (player.getScoreboard().getObjectives().stream().noneMatch(objective -> objective.getName().equals("name"))) {
                final Objective healthName = player.getScoreboard().registerNewObjective("name", "health");
                healthName.setDisplaySlot(DisplaySlot.BELOW_NAME);
                healthName.setDisplayName(ChatColor.DARK_RED + "❤");
            }
            final SimpleBoard simpleBoard = this.boards.get(player.getUniqueId());
            if (simpleBoard == null) {
                return;
            }
            final List<String> list = this.boardProvider.getBoardLines(player);
            simpleBoard.update(list);
            simpleBoard.updateTitle(this.boardProvider.getTitle(player));
        }
    }
}

