package com.yespustak.yespustakapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yespustak.yespustakapp.models.NoteModel;
import com.yespustak.yespustakapp.repos.NotesRepo;

import java.util.Date;
import java.util.List;

public class NotesFragmentViewModel extends AndroidViewModel {
    private final NotesRepo notesRepo;
    private final LiveData<List<NoteModel>> notesLiveData;
    private MutableLiveData<NoteModel> selectedNote;
//    private MutableLiveData<String> searchQuery;

    public NotesFragmentViewModel(@NonNull Application application) {
        super(application);
        notesRepo = NotesRepo.getInstance(application);
//        searchQuery = new MutableLiveData<>();
        notesLiveData = notesRepo.getAllNotesLiveData();
        selectedNote = new MutableLiveData<>();

//        searchQuery.observeForever(notesRepo::getAllNotesLiveData);

    }

    public long insert(NoteModel note) {
        if (note.getCreatedAt() == null)
            note.setCreatedAt(new Date());
        note.setUpdatedAt(new Date());
        return notesRepo.insert(note);
    }

    public void update(NoteModel note) {
        note.setUpdatedAt(new Date());
        notesRepo.update(note);
    }

    public void delete(NoteModel note) {
        notesRepo.delete(note);
    }

    public void deleteAll() {
        notesRepo.deleteAll();
    }

    public LiveData<List<NoteModel>> getNotesLiveData() {
        return notesRepo.getAllNotesLiveData();
//        return notesLiveData;
    }

    public LiveData<NoteModel> getSelectedNote() {
        return this.selectedNote;
    }

    public void setSelectedNote(NoteModel selectedNote) {
        this.selectedNote.setValue(selectedNote);
//        this.selectedNote = selectedNote;
    }

//    public MutableLiveData<String> getSearchQuery() {
//        return searchQuery;
//    }
}
