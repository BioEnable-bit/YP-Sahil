package com.yespustak.yespustakapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yespustak.yespustakapp.api.Retrofit2Client;
import com.yespustak.yespustakapp.api.response.BaseResponse;
import com.yespustak.yespustakapp.api.response.PdfData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendPdfDataService extends Service {
    private static final String TAG = "SendPdfDataService";

    boolean isBackingUp = false;
    PdfData pdfData;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");

        try{
            pdfData = (PdfData) intent.getSerializableExtra("pdf_data");
        }catch (Exception exception){

        }
        if (pdfData != null)
            saveBookData(pdfData);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void saveBookData(PdfData pdfData) {
        isBackingUp = true;
        Call<BaseResponse> call = Retrofit2Client.getInstance().getApiService().saveStudentBookData(pdfData);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                response.toString();
                if (response.isSuccessful())
                    Log.i(TAG, "onResponse: data saved successfully");
                isBackingUp = false;
                //operation complete. then release resources
                stopSelf();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                isBackingUp = false;
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
    }

}
