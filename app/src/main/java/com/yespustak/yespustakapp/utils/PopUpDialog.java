package com.yespustak.yespustakapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.adapters.DefinitionsAdapter;
import com.yespustak.yespustakapp.api.RetrofitDictionaryClient;
import com.yespustak.yespustakapp.api.response.DictionaryItem;
import com.yespustak.yespustakapp.models.Definition;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopUpDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "PopUpDialog";

    private Activity activity;
    private View llData, llInfo, llErrorEmpty;
    //    private EditText etTitle;
    private SearchView svWord;
    private ProgressBar pbLoading;
    private ImageView ivClose;

    private final String selectedText;
    private Handler handler;
    private Runnable runnable;

    RecyclerView rvDefinitions;
    DefinitionsAdapter adapter;

    public PopUpDialog(Activity activity, String selectedText) {
        super(activity);
        this.activity = activity;
        this.selectedText = selectedText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_popup2);
        handler = new Handler();

        setupView();
//        fetchInfo(selectedText);

//        getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        getWindow().setGravity(Gravity.TOP);

    }

    private void setupView() {
        llData = findViewById(R.id.rl_data);
        llInfo = findViewById(R.id.ll_info);
        llErrorEmpty = findViewById(R.id.ll_error_empty);
        pbLoading = findViewById(R.id.pb_loading);
        svWord = findViewById(R.id.sv_word);
        ivClose = findViewById(R.id.iv_close);
        rvDefinitions = findViewById(R.id.rv_definitions);
//
        adapter = new DefinitionsAdapter();
        rvDefinitions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvDefinitions.setAdapter(adapter);
//
        ivClose.setOnClickListener(v -> dismiss());
        svWord.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchInfo(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // Remove all previous callbacks.
                handler.removeCallbacks(runnable);
                runnable = () -> fetchInfo(newText.trim());
                handler.postDelayed(runnable, 500);

                return false;
            }
        });
        svWord.setIconified(false);
        //don't need to pass true as onQueryTextChange will trigger
        svWord.setQuery(utils.capitaliseFirstLetter(selectedText), false);

        //Dialog related
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    private void fetchInfo(String searchText) {
        Call<List<DictionaryItem>> call = RetrofitDictionaryClient.getInstance().getApiService().getWordInfo(searchText);
        showProgressbar(true);
        call.enqueue(new Callback<List<DictionaryItem>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<DictionaryItem>> call, Response<List<DictionaryItem>> response) {
                showProgressbar(false);
                if (!response.isSuccessful()) {
                    showDefinitions(null);
                    utils.showToast("Request fail, code: " + response.code());
                    return;
                }

                if (response.body() == null || response.body().size() == 0) {
                    utils.showToast("Invalid response");
                    return;
                }

                DictionaryItem dictionaryItem = response.body().get(0);
//                tvSelectedText.setText(utils.capitaliseFirstLetter(dictionaryItem.getWord()));

                List<Definition> definitions = new ArrayList<>();
                for (DictionaryItem.Meaning meaning : dictionaryItem.getMeanings()) {
                    for (Definition definition : meaning.getDefinitions()) {
                        definition.setPartOfSpeech(meaning.getPartOfSpeech());
                        definitions.add(definition);
                    }
                }

                showDefinitions(definitions);

            }

            @Override
            public void onFailure(Call<List<DictionaryItem>> call, Throwable t) {
                showProgressbar(false);
                utils.showToast("Error while fetching info " + t.getMessage());
            }
        });
    }

    private void showDefinitions(List<Definition> definitions) {
        boolean isEmpty = definitions == null || definitions.isEmpty();

        if (!isEmpty)
            adapter.setDefinitions(definitions);

        llErrorEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        llInfo.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

    }

    private void showProgressbar(boolean show) {
        pbLoading.setVisibility(show && pbLoading.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        llData.setVisibility(show && llData.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}