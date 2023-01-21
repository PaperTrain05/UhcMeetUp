package io.paper.uhcmeetup.tasks;

import io.paper.uhcmeetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask {
    private Game game;
    private int taskID;
    private boolean running;
    private int startingTime;
    private int resetTime;

    public StartingTask() {
        this.game = Game.getInstance();
        this.startingTime = 60;
        this.resetTime = 60;
    }

    public void startTask() {
        this.running = true;
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.game, (BukkitRunnable)new BukkitRunnable() {
            public void run() {
                switch (StartingTask.this.startingTime) {
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                    case 20:
                    case 30:
                    case 60: {
                        Bukkit.broadcastMessage(StartingTask.this.game.getPrefix() + ChatColor.GREEN + "La dispersione inizia " + StartingTask.this.startingTime + " secondi");
                        StartingTask.this.game.getGameManager().playSound();
                        break;
                    }
                    case 1: {
                        Bukkit.broadcastMessage(StartingTask.this.game.getPrefix() + ChatColor.GREEN + "La dispersione inizia " + StartingTask.this.startingTime + " secondi");
                        StartingTask.this.game.getGameManager().playSound();
                        break;
                    }
                    case 0: {
                        StartingTask.this.game.getGameStateManager().setGameState(1);
                        StartingTask.this.game.getGameManager().playSound();
                        Bukkit.getScheduler().cancelTask(StartingTask.this.taskID);
                        break;
                    }
                }
                StartingTask.this.startingTime--;
            }
        }, 0L, 20L);
    }

    public void stopTask() {
        Bukkit.getScheduler().cancelTask(this.taskID);
        this.startingTime = this.resetTime;
        this.running = false;
        Bukkit.broadcastMessage(this.game.getPrefix() + ChatColor.RED + "L'inizio scatter è stato annullato per mancanza di giocatori.");
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setStartingTime(final int startingTime) {
        this.startingTime = startingTime;
    }

    public int getStartingTime() {
        return this.startingTime;
    }
}
