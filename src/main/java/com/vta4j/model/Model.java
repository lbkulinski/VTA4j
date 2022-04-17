package com.vta4j.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

public final class Model {


    private static final String API_KEY;

    static {
        API_KEY = Model.getApiKey();
    } //static

    private static String getApiKey() {
        String pathString = "src/main/resources/api_key.properties";

        Path path = Path.of(pathString);

        String apiKey = null;

        try (BufferedReader reader = Files.newBufferedReader(path)) {

        } catch (IOException e) {

        } //end try catch

        return apiKey;
    } //getApiKey

    public static Set<Bus> getBuses(int stopId) {
        return null;
    } //getBuses
}