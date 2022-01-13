package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.activities.MainActivity;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.Boards;
import com.yespustak.yespustakapp.api.response.GetSections;
import com.yespustak.yespustakapp.api.response.SaveAcademicDetails;
import com.yespustak.yespustakapp.api.response.Schools;
import com.yespustak.yespustakapp.api.response.Standards;
import com.yespustak.yespustakapp.models.BoardModel;
import com.yespustak.yespustakapp.models.SchoolModel;
import com.yespustak.yespustakapp.models.StandardModel;
import com.yespustak.yespustakapp.utils.SharedPref;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcademicsFragment extends BaseFragment implements View.OnClickListener, SweetAlertDialog.OnSweetClickListener {
    private static final String TAG = "AcademicsFragment";
    private static final int BOARD = 1;
    private static final int SCHOOL = 2;
    private static final int STANDARD = 3;

    //Views
    Spinner spnrSchool, spnrBoard, spnrStandard, spnrSection, spnrAcademicYr;
    EditText etRollNo, etGrNo;
    Button btnSubmit;

    List<BoardModel> boardList;
    List<SchoolModel> schoolList;
    List<StandardModel> standardList;
    List<String> sectionList;
    List<String> academicYrList;

    ArrayAdapter<BoardModel> boardAdapter;
    ArrayAdapter<SchoolModel> schoolAdapter;
    ArrayAdapter<StandardModel> standardAdapter;
    ArrayAdapter<String> sectionAdapter;
    ArrayAdapter<String> academicYrAdapter;

    String uid;

    //Spinner options change listener
    private final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                switch (parent.getId()) {
                    case R.id.spnr_board:
                        //fetch Schools
                        getSchools(getSelectedId(BOARD));
                        break;

                    case R.id.spnr_school:
                        break;
                    case R.id.spnr_standard:
                        int schoolId = getSelectedId(SCHOOL);
                        int standardId = getSelectedId(STANDARD);
                        if (schoolId > 0)
                            getSections(schoolId, standardId);
                        //do nothing
                        break;

                    default:
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    public static AcademicsFragment newInstance(String uid) {
        AcademicsFragment fragment = new AcademicsFragment();
        Bundle args = new Bundle();

        //set args to fragment
        args.putString(Constants.UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bundle getBundleArgs(String uid) {
        Bundle args = new Bundle();
        //set args to fragment
        args.putString(Constants.UID, uid);

        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(Constants.UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_academics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        //fetch data
        getBoards();
        getSchools(0);
        getStandards();
        getSections(0, 0);
        getAcademicYrs();
    }

    private void setupViews(View view) {
        //init views
        spnrSchool = view.findViewById(R.id.spnr_school);
        spnrBoard = view.findViewById(R.id.spnr_board);
        spnrStandard = view.findViewById(R.id.spnr_standard);
        spnrSection = view.findViewById(R.id.spnr_section);
        spnrAcademicYr = view.findViewById(R.id.spnr_academic_year);
        etRollNo = view.findViewById(R.id.et_roll_no);
        etGrNo = view.findViewById(R.id.et_gr_no);
        btnSubmit = view.findViewById(R.id.btn_submit);

        //set listeners
        spnrSchool.setOnItemSelectedListener(onItemSelectedListener);
        spnrBoard.setOnItemSelectedListener(onItemSelectedListener);
        spnrStandard.setOnItemSelectedListener(onItemSelectedListener);
        btnSubmit.setOnClickListener(this);

        //set spinner data
        boardList = new ArrayList<>();
        boardAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, boardList);
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrBoard.setAdapter(boardAdapter);

        schoolList = new ArrayList<>();
        schoolAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, schoolList);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSchool.setAdapter(schoolAdapter);

        standardList = new ArrayList<>();
        standardAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, standardList);
        standardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrStandard.setAdapter(standardAdapter);

        sectionList = new ArrayList<>();
        sectionAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, sectionList);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSection.setAdapter(sectionAdapter);

        academicYrList = new ArrayList<>();
        academicYrAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, academicYrList);
        academicYrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrAcademicYr.setAdapter(academicYrAdapter);
    }

    private void getBoards() {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<Boards> call = Retrofit2Client.getInstance().getApiService().getBoards();
        call.enqueue(new Callback<Boards>() {
            @Override
            public void onResponse(Call<Boards> call, Response<Boards> response) {
                utils.hideProgressBar(progressDialog);
                //clear previous items
                boardList.clear();
                boardList.add(new BoardModel(0, getString(R.string.text_select_board)));

                if (!response.isSuccessful()) {
                    utils.showToast(getString(R.string.text_request_fail));
                    boardAdapter.notifyDataSetChanged();
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    //add items, update view
                    boardList.addAll(response.body().getBoardModels());
                } else
                    utils.showToast(getString(R.string.msg_something_went_wrong));

                boardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Boards> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void getSchools(int boardId) {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<Schools> call = Retrofit2Client.getInstance().getApiService().getSchools(boardId);
        call.enqueue(new Callback<Schools>() {
            @Override
            public void onResponse(Call<Schools> call, Response<Schools> response) {
                utils.hideProgressBar(progressDialog);

                //clear old items
                schoolList.clear();
                schoolList.add(new SchoolModel(0, getString(R.string.text_select_school)));

                if (!response.isSuccessful()) {
                    utils.showToast(getString(R.string.text_request_fail));
                    schoolAdapter.notifyDataSetChanged();
                    return;
                }

                // add new items, update view
                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    if (response.isSuccessful())
                        schoolList.addAll(response.body().getSchoolModels());

                    //reset selection
                    spnrSchool.setSelection(0);
                } else
                    utils.showToast(R.string.msg_something_went_wrong);

                schoolAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Schools> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void getStandards() {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<Standards> call = Retrofit2Client.getInstance().getApiService().getStandards();
        call.enqueue(new Callback<Standards>() {
            @Override
            public void onResponse(Call<Standards> call, Response<Standards> response) {
                utils.hideProgressBar(progressDialog);

                //clear old items
                standardList.clear();
                standardList.add(new StandardModel(0, getString(R.string.text_select_standard)));

                if (!response.isSuccessful()) {
                    utils.showToast(R.string.text_request_fail);
                    standardAdapter.notifyDataSetChanged();
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    //add items, update view

                    standardList.addAll(response.body().getStandardModels());

                    //reset selection
                    spnrSection.setSelection(0);
                } else
                    utils.showToast(R.string.msg_something_went_wrong);

                standardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Standards> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void getSections(int schoolId, int standardId) {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<GetSections> call = Retrofit2Client.getInstance().getApiService().getSections(schoolId, standardId);
        call.enqueue(new Callback<GetSections>() {
            @Override
            public void onResponse(Call<GetSections> call, Response<GetSections> response) {
                utils.hideProgressBar(progressDialog);
                //clear old items
                sectionList.clear();
                sectionList.add(getString(R.string.text_select_section));

                if (!response.isSuccessful()) {
                    utils.showToast(R.string.text_request_fail);
                    sectionAdapter.notifyDataSetChanged();
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    //add new items, update view
                    if (response.isSuccessful() && response.body().getSections() != null)
                        sectionList.addAll(response.body().getSections());

                    //reset selection
//                spnrSchool.setSelection(0);

                } else
                    utils.showToast(R.string.msg_something_went_wrong);

                sectionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<GetSections> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void getAcademicYrs() {
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

    private void sendAcademicDetails() {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        String section = spnrSection.getSelectedItemPosition() > 0 ? spnrSection.getSelectedItem().toString() : "";
        String academicYr = (String) spnrAcademicYr.getSelectedItem();
        Call<SaveAcademicDetails> call = Retrofit2Client.getInstance()
                .getApiService()
                .saveAcademicDetails(uid, getSelectedId(BOARD), getSelectedId(SCHOOL),
                        getSelectedId(STANDARD), section, academicYr, etRollNo.getText().toString().trim(),
                        etGrNo.getText().toString().trim(), utils.getIMEINumber());
        call.enqueue(new Callback<SaveAcademicDetails>() {
            @Override
            public void onResponse(Call<SaveAcademicDetails> call, Response<SaveAcademicDetails> response) {
                utils.hideProgressBar(progressDialog);
                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    onRegistrationComplete(response.body());
                } else
                    utils.showAlert(getContext(), null, response.body().getMessage(), SweetAlertDialog.ERROR_TYPE, null, false);
            }

            @Override
            public void onFailure(Call<SaveAcademicDetails> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private void onRegistrationComplete(SaveAcademicDetails response) {
        //Save login info
        SharedPref.saveBoolean(TAG, SharedPref.isLoggedIn, true);
        SharedPref.saveString(TAG, Constants.AUTH_TOKEN, response.getAuthToken());

        //show alert -> on alert ok click start MainActivity
        //I Added "\n". to end of string to temporary fix text msg truncate
        utils.showAlert(getContext(), null, response.getMessage(), SweetAlertDialog.SUCCESS_TYPE, this, false);

    }

    private int getSelectedId(int dropdownType) {
        switch (dropdownType) {
            case BOARD:
                return ((BoardModel) spnrBoard.getSelectedItem()).getId();
            case SCHOOL:
                return ((SchoolModel) spnrSchool.getSelectedItem()).getId();
            case STANDARD:
                return ((StandardModel) spnrStandard.getSelectedItem()).getId();
            default:
                return 0;
        }
    }

//    private void requestFailure(SweetAlertDialog progressDialog, Throwable t) {
//        utils.hideProgressBar(progressDialog);
//        Log.e(TAG, "requestFailure: ", t);
//        utils.showToast(t.getMessage());
//    }

    private boolean validate() {
        //Validate and show error
        if (spnrBoard.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrBoard);
            return false;
        }

        if (spnrSchool.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrSchool);
            return false;
        }

        if (spnrStandard.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrStandard);
            return false;
        }

        if (spnrAcademicYr.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrAcademicYr);
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            //validate and send request
            if (validate())
                sendAcademicDetails();

            //after some time load unloaded data like boards, schools
            if (boardList.size() == 1) {
                new Handler().postDelayed(this::getBoards,
                        1000); //time in ms
            }
        }
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismiss();
        //Start MainActivity
        utils.gotoNextActivity(getActivity(), MainActivity.class, true);
    }


//    @Override
//    public boolean onBackPressed() {
//        NavController navController = NavHostFragment.findNavController(this);
//
//        //following code start personal details page on back press
//        //but it doesn't make any sense show personal details as its already filled and otp also confirmed
//
//        /*
//        //check if back stack has personal details fragment entry then let the system to handle it
//        @SuppressLint("RestrictedApi")
//        Deque<NavBackStackEntry> backStackEntries = navController.getBackStack();
//        for (NavBackStackEntry entry : backStackEntries) {
//            if (entry.getDestination().getId() == R.id.personalDetailsFragment)
//                return false;
//        }
//
//        //launch personal details
//        navController.navigate(R.id.action_academicsFragment_to_personalDetailsFragment);
//        return true;
//
//         */
//    }
}