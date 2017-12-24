package com.github.shynixn.petblocks.sponge.nms.helper;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.persistence.entity.IPosition;
import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.persistence.entity.SoundMeta;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockCannonEvent;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockMoveEvent;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockRideEvent;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockWearEvent;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SoundBuilder;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeLocationBuilder;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeParticleEffectMeta;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;

public final class PetBlockHelper {
    private static final Random random = new Random();
    private static final SoundMeta explosionSound = new SoundBuilder("EXPLODE", 1.0F, 2.0F);
    private static final ParticleEffectMeta angryParticle = new SpongeParticleEffectMeta()
            .setEffectType(ParticleEffectMeta.ParticleEffectType.VILLAGER_ANGRY)
            .setOffset(2, 2, 2)
            .setSpeed(0.1)
            .setAmount(2);
    private static final ParticleEffectMeta cloud = new SpongeParticleEffectMeta()
            .setEffectType(ParticleEffectMeta.ParticleEffectType.CLOUD)
            .setOffset(1, 1, 1)
            .setSpeed(0.1)
            .setAmount(100);

    private PetBlockHelper() {
        super();
    }

    public static void playParticleEffectForPipeline(Location location, ParticleEffectMeta particleEffectMeta, PetBlock petBlock) {
        if (Config.getInstance().pet().areParticlesForOtherPlayersVisible()) {
            for (final Player player : ((World) location.getExtent()).getPlayers()) {
                ((SpongeParticleEffectMeta) particleEffectMeta).applyTo(location, player);
            }
        } else {
            ((SpongeParticleEffectMeta) particleEffectMeta).applyTo(location, (Player) petBlock.getPlayer());
        }
    }

    public static void playSoundEffectForPipeline(Location location, SoundMeta soundMeta, PetBlock petBlock) {
        if (!petBlock.getMeta().isSoundEnabled())
            return;
        try {
            if (Config.getInstance().pet().isSoundForOtherPlayersHearable()) {
                for (final Player player : ((World) location.getExtent()).getPlayers()) {
                    ((SoundBuilder) soundMeta).apply(location, player);
                }
            } else {
                ((SoundBuilder) soundMeta).apply(location, (Player) petBlock.getPlayer());
            }
        } catch (final IllegalArgumentException e) {
            PetBlocksPlugin.logger().log(Level.WARNING, "Cannot play sound " + soundMeta.getName() + " of " + petBlock.getMeta().getEngine().getGUIItem().getDisplayName().get() + '.');
            PetBlocksPlugin.logger().log(Level.WARNING, "Is this entity or sound supported by your server version? Disable it in the config.yml");
        } catch (final Exception e1) {
            PetBlocksPlugin.logger()
                    .log(Level.WARNING, "Failed playing w sound.", e1);
        }
    }

    public static int afraidWaterEffect(PetBlock petBlock, int counter) {
        final Entity entity = (Entity) petBlock.getEngineEntity();
        if (Config.getInstance().pet().isAfraidOfwater()) {
            final Optional<MatterProperty> optional = entity.getLocation().getBlockType().getProperty(MatterProperty.class);
            if (optional.get().getValue() == MatterProperty.Matter.LIQUID && counter <= 0) {
                final Vector3d vec = new Vector3d(random.nextInt(3) * isNegative(random), random.nextInt(3) * isNegative(random), random.nextInt(3) * isNegative(random));
                entity.setVelocity(vec);
                if (Config.getInstance().pet().isAfraidwaterParticles()) {
                    petBlock.getEffectPipeline().playParticleEffect(entity.getLocation(), angryParticle);
                }
                counter = 20;
            }
            counter--;
        }
        return counter;
    }

    public static int isNegative(Random rand) {
        if (rand.nextInt(2) == 0)
            return -1;
        return 1;
    }

    public static long executeMovingSound(PetBlock petBlock, long previous) {
        if (petBlock.getMeta() == null)
            return previous;
        final long milli = System.currentTimeMillis();
        if (milli - previous > 500) {
            petBlock.getEffectPipeline().playSound(petBlock.getLocation(), petBlock.getMeta().getEngine().getWalkingSound());
            return milli;
        }
        return previous;
    }

