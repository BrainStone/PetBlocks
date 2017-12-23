package com.github.shynixn.petblocks.sponge.logic.persistence.entity;

import com.github.shynixn.petblocks.api.persistence.entity.PlayerMeta;
import com.github.shynixn.petblocks.core.logic.persistence.entity.PersistenceObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

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
public class SpongePlayerData extends PersistenceObject implements PlayerMeta {
    private String name;
    private UUID uuid;

    /**
     * Returns the name of the playerData
     *
     * @return playerData
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the playerData
     *
     * @param name name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the uuid of the playerData
     *
     * @return uuid
     */
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Sets the uuid of the playerData
     *
     * @param uuid uuid
     */
    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the player of the playerData
     *
     * @return player
     */
    @Override
    public Player getPlayer() {
        try {
            final Optional<Player> optPlayer = Sponge.getGame().getServer().getPlayer(this.uuid);
            optPlayer.ifPresent(player -> this.setName(player.getName()));
            return optPlayer.get();
        } catch (final Exception ex) {
            return null;
        }
    }

    /**
     * Generates the playerData from a player
     *
     * @param player player
     * @return playerData
     */
    public static SpongePlayerData from(Player player) {
        final SpongePlayerData playerStats = new SpongePlayerData();
        playerStats.setName(player.getName());
        playerStats.setUuid(player.getUniqueId());
        return playerStats;
    }
}
