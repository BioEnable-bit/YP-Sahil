package com.yespustak.yespustakapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {
    public static final String isLoggedIn = "isLoggedIn";
    public static String isIntroOpened = "isIntroOpened";
    public static String isActive = "isActive";
    public static String selectedTabPosition = "selectedTabPosition";
    public static String notification = "notification";
    public static String fragment = "fragment";
    public static String setHomeKey = "setHomeKey";
    public static String openPustakalayOnStart = "openPustakalayOnStart";
    public static String profileImgUri = "profileImgUri";
    static String prefName = "com.yespustak.yespustakapp";
    private static SharedPreferences shr;
    private static SharedPreferences.Editor editor;

    public static void saveString(String className, Context context, String key, String value) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        editor = shr.edit();
        editor.putString(key, value).apply();
//        LoggerClass.i(className + "SharedPref saveString", "Key:" + key + " value:" + value);
    }

    public static void saveString(String className, String key, String value) {
        saveString(className, App.getAppContext(), key, value);
    }


    public static void saveInt(String className, Context context, String key, int value) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        editor = shr.edit();
        editor.putInt(key, value).apply();
//        LoggerClass.i(className + "SharedPref saveInt", "Key:" + key + " value:" + value);
    }

    public static void saveInt(String key, int value) {
        saveInt("", utils.getContext(), key, value);
    }

    public static void saveBoolean(String className, String key, boolean value) {
        shr = App.getAppContext().getSharedPreferences(prefName, MODE_PRIVATE);
        editor = shr.edit();
        editor.putBoolean(key, value).apply();
//        LoggerClass.i(className + "SharedPref saveBoolean", "Key:" + key + " value:" + value);
    }

    public static void saveLong(String className, Context context, String key, long value) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        editor = shr.edit();
        editor.putLong(key, value).apply();
//        LoggerClass.i(className + "SharedPref saveLong", "Key:" + key + " value:" + value);
    }

    public static String getString(String className, Context context, String key) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        String data = shr.getString(key, null);
//        LoggerClass.i(className + "SharedPref getString", "Key:" + key + " value:" + String.valueOf(data));
        return data;
    }

    public static String getString(String className, String key) {
        return getString(className, utils.getContext(), key);
    }

    public static boolean getBoolean(String className, String key) {
        shr = App.getAppContext().getSharedPreferences(prefName, MODE_PRIVATE);
        boolean data;
        if (key.equals("notification")) {
            data = shr.getBoolean(key, true);
        } else {
            data = shr.getBoolean(key, false);
        }
//        LoggerClass.i(className + "SharedPref getBoolean", "Key:" + key + " value:" + String.valueOf(data));
        return data;
    }

    public static int getInt(String className, Context context, String key) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        int data = shr.getInt(key, 0);
//        LoggerClass.i(className + "SharedPref getInt", "Key:" + key + " value:" + String.valueOf(data));
        return data;
    }

    public static int getInt(String key) {
        return getInt("", utils.getContext(), key);
    }

    public static long getLong(String className, Context context, String key) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        long data = shr.getLong(key, 0);
//        LoggerClass.i(className + "SharedPref getLong", "Key:" + key + " value:" + String.valueOf(data));
        return data;
    }

    public static void removeshr(String className, Context context, String key) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        try {
            editor = shr.edit();
            editor.remove(key).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LoggerClass.i(className + "SharedPref removeshr", "Key:" + key);

    }

    public static void cleardata(String className, Context context) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        editor = shr.edit();
        editor.clear().apply();
//        LoggerClass.i(className + "SharedPref ", "SharedPref Cleared");
    }

    public static String getall(String className, Context context) {
        shr = context.getSharedPreferences(prefName, MODE_PRIVATE);
        String allShared = shr.getAll().toString().replace(",", ",\n");
        LoggerClass.i(className + "GET ALL SHARED======================", shr.getAll().toString().replace(",", ",\n"));
//        Toast.makeText(context, shr.getAll().toString(), Toast.LENGTH_LONG).show();
        return allShared;
    }

}
