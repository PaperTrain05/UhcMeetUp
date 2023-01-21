package io.paper.uhcmeetup.listener.scenarios;

import io.paper.uhcmeetup.enums.Scenarios;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;


public class SoupListener implements Listener {
    @EventHandler
    public void handlePlayerInteractEvent(final PlayerInteractEvent event) {
        if (Scenarios.Soup.isEnabled() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP && event.getPlayer().getHealth() < event.getPlayer().getMaxHealth()) {
            if (event.getPlayer().getHealth() <= 13.0) {
                event.getPlayer().setHealth(event.getPlayer().getHealth() + 7.0);
                event.getPlayer().getItemInHand().setType(Material.BOWL);
                event.getPlayer().getItemInHand().setItemMeta((ItemMeta) null);
            } else {
                event.getPlayer().setHealth(20.0);
                event.getPlayer().getItemInHand().setType(Material.BOWL);
                event.getPlayer().getItemInHand().setItemMeta((ItemMeta) null);
            }
        }
    }
}
