package com.github.shynixn.petblocks.sponge.logic.persistence.entity;

import com.github.shynixn.petblocks.api.persistence.entity.EngineContainer;
import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PlayerMeta;
import com.github.shynixn.petblocks.core.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.petblocks.core.logic.persistence.entity.PlayerIdentifiable;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.CompatibilityItemType;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongePetBlockModifyHelper;
import com.github.shynixn.petblocks.sponge.nms.NMSRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * Implementation of the petMeta interface which is persistence able to the database.
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
public class SpongePetData extends PersistenceObject implements PetMeta, PlayerIdentifiable {

    private String petDisplayName;

    private String skin;
    private int id;
    private int damage;
    private boolean unbreakable;

    private long ageTicks;
    private boolean enabled;
    private boolean sounds;

    private PlayerMeta playerInfo;
    private long playerId;

    private ParticleEffectMeta particleEffectBuilder;
    private long particleId;

    private EngineContainer engineContainer;
    private int engineId;

    /**
     * Initializes a new default petData which is ready to be used.
     *
     * @param player player
     * @param name   nameOfThePet
     */
    public SpongePetData(Player player, String name) {
        super();
        this.petDisplayName = name.replace(":player", player.getName());
        this.playerInfo = SpongePlayerData.from(player);
        this.ageTicks = Config.getInstance().pet().getAge_smallticks();
        this.sounds = true;
        this.particleEffectBuilder = new SpongeParticleEffectMeta();
        this.particleEffectBuilder.setEffectType(ParticleEffectMeta.ParticleEffectType.NONE);
        this.engineContainer = Config.getInstance().getEngineController().getById(Config.getInstance().getDefaultEngine());
        if (this.engineContainer == null) {
            throw new RuntimeException("Engine cannot be null!");
        }
    }

    /**
     * Initializes a new petData.
     */
    public SpongePetData() {
        super();
    }

    /**
     * Sets the id of the engine.
     *
     * @param engineId id
     */
    @Override
    public void setEngineId(int engineId) {
        this.engineId = engineId;
    }

    /**
     * Returns the id of the engine.
     *
     * @return id
     */
    @Override
    public int getEngineId() {
        return this.engineId;
    }

    /**
     * Returns the id of the player
     *
     * @return playerId
     */
    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    /**
     * Sets the id of the player
     *
     * @param id id
     */
    @Override
    public void setPlayerId(long id) {
        this.playerId = id;
    }

    /**
     * Returns the id of the particle
     *
     * @return particleId
     */
    @Override
    public long getParticleId() {
        return this.particleId;
    }

    /**
     * Sets the id of the particle
     *
     * @param id id
     */
    @Override
    public void setParticleId(long id) {
        this.particleId = id;
    }

    /**
     * Sets the own meta
     *
     * @param meta meta
     */
    @Override
    public void setPlayerMeta(PlayerMeta meta) {
        this.playerId = meta.getId();
        this.playerInfo = meta;
    }

    /**
     * Sets the particleEffect meta
     *
     * @param meta meta
     */
    @Override
    public void setParticleEffectMeta(ParticleEffectMeta meta) {
        if (meta == null) {
            throw new IllegalArgumentException("ParticleEffectMeta cannot be null!");
        }
        this.particleId = meta.getId();
        this.particleEffectBuilder = meta;
    }

    /**
     * Returns the particleEffect meta
     *
     * @return meta
     */
    @Override
    public ParticleEffectMeta getParticleEffectMeta() {
        return this.particleEffectBuilder;
    }

    /**
     * Returns the meta of the owner
     *
     * @return player
     */
    @Override
    public PlayerMeta getPlayerMeta() {
        return this.playerInfo;
    }

    /**
     * Returns the id of the item
     *
     * @return itemId
     */
    @Override
    public int getItemId() {
        return this.id;
    }

    /**
     * Returns the name of the item.
     *
     * @return name
     */
    @Override
    public String getItemName() {
        return CompatibilityItemType.getFromId(this.id).getMinecraftId();
    }

    /**
     * Returns the damage of the item
     *
     * @return itemDamage
     */
    @Override
    public int getItemDamage() {
        return this.damage;
    }

    /**
     * Returns if the item is unbreakable
     *
     * @return unbreakable
     */
    @Override
    public boolean isItemUnbreakable() {
        return this.unbreakable;
    }

    /**
     * Returns if the petblock is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the petblock enabled
     *
     * @param enabled enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the age in ticks
     *
     * @return age
     */
    @Override
    public long getAge() {
        return this.ageTicks;
    }

