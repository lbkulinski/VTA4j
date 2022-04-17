package com.vta4j.model;

import java.util.Objects;

/**
 * A bus line of the Valley Transportation Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 17, 2022
 * @param id the ID of this {@link Line}
 * @param name the name of this {@link Line}
 */
public record Line(String id, String name) {
    /**
     * Constructs an instance of the {@link Line} class.
     *
     * @param id the ID to be used in the operation
     * @param name the name to be used in the operation
     */
    public Line {
        Objects.requireNonNull(id, "the specified ID is null");

        Objects.requireNonNull(name, "the specified name is null");
    } //Line
}