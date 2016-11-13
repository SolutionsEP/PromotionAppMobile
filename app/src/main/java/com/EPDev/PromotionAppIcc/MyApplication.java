package com.EPDev.PromotionAppIcc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.EPDev.PromotionAppIcc.estimote.BeaconID;
import com.EPDev.PromotionAppIcc.estimote.BeaconNotificationsManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.SystemRequirementsChecker;
import com.EPDev.PromotionAppIcc.MyApplication;

import java.util.List;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private boolean beaconNotificationsEnabled = false;
    public static String titleNotification = "ProMotion ";

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), "promotion-app-icc", "7a22d1fa9d4e13371abcbaf8487d4698");

        // uncomment to enable debug-level logging
        // it's usually only a good idea when troubleshooting issues with the Estimote SDK
//        EstimoteSDK.enableDebugLogging(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public void enableBeaconNotifications(Stores StoresData) {

        /*/Serialize StoresData
        String jsonStores = StoresData.serializeStore();

        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesStores.edit();
        editor.putString("key_StoresObject", jsonStores);
        editor.commit();*/

        Log.e("FLAG_","Im in enableBeaconNotifications");

        if (beaconNotificationsEnabled) { return; }

        String TAG = "NOTIFICATION";

        //BLUEBERRY Set Notifications

        for (int i = 0; i < StoresData.getStoresData().size(); i++) {
            //Get data, with this line "PostData.get(i).getUserId()" ad put on the listView
            //Blue first Store, Ice second Store, Mint Third Store
            Log.e("JSON_STORE_ACTIVE", StoresData.getStoresData().get(i).getisActive());

            if (StoresData.getStoresData().get(i).getPromotions().size() > 0) {
                Log.e("JSON Response", String.valueOf(StoresData.getStoresData()));

                /*String UUID = StoresData.getStoresData().get(i).getBeaconInfo().get(0).getUUID();
                String Mayor = StoresData.getStoresData().get(i).getBeaconInfo().get(0).getMajor();
                String Minor = StoresData.getStoresData().get(i).getBeaconInfo().get(0).getMinor();

                Log.d(TAG, "INIT Beacon Initializate, UUID: " + UUID + " , Mayor: " + Mayor + " , Minor: " + Minor);
                Log.d(TAG, "Shop name " + StoresData.getStoresData().get(i).getName() + " , size promotions: " + StoresData.getStoresData().get(i).getPromotions().size());
                */
                for (int j = 0; j < StoresData.getStoresData().get(i).getPromotions().size(); j++) {
                    if (StoresData.getStoresData().get(i).getPromotions().get(j).getIsActive()) {

                        if (StoresData.getStoresData().get(i).getBeaconInfo().size() > 0){
                            String UUID = StoresData.getStoresData().get(i).getBeaconInfo().get(0).getUUID();
                            String Mayor = StoresData.getStoresData().get(i).getBeaconInfo().get(0).getMajor();
                            String Minor = StoresData.getStoresData().get(i).getBeaconInfo().get(0).getMinor();

                            Log.d(TAG, "INIT Beacon Initializate, UUID: " + UUID + " , Mayor: " + Mayor + " , Minor: " + Minor);
                            BeaconNotificationsManager beaconNotificationsManager = new BeaconNotificationsManager(this);
                            beaconNotificationsManager.addNotification(
                                    new BeaconID(UUID, Integer.parseInt(Mayor), Integer.parseInt(Minor)),
                                    StoresData.getStoresData().get(i).getPromotions().get(j).getTitle(), StoresData.getStoresData().get(i).getName(),
                                    StoresData.getStoresData().get(i).getPromotions().get(j).getId(),
                                    StoresData.getStoresData().get(i).getShopID());

                            beaconNotificationsManager.startMonitoring(StoresData.getStoresData());
                            Log.d(TAG, "END Beacon Initializate");

                            beaconNotificationsEnabled = true;
                        }
                    }
                }
            }
        }


    }



    public boolean isBeaconNotificationsEnabled() {
        return beaconNotificationsEnabled;
    }



}
