package com.vta4j.model;

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
import java.util.Properties;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public final class Model {
    private static final Logger LOGGER;

    private static final String API_KEY;

    static {
        LOGGER = LogManager.getLogger();

        API_KEY = Model.getApiKey();
    } //static

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

    public static Set<Bus> getBuses(int stopId) {
        if (Model.API_KEY == null) {
            return Set.of();
        } //end if

        String query = "api_key=%s&agency=SC&stopcode=%d&format=json".formatted(Model.API_KEY, stopId);

        String uriString = "https://api.511.org/transit/StopMonitoring?%s".formatted(query);

        System.out.println(uriString);

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

        System.out.println(body);

        return null;
    } //getBuses

    public static void main(String[] args) {
        System.out.println(Model.getBuses(60461));
    } //main
}