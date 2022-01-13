package com.yespustak.yespustakapp.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.APIError;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.VerifyOtp;
import com.yespustak.yespustakapp.utils.utils;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpFragment extends BaseFragment implements View.OnClickListener, OnOtpCompletionListener {
    private static final String TAG = "OtpFragment";

    public static final String VERIFICATION_TYPE = "verification_type";
    public static final int REGISTER = 1;
    public static final int FORGOT_PASSWORD = 2;
    public static final int CHANGE_EMAIL_OR_MOBILE = 3;
    public static final String IS_RESEND = "isResend";

    TextView tvVerifyingSource, tvOtpWaitMsg, tvCountdown, tvOtpRequired;
    Button btnVerify, btnResend;
    OtpView vOtp;
    CountDownTimer timer;

    int verificationType, srcType;
    String src, uid;
    boolean isResend;

    public static OtpFragment newInstance(int verificationType, int srcType, String source, String uid) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();

        //set args
        args.putInt(VERIFICATION_TYPE, verificationType);
        args.putInt(Constants.OTP_SRC_TYPE, srcType);
        args.putString(Constants.OTP_SRC, source);
        args.putString(Constants.UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle getBundleArgs(int verificationType, int srcType, String source, String uid, boolean isResend) {
        Bundle args = new Bundle();

        //set args
        args.putInt(VERIFICATION_TYPE, verificationType);
        args.putInt(Constants.OTP_SRC_TYPE, srcType);
        args.putString(Constants.OTP_SRC, source);
        args.putString(Constants.UID, uid);
        args.putBoolean(IS_RESEND, isResend);

        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            verificationType = getArguments().getInt(VERIFICATION_TYPE);
            srcType = getArguments().getInt(Constants.OTP_SRC_TYPE);
            src = getArguments().getString(Constants.OTP_SRC);
            uid = getArguments().getString(Constants.UID);
            isResend = getArguments().getBoolean(IS_RESEND, isResend);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setData();
        startCountDown();
    }

    private void initViews(View view) {
        //init views
        tvVerifyingSource = view.findViewById(R.id.tv_verifying_source);
        tvOtpWaitMsg = view.findViewById(R.id.tv_otp_wait_msg);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        btnVerify = view.findViewById(R.id.btn_verify);
        btnResend = view.findViewById(R.id.btn_resend);
        vOtp = view.findViewById(R.id.v_otp);
        tvOtpRequired = view.findViewById(R.id.tv_otp_required);

        //set listeners
        btnVerify.setOnClickListener(this);
        btnResend.setOnClickListener(this);

        vOtp.setOtpCompletionListener(this);

        //resend otp if resumed
        if (isResend)
            resendOtp();
    }

    private void setData() {
        tvVerifyingSource.setText(getString(R.string.title_verifying, srcType == Constants.OTP_SRC_MOBILE ? getString(R.string.title_mobile_number) : getString(R.string.title_email)));
        tvOtpWaitMsg.setText(getString(srcType == Constants.OTP_SRC_MOBILE ? R.string.msg_otp_wait_mobile : R.string.msg_otp_wait_email, src));
    }

    //setup and start counter
    public void startCountDown() {
        tvCountdown.setVisibility(View.VISIBLE);

        //disable resent btn
        btnResend.setEnabled(false);

        //keep only 1 instance
        if (timer == null)
            timer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    tvCountdown.setText(getString(R.string.text_resend_otp_text,
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))
                    );
                }

                public void onFinish() {
                    //hide counter and disable enable resend btn
                    tvCountdown.setVisibility(View.GONE);
                    btnResend.setEnabled(true);
                }
            }.start();
        else
            timer.start();
    }

    private void resendOtp() {
        //check internet and show error
        if (!utils.isConnectionAvailable()) {
            utils.showSnackBar(getView(), getString(R.string.text_device_offline));
            return;
        }

        SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().resendOtp(
                utils.getIMEINumber(),
                srcType == Constants.OTP_SRC_EMAIL ? src : null,
                srcType == Constants.OTP_SRC_MOBILE ? src : null,
                uid);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                utils.hideProgressBar(progressDialog);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                        //otp sent, restart timer
                        startCountDown();
                        utils.showToast(response.body().getMessage());
                    } else //in case of any otp service or server related related error
                        utils.showAlert(getContext(), null, response.body().getMessage(), SweetAlertDialog.ERROR_TYPE, null, false);
                } else
                    utils.showAlert(getContext(), null, getString(R.string.text_error_while_fetching), SweetAlertDialog.ERROR_TYPE, null, false);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                utils.hideProgressBar(progressDialog);
                utils.showToast(t.getMessage());
            }
        });
    }

    public void verifyOtp(String otp) {
        //check internet and show error
        if (!utils.isConnectionAvailable()) {
            utils.showSnackBar(getView(), getString(R.string.text_device_offline));
            return;
        }
        SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<VerifyOtp> call = Retrofit2Client.getInstance().getApiService().verifyOtp(uid, otp, utils.getIMEINumber());
        call.enqueue(new Callback<VerifyOtp>() {
            @Override
            public void onResponse(Call<VerifyOtp> call, Response<VerifyOtp> response) {
                utils.hideProgressBar(progressDialog);
                if (response.isSuccessful() && response.body() != null) {
                    //on success show academics or change password fragment
                    VerifyOtp verifyOtpResponse = response.body();
                    if (verifyOtpResponse.getStatus() == Constants.STATUS_SUCCESS) {

                        NavController navController = NavHostFragment.findNavController(OtpFragment.this);
                        Bundle args;
                        int action;

                        //create bundle args
                        if (verificationType == OtpFragment.FORGOT_PASSWORD) {
                            args = ChangePasswordFragment.getBundleArgs(srcType, src, response.body().getUid());
                            action = R.id.action_otpFragment_to_changePasswordFragment;
                        } else if (verificationType == OtpFragment.REGISTER) {
                            args = AcademicsFragment.getBundleArgs(verifyOtpResponse.getUid());
                            action = R.id.action_otpFragment_to_academicsFragment;
                        }else {
                            navController.getPreviousBackStackEntry().getSavedStateHandle().set("is_otp_verified", true);
                            navController.popBackStack();
                            return; //stop further execution
                        }

                        //navigate to otp academic details fragment using nav controller
                        navController.navigate(action, args);
                    }

                } else { //error
                    APIError error = Retrofit2Client.parseError(response);
                    utils.showAlert(getContext(), null, error.getMessage(), SweetAlertDialog.ERROR_TYPE, null, false);
                }
            }

            @Override
            public void onFailure(Call<VerifyOtp> call, Throwable t) {
                utils.hideProgressBar(progressDialog);
                Log.e(TAG, "onFailure: ", t);
                utils.showToast(t.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        //stop timer if switched to another fragment
        timer.cancel();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_verify) {
            if (!vOtp.getText().toString().isEmpty()) {
                verifyOtp(vOtp.getText().toString());
                tvOtpRequired.setVisibility(View.GONE);
            } else
                tvOtpRequired.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.btn_resend) {
            resendOtp();
        }
    }

    @Override
    public void onOtpCompleted(String otp) {
        //OTP entered, enable verify button and make verification request
        btnVerify.setEnabled(true);
        verifyOtp(otp);
    }

    @Override
    public boolean onBackPressed() {
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry prevBackStackEntry = navController.getPreviousBackStackEntry();

        if (prevBackStackEntry != null && verificationType == REGISTER) {
            if (prevBackStackEntry.getDestination().getId() != R.id.personalDetailsFragment) {
                //launch personal details
                navController.navigate(R.id.action_otpFragment_to_personalDetailsFragment);
                return true;
            }
        }

        return false;
    }
}