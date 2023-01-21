package io.paper.uhcmeetup.tasks;

import io.paper.uhcmeetup.Game;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeTask
{
    private Game game;
    private int taskID;
    private int uptimeMinutes;
    private int uptimeSeconds;
    private int borderMinutes;
    private int firstShrink;
    private boolean running;

    public TimeTask() {
        this.game = Game.getInstance();
        this.firstShrink = 2;
        this.running = false;
    }

    private int getNextBorder() {
        if (this.game.getGameManager().getBorderSize() > 100) {
            return 100;
        }
        if (this.game.getGameManager().getBorderSize() == 100) {
            return 75;
        }
        if (this.game.getGameManager().getBorderSize() == 75) {
            return 50;
        }
        if (this.game.getGameManager().getBorderSize() == 50) {
            return 25;
        }
        return 0;
    }

    public void startTask() {
        this.running = true;
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.game, (BukkitRunnable)new BukkitRunnable() {
            public void run() {
                TimeTask.this.uptimeSeconds++;
                if (TimeTask.this.uptimeSeconds == 60) {
                    TimeTask.this.uptimeSeconds = 0;
                    ++TimeTask.this.uptimeMinutes;
                    ++TimeTask.this.borderMinutes;
                }
                if (TimeTask.this.game.getGameManager().getBorderSize() > 25) {
                    if (TimeTask.this.borderMinutes == TimeTask.this.firstShrink - 1 || TimeTask.this.borderMinutes == TimeTask.this.firstShrink || TimeTask.this.borderMinutes == TimeTask.this.firstShrink + 1 || TimeTask.this.borderMinutes == TimeTask.this.firstShrink + 2) {
                        switch (TimeTask.this.uptimeSeconds) {
                            case 30: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 30 secondi");
                                break;
                            }
                            case 40: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 20 secondi");
                                break;
                            }
                            case 50: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 10 secondi");
                                break;
                            }
                            case 51: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 9 secondi");
                                break;
                            }
                            case 52: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 8 secondi");
                                break;
                            }
                            case 53: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 7 secondi");
                                break;
                            }
                            case 54: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 6 secondi");
                                break;
                            }
                            case 55: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 5 secondi");
                                break;
                            }
                            case 56: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 4 secondi");
                                break;
                            }
                            case 57: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 3 secondi");
                                break;
                            }
                            case 58: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 2 secondi");
                                break;
                            }
                            case 59: {
                                TimeTask.this.game.getGameManager().playSound();
                                Bukkit.broadcastMessage(TimeTask.this.game.getPrefix() + TimeTask.this.game.getsColor() + "Il confine sta per restringersi " + TimeTask.this.getNextBorder() + "x" + TimeTask.this.getNextBorder() + " blocchi in 1 secondo");
                                break;
                            }
                        }
                    }
                    if ((TimeTask.this.borderMinutes == TimeTask.this.firstShrink || TimeTask.this.borderMinutes == TimeTask.this.firstShrink + 1 || TimeTask.this.borderMinutes == TimeTask.this.firstShrink + 2 || TimeTask.this.borderMinutes == TimeTask.this.firstShrink + 3) && TimeTask.this.uptimeSeconds == 0) {
                        TimeTask.this.game.getWorldManager().createTotalShrink();
                    }
                }
            }
        }, 0L, 20L);
    }

    public void stopTask() {
        this.running = false;
        Bukkit.getScheduler().cancelTask(this.taskID);
    }

    public String getFormattedTime() {
        String formattedTime = "";
        if (this.uptimeMinutes < 10) {
            formattedTime += "0";
        }
        formattedTime = formattedTime + this.uptimeMinutes + ":";
        if (this.uptimeSeconds < 10) {
            formattedTime += "0";
        }
        formattedTime += this.uptimeSeconds;
        return formattedTime;
    }

    public int getBorderSize() {
        return this.firstShrink;
    }

    public int getFirstShrink() {
        return this.firstShrink;
    }

    public boolean isRunning() {
        return this.running;
    }
}
