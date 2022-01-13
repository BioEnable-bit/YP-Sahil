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
import androidx.navigation.fragment.NavHostFragment;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.ChangePassword;
import com.yespustak.yespustakapp.utils.Validator;
import com.yespustak.yespustakapp.utils.utils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener, SweetAlertDialog.OnSweetClickListener {
    private static final String TAG = "ChangePasswordFragment";

    //Views
    TextView tvEnterNewPass;
    EditText etPassword, etConfirmPassword;
    Button btnChangePassword;

    int srcType;
    String src, uid;

    private EditText[] formFields;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(int srcType, String src, String uid) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();

        //set args to fragment
        args.putInt(Constants.OTP_SRC_TYPE, srcType);
        args.putString(Constants.OTP_SRC, src);
        args.putString(Constants.UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle getBundleArgs(int srcType, String source, String uid) {
        Bundle args = new Bundle();
        //set args to fragment
        args.putInt(Constants.OTP_SRC_TYPE, srcType);
        args.putString(Constants.OTP_SRC, source);
        args.putString(Constants.UID, uid);

        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            srcType = getArguments().getInt(Constants.OTP_SRC_TYPE);
            src = getArguments().getString(Constants.OTP_SRC);
            uid = getArguments().getString(Constants.UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        //create views array
        formFields = new EditText[]{etPassword, etConfirmPassword};

        setData();
    }

    private void setupViews(View view) {
        //initialize views
        tvEnterNewPass = view.findViewById(R.id.tv_enter_new_pass);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        //set text watcher
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword));
        etConfirmPassword.addTextChangedListener(new MyTextWatcher(etConfirmPassword));

        //set listener
        btnChangePassword.setOnClickListener(this);
    }

    private void setData() {
        tvEnterNewPass.setText(getResources().getString(R.string.text_enter_pass, (srcType == Constants.OTP_SRC_MOBILE ? "+91 " : "") + src));
    }

    private void changePassword(String uid, String newPassword) {
        //check internet and show error
        if (!utils.isConnectionAvailable()) {
            utils.showSnackBar(getView(), getString(R.string.text_device_offline));
            return;
        }
        
        final SweetAlertDialog alertDialog = utils.showProgressBar(getContext());
        Call<ChangePassword> call = Retrofit2Client.getInstance().getApiService().changePassword(uid, newPassword, utils.getIMEINumber());
        call.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                utils.hideProgressBar(alertDialog);
                boolean isSuccess = response.body().getStatus() == Constants.STATUS_SUCCESS;

                utils.showAlert(getContext(), null, response.body().getMessage(),
                        isSuccess ? SweetAlertDialog.SUCCESS_TYPE : SweetAlertDialog.ERROR_TYPE,
                        isSuccess ? ChangePasswordFragment.this : null, false);
            }

            @Override
            public void onFailure(Call<ChangePassword> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                utils.showToast(t.getMessage());
                utils.hideProgressBar(alertDialog);
            }
        });
    }

    private boolean validate() {
        if (!Validator.validate(etPassword, Validator.PASSWORD))
            return false;

        if (!Validator.validateConfirmPassword(etPassword, etConfirmPassword, true))
            return false;

        return true;
    }

    //clear input field errors
    private void clearErrors(EditText excludeEditText) {
        for (EditText editText : formFields) {
            if (excludeEditText == null || editText.getId() != excludeEditText.getId())
                utils.setError(editText, null, false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_change_password) {
            //Validate
            if (validate())
                changePassword(uid, etPassword.getText().toString());
        }
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismiss();
        //press back button or reset password success. both cases show login fragment
        NavHostFragment.findNavController(this).navigate(R.id.action_changePasswordFragment_to_loginFragment);
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
                case R.id.et_password:
                    Validator.validate(etPassword, Validator.PASSWORD, false);
                    break;
                case R.id.et_confirm_password:
                    Validator.validateConfirmPassword(etPassword, etConfirmPassword, false);
                    break;
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        utils.showAlert(getContext(), null, getString(R.string.text_really_want_to_cancel), SweetAlertDialog.WARNING_TYPE, this, false);
        return true;
    }
}