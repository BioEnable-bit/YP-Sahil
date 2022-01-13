package com.yespustak.yespustakapp.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.utils.ModelSharedPref;
import com.yespustak.yespustakapp.utils.SharedVariables;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.NotesFragmentViewModel;

import org.jetbrains.annotations.NotNull;

public class EditNoteFragment extends DialogFragment implements View.OnClickListener {

    EditText etTitle, etDescription;
    LinearLayout llTimestampContainer;
    TextView tvTimestamp;
    ImageView ivPin;
    Button btnSave;

    NotesFragmentViewModel viewModel;
    NoteModel note;

    boolean isDialogMode = false;

    public static EditNoteFragment newInstance(int bookId, String desc) {
        EditNoteFragment editNoteDialogFragment = new EditNoteFragment();

        //example of passing args
        Bundle args = new Bundle();
        args.putInt("bookId", bookId);
        args.putString("desc", desc);
        args.putBoolean("isDialogMode", true);
        editNoteDialogFragment.setArguments(args);

        return editNoteDialogFragment;
    }

    public static EditNoteFragment newInstance(NoteModel note) {
        EditNoteFragment editNoteDialogFragment = new EditNoteFragment();

        //example of passing args
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        args.putBoolean("isDialogMode", true);
        editNoteDialogFragment.setArguments(args);

        return editNoteDialogFragment;
    }

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            isDialogMode = getArguments().getBoolean("isDialogMode");
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(NotesFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        if (!isDialogMode)
            note = ModelSharedPref.getInstance().getModel(NoteModel.class);
        else {
            note = (NoteModel) getArguments().getSerializable("note");
//            note.setBookId(getArguments().getInt("bookId"));
//            note.setDescription(getArguments().getString("desc"));
        }

//        note = viewModel.getSelectedNote().getValue();
        updateView();
//
//        //set observer and update view
//        viewModel.getSelectedNote().observe(getViewLifecycleOwner(), noteModel -> {
//            note = noteModel;
//            updateView(note);
//        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!isDialogMode)
            menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void setupViews(View view) {
        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        ivPin = view.findViewById(R.id.iv_pin);
        llTimestampContainer = view.findViewById(R.id.ll_timestamp_container);
        tvTimestamp = view.findViewById(R.id.tv_timestamp);
        btnSave = view.findViewById(R.id.btn_save);

        ivPin.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void updateView() {
        if (note == null)
            return;

        etTitle.setText(note.getTitle());
        etDescription.setText(note.getDescription());
        llTimestampContainer.setVisibility(note.getId() > 0 ? View.VISIBLE : View.GONE);
        tvTimestamp.setText(utils.dateToString(note.getCreatedAt(), SharedVariables.DF_NOTES));

        updatePinView();
    }

    private void updatePinView() {
        ivPin.setImageDrawable(AppCompatResources.getDrawable(requireContext(),
                note.isPinned() ? R.drawable.ic_baseline_bookmark_24 : R.drawable.ic_baseline_bookmark_border_24));
        ivPin.setColorFilter(getResources()
                .getColor(note.isPinned() ? R.color.colorPrimary : android.R.color.darker_gray, requireActivity().getTheme()));
    }

    private boolean validate() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            utils.setError(etTitle, utils.getStringResource(R.string.msg_field_required, getString(R.string.title_title)), true);
            return false;
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            utils.setError(etDescription, utils.getStringResource(R.string.msg_field_required, getString(R.string.title_content)), true);
            return false;
        }

        return true;
    }

    @Override
    public void onPause() {
        boolean isTitleEmpty = etTitle.getText().toString().isEmpty();
        if (!etDescription.getText().toString().isEmpty()) {

            note.setTitle(isTitleEmpty ? "No title" : etTitle.getText().toString().trim());
            note.setDescription(etDescription.getText().toString().trim());
            int id = (int) viewModel.insert(note);
            note.setId(id);
        }
        super.onPause();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_pin:
                note.setPinned(!note.isPinned());
                updatePinView();
                break;
            case R.id.btn_save:
                if (validate()) {
                    if (note == null)
                        note = new NoteModel();
                    note.setTitle(etTitle.getText().toString().trim());
                    note.setDescription(etDescription.getText().toString().trim());

                    //onPause calling .insert() 1 more time -> that's why set id here. so next insert() call will update existing note
                    note.setId((int) viewModel.insert(note));

                    if (note.getId() > 0) {
                        if (!isDialogMode)
                            requireActivity().finish();
                        else
                            dismiss();
                    } else
                        utils.showToast(getString(R.string.msg_something_went_wrong));
                }
                break;
        }
    }

    //Create interface in your DialogFragment (or a new file)
    public interface OnDismissListener {
        void onDismiss(EditNoteFragment dialogFragment);
    }

    //create Pointer and setter to it
    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener dismissListener) {
        this.onDismissListener = dismissListener;
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
    }
}