package com.yespustak.yespustakapp.api;

import com.yespustak.yespustakapp.Constants;
import com.yespustak.yespustakapp.api.response.APIError;
import com.yespustak.yespustakapp.utils.SharedPref;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2Client {

    private static Retrofit2Client instance = null;
    private static Retrofit retrofit;
    private OkHttpClient client;
    private APIService apiService;

    private Retrofit2Client() {

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                String authToken = SharedPref.getString(getClass().getSimpleName(), Constants.AUTH_TOKEN);
                if (authToken != null)
                    requestBuilder.addHeader("auth_token", authToken);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

//        if (BuildConfig.DEBUG) {
//            okHttpBuilder.addInterceptor(loggingInterceptor);
//        }
        okHttpBuilder.addInterceptor(interceptor);
        client = okHttpBuilder.connectTimeout(40, TimeUnit.SECONDS).build();

        retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);
    }

    private static Retrofit getRetrofit() {
        return retrofit;
    }

    public static Retrofit2Client getInstance() {
        if (instance == null) {
            instance = new Retrofit2Client();
        }

        return instance;
    }

    public APIService getApiService() {
        return apiService;
    }


    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter = Retrofit2Client.getRetrofit().responseBodyConverter(APIError.class, new Annotation[0]);
        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }
}