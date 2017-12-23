package com.github.shynixn.petblocks.bukkit.logic.business.entity;

import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.bukkit.PetBlocksPlugin;
import com.github.shynixn.petblocks.bukkit.logic.business.configuration.Config;
import com.github.shynixn.petblocks.bukkit.logic.business.helper.PetBlockModifyHelper;
import com.github.shynixn.petblocks.bukkit.logic.business.helper.SkinHelper;
import com.github.shynixn.petblocks.bukkit.nms.v1_12_R1.MaterialCompatibility12;
import com.github.shynixn.petblocks.core.logic.business.entity.ItemContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BukkitItemContainer extends ItemContainer {

    /**
     * Initializes a new itemContainer
     *
     * @param enabled     enabled
     * @param position    position
     * @param page        page
     * @param id          id
     * @param damage      damage
     * @param skin        skin
     * @param unbreakable unbreakablöe
     * @param name        name
     * @param lore        lore
     */
    public BukkitItemContainer(boolean enabled, int position, GUIPage page, int id, int damage, String skin, boolean unbreakable, String name, String[] lore) {
        super(enabled, position, page, id, damage, skin, unbreakable, name, lore);
    }

    /**
     * Initializes a new itemContainer
     *
     * @param orderNumber orderNumber
     * @param data
     * @throws Exception exception
     */
    public BukkitItemContainer(int orderNumber, Map<String, Object> data) throws Exception {
        super(orderNumber, data);
    }

    /**
     * Generates a new itemStack for the player and his permissions
     *
     * @param player      player
     * @param permissions permission
     * @return itemStack
     */
    @Override
    public Object generate(Object player, String... permissions) {
        if (this.cache != null) {
            this.updateLore((Player) player, permissions);
            return ((ItemStack) this.cache).clone();
        }
        try {
            if (this.isEnabled()) {
                ItemStack itemStack = new ItemStack(MaterialCompatibility12.getMaterialFromId(this.getItemId()), 1, (short) this.getItemDamage());
                if (this.getItemId() == MaterialCompatibility12.getIdFromMaterial(Material.SKULL_ITEM) && this.getSkin() != null) {
                    if (this.getSkin().contains("textures.minecraft.net")) {
                        SkinHelper.setItemStackSkin(itemStack, "http://" + this.getSkin());
                    } else {
                        final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                        meta.setOwner(this.getSkin());
                        itemStack.setItemMeta(meta);
                    }
                }
                final Map<String, Object> data = new HashMap<>();
                data.put("Unbreakable", this.isItemUnbreakable());
                itemStack = PetBlockModifyHelper.setItemStackNBTTag(itemStack, data);
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(this.getDisplayName().get());
                itemStack.setItemMeta(itemMeta);
                this.cache = itemStack;
                this.updateLore((Player) player, permissions);
                return itemStack;
            }
        } catch (final Exception ex) {
            Bukkit.getConsoleSender().sendMessage(PetBlocksPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid config file. Fix the following error or recreate it!");
            PetBlocksPlugin.logger().log(Level.WARNING, "Failed to generate itemStack.", ex);
        }
        return new ItemStack(Material.AIR);
    }

    private void updateLore(Player player, String... permissions) {
        final String[] lore = this.provideLore(player, permissions);
        if (lore != null) {
            final ItemMeta meta = ((ItemStack)this.cache).getItemMeta();
            meta.setLore(Arrays.asList(lore));
            ((ItemStack)this.cache).setItemMeta(meta);
        }
    }

    private String[] provideLore(Player player, String... permissions) {
        if (permissions != null && permissions.length == 1 && permissions[0] != null) {
            if (permissions.length == 1 && permissions[0].equals("minecraft-heads")) {
                return new String[]{ChatColor.GRAY + "Use exclusive pet heads as costume.", ChatColor.YELLOW + "Sponsored by Minecraft-Heads.com"};
            }
            if (permissions.length == 1 && permissions[0].equals("head-database")) {
                final Plugin plugin = Bukkit.getPluginManager().getPlugin("HeadDatabase");
                if (plugin == null) {
                    return new String[]{ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Plugin is not installed - " + ChatColor.YELLOW + "Click me!"};
                }
            }
        }
        final String[] modifiedLore = new String[this.getLore().get().length];
        for (int i = 0; i < modifiedLore.length; i++) {
            modifiedLore[i] = this.getLore().get()[i];
            if (this.getLore().get()[i].contains("<permission>")){
                if (permissions != null && (permissions.length == 0 || this.hasPermission(player, permissions))) {
                    modifiedLore[i] = this.getLore().get()[i].replace("<permission>", Config.getInstance().getPermissionIconYes());
                } else {
                    modifiedLore[i] = this.getLore().get()[i].replace("<permission>", Config.getInstance().getPermissionIconNo());
                }
            }
        }
        return modifiedLore;
    }

    private boolean hasPermission(Player player, String... permissions) {
        for (final String permission : permissions) {
            if (permission.endsWith(".all")) {
                final String subPermission = permission.substring(0, permission.indexOf("all")) + this.getPosition();
                if (player.hasPermission(subPermission)) {
                    return true;
                }
            }
            if (player.hasPermission(permission))
                return true;
        }
        return false;
    }
}
