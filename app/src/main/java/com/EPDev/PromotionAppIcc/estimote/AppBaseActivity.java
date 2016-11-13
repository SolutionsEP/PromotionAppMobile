package com.EPDev.PromotionAppIcc.estimote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by soluciones on 21-08-2016.
 */
public abstract class AppBaseActivity extends Activity {

    public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "com.hrupin.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";

    private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();

    public static final IntentFilter INTENT_FILTER = createIntentFilter();

    private static IntentFilter createIntentFilter() {

        IntentFilter filter = new IntentFilter();

        filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);

        return filter;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish(); // Exit
    }

    protected void registerBaseActivityReceiver() {

        registerReceiver(baseActivityReceiver, INTENT_FILTER);

    }

    protected void unRegisterBaseActivityReceiver() {

        unregisterReceiver(baseActivityReceiver);

    }

    public class BaseActivityReceiver extends BroadcastReceiver {

        @Override

        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {

                finish();

            }

        }

    }

    protected void closeAllActivities() {

        sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));

    }

}