package com.github.shynixn.petblocks.core.logic.persistence.configuration;

import com.github.shynixn.petblocks.api.persistence.controller.CostumeController;
import com.github.shynixn.petblocks.api.persistence.controller.EngineController;
import com.github.shynixn.petblocks.api.persistence.controller.OtherGUIItemsController;
import com.github.shynixn.petblocks.api.persistence.controller.ParticleController;
import com.github.shynixn.petblocks.core.logic.Factory;
import com.github.shynixn.petblocks.core.logic.persistence.entity.IConfig;

import java.util.List;

public abstract class PetBlocksConfig implements IConfig {
    protected final EngineController engineController = Factory.createEngineController();
    private final OtherGUIItemsController guiItemsController = Factory.createGUIItemsController();
    private final ParticleController particleController = Factory.createParticleConfiguration();
    private final CostumeController colorCostumesController = Factory.createCostumesController("color");
    private final CostumeController rareCostumesController = Factory.createCostumesController("rare");
    private final CostumeController minecraftHeadsCostumesController = Factory.createMinecraftHeadsCostumesController();

    /**
     * Reloads the config
     */
    @Override
    public void reload() {
        this.getOrdinaryCostumesController().reload();
     /*   this.colorCostumesController.reload();
        this.rareCostumesController.reload();
        this.minecraftHeadsCostumesController.reload();
        this.guiItemsController.reload();
        this.particleController.reload();
        this.engineController.reload();*/
    }

    public ParticleController getParticleController() {
        return this.particleController;
    }

    public OtherGUIItemsController getGuiItemsController() {
        return this.guiItemsController;
    }

    public EngineController getEngineController() {
        return this.engineController;
    }

    /**
     * Returns the ordinary costume controller.
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

    public String getGUITitle() {
        return this.getData("gui.settings.title");
    }

    public String getPrefix() {
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
}
