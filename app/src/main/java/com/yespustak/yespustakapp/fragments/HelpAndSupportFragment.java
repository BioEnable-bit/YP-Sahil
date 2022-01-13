package com.yespustak.yespustakapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yespustak.yespustakapp.R;

import org.jetbrains.annotations.NotNull;

public class HelpAndSupportFragment extends Fragment {

    /**
     * This fragment just a placeholder to avoid "no start destination" error in fragment_activity_nav_graph
     */

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
//    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
//    private int shortAnimationDuration;

    public HelpAndSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_and_support, container, false);
    }

//    @Override
//    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
////        setupViews(view);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.mi_search).setVisible(false);
//    }

//    private void setupViews(View rootView) {
//        // Hook up clicks on the thumbnail views.
//        final View thumb1View = rootView.findViewById(R.id.thumb_button_1);
//        thumb1View.setOnClickListener(view -> {
////            zoomImageFromThumb(rootView, thumb1View, R.drawable.image_5);
//        });
//
//        // Retrieve and cache the system's default "short" animation time.
//        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
//    }



}