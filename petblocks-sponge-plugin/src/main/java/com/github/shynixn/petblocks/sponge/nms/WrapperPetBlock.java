package com.github.shynixn.petblocks.sponge.nms;

import com.github.shynixn.petblocks.api.business.entity.EffectPipeline;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.entity.SpongePetBlock;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
public class WrapperPetBlock implements SpongePetBlock {

    private SpongePetBlock spongePetBlock;

    public WrapperPetBlock(SpongePetBlock spongePetBlock) {
        this.spongePetBlock = spongePetBlock;
    }

    @Override
    public void setDieing() {
        this.spongePetBlock.setDieing();
    }

    @Override
    public void teleportWithOwner(Location<World> worldLocation) {
        this.spongePetBlock.teleport(worldLocation);
    }

    @Override
    public boolean isDieing() {
        return this.spongePetBlock.isDieing();
    }

    /**
     * Returns the pipeline for managed effect playing.
     *
     * @return effectPipeLine
     */
    @Override
    public EffectPipeline getEffectPipeline() {
        return this.spongePetBlock.getEffectPipeline();
    }

    /**
     * Returns the meta of the petblock.
     *
     * @return meta
     */
    @Override
    public PetMeta getMeta() {
        return this.spongePetBlock.getMeta();
    }

    /**
     * Returns the owner of the petblock.
     *
     * @return player
     */
    @Override
    public Player getPlayer() {
        return this.spongePetBlock.getPlayer();
    }

    /**
     * Removes the petblock.
     */
    @Override
    public void removeEntity() {
        this.spongePetBlock.removeEntity();
    }

    /**
     * Lets the given player ride on the petblock.
     *
     * @param player player
     */
    @Override
    public void ride(Player player) {
        this.spongePetBlock.ride(player);
    }

    /**
     * Lets the given player wear the petblock.
     *
     * @param player player
     */
    @Override
    public void wear(Player player) {
        this.spongePetBlock.wear(player);
    }

    /**
     * Ejects the given player riding from the petblock.
     *
     * @param player player
     */
    @Override
    public void eject(Player player) {
        this.spongePetBlock.eject(player);
    }

    /**
     * Sets the displayName of the petblock.
     *
     * @param name name
     */
    @Override
    public void setDisplayName(String name) {
        this.spongePetBlock.setDisplayName(name);
    }

    /**
     * Sets the displayName of the petblock.
     *
     * @param name name
     */
    @Override
    public void setCustomName(String name) {
        this.spongePetBlock.setCustomName(name);
    }

    /**
     * Returns the displayName of the petblock.
     *
     * @return name
     */
    @Override
    public String getCustomName() {
        return this.spongePetBlock.getCustomName();
    }

    /**
     * Respawns the petblock
     */
    @Override
    public void respawn() {
        this.spongePetBlock = NMSRegistry.createPetBlock(this.getLocation(), this.getMeta());
    }

    /**
     * Returns if the petblock is already removed or dead.
     *
     * @return dead
     */
    @Override
    public boolean isDead() {
        return this.spongePetBlock.isDead();
    }

    /**
     * Returns the armorstand of the petblock.
     *
     * @return armorstand
     */
    @Override
    public Object getArmorStand() {
        return this.spongePetBlock.getArmorStand();
    }

    /**
     * Returns the entity being used as engine.
     *
     * @return entity
     */
    @Override
    public Object getEngineEntity() {
        return this.spongePetBlock.getEngineEntity();
    }

    /**
     * Returns the location of the entity.
     *
     * @return position
     */
    @Override
    public Location<World> getLocation() {
        return this.spongePetBlock.getLocation();
    }

    /**
     * Damages the petblock the given amount of damage.
     *
     * @param amount amount
     */
    @Override
    public void damage(double amount) {
        this.spongePetBlock.damage(amount);
    }

    /**
     * Lets the petblock perform a jump.
     */
    @Override
    public void jump() {
        this.spongePetBlock.jump();
    }

    /**
     * Sets the velocity of the petblock.
     *
     * @param vector vector
     */
    @Override
    public void setVelocity(Object vector) {
        this.spongePetBlock.setVelocity(vector);
    }

    /**
     * Teleports the the petblock to the given location.
     *
     * @param worldLocation location
     */
    @Override
    public void teleport(Location<World> worldLocation) {
        this.spongePetBlock.teleport(worldLocation);
    }
}
