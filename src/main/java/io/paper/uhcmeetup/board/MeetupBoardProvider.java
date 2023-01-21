package io.paper.uhcmeetup.board;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.LobbyState;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MeetupBoardProvider implements BoardProvider {
    private Game game;
    private String scoreboardTitle;

    public MeetupBoardProvider(final Game game) {
        this.game = Game.getInstance();
        this.scoreboardTitle = this.game.getScoreboardConfig().getString("SCOREBOARDS.TITLE");
        this.game = game;
    }

    @Override
    public String getTitle(final Player player) {
        return this.scoreboardTitle.replace("&", "§");
    }

    @Override
    public List<String> getBoardLines(final Player player) {
        final List<String> lines = new ArrayList<String>();
        final Date date = new Date();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final FileConfiguration config = this.game.getScoreboardConfig();
        if (this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            for (String string : config.getStringList("SCOREBOARDS.LOBBY.LINES")) {
                string = string.replace("%players%", String.valueOf(this.game.getPlayers().size())).replace("%maximumPlayers%", String.valueOf(Bukkit.getMaxPlayers())).replace("%defaultVotes%", String.valueOf(Scenarios.Default.getVotes())).replace("%timebombVotes%", String.valueOf(Scenarios.TimeBomb.getVotes())).replace("%nocleanVotes%", String.valueOf(Scenarios.NoClean.getVotes())).replace("%firelessVotes%", String.valueOf(Scenarios.Fireless.getVotes())).replace("%bowlessVotes%", String.valueOf(Scenarios.Bowless.getVotes())).replace("%rodlessVotes%", String.valueOf(Scenarios.Rodless.getVotes())).replace("%soupVotes%", String.valueOf(Scenarios.Soup.getVotes())).replace("%spectators%", "" + this.game.getSpectators().size());
                lines.add(string.replace("&", "§").replace("%spectators%", String.valueOf(this.game.getSpectators().size())));
            }
        }
        else {
            for (String string : config.getStringList("SCOREBOARDS.INGAME.LINES")) {
                string = string.replace("%players%", String.valueOf(this.game.getPlayers().size())).replace("%gameTime%", this.game.getTimeTask().getFormattedTime()).replace("%kills%", String.valueOf(this.game.getPlayerKills().get(player.getUniqueId()))).replace("%borderSize%", String.valueOf(this.game.getGameManager().getBorderSize()).replace("%startedWith%", String.valueOf(this.game.getStartedWith()).replace("%spectators%", "" + this.game.getSpectators().size())));
                lines.add(string.replace("&", "§").replace("%spectators%", String.valueOf(this.game.getSpectators().size())).replace("%startedWith%", String.valueOf(this.game.getStartedWith())));
            }
        }
        return lines;
    }
}
