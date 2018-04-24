package com.github.shynixn.petblocks.core.logic.persistence.entity

import com.github.shynixn.petblocks.api.business.enumeration.GUIPage
import com.github.shynixn.petblocks.api.persistence.entity.GUIScriptableItem

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
abstract class GUIItemContainer<Compatibility> : ItemContainer<Compatibility>, GUIScriptableItem {

    constructor(enabled: Boolean, position: Int, page: GUIPage?, id: Int, damage: Int, skin: String?, unbreakable: Boolean, name: String?, lore: Array<out String>?) : super(enabled, position, page, id, damage, skin, unbreakable, name, lore)
    constructor(orderNumber: Int, data: MutableMap<String, Any>?) : super(orderNumber, data) {
        if (data!!.containsKey("script")) {
            this.executeScript = (data["script"] as String?)!!
        }
    }

    /** Returns the displayName of the item **/
    override val displayName: String
        get() {
            if (getDisplayName().isPresent) {
                return getDisplayName().get()
            }
            return ""
        }

    /** Returns the lore of the item */
    override val lore: List<String>
        get() {
            if (getLore().isPresent) {
                return getLore().get().toList()
            }
            return ArrayList()
        }

    /** Is the GUI Item enable so it should be rendered in the GUI? */
    override val enabled: Boolean
        get() = this.isEnabled

    /** Returns the script which should be executed on click. */
    final override var executeScript: String = ""
}