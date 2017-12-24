package com.github.shynixn.petblocks.sponge.nms.v1_11_R1;

import com.flowpowered.math.vector.Vector3d;
import com.github.shynixn.petblocks.api.business.entity.EffectPipeline;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.business.entity.PetBlockPartEntity;
import com.github.shynixn.petblocks.api.business.enumeration.RideType;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.sponge.event.PetBlockSpawnEvent;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.entity.Pipeline;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeLocationBuilder;
import com.github.shynixn.petblocks.sponge.nms.helper.PetBlockHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

final class CustomGroundArmorstand extends EntityArmorStand implements PetBlock {
    private PetMeta petMeta;
    private Player owner;

    private boolean isSpecial;
    private boolean isGround;
    private boolean firstRide = true;
    private PetBlockPartEntity rabbit;
    private int counter;

    private boolean isDieing;
    private double health = 20.0;

    private boolean hitflor;
    private Vector3d bumper;

    private Pipeline pipeline;

    public CustomGroundArmorstand(World world) {
        super(world);
    }

    public CustomGroundArmorstand(Location location, PetMeta meta) {
        super((World) location.getExtent());
        this.isSpecial = true;
        this.petMeta = meta;
        this.owner = this.petMeta.getPlayerMeta().getPlayer();
        if (this.petMeta.getEngine().getEntityType().equalsIgnoreCase("RABBIT")) {
            this.rabbit = new CustomRabbit(this.owner, this);

        } else if (this.petMeta.getEngine().getEntityType().equalsIgnoreCase("ZOMBIE")) {
            //  this.rabbit = new CustomZombie(this.owner, this);
        }

        this.pipeline = new Pipeline(this);
        this.spawn(location);
    }

