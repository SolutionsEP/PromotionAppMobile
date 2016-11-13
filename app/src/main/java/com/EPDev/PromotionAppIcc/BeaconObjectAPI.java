package com.EPDev.PromotionAppIcc;

import retrofit.Call;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Created by soluciones on 10-08-2016.
 */
public interface BeaconObjectAPI {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
    */
    @GET("api/RetrofitAndroidObjectResponse")
    Call<Beacon> getBeaconDetails();
}
