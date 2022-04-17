package com.vta4j.model;

import java.util.Objects;

/**
 * A bus stop of the Valley Transportation Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 17, 2022
 * @param id the ID of this {@link Stop}
 * @param name the name of this {@link Stop}
 */
public record Stop(String id, String name) {
    /**
     * Constructs an instance of the {@link Stop} class.
     *
     * @param id the ID to be used in the operation
     * @param name the name to be used in the operation
     */
    public Stop {
        Objects.requireNonNull(id, "the specified ID is null");

        Objects.requireNonNull(name, "the specified name is null");
    } //Line
}