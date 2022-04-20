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

package com.vta4j.model.adapter;

import com.google.gson.TypeAdapter;
import com.vta4j.model.Bus;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.google.gson.stream.JsonWriter;
import com.vta4j.model.Line;
import java.io.IOException;
import com.vta4j.model.Stop;
import java.util.Objects;
import com.google.gson.stream.JsonReader;
import java.util.Map;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.DateTimeException;
import com.google.gson.stream.JsonToken;
import java.util.HashMap;

/**
 * A GSON type adapter for the {@link Bus} class.
 * 
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 19, 2022
 */
public final class BusAdapter extends TypeAdapter<Bus> {
    /**
     * The logger of the {@link BusAdapter} class.
     */
    private static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger();
    } //static

    /**
     * Writes the specified line using the specified JSON writer.
     * 
     * @param jsonWriter the JSON writer to be used in the operation
     * @param line the line to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    private static void writeLine(JsonWriter jsonWriter, Line line) throws IOException {
        jsonWriter.name("line");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        String id = line.id();

        jsonWriter.value(id);

        jsonWriter.name("name");

        String name = line.name();

        jsonWriter.value(name);

        jsonWriter.endObject();
    } //writeLine

    /**
     * Writes the specified destination using the specified JSON writer.
     * 
     * @param jsonWriter the JSON writer to be used in the operation
     * @param destination the destination to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    private static void writeDestination(JsonWriter jsonWriter, Stop destination) throws IOException {
        jsonWriter.name("destination");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        String id = destination.id();

        jsonWriter.value(id);

        jsonWriter.name("name");

        String name = destination.name();

        jsonWriter.value(name);

        jsonWriter.endObject();
    } //writeDestination

    /**
     * Writes the specified bus using the specified JSON writer.
     *
     * @param jsonWriter the JSON writer to be used in the operation
     * @param bus the bus to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    public static void writeBus(JsonWriter jsonWriter, Bus bus) throws IOException {
        Objects.requireNonNull(jsonWriter, "the specified JSON writer is null");

        Objects.requireNonNull(bus, "the specified bus is null");

        Line line = bus.line();

        BusAdapter.writeLine(jsonWriter, line);

        Stop destination = bus.destination();

        BusAdapter.writeDestination(jsonWriter, destination);

        jsonWriter.name("direction");

        String direction = bus.direction();

        jsonWriter.value(direction);

        jsonWriter.name("predictionTime");

        String predictionTime = bus.predictionTime()
                                   .toString();

        jsonWriter.value(predictionTime);

        jsonWriter.name("arrivalTime");

        String arrivalTime = bus.arrivalTime()
                                .toString();

        jsonWriter.value(arrivalTime);

        jsonWriter.endObject();
    } //writeBus

    /**
     * Writes the specified bus using the specified JSON writer.
     *
     * @param jsonWriter the JSON writer to be used in the operation
     * @param bus the bus to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(JsonWriter jsonWriter, Bus bus) throws IOException {
        BusAdapter.writeBus(jsonWriter, bus);
    } //write

    /**
     * Reads a time using the specified JSON reader, bus data, and key.
     * 
     * @param jsonReader the JSON reader to be used in the operation
     * @param busData the bus data to be used in the operation
     * @param key the key to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    private static void readTime(JsonReader jsonReader, Map<String, Object> busData, String key) throws IOException {
        String timeString = jsonReader.nextString();

        Instant instant;
        
        try {
            instant = Instant.parse(timeString);
        } catch (DateTimeParseException e) {
            BusAdapter.LOGGER.atError()
                             .withThrowable(e)
                             .log();

            return;
        } //end try catch

        String zoneIdString = "GMT-07:00";

        ZonedDateTime dateTime;

        try {
            ZoneId zoneId = ZoneId.of(zoneIdString);

            dateTime = ZonedDateTime.ofInstant(instant, zoneId);
        } catch (DateTimeException e) {
            BusAdapter.LOGGER.atError()
                             .withThrowable(e)
                             .log();

            return;
        } //end try catch

        busData.put(key, dateTime);
    } //readTime

    /**
     * Reads a monitored call using the specified JSON reader and bus data.
     *
     * @param jsonReader the JSON reader to be used in the operation
     * @param busData the bus data to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    private static void readMonitoredCall(JsonReader jsonReader, Map<String, Object> busData) throws IOException {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();

                continue;
            } //end if

            switch (name) {
                case "StopPointRef" -> {
                    String stopId = jsonReader.nextString();

                    busData.put("stopId", stopId);
                } //case "StopPointRef"
                case "StopPointName" -> {
                    String stopName = jsonReader.nextString();

                    busData.put("stopName", stopName);
                } //case "StopPointName"
                case "ExpectedArrivalTime" -> {
                    String key = "arrivalTime";

                    BusAdapter.readTime(jsonReader, busData, key);
                } //case "ExpectedArrivalTime"
                default -> jsonReader.skipValue();
            } //end switch
        } //end while

        jsonReader.endObject();
    } //readMonitoredCall

    /**
     * Reads a monitored vehicle journey using the specified JSON reader and bus data.
     *
     * @param jsonReader the JSON reader to be used in the operation
     * @param busData the bus data to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    private static void readMonitoredVehicleJourney(JsonReader jsonReader,
                                                    Map<String, Object> busData) throws IOException {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();

                continue;
            } //end if

            switch (name) {
                case "VehicleRef" -> {
                    String id = jsonReader.nextString();

                    busData.put("id", id);
                } //case "VehicleRef"
                case "LineRef" -> {
                    String lineId = jsonReader.nextString();

                    busData.put("lineId", lineId);
                } //case "LineRef"
                case "PublishedLineName" -> {
                    String lineName = jsonReader.nextString();

                    busData.put("lineName", lineName);
                } //case "PublishedLineName"
                case "DirectionRef" -> {
                    String direction = jsonReader.nextString();

                    busData.put("direction", direction);
                } //case "DirectionRef"
                case "DestinationRef" -> {
                    String destinationId = jsonReader.nextString();

                    busData.put("destinationId", destinationId);
                } //case "DestinationRef"
                case "DestinationName" -> {
                    String destinationName = jsonReader.nextString();

                    busData.put("destinationName", destinationName);
                } //case "DestinationRef"
                case "MonitoredCall" -> BusAdapter.readMonitoredCall(jsonReader, busData);
                default -> jsonReader.skipValue();
            } //end switch
        } //end while

        jsonReader.endObject();
    } //readMonitoredVehicleJourney

    /**
     * Reads a bus journey using the specified JSON reader.
     *
     * @param jsonReader the JSON reader to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    public static Bus readBus(JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "the specified JSON reader is null");

        Map<String, Object> busData = new HashMap<>();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.skipValue();

                continue;
            } //end if

            switch (name) {
                case "RecordedAtTime" -> {
                    String key = "predictionTime";

                    BusAdapter.readTime(jsonReader, busData, key);
                } //case "RecordedAtTime"
                case "MonitoredVehicleJourney" -> BusAdapter.readMonitoredVehicleJourney(jsonReader, busData);
                default -> jsonReader.skipValue();
            } //end switch
        } //end while

        jsonReader.endObject();

        String id = (String) busData.get("id");

        String lineId = (String) busData.get("lineId");

        String lineName = (String) busData.get("lineName");

        Line line = new Line(lineId, lineName);

        String stopId = (String) busData.get("stopId");

        String stopName = (String) busData.get("stopName");

        Stop stop;

        if ((stopId == null) && (stopName == null)) {
            stop = null;
        } else {
            stop = new Stop(stopId, stopName);
        } //end if

        String destinationId = (String) busData.get("destinationId");

        String destinationName = (String) busData.get("destinationName");

        Stop destination = new Stop(destinationId, destinationName);

        String direction = (String) busData.get("direction");

        ZonedDateTime predictionTime = (ZonedDateTime) busData.get("predictionTime");

        ZonedDateTime arrivalTime = (ZonedDateTime) busData.get("arrivalTime");

        return new Bus(id, line, stop, destination, direction, predictionTime, arrivalTime);
    } //readBus

    /**
     * Reads a bus journey using the specified JSON reader.
     *
     * @param jsonReader the JSON reader to be used in the operation
     * @throws IOException if an I/O error occurs
     */
    @Override
    public Bus read(JsonReader jsonReader) throws IOException {
        return BusAdapter.readBus(jsonReader);
    } //read
}