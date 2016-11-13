package com.EPDev.PromotionAppIcc;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;
import java.util.Set;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edwin Alvarado on 04-08-2016.
 */
public class ServiceFunctions {

    static String urlPost = "http://promotion-admin.herokuapp.com";
    //static String urlStores = "http://200.74.223.98:8001/";
    static String urlStores = "http://promotion-admin.herokuapp.com/";
    private Context context;
    public Context mainCtx;


    public static void getRetrofitArray(final MyApplication app) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlPost)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = retrofit.create(RetrofitArrayAPI.class);

        Call<List<Post>> call = service.getPostDetails();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response, Retrofit retrofit) {

                try {

                    List<Post> PostData = response.body();
                    Log.e("FLAG_1","I'm Here, and pass, and my value is: "+ PostData.get(1).getTitle()+", and size is: "+PostData.size());

                    //Fill Notifications With Api Data
                    //MainActivity.FillNotifications(PostData,app);
                    //for (int i = 0; i < PostData.size(); i++) {
                    //Get data, with this line "PostData.get(i).getUserId()" ad put on the listView
                    //Log.e("JSON Response", String.valueOf(PostData));
                    //}

                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    public static void getStoresArray(final MyApplication app, final Context context) {
        Log.e("FLAG_STORE_SERVICE","INIT STORE SERVICE");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlStores)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StoresArrayAPI service = retrofit.create(StoresArrayAPI.class);

        Call<List<Store>> call = service.getStoresDetails();

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Response<List<Store>> response, Retrofit retrofit) {

                try {

                    List<Store> StoreData = response.body();
                    Stores StoresData = new Stores(StoreData);
                    //Log.e("FLAG_STORE_SERVICE","I'm Here on the stores service, and my value is: "+ StoreData.get(1).getBeaconInfo()+", and size is: "+StoreData.size());

                    /*List<Beacon> beacon = StoreData.get(1).getBeaconInfo();
                    String Mayor = beacon.get(0).getMajor();
                    Log.e("FLAG_STORE_SERVICE","I'm Here on the stores service, and mayor value is: "+ Mayor);*/

                    String jsonStores = StoresData.serializeStore();

                    //Set stores - Save Store on sharePreferences
                    SharedPreferences preferencesStores = context.getSharedPreferences("PreferencesStores", context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferencesStores.edit();
                    editor.putString("key_StoresObject", jsonStores);
                    editor.commit();

                    /*/Get Stores
                    SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);
                    String jsonStoresSaved = preferencesStores.getString("key_StoresObject", null);
                    */

                    //Fill Notifications With Api Data
                    //MainActivity.FillNotifications(app,StoresData);

                    //for (int i = 0; i < PostData.size(); i++) {
                    //Get data, with this line "PostData.get(i).getUserId()" ad put on the listView
                    //Log.e("JSON Response", String.valueOf(PostData));
                    //}

                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }
}
