package com.github.shynixn.petblocks.sponge.nms.v1_11_R1;

import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.business.entity.PetBlockPartEntity;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.nms.helper.PetBlockHelper;
import com.google.common.collect.Sets;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.logging.Level;

public final class CustomRabbit extends EntityRabbit implements PetBlockPartEntity {
    private PetBlock petBlock;
    private long playedMovingSound = 100000;

    public CustomRabbit(World worldIn) {
        super(worldIn);
    }

    public CustomRabbit(Player player, PetBlock petBlock) {
        super((World) player.getWorld());
        this.setSilent(true);
        try {
            this.tasks.taskEntries = Sets.newLinkedHashSet();
            this.tasks.executingTaskEntries = Sets.newLinkedHashSet();
            this.targetTasks.taskEntries = Sets.newLinkedHashSet();
            this.targetTasks.executingTaskEntries = Sets.newLinkedHashSet();
            this.tasks.addTask(0, new EntityAISwimming(this));
            this.tasks.addTask(1, new OwnerPathfinder(this, petBlock));
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                    .setBaseValue(0.30000001192092896D * Config.getInstance().pet().getModifier_petwalking());
        } catch (final Exception exc) {
            PetBlocksPlugin.logger().log(Level.WARNING, "EntityNMS exception.", exc);
        }
        this.petBlock = petBlock;
        this.stepHeight = (float) Config.getInstance().pet().getModifier_petclimbing();
    }

    @Override
    protected SoundEvent getJumpSound() {
        this.playedMovingSound = PetBlockHelper.executeMovingSound(this.petBlock, this.playedMovingSound);
        return super.getJumpSound();
    }

    /**
     * Returns the entity hidden by this object
     *
     * @return spigotEntity
     */
    @Override
    public Object getEntity() {
        return this;
    }

    /**
     * Spawns the entity at the given location
     *
     * @param mLocation location
     */
    @Override
    public void spawn(Object mLocation) {
        final Location location = (Location) mLocation;
        final Living entity = (Living) this.getEntity();
        final World mcWorld = (World) location.getExtent();
        this.setPosition(location.getX(), location.getY(), location.getZ());
        mcWorld.spawnEntity(this);

        final PotionEffect effect = PotionEffect.builder()
                .potionType(PotionEffectTypes.INVISIBILITY)
                .duration(9999999).amplifier(1).build();
        final PotionEffectData effects = entity.getOrCreate(PotionEffectData.class).get();
        effects.addElement(effect);
        entity.offer(effects);

        entity.offer(Keys.DISPLAY_NAME, Text.of("PetBlockIdentifier"));
        entity.offer(Keys.CUSTOM_NAME_VISIBLE, false);
    }

    /**
     * Removes the entity from the world
     */
    @Override
    public void remove() {
        ((Living) this.getEntity()).remove();
    }

}
