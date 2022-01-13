package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.APIError;
import com.yespustak.yespustakapp.api.response.ForgotPassword;
import com.yespustak.yespustakapp.utils.Validator;
import com.yespustak.yespustakapp.utils.utils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ForgotPasswordFragment";

    EditText etMobileNo, etEmail;
    TextView tvFieldRequired;
    Button btnSubmit;
    TextView tvSignIn;

    private EditText[] formFields;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        //create views array
        formFields = new EditText[]{etMobileNo, etEmail};
    }

    private void setupViews(View view) {
        //initialize views
        etMobileNo = view.findViewById(R.id.et_mobile_no);
        etEmail = view.findViewById(R.id.et_email);
        btnSubmit = view.findViewById(R.id.btn_submit);
        tvSignIn = view.findViewById(R.id.tv_sign_in);
        tvFieldRequired = view.findViewById(R.id.tv_field_required);

        //Set text watcher
        etMobileNo.addTextChangedListener(new MyTextWatcher(etMobileNo));
        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));

        //set OnClickListeners
        btnSubmit.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
    }

    private void forgotPassword(final int otpSrcType, String mobileNo, String email, String deviceId) {
        //check internet and show error
        if (!utils.isConnectionAvailable()) {
            utils.showSnackBar(getView(), getString(R.string.text_device_offline));
            return;
        }

        final SweetAlertDialog progressBar = utils.showProgressBar(getContext());
        Call<ForgotPassword> call = Retrofit2Client.getInstance().getApiService().forgotPassword(mobileNo, email, deviceId);
        call.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(Call<ForgotPassword> call, Response<ForgotPassword> response) {
                utils.hideProgressBar(progressBar);
                Log.i(TAG, "onResponse: is success: " + response.isSuccessful());

                if (response.isSuccessful() && response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    Bundle args = OtpFragment.getBundleArgs(OtpFragment.FORGOT_PASSWORD, otpSrcType,
                            (otpSrcType == Constants.OTP_SRC_MOBILE ? mobileNo : email),
                            response.body().getUid(), false);

                    NavHostFragment.findNavController(ForgotPasswordFragment.this)
                            .navigate(R.id.action_forgotPasswordFragment_to_otpFragment, args);

                } else { //error
                    APIError error = Retrofit2Client.parseError(response);
                    utils.showAlert(getContext(), null, error.getMessage(), SweetAlertDialog.ERROR_TYPE, null, false);
                }
            }

            @Override
            public void onFailure(Call<ForgotPassword> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                utils.hideProgressBar(progressBar);
                utils.showToast(t.getMessage());
            }
        });
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
            //TODO remove error from all other fields except current field
            clearErrors(editText);

            //validate and show errors
            switch (editText.getId()) {
                case R.id.et_mobile_no:
                    Validator.validate(etMobileNo, Validator.MOBILE_NO, false);
                    break;
                case R.id.et_email:
                    Validator.validate(editText, Validator.EMAIL, false);
                    break;
            }
        }
    }

    //clear input field errors
    private void clearErrors(EditText excludeEditText) {
        for (EditText editText : formFields) {
            if (excludeEditText == null || editText.getId() != excludeEditText.getId())
                utils.setError(editText, null, false);
        }

        if (tvFieldRequired.getVisibility() == View.VISIBLE)
            tvFieldRequired.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {

            //first remove all errors
            clearErrors(null);

            //get input
            String mobileNo = etMobileNo.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String deviceId = utils.getIMEINumber();

            boolean isMobileValid = false;

            //hide error msg
            tvFieldRequired.setVisibility(View.GONE);

            if (!mobileNo.isEmpty()) {
                isMobileValid = Validator.validate(etMobileNo, Validator.MOBILE_NO);
                if (!isMobileValid) return;
            } else if (!email.isEmpty()) {
                if (!Validator.validate(etEmail, Validator.EMAIL))
                    return;
            } else { //both empty
                //show error msg in single TextView
                tvFieldRequired.setVisibility(View.VISIBLE);
                return;
            }

            //send request
            int otpSrcType = isMobileValid ? Constants.OTP_SRC_MOBILE : Constants.OTP_SRC_EMAIL;
            forgotPassword(otpSrcType, mobileNo, email, deviceId);

        } else if (v.getId() == R.id.tv_sign_in) {
            getActivity().onBackPressed();
        }
    }
}