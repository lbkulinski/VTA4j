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
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vta4j.model.Bus;
import com.vta4j.model.Line;
import com.vta4j.model.Stop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class BusAdapter extends TypeAdapter<Bus> {
    private static final Logger LOGGER;

    static {
        LOGGER = LogManager.getLogger();
    } //static

    private static void writeLine(JsonWriter jsonWriter, Line line) throws IOException {
        jsonWriter.name("line");

        jsonWriter.beginObject();

        jsonWriter.name("id");

        String id = line.id();

        jsonWriter.value(id);

        jsonWriter.value("name");

        String name = line.name();

        jsonWriter.value(name);

        jsonWriter.endObject();
    } //writeLine

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

        jsonWriter.name("prediction_time");

        String predictionTime = bus.predictionTime()
                                   .toString();

        jsonWriter.value(predictionTime);

        jsonWriter.name("arrival_time");

        String arrivalTime = bus.arrivalTime()
                                .toString();

        jsonWriter.value(arrivalTime);

        jsonWriter.endObject();
    } //writeBus

    @Override
    public void write(JsonWriter jsonWriter, Bus bus) throws IOException {
        BusAdapter.writeBus(jsonWriter, bus);
    } //write

    private static void readTime(JsonReader jsonReader, Map<String, Object> busData, String key) throws IOException {
        Instant instant;

        String timeString = jsonReader.nextString();

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

    private static void readMonitoredCall(JsonReader jsonReader, Map<String, Object> busData) throws IOException {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

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

    private static void readMonitoredVehicleJourney(JsonReader jsonReader,
                                                    Map<String, Object> busData) throws IOException {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

            switch (name) {
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

    public static Bus readBus(JsonReader jsonReader) throws IOException {
        Objects.requireNonNull(jsonReader, "the specified JSON reader is null");

        Map<String, Object> busData = new HashMap<>();

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();

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

        String lineId = (String) busData.get("lineId");

        String lineName = (String) busData.get("lineName");

        Line line = new Line(lineId, lineName);

        String stopId = (String) busData.get("stopId");

        String stopName = (String) busData.get("stopName");

        Stop stop = new Stop(stopId, stopName);

        String destinationId = (String) busData.get("destinationId");

        String destinationName = (String) busData.get("destinationName");

        Stop destination = new Stop(destinationId, destinationName);

        String direction = (String) busData.get("direction");

        ZonedDateTime predictionTime = (ZonedDateTime) busData.get("predictionTime");

        ZonedDateTime arrivalTime = (ZonedDateTime) busData.get("arrivalTime");

        return new Bus(line, stop, destination, direction, predictionTime, arrivalTime);
    } //readBus

    @Override
    public Bus read(JsonReader jsonReader) throws IOException {
        return BusAdapter.readBus(jsonReader);
    } //read
}