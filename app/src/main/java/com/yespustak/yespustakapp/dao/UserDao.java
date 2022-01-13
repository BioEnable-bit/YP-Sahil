package com.yespustak.yespustakapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yespustak.yespustakapp.models.UserModel;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserModel userModel);

    @Update
    void update(UserModel userModel);

    @Delete
    void delete(UserModel userModel);

    @Query("SELECT * FROM users WHERE deviceId LIKE :deviceId")
    LiveData<UserModel> getUser(String deviceId);

    @Query("SELECT * FROM users WHERE deviceId LIKE :deviceId")
    UserModel getUserSync(String deviceId);

    @Query("DELETE FROM users")
    void deleteAll();

    @Query("UPDATE users SET name = :name, email = :email, dob = :dob, mobileNo = :mobileNo, whatsappNo = :whatsappNo WHERE uid = :uid")
    void updateFields(String uid, String name, String email, String dob, String mobileNo, String whatsappNo);

//    @Query("SELECT * FROM users ORDER BY uid DESC")
//    LiveData<List<DownloadBook>> getAllUsers();

//    @Query("SELECT * FROM DOWNLOAD_BOOKS ORDER BY id DESC")
//    List<DownloadBook> getAllDownloadBooksSync();
}
