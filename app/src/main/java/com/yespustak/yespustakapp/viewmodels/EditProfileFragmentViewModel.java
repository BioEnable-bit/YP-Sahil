package com.yespustak.yespustakapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.repos.UserRepo;

import java.util.List;

public class EditProfileFragmentViewModel extends AndroidViewModel {

    private final UserRepo userRepo;
    private LiveData<UserModel> userModelLiveData;
    private MutableLiveData<List<String>> sectionsLiveData;


    public EditProfileFragmentViewModel(Application application) {
        super(application);
        this.userRepo = UserRepo.getInstance(application);
        this.userModelLiveData = userRepo.getUserModelLiveData();
    }

    public void syncUser() {
        userRepo.syncUser();
    }

    public void insert(UserModel userModel) {
        userRepo.insert(userModel);
    }

    public void update(UserModel userModel) {
        userRepo.insert(userModel);
    }

    public void delete(UserModel userModel) {
        userRepo.insert(userModel);
    }

    public LiveData<UserModel> getUserModelLiveData() {
        return userModelLiveData;
    }

    public LiveData<List<String>> getSectionsLiveData(int schoolId, int standardId) {
        if (sectionsLiveData == null)
            sectionsLiveData = userRepo.requestSections(schoolId, standardId);

        return sectionsLiveData;
    }

    public MutableLiveData<BaseResponse> saveUserProfile(UserModel user) {
        return userRepo.saveUserProfile(user);
    }
    public MutableLiveData<BaseResponse> sendOtpNewContact(String uid, String email, String mobileNo) {
        return userRepo.sendOtpNewContact(uid, email, mobileNo);
    }

}
