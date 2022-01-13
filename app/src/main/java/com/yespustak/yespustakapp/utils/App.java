package com.yespustak.yespustakapp.utils;

import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App extends Application {
    private static Context context;
    public static Gson gson;
    private static ExecutorService executorService;
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static Context getAppContext() {
        return App.context;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        executorService = Executors.newFixedThreadPool(4);
        //init gson object, which used for manual serialization
        gson = new Gson();
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }
}
