package com.yespustak.yespustakapp.api;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.api.response.APIError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit4Razorpay {

    private static Retrofit4Razorpay instance = null;
    private static Retrofit retrofit;
    private OkHttpClient client;
    private APIService apiService;

    public Retrofit4Razorpay() {

//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
//        okHttpBuilder.addInterceptor(new TokenInterceptor());

//        if (BuildConfig.DEBUG) {
//            okHttpBuilder.addInterceptor(loggingInterceptor);
//        }

        client = okHttpBuilder.build();

        retrofit = new Retrofit.Builder().baseUrl(Constants.RAZOR_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);
    }

    private static Retrofit getRetrofit() {
        return retrofit;
    }

    public static Retrofit4Razorpay getInstance() {
        if (instance == null) {
            instance = new Retrofit4Razorpay();
        }

        return instance;
    }

    public APIService getApiService() {
        return apiService;
    }


    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter = Retrofit4Razorpay.getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);
        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }
}