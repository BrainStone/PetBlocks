package com.github.shynixn.petblocks.sponge;

import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.sponge.logic.business.commandexecutor.PetBlockReloadCommandExecutor;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.CompatibilityItemType;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SoundBuilder;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeLocationBuilder;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeParticleEffectMeta;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.apache.commons.io.FileUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
@Plugin(id = "petblocks", name = "PetBlocks", version = "6.4.2-SNAPSHOT")
public class PetBlocksPlugin {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Inject
    private Logger logger;

    @Inject
    private AssetManager assetManager;

    @Inject
    private PluginContainer plugin;

    @Inject
    private Config config;

    @Inject
    private Game game;

    @Inject
    private PetBlockReloadCommandExecutor reloadCommandExecutor;

    @Listener
    public void onEnable(GameInitializationEvent event) throws IOException {
        System.out.println("Enabled PetBlocks sponge.");

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpongeParticleEffectMeta.class), new SpongeParticleEffectMeta.ParticleEffectSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SoundBuilder.class), new SoundBuilder.SoundBuilderSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpongeLocationBuilder.class), new SpongeLocationBuilder.LocationBuilderSerializer());


    }

    @Listener
    public void onReload(GameReloadEvent event) throws IOException {
        System.out.println("Reloadinng...");

        Optional<ItemType> type = this.game.getRegistry().getType(ItemType.class, CompatibilityItemType.REDSTONE.getMinecraftId());

        System.out.println("TYPE: " + type.get().getName());

        this.config.reload();

     //   this.reloadCommandExecutor.register("petblockreload", "Reloads the petblock configuration.", "petblocks.reload", "You don't have permission.");
    }

    public static void main(String[] args)
    {
        for(ParticleEffectMeta.ParticleEffectType particleEffectType : ParticleEffectMeta.ParticleEffectType.values())
        {
           ParticleType type =  Sponge.getGame().getRegistry().getType(ParticleType.class, particleEffectType.getSimpleName()).get();
            System.out.println("FOUND  " + type);
        }



    }


    public static Logger logger() {
        return logger();
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        System.out.println("Disabled PetBlocks sponge.");
    }
}
