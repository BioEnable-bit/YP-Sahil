package com.yespustak.yespustakapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.picasso.Picasso;
import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.R;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.utils.utils;
import com.yespustak.yespustakapp.viewmodels.ProfileFragmentViewModel;

import org.jetbrains.annotations.NotNull;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
//    private static final int GALLERY_REQUEST_CODE = 123;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivProfile, ivEditProfile;
    private TextView tvName, tvGender, tvEmail, tvDob, tvMobileNo, tvWhatsappNo, tvBoard, tvSchool, tvClass, tvSection, tvAcademicYr, tvRollNo, tvGrNo;

    private ProfileFragmentViewModel viewModel;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mi_search).setVisible(false);
    }

    private void setupViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        ivProfile = view.findViewById(R.id.iv_profile);
        ivEditProfile = view.findViewById(R.id.iv_edit_profile);
        tvGender = view.findViewById(R.id.tv_gender);
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvDob = view.findViewById(R.id.tv_dob);
        tvMobileNo = view.findViewById(R.id.tv_mobile_no);
        tvWhatsappNo = view.findViewById(R.id.tv_whatsapp_no);
        tvBoard = view.findViewById(R.id.tv_board_name);
        tvSchool = view.findViewById(R.id.tv_school_name);
        tvClass = view.findViewById(R.id.tv_class_name);
        tvSection = view.findViewById(R.id.tv_section_name);
        tvAcademicYr = view.findViewById(R.id.tv_academic_yr);
        tvRollNo = view.findViewById(R.id.tv_roll_no);
        tvGrNo = view.findViewById(R.id.tv_no);

        //set listeners
        ivEditProfile.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.syncUser().observe(getViewLifecycleOwner(), isSyncing -> {
            swipeRefreshLayout.setRefreshing(isSyncing);
        }));

//        after completing all setup -> set observers
        viewModel.getUserModelLiveData().observe(getViewLifecycleOwner(), this::updateUi);
    }

    private void updateUi(UserModel user) {
        if (user == null)
            return;

        if (user.getProfilePhoto() != null) {
            Picasso.get().load(Constants.DASHBOARD_URL + user.getProfilePhoto())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.img_not_available_thumb)
                    .into(ivProfile);
        }
        tvName.setText(user.getName());
        tvGender.setText(user.getGender());
        tvEmail.setText(user.getEmail());
        tvDob.setText(user.getDob());//TODO format it
        tvMobileNo.setText(user.getMobileNo());
        tvWhatsappNo.setText(user.getWhatsappNo() == null ? getString(R.string.text_na) : user.getWhatsappNo());
        tvBoard.setText(user.getBoardName());
        tvSchool.setText(user.getSchoolName());
        tvClass.setText(user.getStandardName());
        tvSection.setText(user.getSectionName() == null ? getString(R.string.text_na) : user.getSectionName());
        tvAcademicYr.setText(user.getAcademicYear());
        tvRollNo.setText(user.getRollNo() == null ? getString(R.string.text_na) : user.getRollNo());
        tvGrNo.setText(user.getGrNumber() == null ? getString(R.string.text_na) : user.getGrNumber());

    }

//    private void setListeners() {
//        ivProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectImageFromGallery();
//            }
//        });
//        String imgUri = SharedPref.getString(TAG, getContext(), SharedPref.profileImgUri);
//        if (imgUri != null) {
//            ivProfile.setImageBitmap(utils.decodeBase64(imgUri));
//        } else {
//            ivProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
//        }
//
//    }

//    public void selectImageFromGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Pick an Image"), GALLERY_REQUEST_CODE);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            Uri targetUri = data.getData();
//            Bitmap bitmap;
//            try {
//                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
//                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
//                String image = utils.encodeToBase64(resizedBitmap);
//                SharedPref.saveString(TAG, getContext(), SharedPref.profileImgUri, image);
//                String imgUri = SharedPref.getString(TAG, getContext(), SharedPref.profileImgUri);
//                ivProfile.setImageBitmap(utils.decodeBase64(imgUri));
//
//                MainActivity.setProfileImg(getContext());
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_edit_profile) {
            utils.gotoNextActivityFragment(requireActivity(), "editProfile");
        }
    }
}
