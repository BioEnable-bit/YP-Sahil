package com.yespustak.yespustakapp.repos;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.GetPurchasedBooks;
import com.yespustak.yespustakapp.dao.DownloadBookDao;
import com.yespustak.yespustakapp.database.YpDatabase;
import com.yespustak.yespustakapp.models.DownloadBook;
import com.yespustak.yespustakapp.utils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadBookRepo {
    private static final String TAG = "DownloadBookRepo";

    private DownloadBookDao downloadBookDao;
    private LiveData<List<DownloadBook>> allDownloadBooks;
    private static DownloadBookRepo downloadBookRepo;

    private MutableLiveData<Boolean> isSyncing = new MutableLiveData<>();

    public static DownloadBookRepo getInstance(Context context) {
        if (downloadBookRepo == null)
            downloadBookRepo = new DownloadBookRepo(context);

        return downloadBookRepo;
    }

    private DownloadBookRepo(Context application) {
        YpDatabase database = YpDatabase.getInstance(application);
        downloadBookDao = database.downloadBookDao();
        allDownloadBooks = downloadBookDao.getAllDownloadBooks();
    }

    public void insert(DownloadBook downloadBook) {
        downloadBookDao.insert(downloadBook);
    }

    public void update(DownloadBook downloadBook) {
        downloadBookDao.update(downloadBook);
    }

    public void delete(DownloadBook downloadBook) {
        downloadBookDao.delete(downloadBook);
    }

    public void deleteAll() {
        downloadBookDao.deleteAll();
    }

    public LiveData<List<DownloadBook>> getAllDownloadBooks() {
        return allDownloadBooks;
    }

    public List<DownloadBook> getAllDownloadBooksSync() {
        return downloadBookDao.getAllDownloadBooksSync();
    }

    public void updateViaRemoteBook(DownloadBook book) {
         downloadBookDao.updateFields(book.getRid(), book.getTitle(), book.getPublication(), book.getImgUrl(), book.getFileUrl());
    }

    private HashMap<Integer, DownloadBook> getBooksMap() {
        HashMap<Integer, DownloadBook> booksMap = new HashMap<>();
        List<DownloadBook> downloadBooks = getAllDownloadBooksSync();
        for (DownloadBook book : downloadBooks)
            booksMap.put(book.getRid(), book);

        return booksMap;
    }

    //TODO insert, update, delete operations must done on separate thread

    public MutableLiveData<Boolean> getPurchasedBooksFromApi() {
        //must run this in background thread
        isSyncing.setValue(true);
        Call<GetPurchasedBooks> call = Retrofit2Client.getInstance().getApiService().getPurchasedBooks(utils.getIMEINumber());
        Log.e("CallAPI",""+call);
        call.enqueue(new Callback<GetPurchasedBooks>() {
            @Override
            public void onResponse(Call<GetPurchasedBooks> call, Response<GetPurchasedBooks> response) {
                if (!response.isSuccessful() || response.body().getPurchasedBooks() == null)
                    return;


                Log.e(TAG, "onResponse: "+response.body().getPurchasedBooks());


                List<DownloadBook> remoteDownloadBooks = response.body().getPurchasedBooks();
                List<Integer> insertedList = new ArrayList<>();
                HashMap<Integer, DownloadBook> booksMap = getBooksMap();


                for (DownloadBook book : remoteDownloadBooks) {
                    if (booksMap.containsKey(book.getRid()))
                        updateViaRemoteBook(book);
                    else
                        insert(book);

                    insertedList.add(book.getRid());
                }

                for (Map.Entry<Integer, DownloadBook> entry : getBooksMap().entrySet()) {
                    if (!insertedList.contains(entry.getValue().getRid()))
                        delete(entry.getValue());
                }

                //this is used to show/hide progress bar
                isSyncing.setValue(false);
            }

            @Override
            public void onFailure(Call<GetPurchasedBooks> call, Throwable t) {
                Log.e(TAG, "onFailure: fail to fetch purchased books ", t);
                isSyncing.setValue(false);
            }
        });

        return isSyncing;
    }
}
