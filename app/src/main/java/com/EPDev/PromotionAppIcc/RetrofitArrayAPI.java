package com.EPDev.PromotionAppIcc;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by soluciones61-edwin on 4/6/16.
 */
public interface RetrofitArrayAPI {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
    */
    @GET("posts")
    Call<List<Post>> getPostDetails();

}


