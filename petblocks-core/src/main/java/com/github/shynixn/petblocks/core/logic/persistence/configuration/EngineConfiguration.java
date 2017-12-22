package com.github.shynixn.petblocks.core.logic.persistence.configuration;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.persistence.controller.EngineController;
import com.github.shynixn.petblocks.api.persistence.entity.EngineContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public abstract class EngineConfiguration implements EngineController{
    protected final List<EngineContainer> engineContainers = new ArrayList<>();

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(EngineContainer item) {
        if (item != null && !this.engineContainers.contains(item)) {
            this.engineContainers.add(item);
        }
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(EngineContainer item) {
        if (this.engineContainers.contains(item)) {
            this.engineContainers.remove(item);
        }
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.engineContainers.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<EngineContainer> getAll() {
        return Collections.unmodifiableList(this.engineContainers);
    }

    /**
     * Returns the engineContainer with the given id
     *
     * @param id id
     * @return engineContainer
     */
    @Override
    public EngineContainer getById(int id) {
        for (final EngineContainer container : this.engineContainers) {
            if (container.getId() == id) {
                return container;
            }
        }
        return null;
    }

    /**
     * Returns all gui items
     *
     * @return gui items
     */
    @Override
    public List<GUIItemContainer> getAllGUIItems() {
        final List<GUIItemContainer> items = new ArrayList<>();
        for (final EngineContainer container : this.getAll()) {
            items.add(container.getGUIItem());
        }
        return items;
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
        this.engineContainers.clear();
    }
}
