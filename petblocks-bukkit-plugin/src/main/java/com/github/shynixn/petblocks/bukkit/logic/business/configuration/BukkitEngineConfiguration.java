package com.github.shynixn.petblocks.bukkit.logic.business.configuration;

import com.github.shynixn.petblocks.bukkit.logic.persistence.entity.EngineData;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.EngineConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.Plugin;

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
public class BukkitEngineConfiguration extends EngineConfiguration {
    private Plugin plugin;

    /**
     * Initializes a new engine repository
     *
     * @param plugin plugin
     */
    public BukkitEngineConfiguration(Plugin plugin) {
        super();
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        this.plugin = plugin;
    }

    /**
     * Reloads the content from the fileSystem
     */
    @Override
    public void reload() {
        this.engineContainers.clear();
        this.plugin.reloadConfig();
        final Map<String, Object> data = ((MemorySection) this.plugin.getConfig().get("engines")).getValues(false);
        for (final String key : data.keySet()) {
            final Map<String, Object> content = ((MemorySection) this.plugin.getConfig().get("engines." + key)).getValues(true);
            try {
                this.engineContainers.add(new EngineData(Integer.parseInt(key), content));
            } catch (final Exception e) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to add content " + key + '.', e);
            }
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        super.close();
        this.plugin = null;
    }
}
