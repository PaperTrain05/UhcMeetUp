package io.paper.uhcmeetup.manager;

import io.paper.uhcmeetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LocationManager {
    private Game game;
    private File file;
    private FileConfiguration config;

    public LocationManager() {
        this.game = Game.getInstance();
        this.file = new File("plugins/" + this.game.getDescription().getName() + "/locations.yml");
        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
    }

    private void saveConfig() {
        try {
            this.config.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLocation(final String name, final Location location) {
        this.config.set(name + ".world", (Object)location.getWorld().getName());
        this.config.set(name + ".x", (Object)location.getX());
        this.config.set(name + ".y", (Object)location.getY());
        this.config.set(name + ".z", (Object)location.getZ());
        this.config.set(name + ".yaw", (Object)location.getYaw());
        this.config.set(name + ".pitch", (Object)location.getPitch());
        this.saveConfig();
        if (!this.file.exists()) {
            try {
                this.file.mkdir();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Location getLocation(final String name) {
        final World world = Bukkit.getWorld(this.config.getString(name + ".world"));
        final double x = this.config.getDouble(name + ".x");
        final double y = this.config.getDouble(name + ".y");
        final double z = this.config.getDouble(name + ".z");
        final Location location = new Location(world, x, y, z);
        location.setYaw((float)this.config.getInt(name + ".yaw"));
        location.setPitch((float)this.config.getInt(name + ".pitch"));
        return location;
    }
}
