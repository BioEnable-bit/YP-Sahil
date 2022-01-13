package com.yespustak.yespustakapp.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.repos.UserRepo;

import java.util.List;

public class ProfileFragmentViewModel extends AndroidViewModel {

    private final UserRepo userRepo;
    private final LiveData<UserModel> userModelLiveData;
    private MutableLiveData<List<String>> sectionsLiveData;


    public ProfileFragmentViewModel(Application application) {
        super(application);
        this.userRepo = UserRepo.getInstance(application);
        this.userModelLiveData = userRepo.getUserModelLiveData();
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

    public LiveData<List<String>> getSectionsLiveData() {
            if (sectionsLiveData == null)
                sectionsLiveData = userRepo.requestSections(2,3);

            return sectionsLiveData;
    }

    public MutableLiveData<BaseResponse> saveUserProfile(UserModel user) {
        return userRepo.saveUserProfile(user);
    }

    public MutableLiveData<Boolean> syncUser() {
        return userRepo.syncUser();
    }
}
