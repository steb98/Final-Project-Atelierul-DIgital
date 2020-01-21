package com.buggy.tripbook;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataRetrofit {
    @GET("/trips")  //  "/trips"
    Call<ArrayList<TripItemModel>> getUsers();
}
