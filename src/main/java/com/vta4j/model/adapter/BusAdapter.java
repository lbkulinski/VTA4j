package com.vta4j.model.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.vta4j.model.Bus;

import java.io.IOException;

public class BusAdapter extends TypeAdapter<Bus> {
    @Override
    public void write(JsonWriter jsonWriter, Bus bus) throws IOException {

    }

    @Override
    public Bus read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
