package com.yespustak.yespustakapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yespustak.yespustakapp.models.DownloadBook;

import java.util.List;

@Dao
public interface DownloadBookDao {

    @Insert
    void insert(DownloadBook downloadBook);

    @Update
    void update(DownloadBook downloadBook);

    @Delete
    void delete(DownloadBook downloadBook);

    @Query("DELETE FROM DOWNLOAD_BOOKS")
    void deleteAll();

    @Query("SELECT * FROM DOWNLOAD_BOOKS ORDER BY id DESC")
    LiveData<List<DownloadBook>> getAllDownloadBooks();

    @Query("SELECT * FROM DOWNLOAD_BOOKS ORDER BY id DESC")
    List<DownloadBook> getAllDownloadBooksSync();

    @Query("UPDATE DOWNLOAD_BOOKS SET title = :title, publication = :publication, imgUrl = :imgUrl, fileUrl = :fileUrl WHERE rid = :rId")
    void updateFields(int rId, String title, String publication, String imgUrl, String fileUrl);

    @Query("select * from download_books where rid= :id")
    DownloadBook getBook(int id);

    @Query("select exists (select * from download_books where title = :name)")
    boolean book_exist(String name);

//    @Query("DELETE FROM DOWNLOAD_BOOKS WHERE id in (:idList)")
//    void deleteBatch(List<Integer> idList);
}
