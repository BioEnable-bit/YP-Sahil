package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

public class WebContentFragment extends Fragment {
    private static final String TAG = WebViewClient.class.getSimpleName();

    public static final String CONTENT_TYPE = "CONTENT_TYPE";
    public static final int CT_OUR_STORY = 1;
    public static final int CT_PRIVACY_POLICY = 2;
    public static final int CT_TERMS_AND_CONDITIONS = 3;

    View llProgressView, llError;
    TextView tvStateDesc;
    Button btnRetry;
    WebView wvAboutUs;
    String url;
    boolean webViewError = false;

    public WebContentFragment() {
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
        return inflater.inflate(R.layout.web_content_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setupViews(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupViews(View view) {
        wvAboutUs = view.findViewById(R.id.wv_about_us);
        llProgressView = view.findViewById(R.id.ll_progress);
        llError = view.findViewById(R.id.ll_error);
        btnRetry = view.findViewById(R.id.btn_retry);
        tvStateDesc = view.findViewById(R.id.tv_state_desc);

        wvAboutUs.setWebViewClient(new WebViewClient());
        wvAboutUs.getSettings().setJavaScriptEnabled(true);

        if (getArguments() != null) {
            int content_type = getArguments().getInt(CONTENT_TYPE);
            if (content_type == CT_PRIVACY_POLICY)
                url = Constants.URL_PRIVACY_POLICY;
            else if (content_type == CT_TERMS_AND_CONDITIONS)
                url = Constants.URL_TERMS_AND_COND;
            else
                url = Constants.URL_OUR_STORY;
            wvAboutUs.loadUrl(Constants.BASE_URL + url);
        }

        btnRetry.setOnClickListener(v -> {
            wvAboutUs.loadUrl(Constants.BASE_URL + url);
        });

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            webViewError = false;
            llProgressView.setVisibility(View.VISIBLE);
            wvAboutUs.setVisibility(View.GONE);
            llError.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            llProgressView.setVisibility(View.GONE);

            wvAboutUs.setVisibility(webViewError ? View.GONE : View.VISIBLE);
            llError.setVisibility(webViewError ? View.VISIBLE : View.GONE);

            if (webViewError)
                tvStateDesc.setText(utils.isConnectionAvailable() ? getString(R.string.msg_problem_connecting_server) : getString(R.string.msg_no_internet));
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            webViewError = true;
        }
    }
}