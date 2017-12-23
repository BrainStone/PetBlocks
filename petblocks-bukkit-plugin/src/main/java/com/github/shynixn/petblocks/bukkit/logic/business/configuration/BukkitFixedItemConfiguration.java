package com.github.shynixn.petblocks.bukkit.logic.business.configuration;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.bukkit.PetBlocksPlugin;
import com.github.shynixn.petblocks.bukkit.logic.business.entity.BukkitItemContainer;
import com.github.shynixn.petblocks.core.logic.business.entity.ItemContainer;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.FixedItemConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
public class BukkitFixedItemConfiguration extends FixedItemConfiguration {
    private Plugin plugin;

    /**
     * Initializes a new engine repository
     *
     * @param plugin plugin
     */
    public BukkitFixedItemConfiguration(Plugin plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        this.plugin = plugin;
    }

    /**
     * Reloads the content from the fileSystem
     */
    @Override
    public void reload() {
        this.items.clear();
        this.plugin.reloadConfig();
        final Map<String, Object> data = ((MemorySection) this.plugin.getConfig().get("gui.items")).getValues(false);
        for (final String key : data.keySet()) {
            try {
                final GUIItemContainer container = new BukkitItemContainer(0, ((MemorySection) data.get(key)).getValues(false));
                if (key.equals("suggest-heads")) {
                    ((ItemContainer) container).setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Suggest Heads");
                }
                this.items.put(key, container);
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Failed to load guiItem " + key + '.', e);
            }
        }
    }

    /**
     * Returns if the given itemStack is a guiItemStack with the given name
     *
     * @param itemStack itemStack
     * @param name      name
     * @return itemStack
     */
    @Override
    public boolean isGUIItem(Object itemStack, String name) {
        if (itemStack == null || name == null)
            return false;
        final GUIItemContainer container = this.getGUIItemByName(name);
        final ItemStack mItemStack = (ItemStack) itemStack;
        return mItemStack.getItemMeta() != null && container.getDisplayName().isPresent()
                && mItemStack.getItemMeta().getDisplayName() != null
                && mItemStack.getItemMeta().getDisplayName().equalsIgnoreCase(container.getDisplayName().get());
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        super.close();
        this.plugin = null;
    }
}
