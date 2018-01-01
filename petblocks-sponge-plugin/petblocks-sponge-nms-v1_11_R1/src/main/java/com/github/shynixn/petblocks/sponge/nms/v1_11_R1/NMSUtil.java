package com.github.shynixn.petblocks.sponge.nms.v1_11_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Field;
import java.util.UUID;

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
public class NMSUtil {

    public static void setItemDamage(ItemStack itemStack, int damage) {
        final net.minecraft.item.ItemStack itemStack1 = (net.minecraft.item.ItemStack) (Object) itemStack;
        itemStack1.setItemDamage(damage);
    }

    public static void updateInventoryFor(Player player) {
        ((EntityPlayerMP) player).sendContainerToPlayer(((EntityPlayerMP) player).openContainer);
    }

    public static void setItemOwner(ItemStack itemStack, String owner) {
        final net.minecraft.item.ItemStack itemStack1 = (net.minecraft.item.ItemStack) (Object) itemStack;
        NBTTagCompound compound = itemStack1.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        compound.setString("SkullOwner", owner);
        itemStack1.setTagCompound(compound);
    }

    public static void setItemSkin(ItemStack itemStack, String skin) {
        final net.minecraft.item.ItemStack itemStack1 = (net.minecraft.item.ItemStack) (Object) itemStack;
        NBTTagCompound compound = itemStack1.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        String newSkin = skin;
        if (!newSkin.startsWith("http://")) {
            newSkin = "http://" + newSkin;
        }
        final com.mojang.authlib.GameProfile newSkinProfile = getNonPlayerProfile(newSkin);
        compound.setTag("SkullOwner", serialize(newSkinProfile));
        itemStack1.setTagCompound(compound);
    }

    private static NBTTagCompound serialize(GameProfile gameProfile) {
        final NBTTagCompound properties = new NBTTagCompound();
        final NBTTagList list = new NBTTagList();
        for (final Property property : gameProfile.getProperties().get("textures")) {
            final NBTTagCompound singleSkin = new NBTTagCompound();
            singleSkin.setString("Value", property.getValue());
            list.appendTag(singleSkin);
        }
        properties.setTag("textures", list);

        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Id", gameProfile.getId().toString());
        compound.setTag("Properties", properties);
        return compound;
    }

    private static GameProfile getNonPlayerProfile(String skinUrl) {
        final GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
        newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\"}}}")));
        return newSkinProfile;
    }
}
