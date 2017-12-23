package com.github.shynixn.petblocks.sponge.logic.persistence.controller;

import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.core.logic.business.helper.ExtensionHikariConnectionContext;
import com.github.shynixn.petblocks.core.logic.persistence.controller.ParticleEffectDataRepository;
import com.github.shynixn.petblocks.sponge.logic.persistence.entity.SpongeParticleEffectMeta;
import com.google.inject.Inject;

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
public class SpongeParticleEffectDataRepository extends ParticleEffectDataRepository {

    @Inject
    private Logger logger;

    /**
     * Initializes a new particleEffect repository.
     *
     * @param connectionContext connectionContext
     */
    @Inject
    public SpongeParticleEffectDataRepository(ExtensionHikariConnectionContext connectionContext) {
        super(connectionContext);
    }

    /**
     * Creates a new particleEffectMeta.
     *
     * @return meta
     */
    @Override
    public ParticleEffectMeta create() {
        return new SpongeParticleEffectMeta();
    }

    /**
     * Returns the logger.
     *
     * @return logger
     */
    @Override
    protected Logger getLogger() {
        return this.logger;
    }
}