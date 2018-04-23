package com.github.shynixn.petblocks.api.persistence.entity

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
interface GUIItem {

    /** Is the GUI Item enable so it should be rendered in the GUI? */
    val enabled :Boolean

    /** Returns the displayName of the ItemStack. */
    val displayName : Optional<String>;

    /**
    val lore : Array<String>


    /**
     * Converts the GUIItem to a new ItemStack depending on the framework.
     */
    fun <T> toItemStack() : T





    /**
     * Returns the displayName of the itemStack if present.
     *
     * @return displayName
     */
    Optional<String> getDisplayName();

    /**
     * Returns the lore of the itemStack if present.
     *
     * @return lore
     */
    Optional<String[]> getLore();

    /**
     * Returns the position of the itemStack in the ui.
     *
     * @return position
     */
    int getPosition();

    /**
     * Returns the guiPage of this container.
     *
     * @return guiPage
     */
    GUIPage getPage();

    /**
     * Returns the skin of the itemStack.
     * @return skin
     */
    String getSkin();

    /**
     * Returns the id of the item.
     * @return itemId
     */
    int getItemId();

    /**
     * Returns the damage of the item.
     * @return itemDamage
     */
    int getItemDamage();

    /**
     * Returns if the item is unbreakable.
     * @return unbreakable
     */
    boolean isItemUnbreakable();
}