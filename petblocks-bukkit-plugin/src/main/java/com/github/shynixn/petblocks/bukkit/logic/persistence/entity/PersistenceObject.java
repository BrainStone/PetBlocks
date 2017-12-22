package com.github.shynixn.petblocks.bukkit.logic.persistence.entity;

import com.github.shynixn.petblocks.api.persistence.entity.Persistenceable;
import com.github.shynixn.petblocks.core.logic.persistence.entity.Identifiable;

public class PersistenceObject implements Persistenceable, Identifiable {
    long id;
    /**
     * Returns the id of the object
     *
     * @return id
     */
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Sets the id of the object
     * @param id id
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
}
