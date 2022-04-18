/*
 * MIT License
 *
 * Copyright (c) 2022 Logan Kulinski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vta4j.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A bus of the Valley Transportation Authority.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 17, 2022
 * @param id the ID of this bus
 * @param line the line of this bus
 * @param stop the stop of this bus
 * @param destination the destination of this bus
 * @param direction the direction of this bus
 * @param predictionTime the prediction time of this bus
 * @param arrivalTime the arrival time of this bus
 */
public record Bus(String id, Line line, Stop stop, Stop destination, String direction, ZonedDateTime predictionTime,
                  ZonedDateTime arrivalTime) {
    /**
     * Constructs an instance of the {@link Bus} class.
     *
     * @param id the ID to be used in construction
     * @param line the line to be used in construction
     * @param stop the stop to be used in construction
     * @param destination the destination to be used in construction
     * @param direction the direction to be used in construction
     * @param predictionTime the prediction time to be used in construction
     * @param arrivalTime the arrival time to be used in construction
     * @throws NullPointerException if the specified line, stop, direction, prediction time, or arrival time is
     * {@code null}
     */
    public Bus {
        Objects.requireNonNull(line, "the specified line is null");

        Objects.requireNonNull(stop, "the specified stop is null");

        Objects.requireNonNull(direction, "the specified direction is null");

        Objects.requireNonNull(predictionTime, "the specified prediction time is null");

        Objects.requireNonNull(arrivalTime, "the specified arrival time is null");
    } //Bus
}