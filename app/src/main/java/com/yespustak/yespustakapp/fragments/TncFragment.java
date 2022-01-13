package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yespustak.yespustakapp.DemoActivity;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.Content;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.utils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TncFragment extends Fragment implements SweetAlertDialog.OnSweetClickListener {
    private static final String TAG = "TncFragment";

//    View llData, llProgress;
//    TextView tvTitle, tvDesc;
    private String mode;

    WebView webView;
    ProgressDialog progressDialog;

    public TncFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tnc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }


//    @SuppressLint("NewApi")
//    private void updateUI(Content content) {
//        tvTitle.setVisibility(View.VISIBLE);
//        // we are targeting above android N
//        tvDesc.setText(Html.fromHtml(content.getResult(), Html.FROM_HTML_MODE_COMPACT));
//    }

/*
    private void getContent(int type) {
//        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        showProgress(true);
        Call<Content> call = Retrofit2Client.getInstance().getApiService().getContent(type);
        call.enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, Response<Content> response) {
//                utils.hideProgressBar(progressDialog);
                showProgress(false);
                if (response.isSuccessful())
                    updateUI(response.body());
                else
                    utils.showAlert(getContext(), null, response.body().getMessage(), SweetAlertDialog.ERROR_TYPE, TncFragment.this, false);

            }

            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
//                utils.hideProgressBar(progressDialog);
                showProgress(false);
                utils.showAlert(getContext(), null, getString(R.string.text_error_while_fetching), SweetAlertDialog.ERROR_TYPE, TncFragment.this, false);
            }
        });

    }
*/

//    private void showProgress(boolean show) {
//        llData.setVisibility(show ? View.GONE : View.VISIBLE);
//        llProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//    }

    private void setupViews(View view) {
        /*llData = view.findViewById(R.id.ll_data);
        llProgress = view.findViewById(R.id.ll_progress);
        tvTitle = view.findViewById(R.id.tv_tnc_title);
        tvDesc = view.findViewById(R.id.tv_tnc_description);
        tvDesc.setMovementMethod(new ScrollingMovementMethod());*/

        webView = view.findViewById(R.id.webviewExtra);

        progressDialog = new ProgressDialog(getContext());

        mode = SharedPref.getString(TAG, requireContext(), SharedPref.fragment);

        if (mode.equals("tnc")) {
           // getContent(0);
            startWebView("https://www.yespustak.com/terms-and-conditions");
         } else {

            startWebView("https://www.yespustak.com/privacy-policies");

           // getContent(1);
        }
    }

    private void startWebView(String url) {

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webView.loadUrl(url);
    }


    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        //go back to previous fragment
        requireActivity().onBackPressed();
    }
}