    /**
     * Returns the skin of the pet
     *
     * @return skin
     */
    @Override
    public String getSkin() {
        return this.skin;
    }

    /**
     * Sets the age in ticks
     *
     * @param ticks ticks
     */
    @Override
    public void setAge(long ticks) {
        this.ageTicks = ticks;
    }

    /**
     * Returns the data of the engine
     *
     * @return engine
     */
    @Override
    public EngineContainer getEngine() {
        return this.engineContainer;
    }

    /**
     * Sets the data of the engine
     *
     * @param engine engine
     */
    @Override
    public void setEngine(EngineContainer engine) {
        this.engineContainer = engine;
    }

    /**
     * Sets the pet sound enabled.
     *
     * @param enabled enabled
     */
    @Override
    public void setSoundEnabled(boolean enabled) {
        this.sounds = enabled;
    }

    /**
     * Returns if the pet-sound is enabled.
     *
     * @return enabled
     */
    @Override
    public boolean isSoundEnabled() {
        return this.sounds;
    }

    /**
     * Returns if the itemStack is unbreakable.
     *
     * @return unbreakable
     */
    @Override
    public boolean isItemStackUnbreakable() {
        return this.unbreakable;
    }

    /**
     * Sets the itemStack.
     *
     * @param id          id
     * @param damage      damage
     * @param skin        skin
     * @param unbreakable unbreakable
     */
    @Override
    public void setSkin(int id, int damage, String skin, boolean unbreakable) {
        String s = skin;
        if (s != null && s.contains("textures.minecraft")) {
            if (!s.contains("http://")) {
                s = "http://" + s;
            }
        }
        this.id = id;
        this.damage = damage;
        this.skin = s;
        this.unbreakable = unbreakable;
    }

    /**
     * Sets the itemStack skin.
     *
     * @param material    material of any supported type
     * @param damage      damage
     * @param skin        skin
     * @param unbreakable unbreakable
     */
    @Override
    public void setDisplaySkin(Object material, int damage, String skin, boolean unbreakable) {
        String s = skin;
        if (s != null && s.contains("textures.minecraft")) {
            if (!s.contains("http://")) {
                s = "http://" + s;
            }
        }
        if (material != null && material instanceof Integer) {
            this.id = (Integer) material;
        } else if (material != null && material instanceof String) {
            this.id = CompatibilityItemType.getFromName((String) material).getId();
        } else if (material != null && material instanceof ItemType) {
            this.id = CompatibilityItemType.getFromItemType((ItemType) material).getId();
        }
        this.damage = damage;
        this.skin = s;
        this.unbreakable = unbreakable;
    }

    /**
     * Returns the itemStack for the head
     *
     * @return headItemStack
     */
    @Override
    public Object getHeadItemStack() {
        final CompatibilityItemType itemType = CompatibilityItemType.getFromId(this.getItemId());
        final ItemStack itemStack = ItemStack.builder().quantity(1)
                .itemType(itemType.getItemType())
                .build();
        NMSRegistry.setItemDamage(itemStack, this.getItemDamage());
        if (this.getSkin() != null) {
            if (this.getSkin().contains("textures.minecraft.net")) {
                NMSRegistry.setSkinUrl(itemStack, this.getSkin());
            } else {
                NMSRegistry.setSkinOwner(itemStack, this.getSkin());
            }
        }
        itemStack.offer(Keys.UNBREAKABLE, this.isItemUnbreakable());
        if (this.getItemName() != null) {
            itemStack.offer(Keys.DISPLAY_NAME, SpongePetBlockModifyHelper.translateStringToText(this.getItemName()));
        }
        return itemStack;
    }

    /**
     * Sets the stored display name of the pet which appears above it's head on respawn.
     *
     * @param name name
     */
    @Override
    public void setPetDisplayName(Object name) {
        if (name == null)
            return;
        String custom;
        if (name instanceof Text) {
            custom = TextSerializers.LEGACY_FORMATTING_CODE.serialize((Text) name);
        } else {
            custom = (String) name;
        }
        custom = custom.replace(":player", "Player");
        if (Config.getInstance().pet().getPetNameBlackList() != null) {
            for (final String blackName : Config.getInstance().pet().getPetNameBlackList()) {
                if (custom.toUpperCase().contains(blackName.toUpperCase())) {
                    throw new RuntimeException("Name is not valid!");
                }
            }
        }
        this.petDisplayName = custom;
    }

    /**
     * Returns the stored display name of the pet which appear above it's head on respawn.
     *
     * @return name
     */
    @Override
    public String getPetDisplayName() {
        return this.petDisplayName;
    }
}
