
package com.yespustak.yespustakapp.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.ar.core.Config;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.DemoActivity;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.utils.utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FullscreenDemoActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener, View.OnClickListener {



    private YouTubePlayer mPlayer;

    private View mPlayButtonLayout;
    private TextView mPlayTimeTextView;

    private Handler mHandler = null;
    private SeekBar mSeekBar;
    private ImageButton play_pause;
    private LinearLayout play_control,layout;
    private boolean status = false;
    private boolean status2 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_demo);
        // Initializing YouTube player view
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.player);
        youTubePlayerView.setOnClickListener(this);
        youTubePlayerView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, this);

        //Add play button to explicitly play video in YouTubePlayerView
        mPlayButtonLayout = findViewById(R.id.video_control);
        layout = findViewById(R.id.layout);
        play_pause = findViewById(R.id.play_video);
        play_control = findViewById(R.id.video_control);
        play_pause.setOnClickListener(this);
        layout.setOnClickListener(this);

        findViewById(R.id.full_screen).setOnClickListener(this);

        Log.e("TAG", "onCreate: "+status );


        mPlayTimeTextView = (TextView) findViewById(R.id.play_time);
        mSeekBar = (SeekBar) findViewById(R.id.video_seekbar);
        mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        mHandler = new Handler();


    }









    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }



    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;
        mPlayer = player;

        displayCurrentTime();

        Log.e("TAG", "onInitializationSuccess: "+getIntent().getStringExtra("videoId") );

        // Start buffering
        if (!wasRestored) {
            player.loadVideo(getIntent().getStringExtra("videoId"));
        }

        player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
       // mPlayButtonLayout.setVisibility(View.VISIBLE);

        // Add listeners to YouTubePlayer instance
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        player.setPlayerStateChangeListener(mPlayerStateChangeListener);
        player.setPlaybackEventListener(mPlaybackEventListener);


        player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {
                player.seekToMillis(0);
                player.play();
                //play_pause.setImageResource(R.drawable.play_button_24);
               // mPlayTimeTextView.setText(mPlayer.getCurrentTimeMillis());



            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });
    }

    YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
            mHandler.removeCallbacks(runnable);
            play_pause.setImageResource(R.drawable.play_button_24);

        }

        @Override
        public void onPlaying() {
            mHandler.postDelayed(runnable, 100);
            displayCurrentTime();
            play_pause.setImageResource(R.drawable.pausebutton_24);

        }

        @Override
        public void onSeekTo(int arg0) {
            mHandler.postDelayed(runnable, 100);
        }

        @Override
        public void onStopped() {
            mHandler.removeCallbacks(runnable);
        }
    };

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
            displayCurrentTime();
        }
    };

    SeekBar.OnSeekBarChangeListener mVideoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            long lengthPlayed = (mPlayer.getDurationMillis() * progress) / 100;
//            //mPlayTimeTextView.setText(mPlayer.getCurrentTimeMillis());
//            mPlayer.seekToMillis((int) lengthPlayed);

            if(fromUser){
                long lengthPlayed = (mPlayer.getDurationMillis() * progress) / 100;
                mPlayer.seekToMillis((int) lengthPlayed);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mSeekBar = seekBar;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mSeekBar = seekBar;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_video:
                if (null != mPlayer && !mPlayer.isPlaying())
                {                    mPlayer.play();
                    play_pause.setImageResource(R.drawable.pausebutton_24);}
                else {
                    mPlayer.pause();
                    play_pause.setImageResource(R.drawable.play_button_24);
                }
                break;
            case R.id.full_screen:
               play_control.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;



        }
    }

    private void displayCurrentTime() {
        if (null == mPlayer) return;
        String formattedTime = formatTime(mPlayer.getDurationMillis() - mPlayer.getCurrentTimeMillis());
        mPlayTimeTextView.setText(formattedTime);
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "--:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayCurrentTime();

            int progress = mPlayer.getCurrentTimeMillis() * 100 / mPlayer.getDurationMillis();
            mSeekBar.setProgress(progress);

           // mPlayTimeTextView.setText(String.valueOf(progress));

            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TAG", "onTouchEvent: " );
        return super.onTouchEvent(event);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if( ev.getAction() == MotionEvent.ACTION_UP){
            play_control.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return super.dispatchTouchEvent(ev);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // play_control.setVisibility(View.VISIBLE);

    }
}