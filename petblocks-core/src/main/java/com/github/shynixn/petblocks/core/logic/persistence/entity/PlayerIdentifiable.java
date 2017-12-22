package com.github.shynixn.petblocks.core.logic.persistence.entity;

import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PlayerMeta;

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
public interface PlayerIdentifiable extends Identifiable {

    /**
     * Sets the id of the engine.
     *
     * @param engineId id
     */
    void setEngineId(int engineId);

    /**
     * Returns the id of the engine.
     *
     * @return id
     */
    int getEngineId();

    /**
     * Returns the id of the player
     *
     * @return playerId
     */
    long getPlayerId();

    /**
     * Sets the id of the player
     *
     * @param id id
     */
    void setPlayerId(long id);

    /**
     * Returns the id of the particle
     *
     * @return particleId
     */
    long getParticleId();

    /**
     * Sets the id of the particle
     *
     * @param id id
     */
    void setParticleId(long id);

    /**
     * Sets the own meta
     *
     * @param meta meta
     */
    void setPlayerMeta(PlayerMeta meta);

    /**
     * Sets the particleEffect meta
     *
     * @param meta meta
     */
    void setParticleEffectMeta(ParticleEffectMeta meta);
}
