package com.EPDev.PromotionAppIcc;

import java.util.List;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Created by soluciones on 10-08-2016.
 */
public interface StoresArrayAPI {

    /*Retrofit get annotation with our URL
    And our method that will return us details of student.
    */
    //@GET("CompensarPDFs/tiendas.txt")
    @GET("/api/stores.json")
    Call<List<Store>> getStoresDetails();
}
