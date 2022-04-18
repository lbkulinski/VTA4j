package com.vta4j.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A bus of the Valley Transportation Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 17, 2022
 * @param line the line of this bus
 * @param stop the stop of this bus
 * @param destination the destination of this bus
 * @param direction the direction of this bus
 * @param predictionTime the prediction time of this bus
 * @param arrivalTime the arrival time of this bus
 */
public record Bus(Line line, Stop stop, Stop destination, String direction, ZonedDateTime predictionTime,
                  ZonedDateTime arrivalTime) {
    /**
     * Constructs an instance of the {@link Bus} class.
     *
     * @param line the line to be used in construction
     * @param stop the stop to be used in construction
     * @param destination the destination to be used in construction
     * @param direction the direction to be used in construction
     * @param predictionTime the prediction time to be used in construction
     * @param arrivalTime the arrival time to be used in construction
     */
    public Bus {
        Objects.requireNonNull(line, "the specified line is null");

        Objects.requireNonNull(stop, "the specified stop is null");

        Objects.requireNonNull(direction, "the specified direction is null");

        Objects.requireNonNull(predictionTime, "the specified prediction time is null");

        Objects.requireNonNull(arrivalTime, "the specified arrival time is null");
    } //Bus
}