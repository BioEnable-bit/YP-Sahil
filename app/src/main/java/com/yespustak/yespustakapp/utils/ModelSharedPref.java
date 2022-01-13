package com.yespustak.yespustakapp.utils;

import com.google.gson.Gson;

public class ModelSharedPref {
    private static final String TAG = "ModelSharedPreferences";

    private static ModelSharedPref instance;
    private final Gson gson;

    private ModelSharedPref() {
        gson = new Gson();
    }

    public static ModelSharedPref getInstance() {
        if (instance == null) {
            instance = new ModelSharedPref();
        }
        return instance;
    }

    public void saveModel(Object model) {
        String modelJson = gson.toJson(model);
        SharedPref.saveString(TAG, model.getClass().getSimpleName(), modelJson);
    }

    public <T> T getModel(Class<T> classOfT) {
        String modelJson = SharedPref.getString(TAG, classOfT.getSimpleName());
        if (modelJson != null)
            return gson.fromJson(modelJson, classOfT);

        return null;
    }
}
