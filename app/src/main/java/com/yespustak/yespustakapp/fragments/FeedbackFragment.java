package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hsalf.smileyrating.SmileyRating;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.ReqBook;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends BaseFragment implements View.OnClickListener {

    private static final int MESSAGE = 4;

    SmileyRating smileyRating;
    EditText etMessage;
    RadioGroup rgFeedbackType;
    Button btnSubmit;

    public FeedbackFragment() {
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
        return inflater.inflate(R.layout.fragment_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void setupViews(View view) {
        smileyRating = view.findViewById(R.id.smile_rating);
        etMessage = view.findViewById(R.id.et_message);
        rgFeedbackType = view.findViewById(R.id.rg_feedback_type);
        btnSubmit = view.findViewById(R.id.btn_submit);

        etMessage.addTextChangedListener(new MyTextWatcher(etMessage));

        btnSubmit.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    private String getSelectedFeedbackType() {
        switch (rgFeedbackType.getCheckedRadioButtonId()) {
            case R.id.rb_bug:
                return "Bug";
            case R.id.rb_suggestion:
                return "Suggestion";
            default:
                return "Other";
        }
    }

    private boolean validate() {
        if (smileyRating.getSelectedSmiley().getRating() < 1) {
            utils.showAlert(requireContext(), getString(R.string.title_failed), getString(R.string.text_select_your_exp), SweetAlertDialog.ERROR_TYPE, null, false);
            return false;
        }
        //its a last statement -> return result
        return validateField(etMessage, MESSAGE, true);
    }

    private boolean validateField(EditText inputField, int inputType, boolean animate) {
        String input = inputField.getText().toString().trim();
        String errorMsg = null, title;
        if (inputType == MESSAGE) {
            title = utils.getStringResource(R.string.title_message);
//            if (input.isEmpty())
//                errorMsg = utils.getStringResource(R.string.msg_field_required, title);
            if (input.length() > 0 && input.length() < 3)
                errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
        }

        utils.setError(inputField, errorMsg, animate);
        return errorMsg == null;
    }

    private void submitRequest(int rating, String message, String feedbackType) {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<ReqBook> call = Retrofit2Client.getInstance().getApiService().sendFeedback(rating, message.isEmpty() ? " " : message, feedbackType, utils.getIMEINumber());
        call.enqueue(new Callback<ReqBook>() {
            @Override
            public void onResponse(Call<ReqBook> call, Response<ReqBook> response) {
                utils.hideProgressBar(progressDialog);
                if (!response.isSuccessful()) {
                    utils.showToast(getString(R.string.text_request_fail));
                    return;
                }

                if (response.body() != null) {
                    boolean isSuccess = response.body().getStatus() == Constants.STATUS_SUCCESS;
                    utils.showAlert(getContext(), isSuccess ? getString(R.string.text_request_submitted) : null,
                            response.body().getMessage(), isSuccess ? SweetAlertDialog.SUCCESS_TYPE : SweetAlertDialog.ERROR_TYPE,
                            sweetAlertDialog -> {
                                sweetAlertDialog.dismiss();
                                requireActivity().finish();
                            }, false);
                }

            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            if (validate())
                submitRequest(smileyRating.getSelectedSmiley().getRating(), utils.getInput(etMessage), getSelectedFeedbackType());
        }
    }

    //Text Watcher - used show validation error while editing
    private class MyTextWatcher implements TextWatcher {
        private final EditText editText;

        private MyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("NonConstantResourceId")
        public void afterTextChanged(Editable s) {
            //remove error from all other fields except current field
//            clearErrors(editText);

            //validate and show errors
            if (editText.getId() == R.id.et_message) {
                validateField(editText, MESSAGE, false);
            }
        }
    }
}
