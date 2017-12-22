package com.github.shynixn.petblocks.core.logic.persistence.configuration;

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer;
import com.github.shynixn.petblocks.api.persistence.controller.CostumeController;

import java.util.*;
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
public abstract class CostumeConfiguration implements CostumeController {
    protected final String costumeCategory;
    protected final List<GUIItemContainer> items = new ArrayList<>();

    /**
     * Initializes a new engine repository
     *
     * @param costumeCategory costume
     */
    public CostumeConfiguration(String costumeCategory) {
        super();
        this.costumeCategory = costumeCategory;
    }

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(GUIItemContainer item) {
        if (this.getContainerFromPosition(item.getPosition()).isPresent()) {
            throw new IllegalArgumentException("Item at this position already exists!");
        }
        this.items.add(item);
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(GUIItemContainer item) {
        if (this.items.contains(item)) {
            this.items.remove(item);
        }
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.items.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<GUIItemContainer> getAll() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * Returns the container by the given order id
     *
     * @param position position
     * @return container
     */
    @Override
    @Deprecated
    public GUIItemContainer getContainerByPosition(int position) {
        final Optional<GUIItemContainer> tmp = this.getContainerFromPosition(position);
        return tmp.orElse(null);
    }

    /**
     * Returns the container by the given order id.
     *
     * @param position position
     * @return container
     */
    @Override
    public Optional<GUIItemContainer> getContainerFromPosition(int position) {
        for (final GUIItemContainer guiItemContainer : this.items) {
            if (guiItemContainer.getPosition() == position) {
                return Optional.of(guiItemContainer);
            }
        }
        return Optional.empty();
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.items.clear();
    }
}
