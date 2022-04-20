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

import com.google.gson.*;
import com.vta4j.model.adapter.BusAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * A model of the VTA4j application.
 *
 * @author Logan Kulinski, lbkulinski@gmail.com
 * @version April 19, 2022
 */
public final class Model {
    /**
     * The logger of the {@link Model} class.
     */
    private static final Logger LOGGER;

    /**
     * The API key of the {@link Model} class.
     */
    private static final String API_KEY;

    static {
        LOGGER = LogManager.getLogger();

        API_KEY = Model.getApiKey();
    } //static

    /**
     * Returns the API key of the VTA4j application.
     *
     * @return the API key of the VTA4j application
     */
    private static String getApiKey() {
        String pathString = "src/main/resources/api_key.properties";

        Path path = Path.of(pathString);

        Properties properties = new Properties();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        } catch (IOException e) {
            Model.LOGGER.atError()
                        .withThrowable(e)
                        .log();

            return null;
        } //end try catch

        return properties.getProperty("api_key");
    } //getApiKey

    /**
     * Returns a {@link Set} of buses parsed from the specified body.
     *
     * @param body the body to be used in the operation
     * @return a {@link Set} of buses parsed from the specified body
     * @throws NullPointerException if the specified body is {@code null}
     */
    private static Set<Bus> parseBody(String body) {
        Objects.requireNonNull(body, "the specified body is null");

        BusAdapter busAdapter = new BusAdapter();

        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Bus.class, busAdapter);

        Gson gson = builder.create();

        JsonObject object = gson.fromJson(body, JsonObject.class);

        JsonObject serviceDelivery = object.getAsJsonObject("ServiceDelivery");

        boolean status0 = serviceDelivery.getAsJsonPrimitive("Status")
                                         .getAsBoolean();

        if (!status0) {
            return Set.of();
        } //end if

        JsonObject stopMonitoringDelivery = serviceDelivery.getAsJsonObject("StopMonitoringDelivery");

        boolean status1 = stopMonitoringDelivery.getAsJsonPrimitive("Status")
                                                .getAsBoolean();

        if (!status1) {
            return Set.of();
        } //end if

        JsonArray monitoredStopVisit = stopMonitoringDelivery.getAsJsonArray("MonitoredStopVisit");

        Set<Bus> buses = new HashSet<>();

        for (JsonElement element : monitoredStopVisit) {
            Bus bus;

            try {
                bus = gson.fromJson(element, Bus.class);
            } catch (Exception e) {
                Model.LOGGER.atError()
                            .withThrowable(e)
                            .log();

                bus = null;
            } //end try catch

            if (bus != null) {
                buses.add(bus);
            } //end if
        } //end for

        return Collections.unmodifiableSet(buses);
    } //parseBody

    /**
     * Returns a {@link Set} of buses set to arrive at the stop with the specified ID.
     *
     * @param stopId the stop ID to be used in the operation
     * @return a {@link Set} of buses set to arrive at the stop with the specified ID
     */
    public static Set<Bus> getBuses(int stopId) {
        if (Model.API_KEY == null) {
            return Set.of();
        } //end if

        String query = "api_key=%s&agency=SC&stopcode=%d&format=json".formatted(Model.API_KEY, stopId);

        String uriString = "https://api.511.org/transit/StopMonitoring?%s".formatted(query);

        URI uri;

        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            Model.LOGGER.atError()
                        .withThrowable(e)
                        .log();

            return Set.of();
        } //end try catch

        HttpRequest request = HttpRequest.newBuilder(uri)
                                         .GET()
                                         .build();

        HttpClient client = HttpClient.newBuilder()
                                      .build();

        HttpResponse.BodyHandler<InputStream> bodyHandler = HttpResponse.BodyHandlers.ofInputStream();

        HttpResponse<InputStream> response;

        try {
            response = client.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            Model.LOGGER.atError()
                        .withThrowable(e)
                        .log();

            return Set.of();
        } //end try catch

        byte[] bytes;

        try (InputStream inputStream = response.body();
             GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            bytes = gzipInputStream.readAllBytes();
        } catch (IOException e) {
            Model.LOGGER.atError()
                        .withThrowable(e)
                        .log();

            return Set.of();
        } //end try catch

        String body = new String(bytes);

        Set<Bus> buses;

        try {
            buses = Model.parseBody(body);
        } catch (RuntimeException e) {
            Model.LOGGER.atError()
                        .withThrowable(e)
                        .log();

            return Set.of();
        } //end try catch

        return buses;
    } //getBuses
}