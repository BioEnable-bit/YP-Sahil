package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.FragmentActivity;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.utils.Validator;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.EditProfileFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private TextInputEditText etName, etEmail, etDob, etMobileNumber, etWhatsappNumber, etRollNo, etGrNo;
    private Spinner spnrSection, spnrGender, spnrAcademicYr;
    private Button btnSave;
    private EditText[] formFields;
    private SweetAlertDialog progressAlert;
    private EditProfileFragmentViewModel viewModel;

    private UserModel user, userOriginal;

    List<String> sectionList, genderList, academicYrList;
    ArrayAdapter<String> sectionAdapter, academicYrAdapter;

    FragmentActivity activity;

    public static boolean show;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(EditProfileFragmentViewModel.class);
        activity = (FragmentActivity) getActivity();
        genderList = Arrays.asList(getResources().getStringArray(R.array.gender));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        //create views array
        formFields = new EditText[]{etName, etEmail, etDob, etMobileNumber, etWhatsappNumber};
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void setupViews(View view) {
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etDob = view.findViewById(R.id.et_dob);
        etMobileNumber = view.findViewById(R.id.et_mobile_no);
        etWhatsappNumber = view.findViewById(R.id.et_whatsapp_no);
        spnrSection = view.findViewById(R.id.spnr_section);
        spnrGender = view.findViewById(R.id.spnr_gender);
        spnrAcademicYr = view.findViewById(R.id.spnr_academic_year);
        etRollNo = view.findViewById(R.id.et_roll_no);
        etGrNo = view.findViewById(R.id.et_gr_no);
        btnSave = view.findViewById(R.id.btn_save);

        //setup adapter
        sectionList = new ArrayList<>();
        sectionList.add(getString(R.string.text_select_section));
        sectionAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, sectionList);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSection.setAdapter(sectionAdapter);
        sectionAdapter.notifyDataSetChanged();

        academicYrList = new ArrayList<>();
        academicYrAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, academicYrList);
        academicYrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrAcademicYr.setAdapter(academicYrAdapter);

        progressAlert = utils.getProgressDialog(requireContext());

        etDob.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        //add text watcher
        etName.addTextChangedListener(new MyTextWatcher(etName));
        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));
        etMobileNumber.addTextChangedListener(new MyTextWatcher(etMobileNumber));
        etWhatsappNumber.addTextChangedListener(new MyTextWatcher(etWhatsappNumber));

        //set observer
        viewModel.getUserModelLiveData().observe(getViewLifecycleOwner(), userModel -> {
            userOriginal = userModel;

            user = activity.user != null ? activity.user : userModel;
            viewModel.getSectionsLiveData(user.getSchoolId(), user.getStandardId()).observe(getViewLifecycleOwner(), this::updateSection);

            updateUi();
        });


        //set listener for otp verification status change
        NavHostFragment.findNavController(this).getCurrentBackStackEntry().getSavedStateHandle().
                getLiveData("is_otp_verified", false).observe(getViewLifecycleOwner(), isOtpVerified -> {

            if (isOtpVerified) {
                saveUser();
            }
        });

        updateAcademicYrs();

    }

    private void updateUi() {
        etName.setText(user.getName());
        spnrGender.setSelection(genderList.indexOf(user.getGender()));
        etEmail.setText(user.getEmail());
        etDob.setText(user.getDob());
        etMobileNumber.setText(user.getMobileNo());
        etWhatsappNumber.setText(user.getWhatsappNo());
        spnrSection.setSelection(sectionList.indexOf(user.getSectionName()));
        spnrAcademicYr.setSelection(academicYrList.indexOf(user.getAcademicYear()));
        etRollNo.setText(user.getRollNo());
        etGrNo.setText(user.getGrNumber());
    }

    private void updateSection(List<String> sections) {
        sectionList.clear();
        sectionList.add(getString(R.string.text_select_section));
        if (sections != null) {
            sectionList.addAll(sections);
            spnrSection.setSelection(sectionList.indexOf(user.getSectionName()));
        }

        sectionAdapter.notifyDataSetChanged();
    }

    private void updateAcademicYrs() {
        int currentYr = Calendar.getInstance().get(Calendar.YEAR) + 1;
        academicYrList.clear();
        academicYrList.add(getString(R.string.text_select_academic_yr));

        for (int i = 0; i < 10; i++) {
            int year = currentYr - i;
            String academicYr = (year - 1) + "-" + year;
            academicYrList.add(academicYr);
        }

        academicYrAdapter.notifyDataSetChanged();
        spnrAcademicYr.setSelection(1);
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

        if (!Validator.validate(etDob, Validator.DOB)) {
            //hide keyboard
            utils.closeKeyboard(getActivity());
            //remove focus
            clearFocus();

            return false;
        }

        if (!Validator.validate(etMobileNumber, Validator.MOBILE_NO))
            return false;

        if (!Validator.validate(etWhatsappNumber, Validator.WHATSAPP_NO))
            return false;

//        if (spnrAcademicYr.getSelectedItemPosition() == 0) {
//            utils.setSpinnerError(spnrAcademicYr);
//            return false;
//        }

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


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_dob:
                //when click on date picker
                //hide keyboard
                if (getActivity() != null)
                    utils.closeKeyboard(getActivity());
                //clear all errors and focus
                clearErrors(null);
                clearFocus();
                //show date picker
                utils.showDatePicker(getContext(), Constants.DF_DD_MM_YYYY, date -> etDob.setText(date));
                break;
            case R.id.btn_save:
                if (validate()) {
                    String mobileNo = etMobileNumber.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    //if mobile or email is changed then show otp page
                    int otpSrcType = 0;
                    if (!userOriginal.getMobileNo().equalsIgnoreCase(mobileNo))
                        otpSrcType = Constants.OTP_SRC_MOBILE;
                    else if (!userOriginal.getEmail().equalsIgnoreCase(email)) {
                        otpSrcType = Constants.OTP_SRC_EMAIL;
                    }

                    getUserInput();

                    if (otpSrcType != 0/*None*/) {

                        //do network call
                        progressAlert.show();
                        int finalOtpSrcType = otpSrcType;
                        viewModel.sendOtpNewContact(user.getUid(), email, mobileNo).observe(getViewLifecycleOwner(), baseResponse -> {
                            utils.hideProgressBar(progressAlert);
                            if (baseResponse != null && baseResponse.getStatus() == Constants.STATUS_SUCCESS) {
                                Bundle args = OtpFragment.getBundleArgs(OtpFragment.CHANGE_EMAIL_OR_MOBILE, finalOtpSrcType,
                                        (finalOtpSrcType == Constants.OTP_SRC_MOBILE ? mobileNo : email), user.getUid(), false);

                                NavHostFragment.findNavController(EditProfileFragment.this)
                                        .navigate(R.id.action_editProfileFragment_to_otpFragment2, args);
                            } else
                                utils.showToast(R.string.msg_something_went_wrong);
                        });

                        return;

                    } else {
                        //network call
                        saveUser();
                    }
                }
                break;
        }
    }

    private void getUserInput() {
        activity.user = new UserModel();
        activity.user.setUid(user.getUid());
        activity.user.setDeviceId(user.getDeviceId());
        activity.user.setName(etName.getText().toString());
        activity.user.setGender((String) spnrGender.getSelectedItem());
        activity.user.setEmail(etEmail.getText().toString());
        activity.user.setDob(etDob.getText().toString());
        activity.user.setMobileNo(etMobileNumber.getText().toString());
        activity.user.setWhatsappNo(etWhatsappNumber.getText().toString());
        activity.user.setSectionName((String) spnrSection.getSelectedItem());
        activity.user.setAcademicYear((String) spnrAcademicYr.getSelectedItem());
        activity.user.setRollNo(etRollNo.getText().toString());
        activity.user.setGrNumber(etGrNo.getText().toString());
    }

    private void saveUser() {

        user.setName(etName.getText().toString());
        user.setGender((String) spnrGender.getSelectedItem());
        user.setEmail(etEmail.getText().toString());
        user.setDob(etDob.getText().toString());
        user.setMobileNo(etMobileNumber.getText().toString());
        user.setWhatsappNo(etWhatsappNumber.getText().toString());
        user.setSectionName(spnrSection.getSelectedItemPosition() > 0 ? ((String) spnrSection.getSelectedItem()) : "");
        user.setAcademicYear((String) spnrAcademicYr.getSelectedItem());
        user.setRollNo(etRollNo.getText().toString().trim());
        user.setGrNumber(etGrNo.getText().toString().trim());
        user.setUid(user.getUid());
        user.setDeviceId(utils.getIMEINumber());

        progressAlert.show();
        viewModel.saveUserProfile(user).observe(getViewLifecycleOwner(), baseResponse -> {
            utils.hideProgressBar(progressAlert);
            if (baseResponse != null && baseResponse.getStatus() == Constants.STATUS_SUCCESS) {
                viewModel.syncUser();
                utils.showToast("User info saved successfully");
                requireActivity().onBackPressed();
            } else
                utils.showToast(getString(R.string.msg_something_went_wrong));
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
                case R.id.et_mobile_no:
                    Validator.validate(etMobileNumber, Validator.MOBILE_NO, false);
                    break;
                case R.id.et_whatsapp_no:
                    Validator.validate(etWhatsappNumber, Validator.WHATSAPP_NO, false);
                    break;
            }
        }
    }
}