    public CustomGroundArmorstand(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    private boolean isJumping() {
        return !this.getPassengers().isEmpty() && ((EntityLiving) this.getPassengers().get(0)).isJumping;
    }

    private Human hasHumanPassenger() {
        for (final net.minecraft.entity.Entity entity : this.getPassengers()) {
            if (entity instanceof Human) {
                return (Human) entity;
            }
        }
        return null;
    }

    @Override
    protected void updateEntityActionState() {
        if (this.isSpecial) {
            this.counter = PetBlockHelper.doTick(this.counter, this, location -> {
                this.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), (float) location.getYaw(), (float) location.getPitch());
                final SPacketEntityTeleport animation = new SPacketEntityTeleport(this);
                for (final Player player : ((ArmorStand) this.getArmorStand()).getWorld().getPlayers()) {
                    ((EntityPlayerMP) player).connection.sendPacket(animation);
                }
            });
        }
        super.updateEntityActionState();
    }

    /**
     * Moves the entity based on the specified heading.
     *
     * @param strafe  sideMot
     * @param forward forward
     */
    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        if (this.hasHumanPassenger() != null) {
            if (this.petMeta.getEngine().getRideType() == RideType.RUNNING) {
                EntityLiving entityLiving = (EntityLiving) this.hasHumanPassenger();
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

                this.stepHeight = (float) Config.getInstance().pet().getModifier_petclimbing();

                this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
                if (this.canPassengerSteer()) {
                    this.setAIMoveSpeed((float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                    super.moveEntityWithHeading(strafe * (float) Config.getInstance().pet().getModifier_petriding(), forward * (float) Config.getInstance().pet().getModifier_petriding());
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
                EntityLiving entityLiving = (EntityLiving) this.hasHumanPassenger();
                final float side = entityLiving.moveStrafing * 0.5F;
                final float forw = entityLiving.moveForward;
                Vector3d v = new Vector3d();
                org.spongepowered.api.world.World world = (org.spongepowered.api.world.World) this.world;
                final SpongeLocationBuilder l = new SpongeLocationBuilder(world.getName(), this.posX, this.posY, this.posZ, 0, 0);

                if (side < 0.0F) {
                    l.setYaw(entityLiving.rotationYaw - 90);
                    v.add(l.getDirection().normalize().mul(-0.5));
                } else if (side > 0.0F) {
                    l.setYaw(entityLiving.rotationYaw + 90);
                    v.add(l.getDirection().normalize().mul(-0.5));
                }

                if (forw < 0.0F) {
                    l.setYaw(entityLiving.rotationYaw);
                    v.add(l.getDirection().normalize().mul(0.5));
                } else if (forw > 0.0F) {
                    l.setYaw(entityLiving.rotationYaw);
                    v.add(l.getDirection().normalize().mul(0.5));
                }

                this.rotationYaw = this.prevRotationYaw = entityLiving.rotationYaw - 180;
                this.rotationPitch = entityLiving.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                if (this.firstRide) {
                    this.firstRide = false;
                    v = new Vector3d(v.getX(), 1, v.getY());
                }

                if (this.isJumping()) {
                    v = new Vector3d(v.getX(), 0.5F, v.getY());
                    this.isGround = true;
                    this.hitflor = false;
                } else if (this.isGround) {
                    v = new Vector3d(v.getX(), -0.2F, v.getY());
                }

                if (this.hitflor) {
                    v = new Vector3d(v.getX(), 0, v.getY());
                    v = v.mul(2.25).mul(Config.getInstance().pet().getModifier_petriding());
                    l.addCoordinates(v.getX(), v.getY(), v.getZ());
                    this.setPosition(l.getX(), l.getY(), l.getZ());
                } else {
                    v = v.mul(2.25).mul(Config.getInstance().pet().getModifier_petriding());
                    l.addCoordinates(v.getX(), v.getY(), v.getZ());
                    this.setPosition(l.getX(), l.getY(), l.getZ());
                }

                final Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
                final Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
                final RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec3d, vec3d1);
                if (movingobjectposition == null) {
                    this.bumper = l.toVector();
                } else {
                    if (this.bumper != null && Config.getInstance().pet().isFollow_wallcolliding())
                        this.setPosition(this.bumper.getX(), this.bumper.getY(), this.bumper.getZ());
                }
            }
        } else {
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    public void spawn(Location location) {
        final PetBlockSpawnEvent event = new PetBlockSpawnEvent(this);
        Sponge.getEventManager().post(event);
        if (!event.isCancelled()) {
            this.rabbit.spawn(location);
            final World mcWorld = (World) location.getExtent();
            this.setPosition(location.getX(), location.getY(), location.getZ());
            mcWorld.spawnEntity(this);
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("invulnerable", true);
            compound.setBoolean("Invisible", true);
            compound.setBoolean("PersistenceRequired", true);
            compound.setBoolean("ShowArms", true);
            compound.setBoolean("NoBasePlate", true);
            this.readEntityFromNBT(compound);
            ((ArmorStand) this.getArmorStand()).getBodyPartRotationalData().bodyRotation().set(new Vector3d(0, 0, 2878));
            ((ArmorStand) this.getArmorStand()).getBodyPartRotationalData().leftArmDirection().set(new Vector3d(2878, 0, 0));
            ((ArmorStand) this.getArmorStand()).offer(Keys.CUSTOM_NAME_VISIBLE, true);
            ((ArmorStand) this.getArmorStand()).offer(Keys.DISPLAY_NAME, Text.of(this.petMeta.getPetDisplayName()));
            this.health = Config.getInstance().pet().getCombat_health();
            if (this.petMeta == null)
                return;
            PetBlockHelper.setItemConsideringAge(this);
        }
    }

    @Override
    public void teleportWithOwner(Object mLocation) {
        final Location location = (Location) mLocation;
        final EntityPlayer player = (EntityPlayer) this.owner;
      /*  player.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        final PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(player);
        for (final Player player1 : this.owner.getWorld().getPlayers()) {
            ((CraftPlayer) player1).getHandle().playerConnection.sendPacket(teleport);
        }*/
    }

    @Override
    public void damage(double amount) {
        if (amount < -1.0) {
            this.hitflor = true;
        } else {
            this.health = PetBlockHelper.setDamage(this, this.health, amount, location -> {
            });
        }
    }

    @Override
    public void respawn() {
        PetBlockHelper.respawn(this, position -> CustomGroundArmorstand.this.spawn(((SpongeLocationBuilder) position).toLocation()));
    }

    @Override
    public void setDieing() {
        this.isDieing = PetBlockHelper.setDieing(this);
    }

    @Override
    public boolean isDieing() {
        return this.isDieing;
    }

    /**
     * Returns the pipeline for managed effect playing.
     *
     * @return effectPipeLine
     */
    @Override
    public EffectPipeline getEffectPipeline() {
        return this.pipeline;
    }

    /**
     * Lets the petblock perform a jump
     */
    @Override
    public void jump() {
        PetBlockHelper.jump(this);
    }

    /**
     * Returns if the petblock is already removed or dead
     *
     * @return dead
     */
    @Override
    public boolean isDead() {
        return PetBlockHelper.isDead(this);
    }

    /**
     * Lets the given player ride on the petblock
     *
     * @param player player
     */
    @Override
    public void ride(Object player) {
        PetBlockHelper.setRiding(this, (Player) player);
    }

    /**
     * Lets the given player wear the petblock
     *
     * @param player oplayer
     */
    @Override
    public void wear(Object player) {
        if (this.getPassengers().isEmpty() && ((Player) player).getPassengers().isEmpty()) {
            final ArmorStand armorStand = (ArmorStand) this.getArmorStand();
            armorStand.offer(Keys.ARMOR_STAND_MARKER, true);
            armorStand.offer(Keys.CUSTOM_NAME_VISIBLE, false);
            PetBlockHelper.wear(this, (Player) player, null);
        }
    }

    /**
     * Ejects the given player riding from the petblock
     *
     * @param player player
     */
    @Override
    public void eject(Object player) {
        final ArmorStand armorStand = (ArmorStand) this.getArmorStand();
        armorStand.offer(Keys.ARMOR_STAND_MARKER, false);
        armorStand.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        PetBlockHelper.eject(this, (Player) player, null);
    }

    /**
     * Sets the displayName of the petblock
     *
     * @param name name
     */
    @Deprecated
    @Override
    public void setDisplayName(String name) {
        this.setCustomName(name);
    }

    /**
     * Sets the displayName of the petblock.
     *
     * @param name name
     */
    @Override
    public void setCustomName(String name) {
        PetBlockHelper.setDisplayName(this, name);
    }

    /**
     * Returns the displayName of the petblock.
     *
     * @return name
     */
    @Override
    public String getCustomName() {
        return ((ArmorStand) this.getArmorStand()).get(Keys.DISPLAY_NAME).get().toPlain();
    }

    /**
     * Returns the armorstand of the petblock
     *
     * @return armorstand
     */
    @Override
    public Object getArmorStand() {
        return this;
    }

    /**
     * Sets the velocity of the petblock
     *
     * @param vector vector
     */
    @Override
    public void setVelocity(Object vector) {
        PetBlockHelper.launch(this, (Vector3d) vector);
    }

    /**
     * Teleports the the petblock to the given location
     *
     * @param location location
     */
    @Override
    public void teleport(Object location) {
        PetBlockHelper.teleport(this, (Location) location);
    }

    /**
     * Returns the meta of the petblock
     *
     * @return meta
     */
    @Override
    public PetMeta getMeta() {
        return this.petMeta;
    }

    /**
     * Returns the owner of the petblock
     *
     * @return player
     */
    @Override
    public Object getPlayer() {
        return this.owner;
    }

    /**
     * Removes the petblock
     */
    @Override
    public void remove() {
        PetBlockHelper.remove(this);
    }

    /**
     * Returns the entity being used as engine
     *
     * @return entity
     */
    @Override
    public Object getEngineEntity() {
        if (this.rabbit == null)
            return null;
        return this.rabbit.getEntity();
    }

    /**
     * Returns the location of the entity
     *
     * @return position
     */
    @Override
    public Object getLocation() {
        return ((ArmorStand) this.getArmorStand()).getLocation();
    }
}