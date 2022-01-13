package com.yespustak.yespustakapp.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;


public class LoggerClass {
    public static boolean logger = true;

    public static void info(String TAG, String Msg) {
        Log.i(TAG, Msg);
    }

    public static void error(String TAG, String Msg) {
        Log.e(TAG, Msg);
    }

    public static void debug(String TAG, String Msg) {
        Log.d(TAG, Msg);
    }

    public static void wtf(String TAG, String Msg) {
        Log.wtf(TAG, Msg);

    }

    public static void LogApi(String className, String methodName, String apiUrl, String apiParams) {
        try {
            Log.wtf(className + "Api " + "Method ", methodName);
            Log.wtf(className + "Api " + "URL ", apiUrl);
            Log.wtf(className + "Api " + "Params ", apiParams.replace(",", ",\n"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf(className, e.toString());
        }
    }

    @SuppressLint("LongLogTag")
    public static void LogApiResponse(String className, String methodName, String apiResponse) {
        try {
            Log.i(className + "Api " + " Method: " + methodName + "Json response ", apiResponse.replace(",", ",\n"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(className, e.toString());
        }
    }

    public static void bundle(String activityname, Bundle bundle) {
        if (logger) {
            Log.i(activityname + " bundle ", String.valueOf(bundle).replace(",", ",\n"));
        }
    }

    public static void i(String activityname, String content) {
        if (logger) {
            Log.i(activityname, content);
        }
    }

    public static void e(String activityname, String content) {
        if (logger) {
            Log.e(activityname + "Exception ", content);
        }
    }

    public static void responseerror(String activityname, String content) {
        if (logger) {
            Log.e(activityname + "Response Error ", content);
        }
    }
}
