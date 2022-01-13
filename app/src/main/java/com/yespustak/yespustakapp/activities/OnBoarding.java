package com.yespustak.yespustakapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.adapters.ViewsSliderAdapter;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.List;

public class OnBoarding extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OnBoarding";
    ViewsSliderAdapter viewsSliderAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;
    private ViewPager2 screenPager;
    private List<Integer> layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isIntroOpened = SharedPref.getBoolean(TAG, SharedPref.isIntroOpened);
        boolean isLoggedIn = SharedPref.getBoolean(TAG, SharedPref.isLoggedIn);

        //check if isIntroOpened || isLoggedIn then open related activity and finish current
        if (isIntroOpened || isLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), isLoggedIn ? MainActivity.class : LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_on_boarding);
        // ini views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        //create layout list
        // add few more layouts if you want
        layouts = new ArrayList<>();
        layouts.add(R.layout.slide_one);
        layouts.add(R.layout.slide_two);
        layouts.add(R.layout.slide_three);

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        viewsSliderAdapter = new ViewsSliderAdapter(layouts);
        screenPager.setAdapter(viewsSliderAdapter);
        screenPager.setPageTransformer(new MyPageTransformer());

        // setup tabLayout with viewpager2
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabIndicator, screenPager,
                true, (tab, position) -> tab.view.setClickable(false));
        tabLayoutMediator.attach();

        // tablayout add change listener
        screenPager.registerOnPageChangeCallback(pageChangeCallback);

        // next button click Listener
        btnNext.setOnClickListener(this);

        // Get Started button click listener
        btnGetStarted.setOnClickListener(this);

        // skip button click listener
        tvSkip.setOnClickListener(this);
    }

    /*
     * ViewPager page change listener
     */
    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
//            addBottomDots(position);

            if (position == layouts.size() - 1) { // when we reach to the last screen
                loadLastScreen();
            }else {
                btnNext.setVisibility(View.VISIBLE);
                btnGetStarted.setVisibility(View.INVISIBLE);
                tvSkip.setVisibility(View.VISIBLE);
                tabIndicator.setVisibility(View.VISIBLE);
            }
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip:
                //                screenPager.setCurrentItem(layouts.size());
                //Skip slides launch LoginRegister activity
                btnGetStarted.callOnClick();//just simulating click
                break;
            case R.id.btn_next:
                //simulate fake swipe
                screenPager.beginFakeDrag();
                screenPager.fakeDragBy(-10f);
                screenPager.endFakeDrag();
                break;
            case R.id.btn_get_started:
                //after intro, open login screen
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                SharedPref.saveBoolean(TAG, SharedPref.isIntroOpened, true);
                utils.gotoNextActivity(OnBoarding.this, LoginRegisterActivity.class, true);
                break;
        }
    }


    public static class MyPageTransformer implements ViewPager2.PageTransformer {

        @Override
        public void transformPage(@NonNull View view, float f) {
            float f2 = 0;
            view.getWidth();
            view.setTranslationX((-f) * ((float) view.getWidth()));
            if (f >= -1.0f) {
                if (f <= 0.0f) {
                    view.setAlpha(1.0f);
                    view.setPivotX(0.0f);
                    f2 = 90.0f;
                } else if (f <= 1.0f) {
                    view.setAlpha(1.0f);
                    view.setPivotX((float) view.getWidth());
                    f2 = -90.0f;
                }
                view.setRotationY(Math.abs(f) * f2);
                return;
            }
            view.setAlpha(0.0f);
        }
    }


    //Comment out old code
    /*private boolean restorePrefData() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;


    }*/

    /*private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();


    }*/

    // show the GET STARTED Button and hide the indicator and the next button
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // setup animation
        btnGetStarted.setAnimation(btnAnim);
        btnGetStarted.animate();


    }
}