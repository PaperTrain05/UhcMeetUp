package io.paper.uhcmeetup.manager;

import io.paper.uhcmeetup.Game;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.LinkedList;
import java.util.function.Consumer;

public class WorldManager {
    private Game game;

    public WorldManager() {
        this.game = Game.getInstance();
    }

    public void createWorld(final String worldName, final World.Environment environment, final WorldType worldType) {
        final World world = Bukkit.createWorld(new WorldCreator(worldName).environment(environment).type(worldType));
        world.setDifficulty(Difficulty.EASY);
        world.setTime(0L);
        world.setThundering(false);
        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
    }

    public void loadWorld(final String worldName, final int worldRadius, final int loadingSpeed) {
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "wb shape square");
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "wb " + worldName + " set " + worldRadius + " " + worldRadius + " 0 0");
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "wb " + worldName + " fill " + loadingSpeed);
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "wb " + worldName + " fill confirm");
    }

    public void shrinkBorder(final String worldName, final int size) {
        Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "wb " + worldName + " set " + size + " " + size + " 0 0");
    }

    public void createTotalShrink() {
        if (this.game.getGameManager().getBorderSize() > 100) {
            this.game.getGameManager().setBorderSize(100);
            this.shrinkBorder("uhc_meetup", 100);
            this.createBorderLayer("uhc_meetup", 100, 4, null);
            Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getsColor() + "Il confine si restringerà " + this.game.getmColor() + 1 + " minuto");
        }
        else if (this.game.getGameManager().getBorderSize() == 100) {
            this.game.getGameManager().setBorderSize(75);
            this.shrinkBorder("uhc_meetup", 75);
            this.createBorderLayer("uhc_meetup", 75, 4, null);
            Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getsColor() + "Il confine si restringerà " + this.game.getmColor() + 1 + " minuto");
        }
        else if (this.game.getGameManager().getBorderSize() == 75) {
            this.game.getGameManager().setBorderSize(50);
            this.shrinkBorder("uhc_meetup", 50);
            this.createBorderLayer("uhc_meetup", 50, 4, null);
            Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getsColor() + "Il confine si restringerà " + this.game.getmColor() + 1 + " minuto");
        }
        else if (this.game.getGameManager().getBorderSize() == 50) {
            this.game.getGameManager().setBorderSize(25);
            this.shrinkBorder("uhc_meetup", 25);
            this.createBorderLayer("uhc_meetup", 25, 4, null);
        }
        Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getsColor() + "Il confine si è ridotto a " + this.game.getmColor() + this.game.getGameManager().getBorderSize() + " blocchi");
        this.game.getGameManager().playSound();
    }

    public void createBorderLayer(final String borderWorld, final int radius, final int amount, final Consumer<String> done) {
        final World world = Bukkit.getWorld(borderWorld);
        if (world == null) {
            return;
        }
        final LinkedList<Location> locations = new LinkedList<Location>();
        for (int i = 0; i < amount; ++i) {
            for (int z = -radius; z <= radius; ++z) {
                final Location location = new Location(world, (double)radius, (double)(world.getHighestBlockYAt(radius, z) + i), (double)z);
                locations.add(location);
            }
            for (int z = -radius; z <= radius; ++z) {
                final Location location = new Location(world, (double)(-radius), (double)(world.getHighestBlockYAt(-radius, z) + i), (double)z);
                locations.add(location);
            }
            for (int x = -radius; x <= radius; ++x) {
                final Location location = new Location(world, (double)x, (double)(world.getHighestBlockYAt(x, radius) + i), (double)radius);
                locations.add(location);
            }
            for (int x = -radius; x <= radius; ++x) {
                final Location location = new Location(world, (double)x, (double)(world.getHighestBlockYAt(x, -radius) + i), (double)(-radius));
                locations.add(location);
            }
        }
        new BukkitRunnable() {
            private int max = 50;

            public void run() {
                for (int i = 0; i < this.max; ++i) {
                    if (locations.isEmpty()) {
                        if (done != null) {
                            done.accept("done");
                        }
                        this.cancel();
                        break;
                    }
                    locations.remove().getBlock().setType(Material.BEDROCK);
                }
            }
        }.runTaskTimer((Plugin)this.game, 0L, 1L);
    }

    public void unloadWorld(final World worldName) {
        if (worldName != null) {
            Bukkit.unloadWorld(worldName, false);
        }
    }

    public void deleteWorld(final String worldName) {
        this.unloadWorld(Bukkit.getWorld(worldName));
        this.deleteFiles(new File(Bukkit.getWorldContainer(), worldName));
    }

    private boolean deleteFiles(final File path) {
        if (path.exists()) {
            final File[] listFiles;
            final File[] files = listFiles = path.listFiles();
            for (final File file : listFiles) {
                if (file.isDirectory()) {
                    this.deleteFiles(file);
                }
                else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }
}
