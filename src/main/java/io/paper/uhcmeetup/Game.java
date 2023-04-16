package io.paper.uhcmeetup;

import io.paper.uhcmeetup.board.MeetupBoardProvider;
import io.paper.uhcmeetup.board.SimpleBoardManager;
import io.paper.uhcmeetup.commands.admin.ForceStartCommand;
import io.paper.uhcmeetup.commands.admin.SetupCommand;
import io.paper.uhcmeetup.commands.StatsCommand;
import io.paper.uhcmeetup.commands.VoteCommand;
import io.paper.uhcmeetup.commands.admin.UhcAdmin;
import io.paper.uhcmeetup.commands.admin.gamemdoes.Gmc;
import io.paper.uhcmeetup.commands.admin.gamemdoes.Gms;
import io.paper.uhcmeetup.enums.PlayerState;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.handler.FileHandler;
import io.paper.uhcmeetup.handler.InventoryHandler;
import io.paper.uhcmeetup.listener.*;
import io.paper.uhcmeetup.listener.scenarios.FirelessListener;
import io.paper.uhcmeetup.listener.scenarios.NoCleanListener;
import io.paper.uhcmeetup.listener.scenarios.SoupListener;
import io.paper.uhcmeetup.listener.scenarios.TimeBombListener;
import io.paper.uhcmeetup.manager.*;
import io.paper.uhcmeetup.tasks.StartingTask;
import io.paper.uhcmeetup.tasks.TimeTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Game extends JavaPlugin {
    public static Game instance;
    private String prefix;
    private String mColor;
    private String sColor;
    private GameStateManager gameStateManager;
    private LocationManager locationManager;
    private GameManager gameManager;
    private WorldManager worldManager;
    private DatabaseManager databaseManager;
    private SimpleBoardManager simpleBoardManager;
    private FileHandler fileHandler;
    private InventoryHandler inventoryHandler;
    private StartingTask startingTask;
    private TimeTask timeTask;
    private ArrayList<Player> players;
    private ArrayList<Player> spectators;
    private ArrayList<UUID> loggedPlayers;
    private HashMap<UUID, Scenarios> hasVoted;
    private HashMap<Player, PlayerState> playerState;
    private HashMap<UUID, Integer> playerKills;
    private boolean preparing;
    private boolean databaseActive;
    private int startedWith;
    private File scoreboardFile;
    private FileConfiguration scoreboardConfig;

    public void onEnable() {
        this.createConfigFile();
        Game.instance = this;
        this.prefix = this.getConfig().getString("SETTINGS.PREFIX").replace("&", "§");
        this.mColor = this.getConfig().getString("SETTINGS.MAIN-COLOR").replace("&", "§");
        this.sColor = this.getConfig().getString("SETTINGS.SECONDARY-COLOR").replace("&", "§");
        this.scoreboardFile = new File(this.getDataFolder(), "scoreboards.yml");
        this.scoreboardConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(this.scoreboardFile);
        if (!this.scoreboardFile.exists()) {
            this.saveResource("scoreboards.yml", false);
        }
        this.gameStateManager = new GameStateManager();
        this.locationManager = new LocationManager();
        this.gameManager = new GameManager();
        this.worldManager = new WorldManager();
        this.databaseManager = new DatabaseManager();
        this.fileHandler = new FileHandler();
        this.inventoryHandler = new InventoryHandler();
        this.startingTask = new StartingTask();
        this.timeTask = new TimeTask();
        this.players = new ArrayList<Player>();
        this.spectators = new ArrayList<Player>();
        this.loggedPlayers = new ArrayList<UUID>();
        this.hasVoted = new HashMap<UUID, Scenarios>();
        this.playerState = new HashMap<Player, PlayerState>();
        this.playerKills = new HashMap<UUID, Integer>();
        this.preparing = true;
        this.databaseActive = this.getConfig().getBoolean("MYSQL.ENABLED");
        this.startedWith = 0;
        if (this.databaseActive) {
            try {
                this.databaseManager.connectToDatabase();
                this.getLogger().info("[MySQL] Connection to database succeeded!");
            }
            catch (ClassNotFoundException | SQLException ex2) {
                final Exception ex;
                this.getLogger().info("[MySQL] Connection to database failed!");
            }
            try {
                this.databaseManager.createTable();
            }
            catch (SQLException e2) {
                e2.printStackTrace();
                this.getLogger().info("[MySQL] Table creation succeeded!");
            }
        }
        this.gameStateManager.setGameState(0);
        this.init(Bukkit.getPluginManager());
        this.worldManager.deleteWorld("uhc_meetup");
        this.worldManager.createWorld("uhc_meetup", World.Environment.NORMAL, WorldType.NORMAL);
        this.worldManager.createBorderLayer("uhc_meetup", this.getConfig().getInt("GAME.MAP-RADIUS"), 4, null);
        Bukkit.getScheduler().runTaskLater((Plugin)this, (BukkitRunnable)new BukkitRunnable() {
            public void run() {
                Game.this.worldManager.loadWorld("uhc_meetup", Game.this.getConfig().getInt("GAME.MAP-RADIUS"), 1000);
            }
        }, 20L);
    }

    public void onDisable() {
        this.players.clear();
        this.spectators.clear();
        if (this.databaseActive) {
            try {
                this.databaseManager.disconnectFromDatabase();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createConfigFile() {
        final FileConfiguration config = this.getConfig();
        config.addDefault("SETTINGS.PREFIX", (Object)"&7[&6Meetup&7] ");
        config.addDefault("SETTINGS.MAIN-COLOR", (Object)"&6");
        config.addDefault("SETTINGS.SECONDARY-COLOR", (Object)"&f");
        config.addDefault("GAME.MIN-PLAYERS", (Object)2);
        config.addDefault("GAME.MAP-RADIUS", (Object)100);
        config.addDefault("MYSQL.ENABLED", (Object)false);
        config.addDefault("MYSQL.HOST", (Object)"localhost");
        config.addDefault("MYSQL.USERNAME", (Object)"root");
        config.addDefault("MYSQL.PASSWORD", (Object)"password");
        config.addDefault("MYSQL.DATABASE", (Object)"meetup");
        config.addDefault("MYSQL.PORT", (Object)3306);
        config.options().copyDefaults(true);
        this.saveConfig();
    }

    private void init(final PluginManager pluginManager) {
        this.getCommand("setspawn").setExecutor((CommandExecutor)new SetupCommand());
        this.getCommand("vote").setExecutor((CommandExecutor)new VoteCommand());
        this.getCommand("stats").setExecutor((CommandExecutor)new StatsCommand());
        this.getCommand("forcestart").setExecutor((CommandExecutor)new ForceStartCommand());
        this.getCommand("gmc").setExecutor((CommandExecutor)new Gmc());
        this.getCommand("gms").setExecutor((CommandExecutor)new Gms());
        this.getCommand("uhcmeetup").setExecutor((CommandExecutor)new UhcAdmin());
        pluginManager.registerEvents((Listener)new ConnectionListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new ChunkListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new WeatherChangeListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new BlockChangeListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new EntityDamageListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new PlayerInteractListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)(this.simpleBoardManager = new SimpleBoardManager(this, new MeetupBoardProvider(this))), (Plugin)this);
        pluginManager.registerEvents((Listener)new GlassBorderListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new FirelessListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new NoCleanListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new SoupListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new TimeBombListener(), (Plugin)this);
        pluginManager.registerEvents((Listener)new VoteCommand(), (Plugin)this);
        pluginManager.registerEvents((Listener)new StatsCommand(), (Plugin)this);
    }

    public void setDatabaseActive(final boolean databaseActive) {
        this.databaseActive = databaseActive;
    }

    public void setPreparing(final boolean preparing) {
        this.preparing = preparing;
    }

    public static Game getInstance() {
        return Game.instance;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getmColor() {
        return this.mColor;
    }

    public String getsColor() {
        return this.sColor;
    }

    public GameStateManager getGameStateManager() {
        return this.gameStateManager;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }

    public HashMap<Player, PlayerState> getPlayerState() {
        return this.playerState;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public ArrayList<Player> getSpectators() {
        return this.spectators;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    public boolean isPreparing() {
        return this.preparing;
    }

    public InventoryHandler getInventoryHandler() {
        return this.inventoryHandler;
    }

    public HashMap<UUID, Scenarios> getVoted() {
        return this.hasVoted;
    }

    public StartingTask getStartingTask() {
        return this.startingTask;
    }

    public TimeTask getTimeTask() {
        return this.timeTask;
    }

    public HashMap<UUID, Integer> getPlayerKills() {
        return this.playerKills;
    }

    public DatabaseManager getDatabaseManager() {
        return this.databaseManager;
    }

    public boolean isDatabaseActive() {
        return this.databaseActive;
    }

    public File getScoreboardFile() {
        return this.scoreboardFile;
    }

    public FileConfiguration getScoreboardConfig() {
        return this.scoreboardConfig;
    }

    public void setStartedWith(final int startedWith) {
        this.startedWith = startedWith;
    }

    public int getStartedWith() {
        return this.startedWith;
    }

    public SimpleBoardManager getSimpleBoardManager() {
        return this.simpleBoardManager;
    }

    public ArrayList<UUID> getLoggedPlayers() {
        return this.loggedPlayers;
    }
}
