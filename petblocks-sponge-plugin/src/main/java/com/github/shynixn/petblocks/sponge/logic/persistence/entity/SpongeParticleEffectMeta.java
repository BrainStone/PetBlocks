package com.github.shynixn.petblocks.sponge.logic.persistence.entity;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.core.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.helper.CompatibilityItemType;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2016
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
public class SpongeParticleEffectMeta extends PersistenceObject implements ParticleEffectMeta {
    private String effect;
    private int amount;
    private double speed;
    private double offsetX;
    private double offsetY;
    private double offsetZ;

    private Integer material;
    private Byte data;

    /**
     * Initializes a new ParticleEffectMeta
     */
    public SpongeParticleEffectMeta() {
        super();
    }

    /**
     * Initializes a new ParticleEffectMeta with the given params
     *
     * @param effectName effect
     * @param amount     amount
     * @param speed      speed
     * @param offsetX    x
     * @param offsetY    y
     * @param offsetZ    z
     */
    public SpongeParticleEffectMeta(String effectName, int amount, double speed, double offsetX, double offsetY, double offsetZ) {
        super();
        if (effectName == null)
            throw new IllegalArgumentException("Effect cannot be null!");
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be less than 0");
        if (getParticleEffectFromName(effectName) == null)
            throw new IllegalArgumentException("Cannot find particleEffect for name!");
        this.effect = effectName;
        this.amount = amount;
        this.speed = speed;
        this.offsetX = (float) offsetX;
        this.offsetY = (float) offsetY;
        this.offsetZ = (float) offsetZ;
    }

    /**
     * Parses the potioneffect out of the map
     *
     * @param items items
     * @throws Exception mapParseException
     */
    public SpongeParticleEffectMeta(Map<String, Object> items) throws Exception {
        super();
        this.effect = (String) items.get("name");
        this.amount = (int) items.get("amount");
        this.speed = (double) items.get("speed");
        if (items.containsKey("offx"))
            this.offsetX = (float) (double) items.get("offx");
        if (items.containsKey("offy"))
            this.offsetY = (float) (double) items.get("offy");
        if (items.containsKey("offz"))
            this.offsetZ = ((float) (double) items.get("offz"));
        if (items.containsKey("id"))
            this.material = (Integer) items.get("id");
        if (items.containsKey("damage"))
            this.data = (byte) (int) (Integer) items.get("damage");
        if (items.containsKey("red"))
            this.setRed((Integer) items.get("red"));
        if (items.containsKey("green"))
            this.setGreen((Integer) items.get("green"));
        if (items.containsKey("blue"))
            this.setBlue((Integer) items.get("blue"));
    }

    /**
     * Sets the RGB colors of the particleEffect
     *
     * @param red   red
     * @param green green
     * @param blue  blue
     * @return builder
     */
    @Override
    public ParticleEffectMeta setColor(int red, int green, int blue) {
        this.setRed(red);
        this.setBlue(blue);
        this.setGreen(green);
        return this;
    }

    /**
     * Sets the color of the particleEffect
     *
     * @param particleColor particleColor
     * @return builder
     */
    @Override
    public ParticleEffectMeta setColor(SpongeParticleEffectMeta.ParticleColor particleColor) {
        if (particleColor == null)
            throw new IllegalArgumentException("Color cannot be null!");
        this.setColor(particleColor.getRed(), particleColor.getGreen(), particleColor.getBlue());
        return this;
    }

    /**
     * Sets the color for note particleEffect
     *
     * @param color color
     * @return builder
     */
    @Override
    public ParticleEffectMeta setNoteColor(int color) {
        if (color > 20 || color < 0) {
            this.offsetX = 5;
        } else {
            this.offsetX = color;
        }
        return this;
    }

    /**
     * Sets the amount of particles of the particleEffect
     *
     * @param amount amount
     * @return builder
     */
    @Override
    public ParticleEffectMeta setAmount(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be less than 0");
        this.amount = amount;
        return this;
    }

    /**
     * Sets the speed of the particleEffect
     *
     * @param speed speed
     * @return builder
     */
    @Override
    public ParticleEffectMeta setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    /**
     * Sets the offsetX of the particleEffect
     *
     * @param offsetX offsetX
     * @return builder
     */
    @Override
    public ParticleEffectMeta setOffsetX(double offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    /**
     * Sets the offsetY of the particleEffect
     *
     * @param offsetY offsetY
     * @return builder
     */
    @Override
    public ParticleEffectMeta setOffsetY(double offsetY) {
        this.offsetY = offsetY;
        return this;
    }

    /**
     * Sets the offsetZ of the particleEffect
     *
     * @param offsetZ offsetZ
     * @return builder
     */
    @Override
    public ParticleEffectMeta setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
        return this;
    }

