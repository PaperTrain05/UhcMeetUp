package io.paper.uhcmeetup.board;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SimpleBoard
{
    private Player player;
    private Scoreboard scoreboard;
    private Objective objective;
    private List<String> oldLines;
    private boolean hidden;

    public SimpleBoard(final Player player) {
        this.player = player;
        this.scoreboard = player.getScoreboard();
        if (this.scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        this.objective = this.scoreboard.getObjective("silent");
        if (this.objective == null) {
            this.objective = this.scoreboard.registerNewObjective("silent", "dummy");
        }
        IntStream.range(0, 16).forEach(i -> {
            if (this.scoreboard.getTeam("team-" + this.getTeamName(i)) == null) {
                this.scoreboard.registerNewTeam("team-" + this.getTeamName(i));
            }
            return;
        });
        this.oldLines = new ArrayList<String>();
        this.player.setScoreboard(this.scoreboard);
    }

    public void updateTitle(final String value) {
        this.objective.setDisplayName(value);
    }

    public void updateLine(final int lineNumber, final String value) {
        final String[] prefixAndSuffix = this.getPrefixAndSuffix(value);
        Team team = this.scoreboard.getTeam("team-" + this.getTeamName(lineNumber));
        if (team == null) {
            team = this.scoreboard.registerNewTeam("team-" + this.getTeamName(lineNumber));
        }
        team.setPrefix(prefixAndSuffix[0]);
        team.setSuffix(prefixAndSuffix[1]);
        if (!team.getEntries().contains(this.getTeamName(lineNumber))) {
            team.addEntry(this.getTeamName(lineNumber));
        }
        this.objective.getScore(this.getTeamName(lineNumber)).setScore(lineNumber);
    }

    public void hide() {
        this.hidden = true;
        this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    public void show() {
        this.hidden = false;
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void update(final List<String> list) {
        if (this.hidden || list == null || this.player == null || list.size() == 0) {
            return;
        }
        if (list.size() != this.oldLines.size()) {
            for (int i = 0; i < this.oldLines.size() + 1; ++i) {
                this.removeLine(i);
            }
        }
        while (list.size() > 15) {
            list.remove(list.size() - 1);
        }
        int score = list.size();
        for (final String value : list) {
            this.updateLine(score--, value);
        }
        this.oldLines = list;
    }

    public void removeLine(final int lineNumber) {
        this.scoreboard.resetScores(this.getTeamName(lineNumber));
    }

    private String[] getPrefixAndSuffix(final String value) {
        final String prefix = this.getPrefix(value);
        final String suffix = this.getPrefix(ChatColor.getLastColors(prefix) + this.getSuffix(value));
        return new String[] { prefix, suffix };
    }

    private String getPrefix(final String value) {
        return (value.length() > 16) ? value.substring(0, 16) : value;
    }

    private String getSuffix(String value) {
        value = ((value.length() > 32) ? value.substring(0, 32) : value);
        return (value.length() > 16) ? value.substring(16) : "";
    }

    private String getTeamName(final int i) {
        return ChatColor.values()[i].toString();
    }
}