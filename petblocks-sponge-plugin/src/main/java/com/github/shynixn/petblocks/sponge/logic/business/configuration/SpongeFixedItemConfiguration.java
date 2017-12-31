package com.github.shynixn.petblocks.sponge.logic.business.configuration;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.core.logic.business.entity.ItemContainer;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.FixedItemConfiguration;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.entity.SpongeItemContainer;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongePetBlockModifyHelper;
import com.google.inject.Inject;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;
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
public class SpongeFixedItemConfiguration extends FixedItemConfiguration {

    /**
     * Reloads the content from the fileSystem
     */
    @Override
    public void reload() {
        this.items.clear();
        final Map<String, Object> data = Config.getInstance().getData("gui.items");
        for (final String key : data.keySet()) {
            try {
                final GUIItemContainer container = new SpongeItemContainer(0, (Map<String, Object>) data.get(key));
                if (key.equals("suggest-heads")) {
                    ((ItemContainer) container).setDisplayName("Suggest Heads");
                }
                this.items.put(key, container);
            } catch (final Exception e) {
                PetBlocksPlugin.logger().warn("Failed to load gui item " + key + '.', e);
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
        final Optional<GUIItemContainer> optContainer = this.getGUIItemFromName(name);
        if (!optContainer.isPresent())
            return false;
        final ItemStack mItemStack = (ItemStack) itemStack;
        return mItemStack.get(Keys.DISPLAY_NAME).isPresent() && mItemStack.get(Keys.DISPLAY_NAME).get().equals(SpongePetBlockModifyHelper.translateStringToText(optContainer.get().getDisplayName().get()));
    }
}