    /**
     * Sets the offset of the particleEffect
     *
     * @param offsetX offsetX
     * @param offsetY offsetY
     * @param offsetZ offsetZ
     * @return instance
     */
    @Override
    public ParticleEffectMeta setOffset(double offsetX, double offsetY, double offsetZ) {
        this.setOffsetX(offsetX);
        this.setOffsetY(offsetY);
        this.setOffsetZ(offsetZ);
        return this;
    }

    /**
     * Sets the effectType of the particleEffect
     *
     * @param name name
     * @return builder
     */
    @Override
    public ParticleEffectMeta setEffectName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        this.effect = name;
        return this;
    }

    /**
     * Sets the effectType of the particlEffect
     *
     * @param type type
     * @return builder
     */
    @Override
    public ParticleEffectMeta setEffectType(ParticleEffectMeta.ParticleEffectType type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null!");
        this.effect = type.getSimpleName();
        return this;
    }

    /**
     * Sets the blue of the RGB color
     *
     * @param blue blue
     * @return builder
     */
    @Override
    public ParticleEffectMeta setBlue(int blue) {
        this.offsetZ = blue / 255.0F;
        return this;
    }

    /**
     * Sets the red of the RGB color
     *
     * @param red red
     * @return builder
     */
    @Override
    public ParticleEffectMeta setRed(int red) {
        this.offsetX = red / 255.0F;
        if (red == 0) {
            this.offsetX = Float.MIN_NORMAL;
        }
        return this;
    }

    /**
     * Sets the green of the RGB color
     *
     * @param green green
     * @return builder
     */
    @Override
    public ParticleEffectMeta setGreen(int green) {
        this.offsetY = green / 255.0F;
        return this;
    }

    /**
     * Sets the material of the particleEffect
     *
     * @param material material
     * @return builder
     */
    @Override
    public ParticleEffectMeta setMaterial(Object material) {
        if (material != null && material instanceof Integer) {
            this.material = (Integer) material;
        } else if (material != null && material instanceof String) {
            this.material = CompatibilityItemType.getFromName((String) material).getId();
        } else if (material != null && material instanceof ItemType) {
            this.material = CompatibilityItemType.getFromItemType((ItemType) material).getId();
        } else {
            this.material = null;
        }
        return this;
    }

    /**
     * Returns the name of the material.
     *
     * @return name
     */
    @Override
    public String getMaterialName() {
        return CompatibilityItemType.getFromId(this.material).name();
    }

    /**
     * Sets the data of the material of the particleEffect
     *
     * @param data data
     * @return builder
     */
    @Override
    public ParticleEffectMeta setData(Byte data) {
        this.data = data;
        return this;
    }

    /**
     * Returns the effect of the particleEffect
     *
     * @return effectName
     */
    @Override
    public String getEffectName() {
        return this.effect;
    }

    /**
     * Returns the particleEffectType of the particleEffect
     *
     * @return effectType
     */
    @Override
    public ParticleEffectMeta.ParticleEffectType getEffectType() {
        return getParticleEffectFromName(this.effect);
    }

    /**
     * Returns the amount of particles of the particleEffect
     *
     * @return amount
     */
    @Override
    public int getAmount() {
        return this.amount;
    }

    /**
     * Returns the speed of the particleEffect
     *
     * @return speed
     */
    @Override
    public double getSpeed() {
        return this.speed;
    }

    /**
     * Returns the offsetX of the particleEffect
     *
     * @return offsetX
     */
    @Override
    public double getOffsetX() {
        return this.offsetX;
    }

    /**
     * Returns the offsetY of the particleEffect
     *
     * @return offsetY
     */
    @Override
    public double getOffsetY() {
        return this.offsetY;
    }

    /**
     * Returns the offsetZ of the particleEffect
     *
     * @return offsetZ
     */
    @Override
    public double getOffsetZ() {
        return this.offsetZ;
    }

    /**
     * Returns the RGB color blue of the particleEffect
     *
     * @return blue
     */
    @Override
    public int getBlue() {
        return (int) this.offsetZ * 255;
    }

    /**
     * Returns the RGB color red of the particleEffect
     *
     * @return red
     */
    @Override
    public int getRed() {
        return (int) this.offsetX * 255;
    }

    /**
     * Returns the RGB color green of the particleEffect
     *
     * @return green
     */
    @Override
    public int getGreen() {
        return (int) this.offsetY * 255;
    }

    /**
     * Returns the material of the particleEffect
     *
     * @return material
     */
    @Override
    public Object getMaterial() {
        if (this.material == null || CompatibilityItemType.getFromId(this.material) == null)
            return null;
        return CompatibilityItemType.getFromId(this.material).getItemType();
    }

    /**
     * Returns the data of the particleEffect
     *
     * @return data
     */
    @Override
    public Byte getData() {
        return this.data;
    }

    /**
     * Copies the current builder
     *
     * @return copyOfBuilder
     */
    @Override
    public ParticleEffectMeta clone() {
        final SpongeParticleEffectMeta particle = new SpongeParticleEffectMeta();
        particle.effect = this.effect;
        particle.amount = this.amount;
        particle.offsetX = this.offsetX;
        particle.offsetY = this.offsetY;
        particle.offsetZ = this.offsetZ;
        particle.speed = this.speed;
        particle.material = this.material;
        particle.data = this.data;
        return particle;
    }

    /**
     * Converts the effect to a bukkitParticle
     *
     * @param clazz Clazz to be given for compatibility
     * @param <T>   Particle
     * @return bukkitParticle
     */
    public <T extends Enum<T>> T toParticle(Class<?> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("Class cannot be null!");
        for (final Object item : clazz.getEnumConstants()) {
            final Enum<T> eItem = (Enum<T>) item;
            if (eItem.name().equalsIgnoreCase(this.effect))
                return (T) eItem;
        }
        return null;
    }

    /**
     * Returns if the particleEffect is a color particleEffect
     *
     * @return isColor
     */
    @Override
    public boolean isColorParticleEffect() {
        return this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.SPELL_MOB.getSimpleName())
                || this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.SPELL_MOB_AMBIENT.getSimpleName())
                || this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.REDSTONE.getSimpleName())
                || this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.NOTE.getSimpleName());
    }

    /**
     * Returns if the particleEffect is a note particleEffect
     *
     * @return isNote
     */
    @Override
    public boolean isNoteParticleEffect() {
        return this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.NOTE.getSimpleName());
    }

    /**
     * Returns if the particleEffect is a materialParticleEffect
     *
     * @return isMaterial
     */
    @Override
    public boolean isMaterialParticleEffect() {
        return this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.BLOCK_CRACK.getSimpleName())
                || this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.BLOCK_DUST.getSimpleName())
                || this.effect.equalsIgnoreCase(ParticleEffectMeta.ParticleEffectType.ITEM_CRACK.getSimpleName());
    }

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     * @param players  players
     */
    @Override
    public void apply(Object location, Collection<Object> players) {
        if (players == null)
            throw new IllegalArgumentException("Players cannot be null!");
        this.applyTo((Location) location, players.toArray(new Player[players.size()]));
    }

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     */
    @Override
    public void apply(Object location) {
        this.applyTo((Location) location);
    }

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     * @param players  players
     */
    public void applyTo(Location location, Player... players) {
        try {
            if(this.effect == null)
                return;
            final ParticleType type = Sponge.getGame().getRegistry().getType(ParticleType.class,"minecraft:" + this.getEffectType().getSimpleName()).get();
            final ParticleEffect.Builder builder = ParticleEffect.builder()
                    .type(type)
                    .quantity(this.getAmount())
                    .offset(new Vector3d(this.getOffsetX(), this.getOffsetY(), this.getOffsetZ()))
                    .velocity(new Vector3d(this.speed, this.speed, this.speed));

            if (this.material != null) {
                builder.option(ParticleOptions.BLOCK_STATE, BlockState.builder().blockType(CompatibilityItemType.getFromId(this.material).getBlockType())
                        .add(Keys.ITEM_DURABILITY, (int) this.data).build());
            }

            final ParticleEffect effect = builder.build();
            for (final Player player : players) {
                player.spawnParticles(effect, new Vector3d(location.getX(), location.getY(), location.getZ()));
            }

        } catch (final Exception e) {
            PetBlocksPlugin.logger().log(Level.WARNING,"Failed to send packet.", e);
        }
    }

    /**
     * Checks if 2 builders are equal
     *
     * @param o secondBuilder
     * @return isSame
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final SpongeParticleEffectMeta that = (SpongeParticleEffectMeta) o;
        return this.amount == that.amount
                && Double.compare(that.speed, this.speed) == 0
                && Double.compare(that.offsetX, this.offsetX) == 0
                && Double.compare(that.offsetY, this.offsetY) == 0
                && Double.compare(that.offsetZ, this.offsetZ) == 0
                & (this.effect != null ? this.effect.equals(that.effect) : that.effect == null)
                && Objects.equals(this.material, that.material) && (this.data != null ? this.data.equals(that.data) : that.data == null);
    }

    /**
     * Displays the builder as string
     *
     * @return string
     */
    @Override
    public String toString() {
        return "effect {" + "name " + this.effect + " amound " + this.amount + " speed " + this.speed + '}';
    }

    /**
     * Returns a text of all particleEffects to let the user easily view them
     *
     * @return potionEffects
     */
    public static String getParticlesText() {
        final StringBuilder builder = new StringBuilder();
        for (final ParticleEffectMeta.ParticleEffectType particleEffect : ParticleEffectMeta.ParticleEffectType.values()) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(particleEffect.getSimpleName());
        }
        return builder.toString();
    }

    /**
     * Returns the particleEffectType from name
     *
     * @param name name
     * @return particleEffectType
     */
    public static ParticleEffectMeta.ParticleEffectType getParticleEffectFromName(String name) {
        for (final ParticleEffectMeta.ParticleEffectType particleEffect : ParticleEffectMeta.ParticleEffectType.values()) {
            if (name != null && particleEffect.getSimpleName().equalsIgnoreCase(name))
                return particleEffect;
        }
        return null;
    }

    /**
     * Invokes a constructor by the given parameters
     *
     * @param clazz      clazz
     * @param paramTypes paramTypes
     * @param params     params
     * @return instance
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws InvocationTargetException exception
     * @throws InstantiationException    exception
     */
    private static Object invokeConstructor(Class<?> clazz, Class[] paramTypes, Object[] params) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor constructor = clazz.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(params);
    }

    /**
     * Invokes a method by the given parameters
     *
     * @param instance   instance
     * @param clazz      clazz
     * @param name       name
     * @param paramTypes paramTypes
     * @param params     params
     * @return returnedObject
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     * @throws NoSuchMethodException     exception
     */
    private static Object invokeMethod(Object instance, Class<?> clazz, String name, Class[] paramTypes, Object[]
            params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final Method method = clazz.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        return method.invoke(instance, params);
    }

    public static class ParticleEffectSerializer implements TypeSerializer<SpongeParticleEffectMeta> {

        @Override
        public SpongeParticleEffectMeta deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
            final SpongeParticleEffectMeta particleEffectMeta = new SpongeParticleEffectMeta();
            particleEffectMeta.setEffectName(configurationNode.getNode("name").getString());
            particleEffectMeta.setAmount(configurationNode.getNode("amount").getInt());
            particleEffectMeta.setSpeed(configurationNode.getNode("speed").getDouble());

            if (configurationNode.getNode("offx") != null) {
                particleEffectMeta.setOffsetX(configurationNode.getNode("offx").getDouble());
            }
            if (configurationNode.getNode("offy") != null) {
                particleEffectMeta.setOffsetY(configurationNode.getNode("offy").getDouble());
            }
            if (configurationNode.getNode("offz") != null) {
                particleEffectMeta.setOffsetZ(configurationNode.getNode("offz").getDouble());
            }
            if (configurationNode.getNode("id") != null) {
                particleEffectMeta.setMaterial(configurationNode.getNode("id").getInt());
            }
            if (configurationNode.getNode("damage") != null) {
                particleEffectMeta.setData((byte) configurationNode.getNode("damage").getInt());
            }
            if (configurationNode.getNode("red") != null) {
                particleEffectMeta.setRed(configurationNode.getNode("red").getInt());
            }
            if (configurationNode.getNode("green") != null) {
                particleEffectMeta.setGreen(configurationNode.getNode("green").getInt());
            }
            if (configurationNode.getNode("blue") != null) {
                particleEffectMeta.setBlue(configurationNode.getNode("blue").getInt());
            }
            return particleEffectMeta;
        }

        @Override
        public void serialize(TypeToken<?> typeToken, SpongeParticleEffectMeta spongeParticleEffectMeta, ConfigurationNode configurationNode) throws ObjectMappingException {
            configurationNode.getNode("name").setValue(spongeParticleEffectMeta.getEffectName().toUpperCase());
            configurationNode.getNode("amount").setValue(spongeParticleEffectMeta.getAmount());
            configurationNode.getNode("speed").setValue(spongeParticleEffectMeta.getSpeed());

            configurationNode.getNode("offx").setValue(spongeParticleEffectMeta.getOffsetX());
            configurationNode.getNode("offy").setValue(spongeParticleEffectMeta.getOffsetY());
            configurationNode.getNode("offz").setValue(spongeParticleEffectMeta.getOffsetZ());

            if (spongeParticleEffectMeta.getMaterial() != null) {
                configurationNode.getNode("id").setValue(CompatibilityItemType.getFromItemType((ItemType) spongeParticleEffectMeta.getMaterial()).getId());
            }
            if (spongeParticleEffectMeta.getData() != null) {
                configurationNode.getNode("damage").setValue(spongeParticleEffectMeta.getData());
            }
        }
    }
}