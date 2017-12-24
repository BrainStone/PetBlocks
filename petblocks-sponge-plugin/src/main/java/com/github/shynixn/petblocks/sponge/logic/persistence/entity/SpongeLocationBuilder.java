package com.github.shynixn.petblocks.sponge.logic.persistence.entity;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.persistence.entity.IPosition;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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
public class SpongeLocationBuilder implements IPosition {
    private String world;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;

    /**
     * Initializes a new IPosition
     */
    public SpongeLocationBuilder() {
        super();
    }

    /**
     * Initializes a new IPosition with the given params
     *
     * @param worldName worldName
     * @param x         x
     * @param y         y
     * @param z         z
     * @param yaw       yaw
     * @param pitch     pitch
     */
    public SpongeLocationBuilder(String worldName, double x, double y, double z, float yaw, float pitch) {
        super();
        this.world = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Initializes a new IPosition with the given params
     *
     * @param worldName worldName
     * @param x         x
     * @param y         y
     * @param z         z
     * @param yaw       yaw
     * @param pitch     pitch
     */
    public SpongeLocationBuilder(String worldName, double x, double y, double z, double yaw, double pitch) {
        this(worldName, x, y, z, (float) yaw, (float) pitch);
    }

    /**
     * Parses the location out of the map
     *
     * @param items items
     * @throws Exception mapParseException
     */
    public SpongeLocationBuilder(Map<String, Object> items) throws Exception {
        super();
        this.x = (double) items.get("x");
        this.y = (double) items.get("y");
        this.z = (double) items.get("z");
        this.yaw = (double) items.get("yaw");
        this.pitch = (double) items.get("pitch");
        this.world = (String) items.get("worldname");
    }

    /**
     * Sets the coordinates x, y, z
     *
     * @param x x
     * @param y y
     * @param z z
     * @return builder
     */
    @Override
    public IPosition setCoordinates(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Sets the rotation yaw, pitch
     *
     * @param yaw   yaw
     * @param pitch pitch
     * @return builder
     */
    @Override
    public IPosition setRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    /**
     * Adds or Subs the given coordinates x, y, z to or from the builder
     *
     * @param x x
     * @param y y
     * @param z z
     * @return builder
     */
    @Override
    public IPosition addCoordinates(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Sets the world of the builder
     *
     * @param world world
     * @return builder
     */
    public IPosition setWorld(World world) {
        this.world = world.getName();
        return this;
    }

    /**
     * Sets the worldName of the builder
     *
     * @param worldName worldName
     * @return builder
     */
    @Override
    public IPosition setWorldName(String worldName) {
        this.world = worldName;
        return this;
    }

    /**
     * Sets the yaw of the builder
     *
     * @param yaw yaw
     * @return builder
     */
    @Override
    public IPosition setYaw(double yaw) {
        this.yaw = yaw;
        return this;
    }

    /**
     * Sets the pitch of the builder
     *
     * @param pitch pitch
     * @return builder
     */
    @Override
    public IPosition setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Sets the x coordinate of the builder
     *
     * @param x x
     * @return builder
     */
    @Override
    public IPosition setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the y coordinate of the builder
     *
     * @param y y
     * @return builder
     */
    @Override
    public IPosition setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the z coordinate of the builder
     *
     * @param z z
     * @return builder
     */
    @Override
    public IPosition setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Returns the yaw of the builder
     *
     * @return yaw
     */
    @Override
    public double getYaw() {
        return this.yaw;
    }

    /**
     * Returns the pitch of the builder
     *
     * @return pitch
     */
    @Override
    public double getPitch() {
        return this.pitch;
    }

    /**
     * Returns the BukkitWorld of the builder
     *
     * @return world
     */
    public Optional<World> getWorld() {
        return Sponge.getServer().getWorld(this.world);
    }

    /**
     * Returns the x coordinate of the builder
     *
     * @return x
     */
    @Override
    public double getX() {
        return this.x;
    }

    /**
     * Returns the y coordinate of the builder
     *
     * @return y
     */
    @Override
    public double getY() {
        return this.y;
    }

    /**
     * Returns the z coordinate of the builder
     *
     * @return z
     */
    @Override
    public double getZ() {
        return this.z;
    }

    /**
     * Returns the x coordinate as int of the builder
     *
     * @return x
     */
    @Override
    public int getBlockX() {
        return (int) this.x;
    }

    /**
     * Returns the y coordinate as int of the builder
     *
     * @return y
     */
    @Override
    public int getBlockY() {
        return (int) this.y;
    }

    /**
     * Returns the z coordinate as int of the builder
     *
     * @return z
     */
    @Override
    public int getBlockZ() {
        return (int) this.z;
    }

    /**
     * Returns the worldname of the builder
     *
     * @return worldName
     */
    @Override
    public String getWorldName() {
        return this.world;
    }

    /**
     * Copies the current builder
     *
     * @return copyOfBuilder
     */
    public IPosition copy() {
        return new SpongeLocationBuilder(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Converts the builder to a bukkitLocation
     *
     * @return bukkitLocation
     */
    public Location toLocation() {
        if (!this.getWorld().isPresent())
            return null;
        return new Location(this.getWorld().get(), this.x, this.y, this.z);
    }

    /**
     * Converts the builder to a bukkitVector
     *
     * @return bukkitVector
     */
    public Vector3d toVector() {
        return new Vector3d(this.x, this.y, this.z);
    }

    /**
     * Returns the rotation.
     *
     * @return rotation
     */
    public Vector3d getRotation() {
        return new Vector3d(this.yaw, this.pitch, 0);
    }

    /**
     * Returns the relativePosition to a given direction
     *
     * @param distance  distance
     * @param direction direction
     * @return builder
     */
    @Override
    public IPosition relativePosition(double distance, Direction direction) {
        if (direction == Direction.FORWARD) {
            this.x += (distance * Math.sin(Math.toRadians(this.yaw + 90)));
            this.z += (distance * Math.cos(Math.toRadians(this.yaw + 90)));
        } else if (direction == Direction.BACKWARDS) {
            this.x += (-1 * distance * Math.sin(Math.toRadians(this.yaw + 90)));
            this.z += (-1 * distance * Math.cos(Math.toRadians(this.yaw + 90)));
        } else if (direction == Direction.LEFT) {
            this.x += (distance * Math.sin(Math.toRadians(this.yaw)));
            this.z += (distance * Math.cos(Math.toRadians(this.yaw)));
        } else if (direction == Direction.RIGHT) {
            this.x += (-1 * distance * Math.sin(Math.toRadians(this.yaw)));
            this.z += (-1 * distance * Math.cos(Math.toRadians(this.yaw)));
        } else if (direction == Direction.UP) {
            this.y += distance;
        } else if (direction == Direction.DOWN) {
            this.y -= distance;
        }
        return this;
    }

    /**
     * Checks if 2 builders are equal
     *
     * @param arg0 secondBuilder
     * @return isSame
     */
    @Override
    public boolean equals(Object arg0) {
        if (arg0 != null && arg0 instanceof IPosition) {
            final IPosition location = (IPosition) arg0;
            if (location.getBlockX() == this.getBlockX() && location.getBlockY() == this.getBlockY() && location.getBlockZ() == this.getBlockZ())
                return true;
        }
        return false;
    }

    /**
     * Displays the builder as string
     *
     * @return string
     */
    @Override
    public String toString() {
        if (this.getWorld() == null)
            return "location {" + " w unloaded" + " x " + this.getBlockX() + " y " + this.getBlockY() + " z " + this.getBlockZ() + '}';
        return "location {" + " w " + this.getWorldName() + " x " + this.getBlockX() + " y " + this.getBlockY() + " z " + this.getBlockZ() + '}';
    }

    /**
     * Returns the direction of the location.
     *
     * @return direction
     */
    public Vector3d getDirection() {
        final Vector3d vector = new Vector3d();
        final double rotX = this.getYaw();
        final double rotY = this.getPitch();
        final double xz = Math.cos(Math.toRadians(rotY));
        return new Vector3d(-xz * Math.sin(Math.toRadians(rotX)), -Math.sin(Math.toRadians(rotY)), xz * Math.cos(Math.toRadians(rotX)));
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

    public static class LocationBuilderSerializer implements TypeSerializer<SpongeLocationBuilder> {

        @Override
        public SpongeLocationBuilder deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
            final SpongeLocationBuilder locationBuilder = new SpongeLocationBuilder();
            locationBuilder.setX(configurationNode.getNode("x").getDouble());
            locationBuilder.setY(configurationNode.getNode("y").getDouble());
            locationBuilder.setZ(configurationNode.getNode("z").getDouble());
            locationBuilder.setYaw(configurationNode.getNode("yaw").getDouble());
            locationBuilder.setPitch(configurationNode.getNode("pitch").getDouble());
            locationBuilder.setWorldName(configurationNode.getNode("worldname").getString());
            return locationBuilder;
        }

        @Override
        public void serialize(TypeToken<?> typeToken, SpongeLocationBuilder spongeLocationBuilder, ConfigurationNode configurationNode) throws ObjectMappingException {
            configurationNode.getNode("x").setValue(spongeLocationBuilder.getX());
            configurationNode.getNode("y").setValue(spongeLocationBuilder.getY());
            configurationNode.getNode("z").setValue(spongeLocationBuilder.getZ());
            configurationNode.getNode("yaw").setValue(spongeLocationBuilder.getYaw());
            configurationNode.getNode("pitch").setValue(spongeLocationBuilder.getPitch());
            configurationNode.getNode("worldname").setValue(spongeLocationBuilder.getWorldName());
        }
    }
}