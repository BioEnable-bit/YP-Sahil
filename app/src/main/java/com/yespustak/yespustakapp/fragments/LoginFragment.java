package com.yespustak.yespustakapp.fragments;

import android.Manifest;
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
import com.yespustak.yespustakapp.activities.MainActivity;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.APIError;
import com.yespustak.yespustakapp.api.response.GetUserPref;
import com.yespustak.yespustakapp.api.response.Login;
import com.yespustak.yespustakapp.api.response.UserPref;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.repos.UserRepo;
import com.yespustak.yespustakapp.utils.App;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.PermissionHelper;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.Validator;
import com.yespustak.yespustakapp.utils.utils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";

    EditText etUsername, etPassword;
    TextView tvForgotPass, tvSignUp, tvContactUs;
    Button btnSignIn;

    EditText[] formFields;

    PermissionHelper permissionHelper;
    private final String[] permissions = {Manifest.permission.READ_PHONE_STATE};
    private static final int PERM_REQ_CODE = 100;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        //set bundle args here
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        //create views array
        formFields = new EditText[]{etUsername, etPassword};

        permissionHelper = new PermissionHelper(this, permissions, PERM_REQ_CODE);
        if (permissionHelper.checkSelfPermission(permissions)) {
            String deviceId = utils.getIMEINumber();
            System.out.println("Device Id : " + deviceId);
            Log.e(TAG, "onViewCreated: "+deviceId );
        }
    }

    private void setupViews(View view) {
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        tvForgotPass = view.findViewById(R.id.tvForgotPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        tvSignUp = view.findViewById(R.id.tvSignUp);
        tvContactUs = view.findViewById(R.id.tv_contact_us);

        //set text watcher
        etUsername.addTextChangedListener(new MyTextWatcher(etUsername));
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword));

        //set listener
        tvForgotPass.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvContactUs.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //
        clearErrors(null);
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
                case R.id.etUsername:
                    Validator.validate(etUsername, Validator.USERNAME, false);
                    break;
                case R.id.etPassword:
                    Validator.validate(etPassword, Validator.PASSWORD, false);
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
    }

    private boolean validate() {
        if (!Validator.validate(etUsername, Validator.NAME))
            return false;

        //only check for empty
        if (etPassword.getText().toString().trim().isEmpty()) {
            utils.setError(etPassword, getResources().getString(R.string.msg_field_required, getString(R.string.title_password)));
            return false;
        }

        return true;
    }

    public void getUserPref() {
        //check internet and show error
        if (!utils.isConnectionAvailable()) {
            utils.showSnackBar(getView(), getString(R.string.text_device_offline));
            return;
        }

        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<GetUserPref> call = Retrofit2Client.getInstance().getApiService().getUserPref(utils.getIMEINumber());
        call.enqueue(new Callback<GetUserPref>() {
            @Override
            public void onResponse(Call<GetUserPref> call, Response<GetUserPref> response) {
                if (!response.isSuccessful()) {
                    utils.showToast(getString(R.string.msg_something_went_wrong));
                    return;
                }

                //handle valid response
                Integer action = null;
                Bundle args = null;
                if (response.body().getStatus() == Constants.STATUS_FAIL && response.body().getUserPref() == null) {
                    action = R.id.action_loginFragment_to_personalDetailsFragment;
                } else {
                    UserPref userPref = response.body().getUserPref();

                    //as we have set those flags in order, so don't need to check multiple flags
                    if (userPref.isAcademicDetails())
                        utils.showAlert(getContext(), null, getString(R.string.msg_device_already_registered), SweetAlertDialog.ERROR_TYPE, null, false);
                    else if (userPref.isOtpVerification()) {
                        args = AcademicsFragment.getBundleArgs(userPref.getUid());
                        action = R.id.action_loginFragment_to_academicsFragment;
                    } else { // userPref.isPersonalDetails() //still show personal details otherwise user will stuck with the details which he first time filled
                        UserModel user = ModelSharedPref.getInstance().getModel(UserModel.class);
                        if (user != null) {
                            action = R.id.action_loginFragment_to_otpFragment;
                            args = OtpFragment.getBundleArgs(OtpFragment.REGISTER, Constants.OTP_SRC_MOBILE, user.getMobileNo(), user.getUid(), true);
                        } else //temporary fix
                            action = R.id.action_loginFragment_to_personalDetailsFragment;

                    }
                }

                if (action != null)
                    NavHostFragment.findNavController(LoginFragment.this).navigate(action, args);

                //hide progressbar at last
                utils.hideProgressBar(progressDialog);
            }

            @Override
            public void onFailure(Call<GetUserPref> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                utils.hideProgressBar(progressDialog);
                utils.showToast(t.getMessage());
            }
        });
    }

    public void login(String username, String password, String deviceId) {
        //check internet and show error
        if (!utils.isConnectionAvailable()) {
            utils.showSnackBar(getView(), getString(R.string.text_device_offline));
            return;
        }

        final SweetAlertDialog alertDialog = utils.showProgressBar(getContext());
        Call<Login> call = Retrofit2Client.getInstance().getApiService().login(username, password, deviceId);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                utils.hideProgressBar(alertDialog);

                if (response.isSuccessful() && response.body() != null) {

                    Login loginResponse = response.body();
                    if (loginResponse.getStatus() == Constants.STATUS_SUCCESS) {

                        //store user is logged in, so next time when user opens app we can decide which screen need to be shown
                        SharedPref.saveBoolean(TAG, SharedPref.isLoggedIn, true);
                        SharedPref.saveString(TAG, Constants.AUTH_TOKEN, loginResponse.getAuthToken());

                        //fetch user info
                        UserRepo userRepo = UserRepo.getInstance(App.getAppContext());
                        userRepo.syncUser();

                        //start main activity
                        utils.gotoNextActivity(getActivity(), MainActivity.class, true);
                    }

                } else { //error

                    APIError error = Retrofit2Client.parseError(response);
                    utils.showAlert(getContext(), null, error.getMessage(), SweetAlertDialog.ERROR_TYPE, null, false);
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                utils.hideProgressBar(alertDialog);
                utils.showToast(t.getMessage());
            }
        });
    }


    // Handle onClick
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvForgotPassword:
                NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
                break;
            case R.id.btnSignIn:
                //Get input
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String deviceId = null;

                //check permission and get IMEI
                if (permissionHelper.checkSelfPermission(permissions))
                    deviceId = utils.getIMEINumber();


                //validate and send request
                if (validate()) {
                    login(username, password, deviceId);
                }
                break;
            case R.id.tvSignUp:
                getUserPref();
                break;
            case R.id.tv_contact_us:
                NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_contactFragment2);
                break;
            default:
                break;
        }
    }
}