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

import com.google.android.material.textfield.TextInputLayout;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.ReqBook;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "ContactFragment";

    private static final int MOBILE = 1;
    private static final int EMAIL = 2;
    private static final int SUBJECT = 3;
    private static final int MESSAGE = 4;
    private static final int NAME = 5;

    EditText etName, etMobileNo, etEmail, etSubject, etMessage;
    RadioGroup rgCommWay;
    Button btnSubmit;

    private EditText[] formFields;

    public ContactFragment() {
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
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (SharedVariables.user != null)
            menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void setupViews(View view) {
        etName = view.findViewById(R.id.et_name);
        etMobileNo = view.findViewById(R.id.et_mobile_no);
        etEmail = view.findViewById(R.id.et_email);
        etSubject = view.findViewById(R.id.et_subject);
        etMessage = view.findViewById(R.id.et_message);
        rgCommWay = view.findViewById(R.id.rg_comm_way);
        btnSubmit = view.findViewById(R.id.btn_submit);

        //create views array
        formFields = new EditText[]{etName, etMobileNo, etEmail, etSubject, etMessage};

        etName.addTextChangedListener(new MyTextWatcher(etName));
        etMobileNo.addTextChangedListener(new MyTextWatcher(etMobileNo));
        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));
        etSubject.addTextChangedListener(new MyTextWatcher(etSubject));
        etMessage.addTextChangedListener(new MyTextWatcher(etMessage));

        UserModel user = SharedVariables.user;
        if (user != null) {
            etName.setText(user.getName());
            etMobileNo.setText(user.getMobileNo());
            etEmail.setText(user.getEmail());

            ((TextInputLayout) etName.getParent().getParent()).setVisibility(View.GONE);
            ((TextInputLayout) etMobileNo.getParent().getParent()).setVisibility(View.GONE);
            ((TextInputLayout) etEmail.getParent().getParent()).setVisibility(View.GONE);
        }

        btnSubmit.setOnClickListener(this);
    }

    private String getSelectedCommWay() {
        return getString(rgCommWay.getCheckedRadioButtonId() == R.id.rb_email ? R.string.title_email : R.string.title_mobile);
    }

    //clear input field errors
    private void clearErrors(EditText excludeEditText) {
        for (EditText editText : formFields) {
            if (excludeEditText == null || editText.getId() != excludeEditText.getId())
                utils.setError(editText, null, false);
        }
    }

    private boolean validate() {
        if (!validateField(etName, NAME, true))
            return false;

        if (!validateField(etEmail, EMAIL, true))
            return false;

        if (!validateField(etSubject, SUBJECT, true))
            return false;

        //its a last statement -> return result
        return validateField(etMessage, MESSAGE, true);
    }

    private boolean validateField(EditText inputField, int inputType, boolean animate) {
        String input = inputField.getText().toString().trim();
        String errorMsg = null, title;
        switch (inputType) {

            case NAME:
                title = utils.getStringResource(R.string.title_name);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                break;

            case MOBILE:
                title = utils.getStringResource(R.string.title_mobile_number);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() != 10)
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);
                break;

            case EMAIL:
                title = utils.getStringResource(R.string.title_email);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches())
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);
                break;

            case SUBJECT:
                title = utils.getStringResource(R.string.title_subject);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                break;

            case MESSAGE:
                title = utils.getStringResource(R.string.title_message);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                break;
        }

        utils.setError(inputField, errorMsg, animate);
        return errorMsg == null;
    }

    private void submitRequest(String mobileNo, String email, String subject, String message, String commMode) {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<ReqBook> call = Retrofit2Client.getInstance().getApiService().contactUs(mobileNo, email, subject, message, commMode, utils.getIMEINumber());
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
            clearErrors(null);
            if (validate())
                submitRequest(utils.getInput(etMobileNo), utils.getInput(etEmail),
                        utils.getInput(etSubject), utils.getInput(etMessage), getSelectedCommWay());
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
            clearErrors(editText);

            //validate and show errors
            switch (editText.getId()) {
                case R.id.et_name:
                    validateField(editText, NAME, false);
                    break;
                case R.id.et_mobile_no:
                    validateField(editText, MOBILE, false);
                    break;
                case R.id.et_email:
                    validateField(editText, EMAIL, false);
                    break;
                case R.id.et_subject:
                    validateField(editText, SUBJECT, false);
                    break;
                case R.id.et_message:
                    validateField(editText, MESSAGE, false);
                    break;
            }
        }
    }

}