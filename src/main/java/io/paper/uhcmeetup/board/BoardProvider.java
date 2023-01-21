package io.paper.uhcmeetup.board;

import org.bukkit.entity.Player;

import java.util.List;

public interface BoardProvider {
    String getTitle(final Player p0);

    List<String> getBoardLines(final Player p0);
}
