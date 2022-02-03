package com.yespustak.yespustakapp.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.utils.utils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                utils.gotoNextActivity(SplashScreen.this, OnBoarding.class, true);
            }
        }, 1000);


        Log.e("TAG", "onCreate: "+utils.getIMEINumber() );
//
//        String uniquePseudoID = "35" +
//                Build.BOARD.length() % 10 +
//                Build.BRAND.length() % 10 +
//                Build.DEVICE.length() % 10 +
//                Build.DISPLAY.length() % 10 +
//                Build.HOST.length() % 10 +
//                Build.ID.length() % 10 +
//                Build.MANUFACTURER.length() % 10 +
//                Build.MODEL.length() % 10 +
//                Build.PRODUCT.length() % 10 +
//                Build.TAGS.length() % 10 +
//                Build.TYPE.length() % 10 +
//                Build.USER.length() % 10;
//        String serial = Build.getRadioVersion();
//        String uuid = new UUID(uniquePseudoID.hashCode(), serial.hashCode()).toString();
//        Log.e("TAG", "onCreate: "+uuid );


        //Device ID

        //Afternoon
        //523c5f8b66f8581c
        //523c5f8b66f8581c
        //523c5f8b66f8581c

        //Evening
        //523c5f8b66f8581c

        //9:30 PM Testing
        //523c5f8b66f8581c


       // 564196ccca30ac50

        //  versionName "1.11.6 Dev"
        //564196ccca30ac50


    }
}