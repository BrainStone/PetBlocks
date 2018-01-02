package com.github.shynixn.petblocks.sponge.logic.business.entity;

import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.core.logic.business.entity.ItemContainer;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.CompatibilityItemType;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongePetBlockModifyHelper;
import com.github.shynixn.petblocks.sponge.nms.NMSRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.catalog.CatalogItemData;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
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
public class SpongeItemContainer extends ItemContainer {
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
    public SpongeItemContainer(boolean enabled, int position, GUIPage page, int id, int damage, String skin, boolean unbreakable, String name, String[] lore) {
        super(enabled, position, page, id, damage, skin, unbreakable, name, lore);
    }

    /**
     * Initializes a new itemContainer
     *
     * @param orderNumber orderNumber
     * @param data
     * @throws Exception exception
     */
    public SpongeItemContainer(int orderNumber, Map<String, Object> data) throws Exception {
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
     /*   if (this.cache != null) {
            this.updateLore((Player) player, permissions);
            return ((ItemStack) this.cache).copy();
        }*/
        try {
            if (this.isEnabled()) {
                final CompatibilityItemType itemType = CompatibilityItemType.getFromId(this.getItemId());
                final ItemStack itemStack = ItemStack.builder().quantity(1)
                        .itemType(itemType.getItemType())
                        .build();
                NMSRegistry.setItemDamage(itemStack, this.getItemDamage());
                if (itemType == CompatibilityItemType.SKULL_ITEM && this.getSkin() != null) {
                    if (this.getSkin().contains("textures.minecraft.net")) {
                        NMSRegistry.setSkinUrl(itemStack, this.getSkin());
                    } else {
                        NMSRegistry.setSkinOwner(itemStack, this.getSkin());
                    }
                }
                itemStack.offer(Keys.UNBREAKABLE, this.isItemUnbreakable());
                if (this.getDisplayName().isPresent()) {
                    itemStack.offer(Keys.DISPLAY_NAME, SpongePetBlockModifyHelper.translateStringToText(this.getDisplayName().get()));
                }
                this.cache = itemStack;
                this.updateLore((Player) player, permissions);
                return itemStack;
            }
        } catch (final Exception ex) {
            PetBlocksPlugin.logger().warn("Invalid config file. Fix the following error or recreate it!");
            PetBlocksPlugin.logger().warn("Failed to generate itemStack.", ex);
        }
        return ItemStack.builder().itemType(ItemTypes.AIR).build();
    }

    private void updateLore(Player player, String... permissions) {
        final Text[] lore = this.provideLore(player, permissions);
        if (lore != null) {
            final ItemStack itemStack = (ItemStack) this.cache;
            if (itemStack.get(Keys.ITEM_LORE).isPresent()) {
                itemStack.offer(Keys.ITEM_LORE, Arrays.asList(lore));
            }
        }
    }

    private Text[] provideLore(Player player, String... permissions) {
        if (permissions != null && permissions.length == 1 && permissions[0] != null) {
            if (permissions.length == 1 && permissions[0].equals("minecraft-heads")) {
                return new Text[]{Text.builder("Use exclusive pet heads as costume.").color(TextColors.GRAY).build(), Text.builder("Sponsored by Minecraft-Heads.com").color(TextColors.YELLOW).build()};
            }
        }
        final Text[] modifiedLore = new Text[this.getLore().get().length];
        for (int i = 0; i < modifiedLore.length; i++) {
            modifiedLore[i] = SpongePetBlockModifyHelper.translateStringToText(this.getLore().get()[i]);
            if (this.getLore().get()[i].contains("<permission>")) {
                if (permissions != null && (permissions.length == 0 || this.hasPermission(player, permissions))) {
                    modifiedLore[i] = SpongePetBlockModifyHelper.translateStringToText(this.getLore().get()[i].replace("<permission>", SpongePetBlockModifyHelper.translateTextToString(Config.getInstance().getPermissionIconYes())));
                } else {
                    modifiedLore[i] = SpongePetBlockModifyHelper.translateStringToText(this.getLore().get()[i].replace("<permission>", SpongePetBlockModifyHelper.translateTextToString(Config.getInstance().getPermissionIconNo())));
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
