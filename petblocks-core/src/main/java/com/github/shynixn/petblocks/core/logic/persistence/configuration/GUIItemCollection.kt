package com.github.shynixn.petblocks.core.logic.persistence.configuration

import com.github.shynixn.petblocks.api.business.entity.GUIItemContainer
import com.github.shynixn.petblocks.api.persistence.controller.CostumeController
import com.github.shynixn.petblocks.api.persistence.controller.GUIItemController
import java.util.*

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
class GUIItemCollection : CostumeController<GUIItemContainer<*>>, GUIItemController {
    private val items = HashMap<Int, GUIItemContainer<*>>()

    /**
     * Returns the [GUIItemContainer] at the given [position].
     */
    override fun getGUIItem(position: Int): Optional<GUIItemContainer<*>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /**
     * Reloads the content from the fileSystem.
     */
    override fun reload() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    override fun getAll(): List<GUIItemContainer<*>> {
        return items.values.toList()
    }

    //region Deprecated

    /**
     * Returns the amount of items in the repository.
     * @return size
     */
    override fun size(): Int {
        return this.items.size
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Deprecated("Items should not be added during runtime.")
    override fun store(item: GUIItemContainer<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Returns the container by the given order id.
     *
     * @param id id
     * @return container
     */
    @Deprecated("Please use getGUIItem(id) instead")
    override fun getContainerFromPosition(id: Int): Optional<GUIItemContainer<*>> {
        return getGUIItem(id)
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Deprecated("Items should not be removed during runtime.")
    override fun remove(item: GUIItemContainer<*>?) {
        if (item != null) {
            if (this.getContainerFromPosition(item.position).isPresent) {
                throw IllegalArgumentException("Item at this position already exists!")
            }

            items.toMap().forEach { key, it ->
                if (item == it) {
                    items.remove(key)
                }
            }
        }
    }

    /**
     * Note that unlike the [close][java.io.Closeable.close]
     * method of [java.io.Closeable], this `close` method
     * is *not* required to be idempotent.  In other words,
     * calling this `close` method more than once may have some
     * visible side effect, unlike `Closeable.close` which is
     * required to have no effect if called more than once.
     *
     * However, implementers of this interface are strongly encouraged
     * to make their `close` methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Deprecated("This resource is no longer necessary to be closed.")
    override fun close() {
        items.clear()
    }


    //endregion
}