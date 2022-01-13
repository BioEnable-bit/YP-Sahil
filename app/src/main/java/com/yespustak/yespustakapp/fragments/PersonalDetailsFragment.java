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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.Register;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.Validator;
import com.yespustak.yespustakapp.utils.utils;

import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PersonalDetailsFragment";

    EditText etName, etEmail, etPassword, etConfirmPassword, etDob, etMobileNo, etWhatsappNo;
    Spinner spnrGender;
    MaterialCheckBox cbSameAsMobileNo, cbAgree;
    TextView tvTnc, tvPrivacyPolicy;
    Button btnNext;
    private EditText[] formFields;

    List<String> genderList;

    public PersonalDetailsFragment() {
        // Required empty public constructor
    }

    public static PersonalDetailsFragment newInstance() {
        return new PersonalDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genderList = Arrays.asList(getResources().getStringArray(R.array.gender));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        //create views array
        formFields = new EditText[]{etName, etEmail, etPassword, etConfirmPassword, etDob, etMobileNo, etWhatsappNo};
    }

    private void setupViews(View view) {
        //init
        etName = view.findViewById(R.id.et_name);
        spnrGender = view.findViewById(R.id.spnr_gender);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        etDob = view.findViewById(R.id.et_dob);
        etMobileNo = view.findViewById(R.id.et_mobile_no);
        etWhatsappNo = view.findViewById(R.id.et_whatsapp_no);
        cbSameAsMobileNo = view.findViewById(R.id.cb_same_as_mobile_no);
        cbAgree = view.findViewById(R.id.cb_agree);
        tvTnc = view.findViewById(R.id.tv_tnc);
        tvPrivacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        btnNext = view.findViewById(R.id.btn_next);

        //fill already saved data
        UserModel user = ModelSharedPref.getInstance().getModel(UserModel.class);
        if (user != null) {
            etName.setText(user.getName());
            spnrGender.setSelection(genderList.indexOf(user.getGender()));
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
            etConfirmPassword.setText(user.getConfirmPassword());
            etDob.setText(user.getDob());
            etMobileNo.setText(user.getMobileNo());
            etWhatsappNo.setText(user.getWhatsappNo());
        }

        //add text watcher
        etName.addTextChangedListener(new PersonalDetailsTextWatcher(etName));
        etEmail.addTextChangedListener(new PersonalDetailsTextWatcher(etEmail));
        etPassword.addTextChangedListener(new PersonalDetailsTextWatcher(etPassword));
        etConfirmPassword.addTextChangedListener(new PersonalDetailsTextWatcher(etConfirmPassword));
        etMobileNo.addTextChangedListener(new PersonalDetailsTextWatcher(etMobileNo));
        etWhatsappNo.addTextChangedListener(new PersonalDetailsTextWatcher(etWhatsappNo));

        //set listeners
        etDob.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        cbSameAsMobileNo.setOnCheckedChangeListener(checkedChangeListener);
        cbAgree.setOnCheckedChangeListener(checkedChangeListener);

        tvTnc.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
    }

    private boolean validate() {
        if (!Validator.validate(etName, Validator.NAME))
            return false;

        if (spnrGender.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrGender);
            return false;
        }

        if (!Validator.validate(etEmail, Validator.EMAIL))
            return false;

        if (!Validator.validate(etPassword, Validator.PASSWORD))
            return false;

        if (!Validator.validateConfirmPassword(etPassword, etConfirmPassword, true))
            return false;

        if (!Validator.validate(etDob, Validator.DOB)) {
            //hide keyboard
            utils.closeKeyboard(getActivity());
            //remove focus
            clearFocus();

            return false;
        }

        if (!Validator.validate(etMobileNo, Validator.MOBILE_NO))
            return false;

        if (!Validator.validate(etWhatsappNo, Validator.WHATSAPP_NO))
            return false;

        return true;
    }

    //remove blinking cursor
    private void clearFocus() {
        for (EditText editText : formFields)
            editText.clearFocus();
    }

    //clear input field errors
    private void clearErrors(EditText excludeEditText) {
        for (EditText editText : formFields) {
            if (excludeEditText == null || editText.getId() != excludeEditText.getId())
                utils.setError(editText, null, false);
        }
    }

    public void register(String name, String gender, String email, String password, String confirmPassword,
                         String dob, String mobileNo, String whatsappNo, String deviceId) {
        final SweetAlertDialog alertDialog = utils.showProgressBar(getContext());

        Call<Register> call = Retrofit2Client.getInstance().getApiService().register(name, gender, email,
                password, confirmPassword, dob, mobileNo, whatsappNo, deviceId);

        call.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                Log.e("SignUpresponse",""+response.toString());
                utils.hideProgressBar(alertDialog);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                        //save user details to use later
                        savePersonalDetails(response.body());

                        //create bundle args
                        Bundle bundle = OtpFragment.getBundleArgs(OtpFragment.REGISTER, Constants.OTP_SRC_MOBILE, mobileNo, response.body().getUid(), false);

                        //navigate to otp fragment using nav controller
                        NavHostFragment.findNavController(PersonalDetailsFragment.this)
                                .navigate(R.id.action_personalDetailsFragment_to_otpFragment, bundle);

                    } else
                        utils.showAlert(getContext(), null, response.body().getMessage(), SweetAlertDialog.ERROR_TYPE, null, false);
                } else
                    utils.showAlert(getContext(), null, "Server Error", SweetAlertDialog.ERROR_TYPE, null, false);

            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                utils.hideProgressBar(alertDialog);
                utils.showToast(t.getMessage());
            }
        });
    }

    private void savePersonalDetails(Register response) {
        UserModel user = new UserModel();
        //instead of edit text use use already collected input strings
        user.setUid(response.getUid());
        user.setName(etName.getText().toString().trim());
        user.setGender((String) spnrGender.getSelectedItem());
        user.setEmail(etEmail.getText().toString().trim());
        user.setPassword(etPassword.getText().toString().trim());
        user.setConfirmPassword(etConfirmPassword.getText().toString().trim());
        user.setDob(etDob.getText().toString().trim());
        user.setMobileNo(etMobileNo.getText().toString().trim());
        user.setWhatsappNo(etWhatsappNo.getText().toString().trim());
        user.setDeviceId(utils.getIMEINumber());

        ModelSharedPref.getInstance().saveModel(user);
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.cb_same_as_mobile_no) {
                //if mobile no. entered, then copy into whatsapp no.
                if (isChecked && !etMobileNo.getText().toString().trim().isEmpty()) {
                    etWhatsappNo.setText(etMobileNo.getText().toString().trim());
                    etWhatsappNo.setEnabled(false);

                    //remove whatsapp field errors
                    utils.setError(etWhatsappNo, null);
                } else {
                    cbSameAsMobileNo.setChecked(false);
                    etWhatsappNo.setEnabled(true);
                }
            } else
                btnNext.setEnabled(isChecked);


        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_tnc:
                utils.gotoNextActivityFragment(requireActivity(), "tnc");


