package com.buggy.tripbook;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final Retrofit retrofit;
    private static final String BASE_URL =  "http://10.0.2.2:3000/";  //"http://10.0.2.2:3000/";

    static{
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static Retrofit getRetrofit(){
        return retrofit;
    }
}