    public static int doTick(int counter, PetBlock petBlock, TickCallBack callBack) {
        final PetMeta petData = petBlock.getMeta();
        if (!getArmorstand(petBlock).isRemoved() && getArmorstand(petBlock).getPassengers().isEmpty() && getEngineEntity(petBlock) != null && getArmorstand(petBlock).getVehicle() == null) {
            IPosition location = null;
            if (petData.getAge() >= Config.getInstance().pet().getAge_largeticks())
                location = new SpongeLocationBuilder(getEngineEntity(petBlock).getLocation().getExtent().getName(), getEngineEntity(petBlock).getLocation().getX(), getEngineEntity(petBlock).getLocation().getY() - 1.2, getEngineEntity(petBlock).getLocation().getZ(), getEngineEntity(petBlock).getRotation().getX(), getEngineEntity(petBlock).getRotation().getY());
            else if (petData.getAge() <= Config.getInstance().pet().getAge_smallticks())
                location = new SpongeLocationBuilder(getEngineEntity(petBlock).getLocation().getExtent().getName(), getEngineEntity(petBlock).getLocation().getX(), getEngineEntity(petBlock).getLocation().getY() - 0.7, getEngineEntity(petBlock).getLocation().getZ(), getEngineEntity(petBlock).getRotation().getX(), getEngineEntity(petBlock).getRotation().getY());
            if (location != null)
                callBack.run(location);
            counter = doTickSounds(counter, petBlock);
        } else if (getEngineEntity(petBlock) != null) {
            getEngineEntity(petBlock).setLocation(getArmorstand(petBlock).getLocation());
        }
        try {
            if (petData.getAge() >= Config.getInstance().pet().getAge_maxticks()) {
                if (Config.getInstance().pet().isAge_deathOnMaxTicks() && !petBlock.isDieing()) {
                    petBlock.setDieing();
                }
            } else {
                boolean respawn = false;
                if (petData.getAge() < Config.getInstance().pet().getAge_largeticks()) {
                    respawn = true;
                }
                petData.setAge(petData.getAge() + 1);
                if (petData.getAge() >= Config.getInstance().pet().getAge_largeticks() && respawn) {
                    petBlock.respawn();
                }
            }
        } catch (final Exception ex) {
            PetBlocksPlugin.logger().log(Level.WARNING, "Catcher prevented server crash, please report the following error to author Shynixn!", ex);
        }
        getArmorstand(petBlock).offer(Keys.FIRE_TICKS, 0);
        if (getEngineEntity(petBlock) != null) {
            getEngineEntity(petBlock).offer(Keys.FIRE_TICKS, 0);
        }
        Sponge.getEventManager().post(new PetBlockMoveEvent(petBlock));
        return counter;
    }

    private static int doTickSounds(int counter, PetBlock petBlock) {
        final PetMeta petData = petBlock.getMeta();
        if (counter <= 0) {
            final Random random = new Random();
            if (!getEngineEntity(petBlock).isOnGround() || petData.getEngine().getEntityType().equalsIgnoreCase("ZOMBIE")) {
                petBlock.getEffectPipeline().playSound(petBlock.getLocation(), petBlock.getMeta().getEngine().getAmbientSound());
            }
            counter = 20 * random.nextInt(20) + 1;
        }
        if (getEngineEntity(petBlock).isRemoved()) {
            PetBlocksApi.getDefaultPetBlockController().remove(petBlock);
        }
        if (petData.getParticleEffectMeta() != null) {
            petBlock.getEffectPipeline().playParticleEffect(getArmorstand(petBlock).getLocation().add(0, 1, 0), petData.getParticleEffectMeta());
        }
        counter--;
        return counter;
    }

    public static void setItemConsideringAge(PetBlock petBlock) {
        final PetMeta petData = petBlock.getMeta();
        getArmorstand(petBlock).setHelmet((ItemStack) petData.getHeadItemStack());
        if (petData.getAge() >= Config.getInstance().pet().getAge_largeticks()) {
            getArmorstand(petBlock).small().set(false);
        } else {
            getArmorstand(petBlock).small().set(true);
        }
    }

    public static void setRiding(PetBlock petBlock, Player player) {
        if (getArmorstand(petBlock).getPassengers().isEmpty() && player.getPassengers().isEmpty()) {
            final PetBlockRideEvent event = new PetBlockRideEvent(petBlock, true);
            Sponge.getEventManager().post(event);
            if (!event.isCancelled()) {
                getArmorstand(petBlock).setVelocity(new Vector3d(0, 1, 0));
                getArmorstand(petBlock).addPassenger(player);
                player.closeInventory(null);
            }
        }
    }

