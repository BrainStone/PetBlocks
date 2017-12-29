package com.github.shynixn.petblocks.core.logic.persistence.configuration;

import com.github.shynixn.petblocks.api.persistence.controller.CostumeController;
import com.github.shynixn.petblocks.api.persistence.controller.EngineController;
import com.github.shynixn.petblocks.api.persistence.controller.OtherGUIItemsController;
import com.github.shynixn.petblocks.api.persistence.controller.ParticleController;
import com.github.shynixn.petblocks.core.logic.persistence.entity.IConfig;

import java.util.List;

public abstract class PetBlocksConfig<T> implements IConfig {
    private final ParticleController particleController = null;
    private final CostumeController colorCostumesController = null;
    private final CostumeController rareCostumesController = null;
    private final CostumeController minecraftHeadsCostumesController = null;

    private PetConf petConf = new PetConf();

    /**
     * Reloads the config
     */
    @Override
    public void reload() {
        this.getOrdinaryCostumesController().reload();
       // this.getGuiItemsController().reload();
        this.getEngineController().reload();
     /*   this.colorCostumesController.reload();
        this.rareCostumesController.reload();
        this.minecraftHeadsCostumesController.reload();
        this.guiItemsController.reload();
        this.particleController.reload();
        this.engineController.reload();*/
    }

    /**
     * Returns the pet config.
     *
     * @return pet
     */
    public PetConf pet() {
        return petConf;
    }

    public ParticleController getParticleController() {
        return this.particleController;
    }

    public abstract OtherGUIItemsController getGuiItemsController();


    public abstract EngineController getEngineController();

    /**
     * Returns the ordinary costume controller.
     *
     * @return controller
     */
    public abstract CostumeController getOrdinaryCostumesController();

    public CostumeController getColorCostumesController() {
        return this.colorCostumesController;
    }

    public CostumeController getRareCostumesController() {
        return this.rareCostumesController;
    }

    public CostumeController getMinecraftHeadsCostumesController() {
        return this.minecraftHeadsCostumesController;
    }

    public int getDefaultEngine() {
        return this.getData("gui.settings.default-engine");
    }

    /**
     * Returns if copySkin is enabled
     *
     * @return copySkin
     */
    public boolean isCopySkinEnabled() {
        return this.getData("gui.settings.copy-skin");
    }

    /**
     * Returns if lore is enabled
     *
     * @return lore
     */
    boolean isLoreEnabled() {
        return this.getData("gui.settings.lore");
    }

    /**
     * Returns if emptyClickBack is enabled
     *
     * @return enabled
     */
    public boolean isEmptyClickBackEnabled() {
        return this.getData("gui.settings.click-empty-slot-back");
    }

    /**
     * Returns if disable item is enabled
     *
     * @return displayItem
     */
    public boolean isOnlyDisableItemEnabled() {
        return this.getData("gui.settings.use-only-disable-pet-item");
    }

    public String getPermissionIconYes() {
        return this.getData("messages.perms-ico-yes");
    }

    public String getPermissionIconNo() {
        return this.getData("messages.perms-ico-no");
    }

    public String getNoPermission() {
        return this.getData("messages.no-perms");
    }

    public T getGUITitle() {
        return this.getData("gui.settings.title");
    }

    public T getPrefix() {
        return this.getData("messages.prefix");
    }

    public String getDefaultPetName() {
        return this.getData("messages.default-petname");
    }

    public String getNamingMessage() {
        return this.getData("messages.naming-message");
    }

    public String getNamingSuccessMessage() {
        return this.getData("messages.naming-success");
    }

    public String getNamingErrorMessage() {
        return this.getData("messages.naming-error");
    }

    public String getSkullNamingMessage() {
        return this.getData("messages.skullnaming-message");
    }

    public String getSkullNamingSuccessMessage() {
        return this.getData("messages.skullnaming-success");
    }

    public String getSkullNamingErrorMessage() {
        return this.getData("messages.skullnaming-error");
    }

    public boolean isJoin_enabled() {
        return (boolean) this.getData("join.enabled");
    }

    public boolean isJoin_overwriteExistingPet() {
        return (boolean) this.getData("join.overwrite-previous-pet");
    }

    public boolean isChat_async() {
        return (boolean) this.getData("chat.async");
    }

    public boolean isChatHighestPriority() {
        return (boolean) this.getData("chat.highest-priority");
    }

    public List<String> getExcludedWorlds() {
        return this.getDataAsStringList("world.excluded");
    }

