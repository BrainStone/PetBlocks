package com.github.shynixn.petblocks.sponge.logic.business.configuration;

import com.github.shynixn.petblocks.api.persistence.controller.CostumeController;
import com.github.shynixn.petblocks.api.persistence.controller.EngineController;
import com.github.shynixn.petblocks.api.persistence.controller.OtherGUIItemsController;
import com.github.shynixn.petblocks.api.persistence.controller.ParticleController;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.PetBlocksConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
@Singleton
public class Config extends PetBlocksConfig<Text> {

    private static Config instance;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Inject
    private PluginContainer plugin;

    @Inject
    private Logger logger;

    private ConfigurationNode node;

    @Inject
    private SpongeCostumeConfiguration blockCostumeConfiguration;

    @Inject
    private SpongeCostumeConfiguration colorCostumeConfiguration;

    @Inject
    private SpongeCostumeConfiguration playerHeadCostumeConfiguration;

    @Inject
    private SpongeEngineConfiguration engineConfiguration;

    @Inject
    private SpongeMinecraftHeadsConfiguration minecraftHeadsConfiguration;

    @Inject
    private SpongeFixedItemConfiguration fixedItemConfiguration;

    @Inject
    private SpongeParticleConfiguration particleConfiguration;

    @Override
    public EngineController getEngineController() {
        return this.engineConfiguration;
    }

    @Deprecated
    public static Config getInstance() {
        return instance;
    }

    public Config() {
        instance = this;
    }

    public Path getPrivateConfigDir() {
        return this.privateConfigDir;
    }

    /**
     * Reloads the config
     */
    @Override
    public void reload() {
        final Path defaultConfig = this.privateConfigDir.resolve("config.yml");
        if (!Files.exists(defaultConfig)) {
            this.logger.log(Level.INFO, "Converting config....");
            try {
                this.plugin.getAsset("config.yml").get().copyToFile(defaultConfig);
            } catch (final IOException e) {
                this.logger.log(Level.WARNING, "Failed to create config.yml.", e);
            }
        }
        final ConfigurationLoader loader = YAMLConfigurationLoader.builder().setPath(defaultConfig).build();
        try {
            this.node = loader.load();
        } catch (final IOException e) {
            this.logger.log(Level.WARNING, "Failed to reload config.yml.", e);
        }
        this.fixedItemConfiguration.reload();
        this.getColorCostumesController().reload();
        this.getRareCostumesController().reload();
        this.minecraftHeadsConfiguration.reload();
        this.particleConfiguration.reload();
        super.reload();
    }

    /**
     * Returns the particle controller.
     *
     * @return controller
     */
    @Override
    public ParticleController getParticleController() {
        return particleConfiguration;
    }

    @Override
    public OtherGUIItemsController getGuiItemsController() {
        return this.fixedItemConfiguration;
    }

    /**
     * Returns the ordinary costume controller.
     *
     * @return controller
     */
    @Override
    public CostumeController getOrdinaryCostumesController() {
        if (this.blockCostumeConfiguration.costumeCategory == null) {
            this.blockCostumeConfiguration.costumeCategory = "ordinary";
        }
        return this.blockCostumeConfiguration;
    }

    /**
     * Returns the color costume controller.
     *
     * @return controller
     */
    @Override
    public CostumeController getColorCostumesController() {
        if (this.colorCostumeConfiguration.costumeCategory == null) {
            this.colorCostumeConfiguration.costumeCategory = "color";
        }
        return this.colorCostumeConfiguration;
    }

    /**
     * Returns the rare costume controller.
     *
     * @return controller
     */
    @Override
    public CostumeController getRareCostumesController() {
        if (this.playerHeadCostumeConfiguration.costumeCategory == null) {
            this.playerHeadCostumeConfiguration.costumeCategory = "rare";
        }
        return this.playerHeadCostumeConfiguration;
    }

    /**
     * Returns the minecraft heads controller.
     *
     * @return controller
     */
    @Override
    public CostumeController getMinecraftHeadsCostumesController() {
        return this.minecraftHeadsConfiguration;
    }

    /**
     * Returns the configuration data from the config.
     *
     * @param path path
     * @return result
     */
    @Override
    public <T> T getData(String path) {
        if (this.node == null)
            return null;
        final String[] items = path.split(Pattern.quote("."));
        final ConfigurationNode targetNode = this.node.getNode((Object[]) items);
        Object data = targetNode.getValue();
        if (data instanceof String) {
            String s = (String) data;
            s = s.replace('&', '§');
            data = TextSerializers.LEGACY_FORMATTING_CODE.deserialize(s);
        }
        return (T) data;
    }

    public boolean allowRidingOnRegionChanging() {
        return true;
    }

    public boolean allowPetSpawning(Location location) {
        final List<String> includedWorlds = this.getIncludedWorlds();
        final List<String> excludedWorlds = this.getExcludedWorlds();
        if (includedWorlds.contains("all")) {
            return !excludedWorlds.contains(((World) location.getExtent()).getName()) && this.handleRegionSpawn(location);
        } else if (excludedWorlds.contains("all")) {
            return includedWorlds.contains(((World) location.getExtent()).getName()) && this.handleRegionSpawn(location);
        } else {
            this.logger.log(Level.WARNING, "Please add 'all' to excluded or included worlds inside of the config.yml");
        }
        return true;
    }

    private boolean handleRegionSpawn(Location location) {
        return true;
    }

    public void fixJoinDefaultPet(PetMeta meta) {
    }
}
