package com.github.shynixn.petblocks.sponge;

import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.business.controller.PetBlockController;
import com.github.shynixn.petblocks.api.persistence.controller.PetMetaController;
import com.github.shynixn.petblocks.core.logic.business.helper.ReflectionUtils;
import com.github.shynixn.petblocks.sponge.logic.business.PetBlockManager;
import com.github.shynixn.petblocks.sponge.logic.business.configuration.Config;
import com.github.shynixn.petblocks.sponge.logic.business.helper.SpongePetBlockModifyHelper;
import com.github.shynixn.petblocks.sponge.logic.business.helper.UpdateUtils;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SoundBuilder;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeLocationBuilder;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeParticleEffectMeta;
import com.github.shynixn.petblocks.sponge.nms.NMSRegistry;
import com.github.shynixn.petblocks.sponge.nms.VersionSupport;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bstats.sponge.Metrics;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
@Plugin(id = "petblocks", name = "PetBlocks", version = "6.4.2-SNAPSHOT", description = "PetBlocks is a spigot and sponge plugin to use custom Pets in minecraft.")
public class PetBlocksPlugin {

    public static final String PREFIX_CONSOLE = "&b[PetBlocks] ";
    private static final long SPIGOT_RESOURCEID = 12056;
    private static final String PLUGIN_NAME = "PetBlocks";

    private static org.slf4j.Logger slogger;
    private boolean disabled;

    @Inject
    private PluginContainer pluginContainer;
    @Inject
    private Config config;
    @Inject
    private PetBlockManager petBlockManager;
    @Inject
    private Metrics metrics;

    @Inject
    private Logger logger;

    public static org.slf4j.Logger logger() {
        return slogger;
    }

    @Listener
    public void onEnable(GameInitializationEvent event) throws IOException {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpongeParticleEffectMeta.class), new SpongeParticleEffectMeta.ParticleEffectSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SoundBuilder.class), new SoundBuilder.SoundBuilderSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpongeLocationBuilder.class), new SpongeLocationBuilder.LocationBuilderSerializer());
        slogger = this.logger;
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.disabled = true;
            this.unload();
        } else {
            sendServerMessage(PREFIX_CONSOLE + "&aLoading PetBlocks ...");
            this.config.reload();
            if (Config.getInstance().isMetricsEnabled()) {
                //disable metrics
            }
            Task.builder().async().execute(() -> {
                try {
                    UpdateUtils.checkPluginUpToDateAndPrintMessage(SPIGOT_RESOURCEID, PREFIX_CONSOLE, PLUGIN_NAME, pluginContainer);
                } catch (final IOException e) {
                    this.logger.warn("Failed to check for updates.");
                }
            }).submit(this.pluginContainer);
            try {
                ReflectionUtils.invokeMethodByClass(PetBlocksApi.class, "initialize", new Class[]{PetMetaController.class, PetBlockController.class}, new Object[]{this.petBlockManager.getPetMetaController(), this.petBlockManager.getPetBlockController()});
                sendServerMessage(PREFIX_CONSOLE + "&aEnabled PetBlocks " + this.pluginContainer.getVersion().get() + " by Shynixn");
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                PetBlocksPlugin.logger().warn("Failed to enable plugin.", e);
            }
        }
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        if (!this.disabled) {
            this.config.reload();
            sendServerMessage(PREFIX_CONSOLE + "&aReloaded PetBlocks configuration.");
        }
    }

    @Listener
    public void onDisable(GameStoppingServerEvent event) {
        if (!this.disabled) {
            try {
                NMSRegistry.unregisterCustomEntities();
            } catch (final Exception e) {
                PetBlocksPlugin.logger().warn("Failed to disable petblocks.", e);
            }
        }
    }

    private void unload() {
        Sponge.getGame().getEventManager().unregisterPluginListeners(this);
        Sponge.getGame().getCommandManager().getOwnedBy(this).forEach(Sponge.getGame().getCommandManager()::removeMapping);
        Sponge.getGame().getScheduler().getScheduledTasks(this).forEach(Task::cancel);
    }

    private static void sendServerMessage(String text) {
        Sponge.getServer().getConsole().sendMessage(SpongePetBlockModifyHelper.translateStringToText(text));
    }
}
