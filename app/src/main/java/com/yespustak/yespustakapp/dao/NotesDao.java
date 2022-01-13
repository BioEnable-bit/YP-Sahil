package com.yespustak.yespustakapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.yespustak.yespustakapp.models.NoteModel;

import java.util.List;

@Dao
public interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NoteModel note);

    @Update
    void update(NoteModel note);

    @Delete
    void delete(NoteModel note);

    @Query("DELETE FROM notes")
    void deleteAll();

//    @Query("SELECT * FROM notes ORDER BY updated_at DESC")
//    @Query("SELECT * FROM notes WHERE description LIKE '%' || :searchQuery || '%' ORDER BY isPinned DESC, updated_at DESC")
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updated_at DESC")
    LiveData<List<NoteModel>> getAllNotes();

    @Query("SELECT * FROM notes WHERE book_id = :bookId ORDER BY id DESC")
    LiveData<List<NoteModel>> getNotesForBookId(int bookId);

//    @Query("SELECT * FROM notes WHERE description LIKE '%' || :search || '%' ORDER BY isPinned DESC, updated_at DESC")
//    @Query("SELECT * FROM notes WHERE description LIKE :search ORDER BY isPinned DESC, updated_at DESC")
//    LiveData<List<NoteModel>> getAllNotes(String search);

//    @Query("SELECT * FROM notes ORDER BY updated_at DESC")
//    List<NoteModel> getAllNotesSync();

//    @Query("UPDATE notes SET title = :title, publication = :publication, imgUrl = :imgUrl, fileUrl = :fileUrl WHERE rid = :rId")
//    void updateFields(int rId, String title, String publication, String imgUrl, String fileUrl);

//    @Query("DELETE FROM DOWNLOAD_BOOKS WHERE id in (:idList)")
//    void deleteBatch(List<Integer> idList);
}
