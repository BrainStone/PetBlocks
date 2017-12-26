package com.github.shynixn.petblocks.sponge.logic.persistence.entity;

import com.github.shynixn.petblocks.api.persistence.entity.SoundMeta;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public class SoundBuilder implements SoundMeta {

    private String text;
    private float volume;
    private float pitch;

    /**
     * Initializes a new soundBuilder
     */
    public SoundBuilder() {
        super();
    }

    /**
     * Initializes a new soundBuilder
     *
     * @param text text
     */
    public SoundBuilder(String text) {
        super();
        this.text = text;
        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    /**
     * Initializes a new soundBuilder.
     *
     * @param data data data
     */
    public SoundBuilder(Map<String, Object> data) {
        this.text = (String) data.get("name");
        this.volume = (float) (double) data.get("volume");
        this.pitch = (float) (double) data.get("pitch");
    }

    /**
     * Initializes a new soundBuilder
     *
     * @param text   text
     * @param volume volume
     * @param pitch  pitch
     */
    public SoundBuilder(String text, double volume, double pitch) {
        super();
        this.text = text;
        this.volume = (float) volume;
        this.pitch = (float) pitch;
    }

    /**
     * Plays the sound to all given players at their location
     *
     * @param players players
     * @throws Exception exception
     */
    public void apply(Collection<Player> players) throws Exception {
        this.apply(players.toArray(new Player[players.size()]));
    }

    /**
     * Plays the sound to all given players at their location
     *
     * @param players players
     * @throws Exception exception
     */
    public void apply(Player... players) throws Exception {
        if (this.text.equals("none"))
            return;
        for (final Player player : players) {
            player.playSound(this.getSoundTypeFromName(this.text), player.getLocation().getPosition(), this.volume, this.pitch);
        }
    }

    /**
     * Plays the sound to all players in the world at the given location. Players to far away cannot hear the sound.
     *
     * @param location location
     * @throws Exception exception
     */
    public void apply(Location location) throws Exception {
        if (this.text.equals("none"))
            return;
        final World world = (World) location.getExtent();
        for (final Player player : world.getPlayers()) {
            player.playSound(this.getSoundTypeFromName(this.text), location.getPosition(), this.volume, this.pitch);
        }
    }

    /**
     * Plays the sound to the given players at the given location. Given players to far away cannot hear the sound.
     *
     * @param location location
     * @param players  players
     * @throws Exception exception
     */
    public void apply(Location location, Collection<Player> players) throws Exception {
        this.apply(location, players.toArray(new Player[players.size()]));
    }

    /**
     * Plays the sound to the given players at the given location. Given players to far away cannot hear the sound.
     *
     * @param location location
     * @param players  players
     * @throws Exception exception
     */
    public void apply(Location location, Player... players) throws Exception {
        for (final Player player : players) {
            if (this.text.equals("none"))
                return;
            player.playSound(this.getSoundTypeFromName(this.text), location.getPosition(), this.volume, this.pitch);
        }
    }

    /**
     * Applies the sound at the given location
     *
     * @param location location
     */
    @Override
    public void applyToLocation(Object location) throws Exception {
        this.apply((Location) location);
    }

    /**
     * Applies the sound to the given player
     *
     * @param players players
     */
    @Override
    public void applyToPlayers(Object... players) throws Exception {
        this.apply((Player[]) players);
    }

    /**
     * Returns the name of the sound
     *
     * @return name
     */
    @Override
    public String getName() {
        return this.text;
    }

    /**
     * Sets the name of the sound
     *
     * @param name name
     * @return builder
     */
    @Override
    public SoundBuilder setName(String name) {
        this.text = name;
        return this;
    }

    /**
     * Returns the sound and throws exception if the sound does not exist
     *
     * @return sound
     * @throws Exception exception
     */
    public SoundType getSound() throws Exception {
        return SoundType.builder().build(this.text);
    }

    /**
     * Sets the bukkit sound of the sound
     *
     * @param sound sound
     * @return builder
     */
    public SoundBuilder setSound(SoundType sound) {
        this.text = sound.getId();
        return this;
    }

    /**
     * Returns the volume of the sound
     *
     * @return volume
     */
    @Override
    public double getVolume() {
        return this.volume;
    }

    /**
     * Sets the volume of the sound
     *
     * @param volume volume
     * @return builder
     */
    @Override
    public SoundBuilder setVolume(double volume) {
        this.volume = (float) volume;
        return this;
    }

    /**
     * Returns the pitch of the sound
     *
     * @return pitch
     */
    @Override
    public double getPitch() {
        return this.pitch;
    }

    /**
     * Sets the pitch of the sound
     *
     * @param pitch pitch
     * @return builder
     */
    @Override
    public SoundBuilder setPitch(double pitch) {
        this.pitch = (float) pitch;
        return this;
    }

    private SoundType getSoundTypeFromName(String name) {
        return null;
    }

    /**
     * Returns the id of the object
     *
     * @return id
     */
    @Override
    public long getId() {
        return this.hashCode();
    }

    public static class SoundBuilderSerializer implements TypeSerializer<SoundBuilder> {

        @Override
        public SoundBuilder deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
            final SoundBuilder soundBuilder = new SoundBuilder();
            soundBuilder.setName(configurationNode.getNode("name").getString());
            soundBuilder.setVolume(configurationNode.getNode("volume").getDouble());
            soundBuilder.setPitch(configurationNode.getNode("pitch").getDouble());
            return soundBuilder;
        }

        @Override
        public void serialize(TypeToken<?> typeToken, SoundBuilder soundBuilder, ConfigurationNode configurationNode) throws ObjectMappingException {
            configurationNode.getNode("name").setValue(soundBuilder.getName());
            configurationNode.getNode("volume").setValue(soundBuilder.getVolume());
            configurationNode.getNode("pitch").setValue(soundBuilder.getPitch());
        }
    }

}