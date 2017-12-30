package com.github.shynixn.petblocks.sponge.logic.business.configuration;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.business.enumeration.GUIPage;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.InternalMinecraftHeadConfiguration;
import com.github.shynixn.petblocks.sponge.logic.business.entity.SpongeItemContainer;
import com.google.inject.Inject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.IOException;
import java.io.InputStream;

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
public class SpongeMinecraftHeadsConfiguration extends InternalMinecraftHeadConfiguration {

    @Inject
    private PluginContainer pluginContainer;

    /**
     * Returns the stream to the database. Gets automatically closed.
     *
     * @return stream
     */
    @Override
    protected InputStream openDatabaseInputStream() {
        try {
            return Sponge.getAssetManager().getAsset(this.pluginContainer, "minecraftheads.db").get().getUrl().openStream();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decodes base64.
     *
     * @param base64Content base64Content.
     * @return text
     */
    @Override
    protected String decodeBase64String(String base64Content) {
        return Base64Coder.decodeString(base64Content);
    }

    /**
     * Decodes base64.
     *
     * @param base64Content base64Content.
     * @return data
     */
    @Override
    protected byte[] decodeBase64(String base64Content) {
        return Base64Coder.decode(base64Content);
    }

    /**
     * Creates a new gui itemContainer.
     *
     * @param enabled     enabled
     * @param position    position
     * @param page        page
     * @param id          id
     * @param damage      damage
     * @param skin        skin
     * @param unbreakable unbreakable
     * @param name        name
     * @param lore        lore
     * @return container.
     */
    @Override
    protected GUIItemContainer create(boolean enabled, int position, GUIPage page, int id, int damage, String skin, boolean unbreakable, String name, String[] lore) {
        return new SpongeItemContainer(enabled, position, page, id, damage, skin, unbreakable, name, lore);
    }
}
