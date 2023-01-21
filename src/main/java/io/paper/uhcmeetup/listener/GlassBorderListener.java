package io.paper.uhcmeetup.listener;

import io.paper.uhcmeetup.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class GlassBorderListener
        implements Listener {
    private Game game = Game.getInstance();
    private Map<Player, List<Location>> players = new WeakHashMap<Player, List<Location>>();
    private byte color = (byte)14;

    private static boolean isInBetween(int xone, int xother, int mid) {
        int distance = Math.abs(xone - xother);
        return distance == Math.abs(mid - xone) + Math.abs(mid - xother);
    }

    private static int closestNumber(int from, int ... numbers) {
        int distance = Math.abs(numbers[0] - from);
        int idx = 0;
        for (int c = 1; c < numbers.length; ++c) {
            int cdistance = Math.abs(numbers[c] - from);
            if (cdistance >= distance) continue;
            idx = c;
            distance = cdistance;
        }
        return numbers[idx];
    }

    @EventHandler
    public void handlePlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (Math.abs(to.getBlockX()) > this.game.getGameManager().getBorderSize() || Math.abs(to.getBlockZ()) > this.game.getGameManager().getBorderSize()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can't pearl to outside of the border.");
                return;
            }
            this.handlePlayerMovement(event);
        }
    }

    @EventHandler
    public void handlePlayerMovement(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (!from.getWorld().getName().equalsIgnoreCase("uhc_meetup")) {
            return;
        }
        if (from.getWorld().getName().equalsIgnoreCase("uhc_meetup") && (from.getBlockX() != to.getBlockX() || to.getBlockZ() != from.getBlockZ())) {
            this.placeGlass(event.getPlayer(), to, -this.game.getGameManager().getBorderSize() - 1, this.game.getGameManager().getBorderSize(), -this.game.getGameManager().getBorderSize() - 1, this.game.getGameManager().getBorderSize());
        }
    }

    private boolean placeGlass(Player player, Location to, int minX, int maxX, int minZ, int maxZ) {
        Location location;
        int x;
        int y;
        boolean updateZ;
        int closerx = GlassBorderListener.closestNumber(to.getBlockX(), minX, maxX);
        int closerz = GlassBorderListener.closestNumber(to.getBlockZ(), minZ, maxZ);
        boolean updateX = Math.abs(to.getX() - (double)closerx) < 15.0;
        boolean bl = updateZ = Math.abs(to.getZ() - (double)closerz) < 15.0;
        if (!updateX && !updateZ) {
            this.removeGlass(player);
            return false;
        }
        ArrayList<Location> toUpdate = new ArrayList<Location>();
        if (updateX) {
            for (y = -3; y < 7; ++y) {
                for (x = -5; x < 5; ++x) {
                    if (!GlassBorderListener.isInBetween(minZ, maxZ, to.getBlockZ() + x) || toUpdate.contains(location = new Location(to.getWorld(), closerx, to.getBlockY() + y, to.getBlockZ() + x)) || location.getBlock().getType().isOccluding()) continue;
                    toUpdate.add(location);
                }
            }
        }
        if (updateZ) {
            for (y = -3; y < 7; ++y) {
                for (x = -5; x < 5; ++x) {
                    if (!GlassBorderListener.isInBetween(minX, maxX, to.getBlockX() + x) || toUpdate.contains(location = new Location(to.getWorld(), to.getBlockX() + x, to.getBlockY() + y, closerz)) || location.getBlock().getType().isOccluding()) continue;
                    toUpdate.add(location);
                }
            }
        }
        this.updateGlass(player, toUpdate);
        return !toUpdate.isEmpty();
    }

    public void removeGlass(Player player) {
        if (this.players.containsKey(player)) {
            for (Location location : this.players.get(player)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            this.players.remove(player);
        }
    }

    public void updateGlass(Player player, List<Location> toUpdate) {
        if (this.players.containsKey(player)) {
            for (Location location : this.players.get(player)) {
                Block block = location.getBlock();
                player.sendBlockChange(location, block.getTypeId(), block.getData());
            }
            for (Location location2 : toUpdate) {
                player.sendBlockChange(location2, 95, this.color);
            }
            this.players.put(player, toUpdate);
        } else {
            for (Location location2 : toUpdate) {
                player.sendBlockChange(location2, 95, this.color);
            }
            this.players.put(player, toUpdate);
        }
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equalsIgnoreCase("uhc_meetup")) {
            return;
        }
        if (!this.players.containsKey(player)) {
            return;
        }
        this.updateGlass(player, this.players.get(player));
    }
}