    public List<String> getIncludedWorlds() {
        return this.getDataAsStringList("world.included");
    }

    public List<String> getExcludedRegion() {
        return this.getDataAsStringList("region.excluded");
    }

    public List<String> getIncludedRegions() {
        return this.getDataAsStringList("region.included");
    }

    /**
     * Returns if metrics is enabled
     *
     * @return enabled
     */
    public boolean isMetricsEnabled() {
        return (boolean) this.getData("metrics");
    }

    private List<String> getDataAsStringList(String path) {
        return ((List<String>) this.getData(path));
    }

    public class PetConf {

        /**
         * Returns the forbidden pet names.
         *
         * @return names
         */
        public List<String> getPetNameBlackList() {
            return PetBlocksConfig.this.getData("pet.design.petname-blacklist");
        }

        /**
         * Returns the amount of blocks the pet has to stay away from the player.
         *
         * @return amount
         */
        public int getBlocksAwayFromPlayer() {
            return (int) PetBlocksConfig.this.getData("pet.follow.amount-blocks-away");
        }

        /**
         * Returns if feeding is enabled.
         *
         * @return feeding
         */
        public boolean isFeedingEnabled() {
            return PetBlocksConfig.this.getData("pet.feeding.enabled");
        }

        public boolean isAfraidOfwater() {
            return (boolean) PetBlocksConfig.this.getData("pet.follow.afraid-water");
        }

        public boolean isAfraidwaterParticles() {
            return (boolean) PetBlocksConfig.this.getData("pet.follow.afraid-water-particles");
        }

        public int getAge_smallticks() {
            return (int) PetBlocksConfig.this.getData("pet.age.small-ticks");
        }

        public int getAge_largeticks() {
            return (int) PetBlocksConfig.this.getData("pet.age.large-ticks");
        }

        public int getAge_maxticks() {
            return (int) PetBlocksConfig.this.getData("pet.age.max-ticks");
        }

        public boolean isAge_deathOnMaxTicks() {
            return (boolean) PetBlocksConfig.this.getData("pet.age.death-on-maxticks");
        }

        public double getCombat_health() {
            return (double) PetBlocksConfig.this.getData("pet.combat.health");
        }

        public boolean isCombat_invincible() {
            return (boolean) PetBlocksConfig.this.getData("pet.combat.invincible");
        }

        public int getFollow_maxRangeTeleport() {
            return (int) PetBlocksConfig.this.getData("pet.follow.max-range-teleport");
        }

        public boolean isFollow_fallOffHead() {
            return (boolean) PetBlocksConfig.this.getData("pet.follow.teleport-fall");
        }

        public boolean isFollow_carry() {
            return (boolean) PetBlocksConfig.this.getData("pet.follow.carry");
        }

        public int getDesign_maxPetNameLength() {
            return (int) PetBlocksConfig.this.getData("pet.design.max-petname-length");
        }

        public boolean isDesign_showDamageAnimation() {
            return (boolean) PetBlocksConfig.this.getData("pet.design.show-damage-animation");
        }

        public boolean isSoundForOtherPlayersHearable() {
            return (boolean) PetBlocksConfig.this.getData("pet.design.sounds-other-players");
        }

        /**
         * Returns if particles are visible for other players.
         *
         * @return visible
         */
        public boolean areParticlesForOtherPlayersVisible() {
            return PetBlocksConfig.this.getData("pet.design.particles-other-players");
        }

        public double getModifier_petriding() {
            return (double) PetBlocksConfig.this.getData("pet.modifier.riding-speed");
        }

        public double getModifier_petwalking() {
            return (double) PetBlocksConfig.this.getData("pet.modifier.walking-speed");
        }

        public double getModifier_petclimbing() {
            return (double) PetBlocksConfig.this.getData("pet.modifier.climbing-height");
        }

        public boolean isFollow_wallcolliding() {
            return (boolean) PetBlocksConfig.this.getData("pet.follow.flying-wall-colliding");
        }

        public boolean isFleesInCombat() {
            return (boolean) PetBlocksConfig.this.getData("pet.flee.flees-in-combat");
        }

        public int getReappearsInSeconds() {
            return (int) PetBlocksConfig.this.getData("pet.flee.reappears-in-seconds");
        }

        public int getWarpDelay() {
            return (int) PetBlocksConfig.this.getData("pet.warp.teleports-in-seconds");
        }
    }
}
