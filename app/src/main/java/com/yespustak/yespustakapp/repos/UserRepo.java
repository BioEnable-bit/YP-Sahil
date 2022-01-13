package com.yespustak.yespustakapp.repos;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.GetSections;
import com.yespustak.yespustakapp.api.response.GetUserProfile;
import com.yespustak.yespustakapp.dao.UserDao;
import com.yespustak.yespustakapp.database.YpDatabase;
import com.yespustak.yespustakapp.models.UserModel;
import com.yespustak.yespustakapp.utils.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepo {
    private static final String TAG = "UserRepo";

    private final UserDao userDao;
    private final LiveData<UserModel> userModelLiveData;
    private static UserRepo userRepo;

    public static UserRepo getInstance(Context context) {
        if (userRepo == null)
            userRepo = new UserRepo(context);

        return userRepo;
    }

    private UserRepo(Context context) {
        YpDatabase database = YpDatabase.getInstance(context);
        this.userDao = database.userDao();
        this.userModelLiveData = userDao.getUser(utils.getIMEINumber());
    }

    public void insert(UserModel userModel) {
        userDao.insert(userModel);
    }

    public void delete(UserModel userModel) {
        userDao.delete(userModel);
    }

    public void deleteAll() {
        userDao.deleteAll();
    }

    public LiveData<UserModel> getUserModelLiveData() {
        return userModelLiveData;
    }

    public UserModel getUserModel() {
        return userDao.getUserSync(utils.getIMEINumber());
    }

    public MutableLiveData<Boolean> syncUser() {
        MutableLiveData<Boolean> isSyncing = new MutableLiveData<>();
        isSyncing.setValue(true);
        Call<GetUserProfile> call = Retrofit2Client.getInstance().getApiService().getUser(utils.getIMEINumber());
        call.enqueue(new Callback<GetUserProfile>() {
            @Override
            public void onResponse(@NotNull Call<GetUserProfile> call, @NotNull Response<GetUserProfile> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "syncUser onResponse: " + response.raw().toString());
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS) {
                    UserModel userModel = response.body().getUser();
                    userModel.setDeviceId(utils.getIMEINumber());
                    insert(response.body().getUser());
                } else
                    Log.e(TAG, "syncUser onResponse: " + response.body().getMessage());

                isSyncing.setValue(false);
            }

            @Override
            public void onFailure(Call<GetUserProfile> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                isSyncing.setValue(false);
            }
        });
        return isSyncing;
    }

    public MutableLiveData<BaseResponse> sendOtpNewContact(String uid, String email, String mobileNo) {
        MutableLiveData<BaseResponse> responseMutableLiveData = new MutableLiveData<>();
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().sendOtpNewContact(uid, email, mobileNo, utils.getIMEINumber());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                responseMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                responseMutableLiveData.setValue(null);
            }
        });

        return responseMutableLiveData;
    }

    public MutableLiveData<BaseResponse> saveUserProfile(UserModel user) {
        MutableLiveData<BaseResponse> saveUserResponse = new MutableLiveData<>();
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().saveUser(
                user.getName(),
                user.getGender(),
                user.getEmail(),
                user.getDob(),
                user.getMobileNo(),
                user.getWhatsappNo(),
                user.getSectionName(),
//                user.getAcademicYear(),
                user.getRollNo(),
//                user.getGrNumber(),
                user.getDeviceId()
        );
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "saveUserProfile onResponse: " + response.raw().toString());
                    saveUserResponse.setValue(null);
                    return;
                }
                saveUserResponse.setValue(response.body());

                userDao.updateFields(user.getUid(), user.getName(), user.getEmail(), user.getDob(),
                        user.getMobileNo(), user.getWhatsappNo());
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                saveUserResponse.setValue(null);
            }
        });

        return saveUserResponse;
    }

    public MutableLiveData<List<String>> requestSections(int schoolId, int standardId) {
        final MutableLiveData<List<String>> sectionsLiveData = new MutableLiveData<>();

        Call<GetSections> call = Retrofit2Client.getInstance().getApiService().getSections(schoolId, standardId);
        call.enqueue(new Callback<GetSections>() {
            @Override
            public void onResponse(Call<GetSections> call, Response<GetSections> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "fetchSections onResponse: " + response.raw().toString());
                    return;
                }

                if (response.body().getStatus() == Constants.STATUS_SUCCESS)
                    sectionsLiveData.setValue(response.body().getSections());

                else
                    Log.e(TAG, "fetchSections onResponse: " + response.body().getMessage());
            }

            @Override
            public void onFailure(Call<GetSections> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
        return sectionsLiveData;
    }
}
