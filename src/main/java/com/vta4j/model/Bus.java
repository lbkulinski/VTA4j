package com.vta4j.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A bus of the Valley Transportation Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 17, 2022
 * @param line the line of this bus
 * @param destination the destination of this bus
 * @param direction the direction of this bus
 * @param aimedArrivalTime the aimed arrival time of this bus
 * @param expectedArrivalTime the expected arrival time of this bus
 */
public record Bus(Line line, Stop destination, String direction, LocalDateTime aimedArrivalTime,
                  LocalDateTime expectedArrivalTime) {
    /**
     * Constructs an instance of the {@link Bus} class.
     *
     * @param line the line to be used in construction
     * @param destination the destination to be used in construction
     * @param direction the direction to be used in construction
     * @param aimedArrivalTime the aimed arrival time to be used in construction
     * @param expectedArrivalTime the expected arrival time to be used in construction
     */
    public Bus {
        Objects.requireNonNull(line, "the specified line is null");

        Objects.requireNonNull(direction, "the specified direction is null");

        Objects.requireNonNull(aimedArrivalTime, "the specified aimed arrival time is null");

        Objects.requireNonNull(expectedArrivalTime, "the specified expected arrival time is null");
    } //Bus
}