    public static boolean setDieing(final PetBlock petBlock) {
        if (!petBlock.isDieing()) {
            petBlock.jump();
            if (petBlock.getArmorStand() != null && !getArmorstand(petBlock).isRemoved())
                getArmorstand(petBlock).setHeadRotation(new Vector3d(0, 1, 0));
            Task.builder().delayTicks(20 * 2)
                    .execute(() -> {
                        petBlock.getEffectPipeline().playParticleEffect(petBlock.getLocation(), cloud);
                        petBlock.remove();
                    }).submit(Sponge.getPluginManager().getPlugin("petblocks"));
            return true;
        }
        return petBlock.isDieing();
    }

    public static double setDamage(PetBlock petBlock, double health, double damage, TickCallBack callBack) {
        if (Config.getInstance().pet().isDesign_showDamageAnimation()) {
            callBack.run(null);
        }
        if (!Config.getInstance().pet().isCombat_invincible()) {
            health -= damage;
            if (health <= 0) {
                petBlock.setDieing();
            }
        }
        return health;
    }

    public static void launch(PetBlock petBlock, Vector3d vector) {
        final PetBlockCannonEvent event = new PetBlockCannonEvent(petBlock);
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            getEngineEntity(petBlock).setVelocity(vector);
            petBlock.getEffectPipeline().playSound(((Player) petBlock.getPlayer()).getLocation(), explosionSound);
        }
    }

    public static void wear(PetBlock petBlock, Player player, TickCallBack callBack) {
        if (getArmorstand(petBlock).getPassengers().isEmpty() && player.getPassengers().isEmpty()) {
            final PetBlockWearEvent event = new PetBlockWearEvent(petBlock, true);
            Sponge.getEventManager().post(event);
            if (!event.isCancelled()) {
                player.addPassenger(getArmorstand(petBlock));
                player.closeInventory(null);
                if (callBack != null)
                    callBack.run(null);
            }
        }
    }

    public static void jump(PetBlock petBlock) {
        getEngineEntity(petBlock).setVelocity(new Vector3d(0, 0.5, 0));
    }

    public static void teleport(PetBlock petBlock, Location location) {
        getEngineEntity(petBlock).setLocation(location);
        getArmorstand(petBlock).setLocation(location);
    }

    public static boolean isDead(PetBlock petBlock) {
        return (getEngineEntity(petBlock).isRemoved()
                || getArmorstand(petBlock).isRemoved())
                || (getEngineEntity(petBlock).getWorld().getName().equals(getArmorstand(petBlock).getWorld().getName())
                && getEngineEntity(petBlock).getLocation().getPosition().distance(getArmorstand(petBlock).getLocation().getPosition()) > 10);
    }

    public static void setDisplayName(PetBlock petBlock, String name) {
        getArmorstand(petBlock).offer(Keys.DISPLAY_NAME, Text.of(name));
        getArmorstand(petBlock).offer(Keys.CUSTOM_NAME_VISIBLE, true);
    }

    public static void eject(PetBlock petBlock, Player player, TickCallBack callBack) {
        final PetBlockWearEvent event = new PetBlockWearEvent(petBlock, false);
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            player.clearPassengers();
            if (callBack != null)
                callBack.run(null);
        }
    }

    public static void respawn(PetBlock petBlock, TickCallBack callBack) {
        final Location location = (Location) petBlock.getLocation();
        final IPosition position = new SpongeLocationBuilder();
        position.setWorldName(((World) ((Location) petBlock.getLocation()).getExtent()).getName())
                .setCoordinates(location.getX(), location.getY(), location.getZ())
                .setRotation(getEngineEntity(petBlock).getRotation().getX(), getEngineEntity(petBlock).getRotation().getY());

        petBlock.remove();
        callBack.run(position);
    }

    public static void remove(PetBlock petBlock) {
        if (petBlock.getEngineEntity() != null && !((Living) petBlock.getEngineEntity()).isRemoved()) {
            ((Living) petBlock.getEngineEntity()).remove();
        }
        if (!((Living) petBlock.getArmorStand()).isRemoved()) {
            ((Living) petBlock.getArmorStand()).remove();
        }
    }

    private static ArmorStand getArmorstand(PetBlock petBlock) {
        return (ArmorStand) petBlock.getArmorStand();
    }

    private static Living getEngineEntity(PetBlock petBlock) {
        return (Living) petBlock.getEngineEntity();
    }

    @FunctionalInterface
    public interface TickCallBack {
        void run(IPosition position);
    }
}
