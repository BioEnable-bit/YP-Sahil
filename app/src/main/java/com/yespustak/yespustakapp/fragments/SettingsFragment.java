package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.utils;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    Switch openMyPustakalay, appNotification, swLivePayment;
    View view;
    String TAG = this.getClass().getSimpleName();
    TextView tvTnc, tvPP, tvFaq, tvFeedback, tvHelpAndSupport;


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialise();
        return view;
    }

    private void initialise() {
        assignViews();
    }

    private void assignViews() {
        openMyPustakalay = view.findViewById(R.id.switch2);
        appNotification = view.findViewById(R.id.switch1);
        swLivePayment = view.findViewById(R.id.sw_live_payment);
        tvTnc = view.findViewById(R.id.tv_tnc);
        tvPP = view.findViewById(R.id.tv_privacy_policy);
        tvFaq = view.findViewById(R.id.tv_faq);
        tvFeedback = view.findViewById(R.id.tv_feedback);
        tvHelpAndSupport = view.findViewById(R.id.tv_help_and_support);

        swLivePayment.setChecked(SharedPref.getBoolean(TAG, "enable_live_payment"));
        swLivePayment.setOnCheckedChangeListener((buttonView, isChecked) -> SharedPref.saveBoolean(TAG, "enable_live_payment", isChecked));
        setListeners();

    }

    private void setListeners() {
        tvTnc.setOnClickListener(this);
        tvPP.setOnClickListener(this);
        tvFaq.setOnClickListener(this);
        tvFeedback.setOnClickListener(this);
        tvHelpAndSupport.setOnClickListener(this);
        openMyPustakalay.setChecked(SharedPref.getBoolean(TAG, SharedPref.openPustakalayOnStart));
        openMyPustakalay.setOnCheckedChangeListener((compoundButton, b) -> SharedPref.saveBoolean(TAG, SharedPref.openPustakalayOnStart, b));
        appNotification.setChecked(SharedPref.getBoolean(TAG, SharedPref.notification));
        appNotification.setOnCheckedChangeListener((compoundButton, b) -> SharedPref.saveBoolean(TAG, SharedPref.notification, b));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tnc:
                utils.gotoNextActivityFragment(requireActivity(), "tnc");
                break;
            case R.id.tv_privacy_policy:
                utils.gotoNextActivityFragment(requireActivity(), "pp");
                break;
            case R.id.tv_faq:
                utils.gotoNextActivityFragment(requireActivity(), "faq");
                break;
            case R.id.tv_feedback:
                utils.gotoNextActivityFragment(requireActivity(), "feedback");
                break;
            case R.id.tv_help_and_support:
                utils.gotoNextActivityFragment(requireActivity(), "help");
                break;
        }
    }
}