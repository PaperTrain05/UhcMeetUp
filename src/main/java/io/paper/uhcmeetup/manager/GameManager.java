package io.paper.uhcmeetup.manager;


import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.enums.PlayerState;
import io.paper.uhcmeetup.enums.Scenarios;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import io.paper.uhcmeetup.handler.ItemHandler;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private Game game;
    public ArrayList<Scenarios> wonScenarios;
    private int borderSize;

    public GameManager() {
        this.game = Game.getInstance();
        this.wonScenarios = new ArrayList<Scenarios>();
        this.borderSize = this.game.getConfig().getInt("GAME.MAP-RADIUS");
    }

    public void setBorderSize(final int borderSize) {
        this.borderSize = borderSize;
    }

    public void resetPlayer(final Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents((ItemStack[])null);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.setExp(0.0f);
    }

    public void playSound() {
        for (final Player allPlayers : Bukkit.getOnlinePlayers()) {
            allPlayers.playSound(allPlayers.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
        }
    }

    public void checkWinner() {
        if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState && this.game.getPlayers().size() == 1) {
            this.game.getGameStateManager().setGameState(2);
            for (final Player allPlayers : this.game.getPlayers()) {
                Bukkit.broadcastMessage(this.game.getPrefix() + this.game.getmColor() + "Congratulazioni a " + allPlayers.getName() + " per aver vinto il game");
                if (this.game.isDatabaseActive()) {
                    this.game.getDatabaseManager().addWins(allPlayers, 1);
                }
                new BukkitRunnable() {
                    public void run() {
                        allPlayers.getWorld().spawn(allPlayers.getLocation(), (Class) Firework.class);
                    }
                }.runTaskTimer((Plugin)this.game, 0L, 20L);
            }
            Bukkit.broadcastMessage(this.game.getPrefix() + ChatColor.RED + "Il server si restarta tra pochi secondi.");
            new BukkitRunnable() {
                public void run() {
                    Bukkit.shutdown();
                }
            }.runTaskLater((Plugin)this.game, 200L);
        }
    }

    public void setPlayerState(final Player player, final PlayerState playerState) {
        this.game.getPlayerState().put(player, playerState);
        if (playerState == PlayerState.PLAYER) {
            this.game.getPlayers().add(player);
            this.game.getSpectators().remove(player);
            player.setGameMode(GameMode.SURVIVAL);
            for (final Player allPlayers : Bukkit.getOnlinePlayers()) {
                allPlayers.showPlayer(player);
            }
        }
        else if (playerState == PlayerState.SPECTATOR) {
            this.game.getSpectators().add(player);
            this.game.getPlayers().remove(player);
            player.teleport(new Location(Bukkit.getWorld("uhc_meetup"), 0.0, 100.0, 0.0));
            this.resetPlayer(player);
            player.setGameMode(GameMode.CREATIVE);
            for (final Player allPlayers : Bukkit.getOnlinePlayers()) {
                allPlayers.hidePlayer(player);
            }
            if (player.hasPermission("meetup.staff")) {
                this.game.getInventoryHandler().handleStaffInventory(player);
            }
            else {
                player.getInventory().setItem(0, new ItemHandler(Material.WATCH).setDisplayName(this.game.getmColor() + "Players").build());
                player.getInventory().setItem(1, new ItemHandler(Material.BEACON).setDisplayName(this.game.getmColor() + "Random Player").build());
            }
            player.sendMessage(this.game.getPrefix() + ChatColor.YELLOW + "Stai spectatndo il game!");
        }
    }

    public void scatterPlayers() {
        for (final Player allPlayers : this.game.getPlayers()) {
            final int x = new Random().nextInt(this.game.getConfig().getInt("GAME.MAP-RADIUS") - 1);
            final int z = new Random().nextInt(this.game.getConfig().getInt("GAME.MAP-RADIUS") - 1);
            final int y = Bukkit.getWorld("uhc_meetup").getHighestBlockYAt(x, z) + 2;
            final Location teleportLocation = new Location(Bukkit.getWorld("uhc_meetup"), (double)x, (double)y, (double)z);
            allPlayers.teleport(teleportLocation);
        }
    }

    public void activateScenarios() {
        final List<Integer> noScenario = new ArrayList<Integer>();
        final List<Integer> bowlessScenario = new ArrayList<Integer>();
        final List<Integer> noCleanScenario = new ArrayList<Integer>();
        final List<Integer> rodlessScenario = new ArrayList<Integer>();
        final List<Integer> firelessScenario = new ArrayList<Integer>();
        final List<Integer> timeBombScenario = new ArrayList<Integer>();
        final List<Integer> soupScenario = new ArrayList<Integer>();
        for (final Player allPlayers : this.game.getPlayers()) {
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Default) {
                noScenario.add(Scenarios.Default.getVotes());
            }
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Bowless) {
                bowlessScenario.add(Scenarios.Bowless.getVotes());
            }
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.NoClean) {
                noCleanScenario.add(Scenarios.NoClean.getVotes());
            }
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Rodless) {
                rodlessScenario.add(Scenarios.Rodless.getVotes());
            }
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Fireless) {
                firelessScenario.add(Scenarios.Fireless.getVotes());
            }
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.TimeBomb) {
                timeBombScenario.add(Scenarios.TimeBomb.getVotes());
            }
            if (this.game.getVoted().get(allPlayers.getUniqueId()) == Scenarios.Soup) {
                soupScenario.add(Scenarios.Soup.getVotes());
            }
        }
        if (noScenario.size() > bowlessScenario.size() && noScenario.size() > noCleanScenario.size() && noScenario.size() > rodlessScenario.size() && noScenario.size() > firelessScenario.size() && noScenario.size() > timeBombScenario.size() && noScenario.size() > soupScenario.size()) {
            this.wonScenarios.add(Scenarios.Default);
        }
        if (bowlessScenario.size() > noScenario.size() && bowlessScenario.size() > noCleanScenario.size() && bowlessScenario.size() > rodlessScenario.size() && bowlessScenario.size() > firelessScenario.size() && bowlessScenario.size() > timeBombScenario.size() && bowlessScenario.size() > soupScenario.size()) {
            this.wonScenarios.add(Scenarios.Bowless);
        }
        if (noCleanScenario.size() > noScenario.size() && noCleanScenario.size() > bowlessScenario.size() && noCleanScenario.size() > rodlessScenario.size() && noCleanScenario.size() > firelessScenario.size() && noCleanScenario.size() > timeBombScenario.size() && noCleanScenario.size() > soupScenario.size()) {
            this.wonScenarios.add(Scenarios.NoClean);
        }
        if (rodlessScenario.size() > bowlessScenario.size() && rodlessScenario.size() > noCleanScenario.size() && rodlessScenario.size() > noScenario.size() && rodlessScenario.size() > firelessScenario.size() && rodlessScenario.size() > timeBombScenario.size() && rodlessScenario.size() > soupScenario.size()) {
            this.wonScenarios.add(Scenarios.Rodless);
        }
        if (firelessScenario.size() > bowlessScenario.size() && firelessScenario.size() > noCleanScenario.size() && firelessScenario.size() > rodlessScenario.size() && firelessScenario.size() > noScenario.size() && firelessScenario.size() > timeBombScenario.size() && firelessScenario.size() > soupScenario.size()) {
            this.wonScenarios.add(Scenarios.Fireless);
        }
        if (timeBombScenario.size() > bowlessScenario.size() && timeBombScenario.size() > noCleanScenario.size() && timeBombScenario.size() > rodlessScenario.size() && timeBombScenario.size() > firelessScenario.size() && timeBombScenario.size() > noScenario.size() && timeBombScenario.size() > soupScenario.size()) {
            this.wonScenarios.add(Scenarios.TimeBomb);
        }
        if (soupScenario.size() > bowlessScenario.size() && soupScenario.size() > noCleanScenario.size() && soupScenario.size() > rodlessScenario.size() && soupScenario.size() > firelessScenario.size() && soupScenario.size() > timeBombScenario.size() && soupScenario.size() > noScenario.size()) {
            this.wonScenarios.add(Scenarios.Soup);
        }
        final List<String> scenariosToString = new ArrayList<String>();
        for (final Scenarios votedScenarios : Scenarios.values()) {
            if (!this.wonScenarios.isEmpty()) {
                if (this.wonScenarios.contains(votedScenarios)) {
                    votedScenarios.setEnabled(true);
                    scenariosToString.add(this.wonScenarios.toString());
                }
            }
            else {
                this.wonScenarios.add(Scenarios.Default);
                votedScenarios.setEnabled(true);
                scenariosToString.add(this.wonScenarios.toString());
            }
        }
        final String scenarioInString = this.wonScenarios.toString().replaceAll("(^\\[|\\]$)", "");
        Bukkit.broadcastMessage(this.game.getPrefix() + ChatColor.GRAY + "The voted scenario is " + this.game.getmColor() + scenarioInString + ChatColor.GRAY + ".");
    }

    public Player getRandomPlayer() {
        final int playerNumber = new Random().nextInt(this.game.getPlayers().size());
        return (Player)this.game.getPlayers().toArray()[playerNumber];
    }

    public ArrayList<Scenarios> getWonScenarios() {
        return this.wonScenarios;
    }

    public void equipPlayerRandomly(final Player player) {
        this.randomizeArmor(player);
        player.getInventory().setItem(0, this.getRandomSword());
        player.getInventory().setItem(1, this.getRandomBow());
        player.getInventory().setItem(2, new ItemHandler(Material.FISHING_ROD).build());
        player.getInventory().setItem(3, this.getRandomSecondary());
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.WOOD).setAmount(64).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.COBBLESTONE).setAmount(64).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.GOLDEN_APPLE).setAmount(8).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.GOLDEN_APPLE).setAmount(2).setDisplayName(ChatColor.GOLD + "Golden Head").build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.DIAMOND_PICKAXE).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.DIAMOND_AXE).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.WATER_BUCKET).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.WATER_BUCKET).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.LAVA_BUCKET).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.LAVA_BUCKET).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.COOKED_BEEF).setAmount(32).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.ARROW).setAmount(32).build() });
        player.getInventory().addItem(new ItemStack[] { new ItemHandler(Material.ANVIL).build() });
    }

    private void randomizeArmor(final Player player) {
        player.getInventory().setHelmet(this.getRandomHelmet());
        player.getInventory().setChestplate(this.getRandomChestplate());
        player.getInventory().setLeggings(this.getRandomLegs());
        player.getInventory().setBoots(this.getRandomBoots());
    }

    private ItemStack getRandomHelmet() {
        ItemStack item = null;
        final int rand = (int)(Math.random() * 2.0 + 1.0);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_HELMET);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_HELMET);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        return item;
    }

    private ItemStack getRandomChestplate() {
        ItemStack item = null;
        final int rand = (int)(Math.random() * 3.0 + 1.0);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        if (rand == 3) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        }
        return item;
    }

    private ItemStack getRandomLegs() {
        ItemStack item = null;
        final int rand = (int)(Math.random() * 3.0 + 1.0);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        if (rand == 3) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
        }
        return item;
    }

    private ItemStack getRandomBoots() {
        ItemStack item = null;
        final int rand = (int)(Math.random() * 2.0 + 1.0);
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_BOOTS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_BOOTS);
            item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        }
        return item;
    }

    private ItemStack getRandomSword() {
        ItemStack item = null;
        final int rand = (int)(Math.random() * 4.0) + 1;
        if (rand == 1) {
            item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        }
        if (rand == 2) {
            item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        }
        if (rand == 3) {
            item = new ItemStack(Material.DIAMOND_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
        }
        if (rand == 4) {
            item = new ItemStack(Material.IRON_SWORD);
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
        }
        return item;
    }

    private ItemStack getRandomBow() {
        ItemStack item = null;
        final int rand = (int)(Math.random() * 5.0) + 1;
        if (rand == 1) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        }
        if (rand == 2) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        }
        if (rand == 3) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        }
        if (rand == 4) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        }
        if (rand == 5) {
            item = new ItemStack(Material.BOW);
            item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
        }
        return item;
    }

    private ItemStack getRandomSecondary() {
        ItemStack item = null;
        final int randomInt = (int)(Math.random() * 3.0 + 1.0);
        if (randomInt == 1) {
            final Random random = new Random();
            final int randomNum = random.nextInt(5) + 5;
            item = new ItemStack(Material.WEB, randomNum);
        }
        if (randomInt == 2) {
            item = new ItemStack(Material.FLINT_AND_STEEL);
        }
        if (randomInt == 3) {
            final Random random = new Random();
            item = new ItemStack(Material.ENDER_PEARL, 2);
        }
        return item;
    }

    public int getBorderSize() {
        return this.borderSize;
    }
}
