package com.yespustak.yespustakapp.repos;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.yespustak.yespustakapp.dao.NotesDao;
import com.yespustak.yespustakapp.database.YpDatabase;
import com.yespustak.yespustakapp.models.NoteModel;

import java.util.List;

public class NotesRepo {
    private static final String TAG = "DownloadBookRepo";

    private NotesDao notesDao;
    private LiveData<List<NoteModel>> allNotesLiveData;
    private static NotesRepo notesRepo;

    public static NotesRepo getInstance(Context context) {
        if (notesRepo == null)
            notesRepo = new NotesRepo(context);

        return notesRepo;
    }

    private NotesRepo(Context application) {
        YpDatabase database = YpDatabase.getInstance(application);
        notesDao = database.notesDao();
        allNotesLiveData = notesDao.getAllNotes();

    }

    public long insert(NoteModel note) {
        return notesDao.insert(note);
    }

    public void update(NoteModel note) {
        notesDao.update(note);
    }

    public void delete(NoteModel note) {
        notesDao.delete(note);
    }

    public void deleteAll() {
        notesDao.deleteAll();
    }

    public LiveData<List<NoteModel>> getAllNotesLiveData() {
        return allNotesLiveData;
//        return notesDao.getAllNotes("%" + search + "%");
    }

    public LiveData<List<NoteModel>> getNotesByBookIdLiveData(int bookId) {
        return notesDao.getNotesForBookId(bookId);
    }

//    public List<NoteModel> getAllNotesSync() {
//        return notesDao.getAllNotesSync();
//    }

}
