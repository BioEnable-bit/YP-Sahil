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

public class RetrofitDictionaryClient {

    private static RetrofitDictionaryClient instance = null;
    private static Retrofit retrofit;
    private final APIService apiService;

    public RetrofitDictionaryClient() {

//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
//        okHttpBuilder.addInterceptor(new TokenInterceptor());

//        if (BuildConfig.DEBUG) {
//            okHttpBuilder.addInterceptor(loggingInterceptor);
//        }

        OkHttpClient client = okHttpBuilder.build();

        retrofit = new Retrofit.Builder().baseUrl(Constants.DICTIONARY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);
    }

    private static Retrofit getRetrofit() {
        return retrofit;
    }

    public static RetrofitDictionaryClient getInstance() {
        if (instance == null) {
            instance = new RetrofitDictionaryClient();
        }

        return instance;
    }

    public APIService getApiService() {
        return apiService;
    }


    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter = RetrofitDictionaryClient.getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);
        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }
}