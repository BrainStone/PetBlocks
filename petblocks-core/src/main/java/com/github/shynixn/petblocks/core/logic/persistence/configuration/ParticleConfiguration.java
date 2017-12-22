package com.github.shynixn.petblocks.core.logic.persistence.configuration;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.persistence.controller.ParticleController;
import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;

import java.util.*;

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
public abstract class ParticleConfiguration implements ParticleController {
    protected final Map<GUIItemContainer, ParticleEffectMeta> particleCache = new HashMap<>();

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(GUIItemContainer item) {
        throw new RuntimeException("Not implemented!");
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(GUIItemContainer item) {
        if (this.particleCache.containsKey(item)) {
            this.particleCache.remove(item);
        }
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.particleCache.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<GUIItemContainer> getAll() {
        final List<GUIItemContainer> containers = new ArrayList<>(this.particleCache.keySet());
        containers.sort(Comparator.comparingInt(GUIItemContainer::getPosition));
        return containers;
    }

    /**
     * Returns the container by the given order id
     *
     * @param id id
     * @return container
     */
    @Override
    @Deprecated
    public GUIItemContainer getContainerByPosition(int id) {
        for (final GUIItemContainer guiItemContainer : this.particleCache.keySet()) {
            if (guiItemContainer.getPosition() == id) {
                return guiItemContainer;
            }
        }
        return null;
    }

    /**
     * Returns the container by the given order id.
     *
     * @param id id
     * @return container
     */
    @Override
    public Optional<GUIItemContainer> getContainerFromPosition(int id) {
        for (final GUIItemContainer guiItemContainer : this.particleCache.keySet()) {
            if (guiItemContainer.getPosition() == id) {
                return Optional.of(guiItemContainer);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the particleEffect by the given container
     *
     * @param container container
     * @return particleEffect
     */
    @Override
    public ParticleEffectMeta getByItem(GUIItemContainer container) {
        if (this.particleCache.containsKey(container)) {
            return this.particleCache.get(container);
        }
        return null;
    }

    /**
     * Returns the particleEffect by the given container.
     *
     * @param container container
     * @return particleEffect
     */
    @Override
    public Optional<ParticleEffectMeta> getFromItem(GUIItemContainer container) {
        if (this.particleCache.containsKey(container)) {
            return Optional.of(this.particleCache.get(container));
        }
        return Optional.empty();
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
        this.particleCache.clear();
    }
}
