package com.EPDev.PromotionAppIcc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;

/**
 * Created by soluciones on 08-08-2016.
 */
public class BeaconBackgroundService extends Service {

    public static ConnectivityManager cm;
    private static final String TAG = "BeaconBackgroundService";

    @Override
    public IBinder onBind(final Intent intent) {
        // TODO Auto-generated method stub
        return new LocalBinder<BeaconBackgroundService>(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // TODO Auto-generated method stub
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        startBeaconBackgroudService();
        //start a separate thread and start listening to your network object
    }

    private void startBeaconBackgroudService(){
        Log.e("SERVICES","Beacon service is now on backGround");

        MyApplication app = (MyApplication) getApplication();
        Log.e(TAG,"Preshow Frm BeaconBackgroundService");

        //CallService

        //ServiceFunctions.getRetrofitArray();
    }
}

