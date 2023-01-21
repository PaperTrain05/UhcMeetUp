package io.paper.uhcmeetup.handler;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHandler {
    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemHandler(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemHandler setDisplayName(String name) {
        this.itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemHandler setMetaID(byte metaID) {
        this.itemStack.getData().setData(metaID);
        return this;
    }

    public ItemHandler setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemHandler setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemHandler addEnchantment(Enchantment enchantment, int lvl) {
        this.itemMeta.addEnchant(enchantment, lvl, false);
        return this;
    }

    public ItemHandler clearEnchantments() {
        this.itemMeta.getEnchants().forEach((enchantment, integer) -> this.itemMeta.removeEnchant((Enchantment)enchantment));
        return this;
    }

    public ItemHandler removeEnchantment(Enchantment enchantment) {
        if (this.itemMeta.getEnchants().containsKey(enchantment)) {
            this.itemMeta.removeEnchant(enchantment);
        }
        return this;
    }

    public ItemHandler setLore(List<String> lines) {
        this.itemMeta.setLore(lines);
        return this;
    }

    public ItemHandler setLore(String ... lines) {
        this.itemMeta.setLore(Arrays.asList(lines));
        return this;
    }

    public ItemHandler resetLore() {
        this.itemMeta.getLore().clear();
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }
}
