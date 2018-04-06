package com.github.shynixn.petblocks.sponge.nms.v1_12_R1;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.business.entity.PetBlockPartEntity;
import com.github.shynixn.petblocks.api.business.enumeration.RideType;
import com.github.shynixn.petblocks.api.persistence.entity.IPosition;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockMoveEvent;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockSpawnEvent;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.business.helper.ExtensionMethodsKt;
import com.github.shynixn.petblocks.sponge.logic.persistence.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeLocationBuilder;
import com.github.shynixn.petblocks.sponge.nms.helper.PetBlockPartWrapper;
import com.github.shynixn.petblocks.sponge.nms.helper.PetBlockWrapper;
import net.minecraft.anchor.v1_12_mcpR1.entity.Entity;
import net.minecraft.anchor.v1_12_mcpR1.entity.EntityLivingBase;
import net.minecraft.anchor.v1_12_mcpR1.entity.SharedMonsterAttributes;
import net.minecraft.anchor.v1_12_mcpR1.entity.item.EntityArmorStand;
import net.minecraft.anchor.v1_12_mcpR1.entity.player.EntityPlayer;
import net.minecraft.anchor.v1_12_mcpR1.entity.player.EntityPlayerMP;
import net.minecraft.anchor.v1_12_mcpR1.nbt.NBTTagCompound;
import net.minecraft.anchor.v1_12_mcpR1.network.play.server.SPacketEntityTeleport;
import net.minecraft.anchor.v1_12_mcpR1.util.math.MathHelper;
import net.minecraft.anchor.v1_12_mcpR1.util.math.RayTraceResult;
import net.minecraft.anchor.v1_12_mcpR1.util.math.Vec3d;
import net.minecraft.anchor.v1_12_mcpR1.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;

import java.util.Random;

final class CustomGroundArmorstand extends EntityArmorStand {
    private PetBlock<Player, Location<org.spongepowered.api.world.World>> petBlock;

    private boolean isSpecial;
    private boolean isGround;
    private boolean firstRide = true;
    private PetBlockPartEntity rabbit;
    private int counter;
    private Vector3d bumper;

    public CustomGroundArmorstand(World world) {
        super(world);
    }