//                SharedPref.saveString("FragmentActivity", SharedPref.fragment, "tnc");
//                //navigate to otp fragment using nav controller
//                NavHostFragment.findNavController(PersonalDetailsFragment.this)
//                        .navigate(R.id.action_personalDetailsFragment_to_tncFragment2, null);
                break;

            case R.id.tv_privacy_policy:
                utils.gotoNextActivityFragment(requireActivity(), "pp");


//                SharedPref.saveString("FragmentActivity", SharedPref.fragment, "pp");
//                NavHostFragment.findNavController(PersonalDetailsFragment.this)
//                        .navigate(R.id.action_personalDetailsFragment_to_tncFragment2, null);
                break;

            case R.id.btn_next:
                //validate and send request
                if (validate()) {
                    register(utils.getInput(etName), (String) spnrGender.getSelectedItem(), utils.getInput(etEmail), utils.getInput(etPassword),
                            utils.getInput(etConfirmPassword), utils.getInput(etDob), utils.getInput(etMobileNo),
                            utils.getInput(etWhatsappNo), utils.getIMEINumber()
                    );
                }

                break;
            case R.id.et_dob:
                //when click on date picker
                //hide keyboard
                utils.closeKeyboard(getActivity());
                //clear all errors and focus
                clearErrors(null);
                clearFocus();
                //show date picker
                utils.showDatePicker(getContext(), Constants.DF_DD_MM_YYYY, date -> {
                    etDob.setText(date);
                });
                break;
        }
    }

    //Text Watcher - used show validation error while editing
    private class PersonalDetailsTextWatcher implements TextWatcher {
        private final EditText editText;

        private PersonalDetailsTextWatcher(EditText editText) {
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
                    Validator.validate(editText, Validator.NAME, false);
                    break;
                case R.id.et_email:
                    Validator.validate(editText, Validator.EMAIL, false);
                    break;
                case R.id.et_password:
                    Validator.validate(editText, Validator.PASSWORD, false);
                    break;
                case R.id.et_confirm_password:
                    Validator.validateConfirmPassword(etPassword, etConfirmPassword, false);
                    break;
                case R.id.et_mobile_no:
                    Validator.validate(etMobileNo, Validator.MOBILE_NO, false);
                    break;
                case R.id.et_whatsapp_no:
                    Validator.validate(etWhatsappNo, Validator.WHATSAPP_NO, false);
                    break;
            }
        }
    }
}