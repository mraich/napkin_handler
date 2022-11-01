package com.mrichard.napkin_handler.data.db;

import com.google.gson.Gson;

/**
 * It's a kind of antipattern.
 */
public class GsonHandler {

    protected static GsonHandler INSTANCE = null;

    protected Gson gson = null;

    protected GsonHandler() {
        gson = new Gson();
    }

    public static GsonHandler GetInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GsonHandler();
        }
        return INSTANCE;
    }

    public Gson GetGson() {
        return gson;
    }

}