    public CustomGroundArmorstand(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public CustomGroundArmorstand(Transform<org.spongepowered.api.world.World> location, PetBlock<Player, Location<org.spongepowered.api.world.World>> petBlock) {
        super((World) location.getExtent());
        this.isSpecial = true;
        this.petBlock = petBlock;

        this.spawn(location);
    }

    @Override
    protected void updateEntityActionState() {
        if (this.isSpecial) {
            ((Living) this.petBlock.getArmorStand()).getHealthData().health().set(((Living) this.petBlock.getArmorStand()).getHealthData().maxHealth().getMaxValue());
            PetMeta petData = petBlock.getMeta();
            ArmorStand armorStand = (ArmorStand) petBlock.getArmorStand();
            Living engine = (Living) petBlock.getEngineEntity();

            if (!armorStand.isRemoved() && armorStand.getPassengers().isEmpty() && engine != null && !armorStand.getVehicle().isPresent()) {
                IPosition location = null;
                if (petData.getAge() >= Config.INSTANCE.getAge_largeticks()) {
                    location = new SpongeLocationBuilder(engine.getLocation().getExtent().getName(), engine.getLocation().getX(), engine.getLocation().getY(), engine.getLocation().getZ(), engine.getHeadRotation().getX(), engine.getHeadRotation().getZ());
                    location.setY(location.getY() - 1.2);
                } else if (petData.getAge() <= Config.INSTANCE.getAge_smallticks())
                    location = new SpongeLocationBuilder(engine.getLocation().getExtent().getName(), engine.getLocation().getX(), engine.getLocation().getY() - 0.7, engine.getLocation().getZ(), engine.getRotation().getX(), engine.getRotation().getY());
                if (location != null) {
                    this.setLocationAndAngles(location.getX(), location.getY() + 0.2, location.getZ(), (float) location.getYaw(), (float) location.getPitch());
                    final SPacketEntityTeleport animation = new SPacketEntityTeleport(this);
                    for (final Player player : ((ArmorStand) this.petBlock.getArmorStand()).getWorld().getPlayers()) {
                        ((EntityPlayerMP) player).connection.sendPacket(animation);
                    }
                }
                if (counter <= 0) {
                    final Random random = new Random();
                    if (!engine.isOnGround() || petData.getEngine().getEntityType().equalsIgnoreCase("ZOMBIE")) {
                        petBlock.getEffectPipeline().playSound(petBlock.getLocation(), petBlock.getMeta().getEngine().getAmbientSound());
                    }
                    counter = 20 * random.nextInt(20) + 1;
                }
                if (engine.isRemoved()) {
                    petBlock.remove();
                    PetBlocksApi.getDefaultPetBlockController().remove((PetBlock<Object, Object>)(Object)petBlock);
                }
                if (petData.getParticleEffectMeta() != null) {
                    petBlock.getEffectPipeline().playParticleEffect(armorStand.getLocation().add(0, 1, 0), petData.getParticleEffectMeta());
                }
                counter--;
            } else if (engine != null) {
                engine.setLocation(armorStand.getLocation());
            }
            try {
                if (petData.getAge() >= Config.INSTANCE.getAge_maxticks()) {
                    if (Config.INSTANCE.isAge_deathOnMaxTicks() && !petBlock.isDieing()) {
                        petBlock.setDieing();
                    }
                } else {
                    boolean respawn = false;
                    if (petData.getAge() < Config.INSTANCE.getAge_largeticks()) {
                        respawn = true;
                    }
                    petData.setAge(petData.getAge() + 1);
                    if (petData.getAge() >= Config.INSTANCE.getAge_largeticks() && respawn) {
                        petBlock.respawn();
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            armorStand.offer(Keys.FIRE_TICKS, 0);
            if (engine != null) {
                engine.offer(Keys.FIRE_TICKS, 0);
            }
            Sponge.getEventManager().post(new PetBlockMoveEvent(petBlock));
        }
        super.updateEntityActionState();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.hasHumanPassenger() != null) {
            if (this.petBlock.getMeta().getEngine().getRideType() == RideType.RUNNING) {
                EntityLivingBase entityLiving = (EntityLivingBase) this.hasHumanPassenger();
                this.rotationYaw = this.prevRotationYaw = entityLiving.rotationYaw;
                this.rotationPitch = entityLiving.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.rotationYawHead = this.renderYawOffset = this.rotationYaw;

                strafe = entityLiving.moveStrafing * 0.5F;
                forward = entityLiving.moveForward;

                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }
                if (this.onGround && this.isJumping()) {
                    this.motionY = 0.5D;
                }

                this.stepHeight = (float) Config.INSTANCE.getModifier_petclimbing();
                this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
                if (!this.world.isRemote) {
                    this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                    super.travel(strafe * (float) Config.INSTANCE.getModifier_petriding() * 0.75F, forward * (float) Config.INSTANCE.getModifier_petriding() * 0.75F, vertical);
                }

                this.prevLimbSwingAmount = this.limbSwingAmount;
                double d1 = this.posX - this.prevPosX;
                double d0 = this.posZ - this.prevPosZ;
                float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

                if (f2 > 1.0F) {
                    f2 = 1.0F;
                }

                this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
                this.limbSwing += this.limbSwingAmount;
            } else {
                EntityLivingBase entityLiving = (EntityLivingBase) this.hasHumanPassenger();
                this.rotationYaw = this.prevRotationYaw = entityLiving.rotationYaw;
                this.rotationPitch = entityLiving.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.rotationYawHead = this.renderYawOffset = this.rotationYaw;

                final float side = entityLiving.moveStrafing * 0.5F;
                final float forw = entityLiving.moveForward;
                Vector3d v = new Vector3d();
                org.spongepowered.api.world.World world = (org.spongepowered.api.world.World) this.world;
                final SpongeLocationBuilder l = new SpongeLocationBuilder(world.getName(), this.posX, this.posY, this.posZ, 0, 0);

                if (side < 0.0F) {
                    l.setYaw(entityLiving.rotationYaw - 90);
                    v = v.add(l.getDirection().normalize().mul(-0.5));
                } else if (side > 0.0F) {
                    l.setYaw(entityLiving.rotationYaw + 90);
                    v = v.add(l.getDirection().normalize().mul(-0.5));
                }

                if (forw < 0.0F) {
                    l.setYaw(entityLiving.rotationYaw);
                    v = v.add(l.getDirection().normalize().mul(0.5));
                } else if (forw > 0.0F) {
                    l.setYaw(entityLiving.rotationYaw);
                    v = v.add(l.getDirection().normalize().mul(0.5));
                }
                if (this.firstRide) {
                    this.firstRide = false;
                    v = new Vector3d(v.getX(), 1, v.getZ());
                }
                if (this.isJumping()) {
                    v = new Vector3d(v.getX(), 0.5F, v.getZ());
                    this.isGround = true;
                    ((PetBlockWrapper) (Object) this.petBlock).setHitflor(false);
                } else if (this.isGround) {
                    v = new Vector3d(v.getX(), -0.2F, v.getZ());
                }
                if (((PetBlockWrapper) (Object) this.petBlock).getHitflor()) {
                    v = new Vector3d(v.getX(), 0, v.getZ());
                    v = v.mul(2.25).mul(Config.INSTANCE.getModifier_petriding());
                    l.addCoordinates(v.getX(), v.getY(), v.getZ());
                    this.setPosition(l.getX(), l.getY(), l.getZ());
                } else {
                    v = v.mul(2.25).mul(Config.INSTANCE.getModifier_petriding());
                    l.addCoordinates(v.getX(), v.getY(), v.getZ());
                    this.setPosition(l.getX(), l.getY(), l.getZ());
                }
                final Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
                final Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
                final RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec3d, vec3d1);
                if (movingobjectposition == null) {
                    this.bumper = l.toVector();
                } else {
                    if (this.bumper != null && Config.INSTANCE.isFollow_wallcolliding())
                        this.setPosition(this.bumper.getX(), this.bumper.getY(), this.bumper.getZ());
                }
            }
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    public void spawn(Transform<org.spongepowered.api.world.World> location) {
        final PetBlockSpawnEvent event = new PetBlockSpawnEvent(this.petBlock);
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            if (this.petBlock.getMeta().getEngine().getEntityType().equalsIgnoreCase("RABBIT")) {
                this.rabbit = new PetBlockPartWrapper((Living) (Object) new CustomRabbit(this.petBlock.getPlayer(), this.petBlock));

            } else if (this.petBlock.getMeta().getEngine().getEntityType().equalsIgnoreCase("ZOMBIE")) {
                //  this.rabbit = new CustomZombie(this.owner, this);
            }

            this.dead = false;
            this.isDead = false;

            this.rabbit.spawn(location);
            final World mcWorld = (World) location.getExtent();
            this.setPosition(location.getLocation().getX(), location.getLocation().getY(), location.getLocation().getZ());
            mcWorld.spawnEntity(this);
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("invulnerable", true);
            compound.setBoolean("Invisible", true);
            compound.setBoolean("PersistenceRequired", true);
            compound.setBoolean("ShowArms", true);
            compound.setBoolean("NoBasePlate", true);
            this.readEntityFromNBT(compound);
            ((ArmorStand) this.petBlock.getArmorStand()).gravity().set(false);
            ((ArmorStand) this.petBlock.getArmorStand()).getBodyPartRotationalData().bodyRotation().set(new Vector3d(0, 0, 2878));
            ((ArmorStand) this.petBlock.getArmorStand()).getBodyPartRotationalData().leftArmDirection().set(new Vector3d(2878, 0, 0));
            ((ArmorStand) this.petBlock.getArmorStand()).offer(Keys.CUSTOM_NAME_VISIBLE, true);
            ((ArmorStand) this.petBlock.getArmorStand()).offer(Keys.DISPLAY_NAME, ExtensionMethodsKt.translateToText(this.petBlock.getMeta().getPetDisplayName()));
            ((PetBlockWrapper) (Object) this.petBlock).setHealth(Config.INSTANCE.getCombat_health());
            if (this.petBlock.getMeta() == null)
                return;
            ((ArmorStand) this.petBlock.getArmorStand()).setHelmet((ItemStack) this.petBlock.getMeta().getHeadItemStack());
            if (this.petBlock.getMeta().getAge() >= Config.INSTANCE.getAge_largeticks()) {
                ((ArmorStand) this.petBlock.getArmorStand()).small().set(false);
            } else {
                ((ArmorStand) this.petBlock.getArmorStand()).small().set(true);
            }
        }
    }

    private boolean isJumping() {
        return !this.getPassengers().isEmpty() && ((EntityLivingBase) this.getPassengers().get(0)).isJumping;
    }

    private Player hasHumanPassenger() {
        for (final Entity entity : this.getPassengers()) {
            if (entity instanceof EntityPlayer) {
                return (Player) entity;
            }
        }
        return null;
    }
}