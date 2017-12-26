package com.github.shynixn.petblocks.sponge.logic.business.configuration;

import com.github.shynixn.petblocks.api.persistence.entity.EngineContainer;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.EngineConfiguration;
import com.github.shynixn.petblocks.sponge.PetBlocksPlugin;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeEngineData;
import com.google.inject.Inject;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Map;
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
public class SpongeEngineConfiguration extends EngineConfiguration {

    @Inject
    private PluginContainer plugin;

    @Inject
    private Config config;

    /**
     * Reloads the content from the fileSystem
     */
    @Override
    public void reload() {
        this.engineContainers.clear();
        final Map<Object, Object> data = this.config.getData("engines");
        for (final Object key : data.keySet()) {
            try {
                final EngineContainer container = new SpongeEngineData((Integer) key, (Map<String, Object>) data.get(key));
                this.engineContainers.add(container);
            } catch (final Exception e) {
                PetBlocksPlugin.logger().log(Level.INFO,"Failed to load engine " + key + '.', e);
            }
        }
    }
}
