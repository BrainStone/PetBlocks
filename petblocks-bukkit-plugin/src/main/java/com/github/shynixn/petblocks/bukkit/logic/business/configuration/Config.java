package com.github.shynixn.petblocks.bukkit.logic.business.configuration;

import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.persistence.entity.SoundMeta;
import com.github.shynixn.petblocks.bukkit.PetBlocksPlugin;
import com.github.shynixn.petblocks.bukkit.logic.persistence.entity.ParticleEffectData;
import com.github.shynixn.petblocks.bukkit.logic.persistence.entity.PetData;
import com.github.shynixn.petblocks.bukkit.logic.persistence.entity.SoundBuilder;
import com.github.shynixn.petblocks.bukkit.nms.NMSRegistry;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.ConfigPet;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.PetBlocksConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

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
public class Config extends PetBlocksConfig {
    private Plugin plugin;

    private static final Config config = new Config();
    private ConfigPet configPet;

    private ParticleEffectMeta feedingClickParticleCache;
    private SoundMeta feedingClickSoundCache;

    public static Config getInstance() {
        return config;
    }

    /**
     * Reloads the config
     */
    @Override
    public void reload() {
        this.plugin = JavaPlugin.getPlugin(PetBlocksPlugin.class);
        this.plugin.reloadConfig();
        if (this.pet() == null) {
            this.configPet = new ConfigPet() {
                @Override
                public <T> T getData(String path) {
                    return this.getData(path);
                }
            };
        }
        this.pet().reload();
        super.reload();
    }

    /**
     * Returns the pet config.
     *
     * @return pet
     */
    public ConfigPet pet() {
        return this.configPet;
    }

    /**
     * Returns the configuration data from the config.
     *
     * @param path path
     * @return result
     */
    @Override
    public <T> T getData(String path) {
        if (this.plugin == null)
            return null;
        Object data = this.plugin.getConfig().get(path);
        if (data instanceof String) {
            data = ChatColor.translateAlternateColorCodes('&', (String) data);
        }
        return (T) data;
    }

    public void fixJoinDefaultPet(PetMeta petMeta) {
        petMeta.setSkin(this.getData("join.settings.id"), (short) (int) this.getData("join.settings.damage"), this.getData("join.settings.skin"), this.getData("unbreakable"));
        petMeta.setEngine(this.engineController.getById(this.getData("join.settings.engine")));
        petMeta.setPetDisplayName(this.getData("join.settings.petname"));
        petMeta.setEnabled(this.getData("join.settings.enabled"));
        petMeta.setAge(this.getData("join.settings.age"));
        if (!((String) this.getData("join.settings.particle.name")).equalsIgnoreCase("none")) {
            final ParticleEffectMeta meta;
            try {
                meta = new ParticleEffectData(((MemorySection) this.getData("effect")).getValues(false));
                ((PetData) petMeta).setParticleEffectMeta(meta);
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Failed to load particle effect for join pet.");
            }
        }
    }

    /**
     * Returns the feeding click sound.
     *
     * @return sound
     */
    public SoundMeta getFeedingClickSound() {
        if (this.feedingClickSoundCache == null) {
            try {
                this.feedingClickSoundCache = new SoundBuilder(((MemorySection) this.getData("pet.feeding.click-sound")).getValues(false));
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Failed to load feeding click-sound.", e);
            }
        }
        return this.feedingClickSoundCache;
    }

    /**
     * Returns the feeding particleEffect.
     *
     * @return particleEffect
     */
    public ParticleEffectMeta getFeedingClickParticleEffect() {
        if (this.feedingClickParticleCache == null) {
            try {
                this.feedingClickParticleCache = new ParticleEffectData(((MemorySection) this.getData("pet.feeding.click-particle")).getValues(false));
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.WARNING, "Failed to load feeding click-sound.", e);
            }
        }
        return this.feedingClickParticleCache;
    }

    public boolean allowRidingOnRegionChanging() {
        return true;
    }

    public boolean allowPetSpawning(Location location) {
        final List<String> includedWorlds = this.getIncludedWorlds();
        final List<String> excludedWorlds = this.getExcludedWorlds();
        if (includedWorlds.contains("all")) {
            return !excludedWorlds.contains(location.getWorld().getName()) && this.handleRegionSpawn(location);
        } else if (excludedWorlds.contains("all")) {
            return includedWorlds.contains(location.getWorld().getName()) && this.handleRegionSpawn(location);
        } else {
            Bukkit.getConsoleSender().sendMessage(PetBlocksPlugin.PREFIX_CONSOLE + ChatColor.RED + "Please add 'all' to excluded or included worlds inside of the config.yml");
        }
        return true;
    }

    private boolean handleRegionSpawn(Location location) {
        final List<String> includedRegions = this.getIncludedRegions();
        final List<String> excludedRegions = this.getExcludedRegion();
        if (includedRegions.contains("all")) {
            for (final String k : NMSRegistry.getWorldGuardRegionsFromLocation(location)) {
                if (excludedRegions.contains(k)) {
                    return false;
                }
            }
            return true;
        } else if (excludedRegions.contains("all")) {
            for (final String k : NMSRegistry.getWorldGuardRegionsFromLocation(location)) {
                if (includedRegions.contains(k)) {
                    return true;
                }
            }
            return false;
        } else {
            Bukkit.getConsoleSender().sendMessage(PetBlocksPlugin.PREFIX_CONSOLE + ChatColor.RED + "Please add 'all' to excluded or included regions inside of the config.yml");
        }
        return true;
    }
}
