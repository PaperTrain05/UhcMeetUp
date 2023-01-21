package io.paper.uhcmeetup.listener;

import io.paper.uhcmeetup.Game;
import io.paper.uhcmeetup.gamestate.states.IngameState;
import io.paper.uhcmeetup.gamestate.states.LobbyState;
import io.paper.uhcmeetup.handler.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerInteractListener implements Listener {
    private Game game = Game.getInstance();

    @EventHandler
    public void handlePlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() != null && event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
        } else if (event.getItem().getType() != null && event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getData().getData() == 1) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerPickupEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (!(this.game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else if (!player.getWorld().getName().equals("uhc_meetup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerDropEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (!(this.game.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            event.setCancelled(true);
        } else if (!player.getWorld().getName().equals("uhc_meetup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (this.game.getSpectators().contains(player)) {
            if (player.getItemInHand().getType() == Material.NETHER_STAR) {
                if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.game.getmColor() + "Teletrasportati al centro")) {
                    player.teleport(new Location(Bukkit.getWorld("uhc_meetup"), 0.0, 100.0, 0.0));
                    player.sendMessage(this.game.getPrefix() + this.game.getsColor() + "Sei stato teletrasportato al " + this.game.getmColor() + "centro della mappa" + this.game.getsColor() + "!");
                }
            } else if (player.getItemInHand().getType() == Material.BEACON) {
                if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.game.getmColor() + "Random Player")) {
                    if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                        if (this.game.getPlayers().size() >= 2 && this.game.getGameManager().getRandomPlayer() != player) {
                            player.teleport(this.game.getGameManager().getRandomPlayer());
                        }
                    } else {
                        player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Al momento non ci sono giochi in esecuzione!");
                    }
                }
            } else if (player.getItemInHand().getType() == Material.WATCH && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.game.getmColor() + "Players")) {
                if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                    this.game.getInventoryHandler().handlePlayersInventory(player);
                } else {
                    player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Al momento non ci sono giochi in esecuzione!");
                }
            }
        } else if (player.getItemInHand().getType() == Material.PAPER && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.game.getmColor() + "§lScenario Voting") && this.game.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
            this.game.getInventoryHandler().handleVotingInventory(player);
        }
    }

    @EventHandler
    public void handlePlayerInteractAtEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player interacted = (Player)event.getRightClicked();
            if (player.getItemInHand().getType() == Material.BOOK && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(this.game.getmColor() + "Ispezionare l'inventario")) {
                if (this.game.getGameStateManager().getCurrentGameState() instanceof IngameState) {
                    Inventory inventory = Bukkit.createInventory(null, 54, this.game.getmColor() + "" + interacted.getName() + "'s Inventory");
                    ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE);
                    PlayerInventory playerInventory = interacted.getInventory();
                    inventory.setItem(0, pane);
                    inventory.setItem(1, pane);
                    inventory.setItem(2, playerInventory.getHelmet());
                    inventory.setItem(3, playerInventory.getChestplate());
                    inventory.setItem(4, pane);
                    inventory.setItem(5, playerInventory.getLeggings());
                    inventory.setItem(6, playerInventory.getBoots());
                    inventory.setItem(7, pane);
                    inventory.setItem(8, pane);
                    for (int i = 9; i < 45; ++i) {
                        int slot = i - 9;
                        inventory.setItem(i, playerInventory.getItem(slot));
                    }
                    ItemStack level = new ItemHandler(Material.EXP_BOTTLE).setDisplayName(this.game.getmColor() + interacted.getLevel() + " livelli").build();
                    ItemStack health = new ItemHandler(Material.POTION).setDisplayName(this.game.getmColor() + Math.round(interacted.getHealth()) + "/" + (int)interacted.getMaxHealth()).build();
                    ItemStack head = new ItemHandler(Material.CAKE).setDisplayName(this.game.getmColor() + interacted.getName()).build();
                    ItemStack hunger = new ItemHandler(Material.COOKED_BEEF).setDisplayName(this.game.getmColor() + interacted.getFoodLevel() + "/20").build();
                    inventory.setItem(45, pane);
                    inventory.setItem(46, level);
                    inventory.setItem(47, pane);
                    inventory.setItem(48, health);
                    inventory.setItem(49, pane);
                    inventory.setItem(50, head);
                    inventory.setItem(51, pane);
                    inventory.setItem(52, hunger);
                    inventory.setItem(53, pane);
                    player.openInventory(inventory);
                } else {
                    player.sendMessage(this.game.getPrefix() + ChatColor.RED + "Al momento non ci sono giochi in esecuzione!");
                }
            }
        }
    }

    @EventHandler
    public void handleInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player)((Object)event.getWhoClicked());
        if (this.game.getSpectators().contains(player)) {
            event.setCancelled(true);
        }
        if (event.getClickedInventory() != null && event.getCurrentItem() != null) {
            if (event.getClickedInventory().getName().contains(this.game.getsColor() + "Stats")) {
                event.setCancelled(true);
            }
            if (event.getCurrentItem().getType() == Material.SKULL_ITEM && this.game.getSpectators().contains(player)) {
                Player target = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName());
                player.teleport(target);
            }
        }
    }
}