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

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.Boards;
import com.yespustak.yespustakapp.api.response.ReqBook;
import com.yespustak.yespustakapp.api.response.Standards;
import com.yespustak.yespustakapp.models.BoardModel;
import com.yespustak.yespustakapp.models.StandardModel;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.repos.UserRepo;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestABookFragment extends BaseFragment implements View.OnClickListener, SweetAlertDialog.OnSweetClickListener {

    private static final int BOARD = 1, STANDARD = 2;//, SUBJECT = 3;
    private static final int SUBJECT = 1, PUBLICATION = 2, BOOK_TITLE = 3, AUTHOR = 4, EDITION = 5, RELEASE_YR = 6, COMMENT = 7;

    Spinner spnrBoard, spnrStandard, spnrSubject;
    EditText etSubject, etPublication, etBookTitle, etAuthor, etEdition, etReleaseYr, etComments;
    Button btnSubmit;

    UserRepo userRepo;
    UserModel user;

    List<BoardModel> boardList;
    List<StandardModel> standardList;

    ArrayAdapter<BoardModel> boardAdapter;
    ArrayAdapter<StandardModel> standardAdapter;

    private EditText[] formFields;

    public RequestABookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepo = UserRepo.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_a_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        //create views array
        formFields = new EditText[]{etSubject, etPublication, etBookTitle, etAuthor, etEdition, etReleaseYr, etComments};

        userRepo.getUserModelLiveData().observe(getViewLifecycleOwner(), userModel -> {
            user = userModel;
            getBoards();
            getStandards();
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void setupViews(View view) {
        spnrBoard = view.findViewById(R.id.spnr_board);
        spnrStandard = view.findViewById(R.id.spnr_standard);
        spnrSubject = view.findViewById(R.id.spnr_subject);
        etSubject = view.findViewById(R.id.et_subject);
        etPublication = view.findViewById(R.id.et_publication);
        etBookTitle = view.findViewById(R.id.et_book_title);
        etAuthor = view.findViewById(R.id.et_author);
        etEdition = view.findViewById(R.id.et_edition);
        etReleaseYr = view.findViewById(R.id.et_release_yr);
        etComments = view.findViewById(R.id.et_comments);
        btnSubmit = view.findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(this);

        //set spinner data
        boardList = new ArrayList<>();
        boardAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, boardList);
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrBoard.setAdapter(boardAdapter);

        standardList = new ArrayList<>();
        standardAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, standardList);
        standardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrStandard.setAdapter(standardAdapter);

        etSubject.addTextChangedListener(new ReqBookTextWatcher(etSubject));
        etPublication.addTextChangedListener(new ReqBookTextWatcher(etPublication));
        etBookTitle.addTextChangedListener(new ReqBookTextWatcher(etBookTitle));
        etAuthor.addTextChangedListener(new ReqBookTextWatcher(etAuthor));
        etEdition.addTextChangedListener(new ReqBookTextWatcher(etEdition));
        etReleaseYr.addTextChangedListener(new ReqBookTextWatcher(etReleaseYr));
        etComments.addTextChangedListener(new ReqBookTextWatcher(etComments));
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

                if (!response.isSuccessful() || response.body() == null) {
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
                spnrBoard.setSelection(getPositionById(user.getBoardId(), BOARD));
            }

            @Override
            public void onFailure(Call<Boards> call, Throwable t) {
                requestFailure(progressDialog, t);
                utils.showAlert(getContext(), null, getString(R.string.text_error_while_fetching), SweetAlertDialog.ERROR_TYPE, RequestABookFragment.this, false);
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

                if (!response.isSuccessful() || response.body() == null) {
                    utils.showToast(R.string.text_request_fail);
                    standardAdapter.notifyDataSetChanged();
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    //add items, update view
                    standardList.addAll(response.body().getStandardModels());

                } else
                    utils.showToast(R.string.msg_something_went_wrong);

                standardAdapter.notifyDataSetChanged();
                spnrStandard.setSelection(getPositionById(user.getStandardId(), STANDARD));
            }

            @Override
            public void onFailure(Call<Standards> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
    }

    private int getPositionById(int id, int type) {
        if (type == BOARD) {
            for (int i = 0; i < boardList.size(); i++) {
                BoardModel boardModel = boardList.get(i);
                if (boardModel.getId() == id)
                    return i;
            }
        } else if (type == STANDARD) {
            for (int i = 0; i < standardList.size(); i++) {
                StandardModel standardModel = standardList.get(i);
                if (standardModel.getId() == id)
                    return i;
            }
        }

        return 0;
    }

    private boolean validate() {
        //Validate and show error
        if (spnrBoard.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrBoard);
            return false;
        }

        if (spnrStandard.getSelectedItemPosition() == 0) {
            utils.setSpinnerError(spnrStandard);
            return false;
        }

//        if (spnrSubject.getSelectedItemPosition() == 0) {
//            utils.setSpinnerError(spnrSubject);
//            return false;
//        }

        if (!validateField(etSubject, SUBJECT, true))
            return false;

        if (!validateField(etPublication, PUBLICATION, true))
            return false;

        //its a last statement -> return result
        return validateField(etBookTitle, BOOK_TITLE, true);
    }

    private boolean validateField(EditText inputField, int inputType, boolean animate) {
        String input = inputField.getText().toString().trim();
        String errorMsg = null, title;
        switch (inputType) {
            case SUBJECT:
                title = utils.getStringResource(R.string.title_subject);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                break;

            case PUBLICATION:
                title = utils.getStringResource(R.string.title_publication);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                break;

            case BOOK_TITLE:
                title = utils.getStringResource(R.string.text_book_title);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                break;

            case RELEASE_YR:
                title = utils.getStringResource(R.string.text_release_year);
                if (input.length() > 0 && input.length() < 4)
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);
                break;
        }

        utils.setError(inputField, errorMsg, animate);
        return errorMsg == null;
    }

    private int getSelectedId(int dropdownType) {
        switch (dropdownType) {
            case BOARD:
                return ((BoardModel) spnrBoard.getSelectedItem()).getId();
            case STANDARD:
                return ((StandardModel) spnrStandard.getSelectedItem()).getId();
//            case SUBJECT:
//                return ((StandardModel) spnrSubject.getSelectedItem()).getId();
            default:
                return 0;
        }
    }

    private void submitRequest(int boardId, int selectedId, String subject, String publication, String bookTitle, String author, String edition, String releaseYr, String comments) {
        final SweetAlertDialog progressDialog = utils.showProgressBar(getContext());
        Call<ReqBook> call = Retrofit2Client.getInstance().getApiService().requestBook(boardId, selectedId, subject, publication, bookTitle, author, edition, releaseYr, comments, utils.getIMEINumber());
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
                    utils.showAlert(getContext(), isSuccess ? getString(R.string.text_request_submitted) : null, response.body().getMessage() + (isSuccess ? ".\n Request ID is : " + response.body().getResult() : "Unable to submit request"), isSuccess ? SweetAlertDialog.SUCCESS_TYPE : SweetAlertDialog.ERROR_TYPE, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            requireActivity().finish();
                        }
                    }, false);
                }

            }

            @Override
            public void onFailure(Call<ReqBook> call, Throwable t) {
                requestFailure(progressDialog, t);
            }
        });
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
        if (v.getId() == R.id.btn_submit) {
            if (validate()) {
                submitRequest(getSelectedId(BOARD), getSelectedId(STANDARD), utils.getInput(etSubject),
                        utils.getInput(etPublication), utils.getInput(etBookTitle),
                        utils.getInput(etAuthor), utils.getInput(etEdition), utils.getInput(etReleaseYr),
                        utils.getInput(etComments)
                );
            }
        }
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        //go back to previous fragment
        requireActivity().onBackPressed();
    }

    //Text Watcher - used show validation error while editing
    private class ReqBookTextWatcher implements TextWatcher {
        private final EditText editText;

        private ReqBookTextWatcher(EditText editText) {
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
                case R.id.et_subject:
                    validateField(editText, SUBJECT, false);
                    break;
                case R.id.et_publication:
                    validateField(editText, PUBLICATION, false);
                    break;
                case R.id.et_book_title:
                    validateField(editText, BOOK_TITLE, false);
                    break;
                case R.id.et_author:
                    validateField(editText, AUTHOR, false);
                    break;
                case R.id.et_edition:
                    validateField(editText, EDITION, false);
                    break;
                case R.id.et_release_yr:
                    validateField(editText, RELEASE_YR, false);
                    break;
                case R.id.et_comments:
                    validateField(editText, COMMENT, false);
                    break;
            }
        }
    }
}