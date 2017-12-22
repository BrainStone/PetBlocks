package com.github.shynixn.petblocks.bukkit.logic.persistence.controller;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.bukkit.logic.business.configuration.Config;
import com.github.shynixn.petblocks.bukkit.logic.persistence.entity.PetData;
import com.github.shynixn.petblocks.core.logic.business.helper.ExtensionHikariConnectionContext;
import com.github.shynixn.petblocks.core.logic.persistence.configuration.PetBlocksConfig;
import com.github.shynixn.petblocks.core.logic.persistence.controller.PetDataRepository;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

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
public class BukkitPetDataRepository extends PetDataRepository {

    private final Plugin plugin;

    /**
     * Initializes a new petData repository.
     *
     * @param connectionContext connectionContext
     */
    public BukkitPetDataRepository(Plugin plugin,ExtensionHikariConnectionContext connectionContext) {
        super(connectionContext);
        this.plugin = plugin;
    }

    /**
     * Creates a petMeta for the given player.
     *
     * @param player player
     * @return petMeta
     */
    @Override
    public PetMeta create(Object player) {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null!");
        final Optional<GUIItemContainer> containerOpt = Config.getInstance().getGuiItemsController().getGUIItemFromName("default-appearance");
        if (!containerOpt.isPresent())
            throw new IllegalArgumentException("Default appearance could not be loaded from the config.yml!");
        final PetData petData = new PetData((Player) player, Config.getInstance().getDefaultPetName());
        petData.setSkin(containerOpt.get().getItemId(), containerOpt.get().getItemDamage(), containerOpt.get().getSkin(), containerOpt.get().isItemUnbreakable());
        return petData;
    }

    /**
     * Returns the logger.
     *
     * @return logger
     */
    @Override
    protected Logger getLogger() {
        return this.plugin.getLogger();
    }

    /**
     * Creates a new petData instance.
     *
     * @return playerMeta
     */
    @Override
    protected PetMeta create() {
        return new PetData();
    }

    /**
     * Returns the config.
     *
     * @return config
     */
    @Override
    protected PetBlocksConfig getConfig() {
        return Config.getInstance();
    }

    /**
     * Returns the uuid from the given player.
     *
     * @param player player
     * @return uudi
     */
    @Override
    protected UUID getUUIDFromPlayer(Object player) {
        return ((Player)player).getUniqueId();
    }
}
