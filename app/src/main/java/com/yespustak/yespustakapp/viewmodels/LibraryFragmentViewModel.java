package com.yespustak.yespustakapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.repos.DownloadBookRepo;

import java.lang.invoke.MutableCallSite;
import java.util.List;

public class LibraryFragmentViewModel extends AndroidViewModel {
    private final DownloadBookRepo downloadBookRepo;
    private final LiveData<List<DownloadBook>> allDownloadBooks;

    public LibraryFragmentViewModel(@NonNull Application application) {
        super(application);
        downloadBookRepo = DownloadBookRepo.getInstance(application);
        allDownloadBooks = downloadBookRepo.getAllDownloadBooks();
    }

    public void insert(DownloadBook downloadBook) {
        downloadBookRepo.insert(downloadBook);
    }

    public void update(DownloadBook downloadBook) {
        downloadBookRepo.update(downloadBook);
    }

    public void delete(DownloadBook downloadBook) {
        downloadBookRepo.delete(downloadBook);
    }

    public void deleteAll() {
        downloadBookRepo.deleteAll();
    }

    public LiveData<List<DownloadBook>> getAllDownloadBooks() {
        return allDownloadBooks;
    }

    public MutableLiveData<Boolean> syncBooks() {

        return downloadBookRepo.getPurchasedBooksFromApi();
    }

    public MutableLiveData<Boolean> syncFreeBooks() {

        return downloadBookRepo.getNCERTDownloadedBooksFromApi();
    }
}
