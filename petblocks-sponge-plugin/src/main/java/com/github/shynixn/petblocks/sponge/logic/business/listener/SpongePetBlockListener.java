package com.github.shynixn.petblocks.sponge.logic.business.listener;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.entity.SpongePetBlock;
import com.github.shynixn.petblocks.core.logic.business.PetRunnable;
import com.github.shynixn.petblocks.sponge.logic.business.PetBlockManager;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongeConfigurationHelper;
import com.github.shynixn.petblocks.sponge.nms.NMSRegistry;
import com.google.inject.Inject;
import com.sun.prism.Material;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;
import org.spongepowered.api.event.entity.*;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.text.RuleBasedCollator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Listens to events related to the petblock entity.
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
public class SpongePetBlockListener extends SimpleSpongeListener {
    private final PetBlockManager manager;
    private final Set<PetBlock> jumped = new HashSet<>();

    /**
     * Initializes a new petblockListener from the manager and plugin.
     *
     * @param manager manager
     * @param plugin  plugin
     */
    @Inject
    public SpongePetBlockListener(PetBlockManager manager, PluginContainer plugin) {
        super(plugin);
        this.manager = manager;
        Task.builder().intervalTicks(60L).execute(new ParticleRunnable()).submit(plugin);
        Task.builder().intervalTicks(20L).execute(new PetHunterRunnable()).submit(plugin);
    }

