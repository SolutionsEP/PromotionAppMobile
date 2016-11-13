package com.EPDev.PromotionAppIcc;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by soluciones on 11-08-2016.
 */
public class Splash extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Thread myThreard = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(10600);

                    Log.e("HOME_FRAGMENT","Before main");
                    Intent startMainScreen = new Intent(Splash.this, MainActivity.class);
                    startActivity(startMainScreen);
                    /*Log.e("HOME_FRAGMENT","Before home");
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.home_layout, new MyFragment())
                            .commit();
                    */
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThreard.start();

        //Call application service
        MyApplication app = new MyApplication();
        ServiceFunctions.getStoresArray(app,getApplicationContext());
    }
}
