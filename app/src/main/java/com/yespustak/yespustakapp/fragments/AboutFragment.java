package com.yespustak.yespustakapp.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.AboutUs;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutFragment extends Fragment implements SweetAlertDialog.OnSweetClickListener {

    TextView tvTitle, tvStory1, tvStory2, tvVision, tvMission, tvHow;
    View svAboutUs, llProgressView;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        svAboutUs = view.findViewById(R.id.sv_about_us);
        llProgressView = view.findViewById(R.id.ll_progress);
        tvStory1 = view.findViewById(R.id.tv_story_1);
        tvStory2 = view.findViewById(R.id.tv_story_2);
        tvVision = view.findViewById(R.id.tv_vision);
        tvMission = view.findViewById(R.id.tv_mission);
        tvHow = view.findViewById(R.id.tv_how);

        getContent();
    }

    private void getContent() {
        showProgress();
        Call<AboutUs> call = Retrofit2Client.getInstance().getApiService().aboutUs();
        call.enqueue(new Callback<AboutUs>() {
            @Override
            public void onResponse(Call<AboutUs> call, Response<AboutUs> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setData(response.body());
                    svAboutUs.setVisibility(View.VISIBLE);
                } else
                    utils.showAlert(getContext(), null, "Fail to load data", SweetAlertDialog.ERROR_TYPE, AboutFragment.this, false);

                llProgressView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<AboutUs> call, Throwable t) {
                utils.showAlert(getContext(), null, "Fail to load data", SweetAlertDialog.ERROR_TYPE, AboutFragment.this, false);
                llProgressView.setVisibility(View.GONE);
            }
        });
    }

    private void setData(AboutUs aboutUs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvStory1.setText(Html.fromHtml(aboutUs.getMsgOurStory1(), Html.FROM_HTML_MODE_LEGACY));
            tvStory2.setText(Html.fromHtml(aboutUs.getMsgOurStory2(), Html.FROM_HTML_MODE_LEGACY));
            tvVision.setText(Html.fromHtml(aboutUs.getMsgVision(), Html.FROM_HTML_MODE_LEGACY));
            tvMission.setText(Html.fromHtml(aboutUs.getMsgMission(), Html.FROM_HTML_MODE_LEGACY));
            tvHow.setText(Html.fromHtml(aboutUs.getMsgHow(), Html.FROM_HTML_MODE_LEGACY));
        }
    }

    private void showProgress() {
        svAboutUs.setVisibility(View.VISIBLE);
        llProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismiss();
    }
}