    /**
     * Cancels leashing the petblock.
     *
     * @param event event
     */
    @Listener
    public void onEntityLeashEvent(LeashEntityEvent event) {
        if (this.isPet(event.getTargetEntity())) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels interactions with the petblock entity.
     *
     * @param event event
     */
    @Listener
    public void onEntityInteractEntityEvent(InteractEntityEvent event) {
        if (this.isPet(event.getTargetEntity())) {
            event.setCancelled(true);
        }
        if (this.isDeadPet(event.getTargetEntity())) {
            event.setCancelled(true);
        }
    }

    /**
     * Ejects the player when pressing sneak if he is riding his petblock.
     *
     * @param event  event
     * @param player player
     */
    @Listener
    public void onEntityToggleSneakEvent(ChangeDataHolderEvent event, @First(typeFilter = Player.class) Player player) {
        final SpongePetBlock petBlock;
        if (event.getTargetHolder().get(SneakingData.class).get().sneaking().get()) {
            if (!player.getPassengers().isEmpty() && this.isPet(player.getPassengers().get(0)) &&
                    (petBlock = (SpongePetBlock) this.manager.getPetBlockController().getByPlayer(player)) != null) {
                petBlock.eject(player);
            }
        }
    }

    @Listener
    public void onEntityDamageByEntityEvent(DamageEntityEvent event) {
        if (!event.getCause().first(Entity.class).isPresent()) {
            return;
        }
        Entity damager = event.getCause().first(Entity.class).get();
        Entity entity = event.getTargetEntity();
        if (this.isPet(damager)) {
            final PetBlock petBlock = this.getPet(damager);
            if (petBlock != null && petBlock.getPlayer() != null && petBlock.getPlayer().equals(entity)) {
                event.setCancelled(true);
            }
        }
        if (Config.getInstance().pet().isFleesInCombat()) {
            if (damager instanceof Player && this.manager.getPetBlockController().getByPlayer(damager) != null) {
                this.manager.timeBlocked.put((Player) damager, Config.getInstance().pet().getReappearsInSeconds());
                this.manager.getPetBlockController().removeByPlayer((Player) damager);
            } else if (entity instanceof Player && this.manager.getPetBlockController().getByPlayer(entity) != null) {
                this.manager.timeBlocked.put((Player) entity, Config.getInstance().pet().getReappearsInSeconds());
                this.manager.getPetBlockController().removeByPlayer((Player) entity);
            }
        }
    }

    @Listener
    public void onPlayerTeleportEvent(MoveEntityEvent.Teleport event) {
        if (!(event.getTargetEntity() instanceof Player))
            return;
        Player player = (Player) event.getTargetEntity();
        if (this.manager.getPetBlockController().getByPlayer(player) != null) {
            if (!event.getToTransform().getExtent().getName().equals(event.getFromTransform().getExtent().getName())) {
                this.manager.getPetBlockController().removeByPlayer(player);
                if (Config.getInstance().allowPetSpawning(event.getToTransform())) {
                    this.providePet(player, (petMeta, petBlock) -> Task.builder().delayTicks(Config.getInstance().pet().getWarpDelay() * 20L).execute(() -> {
                        final SpongePetBlock petBlock1 = this.manager.getPetBlockController().create(player, petMeta);
                        this.manager.getPetBlockController().store(petBlock1);
                    }).submit(this.plugin));
                }
            } else if (!player.getPassengers().isEmpty() && this.isPet(player.getPassengers().get(0))) {
                if (!Config.getInstance().pet().isFollow_fallOffHead()) {
                    final PetBlock petBlock = this.manager.getPetBlockController().getByPlayer(player);
                    if (petBlock != null)
                        petBlock.teleportWithOwner(event.getToTransform());
                    event.setCancelled(true);
                } else {
                    final PetBlock petBlock = this.manager.getPetBlockController().getByPlayer(player);
                    if (petBlock != null)
                        petBlock.eject(player);
                }
            }
        }
    }

    @Listener
    public void onPlayerRespawnEvent(RespawnPlayerEvent event) {
        Player player = event.getTargetEntity();
        if (this.manager.getPetBlockController().getByPlayer(player) != null) {
            this.manager.getPetBlockController().remove(this.manager.getPetBlockController().getFromPlayer(player).get());
            Task.builder().delayTicks(Config.getInstance().pet().getWarpDelay() * 20L).execute(() -> this.providePet(player, (petMeta, petBlock) -> {
                final SpongePetBlock petBlock1 = this.manager.getPetBlockController().create(player, petMeta);
                this.manager.getPetBlockController().store(petBlock1);
            })).submit(this.plugin);
        }
    }

    @Listener
    public void entityRightClickEvent(InteractEntityEvent event) {
        if (!event.getCause().first(Entity.class).isPresent()) {
            return;
        }
        final Entity p = event.getCause().first(Entity.class).get();
        if (!(p instanceof Player))
            return;
        final Player player = (Player) p;
        if (this.manager.carryingPet.contains(player)) {
            player.setItemInHand(HandTypes.OFF_HAND, null);
            if (this.manager.getPetBlockController().getByPlayer(player) != null)
                this.manager.getPetBlockController().removeByPlayer(player);
            event.setCancelled(true);
        } else if (this.isPet(event.getTargetEntity())) {
            final PetBlock petBlock = this.getPet(event.getTargetEntity());
            if (petBlock != null && petBlock.getPlayer().equals(player)) {
                if (Config.getInstance().pet().isFeedingEnabled() && player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getItem() == ItemTypes.CARROT) {
                    petBlock.getEffectPipeline().playParticleEffect(event.getTargetEntity().getLocation(), Config.getInstance().getFeedingClickParticleEffect());
                    petBlock.getEffectPipeline().playSound(event.getTargetEntity().getLocation(), Config.getInstance().getFeedingClickSound());
                    if (player.getItemInHand(HandTypes.MAIN_HAND).get().getQuantity() == 1) {
                        player.setItemInHand(HandTypes.MAIN_HAND, null);
                    } else {
                        player.getItemInHand(HandTypes.MAIN_HAND).get().setQuantity(player.getItemInHand(HandTypes.MAIN_HAND).get().getQuantity() - 1);
                    }
                    if (!this.jumped.contains(petBlock)) {
                        this.jumped.add(this.getPet(event.getTargetEntity()));
                        petBlock.jump();
                        Task.builder().delayTicks(20L).execute(() -> jumped.remove(getPet(event.getTargetEntity())));
                    }
                } else if (Config.getInstance().pet().isFollow_carry() && (player.getInventory() == null || !player.getItemInHand(HandTypes.OFF_HAND).isPresent())) {
                    player.setItemInHand(HandTypes.OFF_HAND, ((ArmorStand) petBlock.getArmorStand()).getHelmet().get().copy());
                    this.manager.getPetBlockController().remove((SpongePetBlock) petBlock);
                    this.manager.carryingPet.add(player);
                }
            }
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerInteractEvent(HandInteractEvent event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            this.removePetFromArm(player, true);
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerCommandEvent(SendCommandEvent event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onInventoryOpenEvent(InteractInventoryEvent.Open event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            event.setCancelled(true);
            player.closeInventory(SpongeConfigurationHelper.createCause(this.plugin));
        }
    }

    @Listener
    public void onPlayerEntityEvent(InteractEntityEvent event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            this.removePetFromArm(player, false);
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerDeathEvent(DestructEntityEvent.Death event) {
        if (event.getTargetEntity() instanceof Player && this.manager.carryingPet.contains(event.getTargetEntity())) {
            this.removePetFromArm((Player) event.getTargetEntity(), false);
        }
    }

    @Listener
    public void onPlayerQuitEvent(ClientConnectionEvent.Disconnect event) {
        final Player player = event.getTargetEntity();
        if (this.manager.carryingPet.contains(player)) {
            player.setItemInHand(HandTypes.OFF_HAND, null);
            this.manager.carryingPet.remove(player);
        }
    }

    @Listener
    public void onInventoryOpen(ClickInventoryEvent event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            this.removePetFromArm(player, false);
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPlayerDropItem(DropItemEvent.Dispense event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            this.removePetFromArm(player, false);
            for (Entity entity : event.getEntities()) {
                entity.remove();
            }
        }
    }

    @Listener
    public void onSlotChange(ChangeDataHolderEvent event, @First(typeFilter = Player.class) Player player) {
        if (this.manager.carryingPet.contains(player)) {
            this.removePetFromArm(player, false);
            event.setCancelled(true);
        }
    }

    @Listener
    public void entityDamageEvent(DamageEntityEvent event) {
        if (this.isPet(event.getTargetEntity())) {
            final PetBlock petBlock = this.getPet(event.getTargetEntity());
            if (petBlock == null)
                return;
            if (!event.getCause().getNamedCauses().containsKey(NamedCause.PHYSICAL))
                petBlock.damage(event.getFinalDamage());
            else if (event.getCause().getNamedCauses().containsKey(NamedCause.PHYSICAL))
                petBlock.damage(-2.0);
            event.setCancelled(true);
        }
    }

    private class ParticleRunnable implements Runnable {
        @Override
        public void run() {
            for (final SpongePetBlock petBlock : SpongePetBlockListener.this.manager.getPetBlockController().getAll()) {
                if (petBlock.isDead() || !Config.getInstance().allowPetSpawning(petBlock.getPlayer().getTransform())) {
                    SpongePetBlockListener.this.manager.getPetBlockController().remove(petBlock);
                    if (petBlock.getPlayer().isOnline() && Config.getInstance().allowPetSpawning(petBlock.getPlayer().getTransform())) {
                        Task.builder().async().execute(() -> {
                            final PetMeta petMeta = SpongePetBlockListener.this.manager.getPetMetaController().getByPlayer(petBlock.getPlayer());
                            Task.builder().execute(() -> SpongePetBlockListener.this.setPetBlock(petBlock.getPlayer(), petMeta)).submit(SpongePetBlockListener.this.plugin);
                        }).submit(SpongePetBlockListener.this.plugin);
                    }

                }
            }
        }
    }

    private class PetHunterRunnable implements Runnable {
        @Override
        public void run() {
            for (final Player player : SpongePetBlockListener.this.manager.timeBlocked.keySet().toArray(new Player[SpongePetBlockListener.this.manager.timeBlocked.size()])) {
                SpongePetBlockListener.this.manager.timeBlocked.put(player, SpongePetBlockListener.this.manager.timeBlocked.get(player) - 1);
                if (SpongePetBlockListener.this.manager.timeBlocked.get(player) <= 0) {
                    SpongePetBlockListener.this.manager.timeBlocked.remove(player);
                    SpongePetBlockListener.this.providePet(player, (petMeta, petBlock) -> SpongePetBlockListener.this.setPetBlock(player, petMeta));
                }
            }
            for (final World world : Sponge.getGame().getServer().getWorlds()) {
                for (final Entity entity : world.getEntities()) {
                    if (entity instanceof ArmorStand && SpongePetBlockListener.this.isDeadPet(entity)) {
                        entity.remove();
                    } else if (!SpongePetBlockListener.this.isPet(entity) && entity.get(Keys.DISPLAY_NAME).isPresent() && entity.get(Keys.DISPLAY_NAME).get().equals("PetBlockIdentifier")) {
                        entity.remove();
                    }
                }
            }
        }
    }

    private boolean isPet(Entity entity) {
        return this.getPet(entity) != null;
    }

    private PetBlock getPet(Entity entity) {
        try {
            for (final PetBlock block : this.manager.getPetBlockController().getAll()) {
                if (block != null && entity != null && block.getArmorStand() != null && block.getEngineEntity() != null && (block.getArmorStand().equals(entity) || block.getEngineEntity().equals(entity)))
                    return block;
            }
        } catch (final Exception ignored) {
        }
        return null;
    }

    private void removePetFromArm(Player player, boolean launch) {
        this.providePet(player, (petMeta, petBlock) -> {
            if (petBlock == null) {
                this.setPetBlock(player, petMeta);
            }
            player.setItemInHand(HandTypes.OFF_HAND, null);
            this.manager.carryingPet.remove(player);
            if (launch) {
                final Optional<SpongePetBlock> optPetblock = this.manager.getPetBlockController().getFromPlayer(player);
                optPetblock.ifPresent(spongePetBlock -> spongePetBlock.getEngineEntity().setVelocity(this.getDirection(player)));
            }
        });
    }

    /**
     * Creates a new petblock for the player and petMeta and sets it managed for the default controller.
     *
     * @param player  player
     * @param petMeta petMeta
     */
    private void setPetBlock(Player player, PetMeta petMeta) {
        final SpongePetBlock petBlock = this.manager.getPetBlockController().create(player, petMeta);
        this.manager.getPetBlockController().store(petBlock);
    }

    /**
     * Gets the pet meta and petblock and calls the callBack .
     *
     * @param player   player
     * @param runnable Runnable
     */
    private void providePet(Player player, PetRunnable<SpongePetBlock> runnable) {
        final SpongePetBlock petBlock;
        if ((petBlock = (SpongePetBlock) this.manager.getPetBlockController().getByPlayer(player)) != null) {
            runnable.run(petBlock.getMeta(), petBlock);
        } else {
            Task.builder().async().execute(() -> {
                final PetMeta petMeta = this.manager.getPetMetaController().getByPlayer(player);
                Task.builder().execute(() -> runnable.run(petMeta, null)).submit(this.plugin);
            }).submit(this.plugin);
        }
    }

    private boolean isDeadPet(Entity entity) {
        if (entity instanceof ArmorStand && !this.isPet(entity)) {
            final ArmorStand stand = (ArmorStand) entity;
            final int xidentifier = (int) stand.getBodyPartRotationalData().bodyRotation().get().getZ();
            final int identifier = (int) stand.getBodyPartRotationalData().rightArmDirection().get().getX();
            final int lidentifier = (int) stand.getBodyPartRotationalData().leftArmDirection().get().getX();
            if (xidentifier == 2877 && (identifier == 2877 || lidentifier == 2877)) {
                return true;
            }
            if (Math.floor(stand.getBodyPartRotationalData().bodyRotation().get().getZ() * 1000) == 301) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the launch Direction for holding pets.
     *
     * @param player player
     * @return launchDirection
     */
    private Vector3d getDirection(Player player) {
        final double rotX = player.getHeadRotation().getY();
        final double rotY = player.getHeadRotation().getX();
        final double h = Math.cos(Math.toRadians(rotY));
        return new Vector3d(-h * Math.sin(Math.toRadians(rotX)), 0.5, h * Math.cos(Math.toRadians(rotX)))
                .mul(1.2);
    }
}
