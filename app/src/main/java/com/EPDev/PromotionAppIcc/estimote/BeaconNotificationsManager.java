package com.EPDev.PromotionAppIcc.estimote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.EPDev.PromotionAppIcc.MainActivity;
import com.EPDev.PromotionAppIcc.MyApplication;
import com.EPDev.PromotionAppIcc.Notification;
import com.EPDev.PromotionAppIcc.PromotionDetailActivity;
import com.EPDev.PromotionAppIcc.R;
import com.EPDev.PromotionAppIcc.Store;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BeaconNotificationsManager{

    public static ArrayList<Notification> MyListBeaconNotif = new ArrayList<Notification>();
    private static final String TAG = "BeaconNotifications";
    public List<Store> StoreData = null;
    private BeaconManager beaconManager;
    public boolean addBeacon = false;

    private boolean finish = true;
    public Random random = new Random();

    private List<Region> regionsToMonitor = new ArrayList<>();
    private HashMap<String, String> enterMessages = new HashMap<>();
    private HashMap<String, String> exitMessages = new HashMap<>();
    private HashMap<String, String> tittleMessages = new HashMap<>();
    private HashMap<String, String> storeIdPrefereces = new HashMap<>();
    private HashMap<String, String> promotionIdPrefereces = new HashMap<>();


    private Context context;

    private int notificationID = 0;

    public BeaconNotificationsManager(Context context) {
        this.context = context;
        addBeacon = false;
        beaconManager = new BeaconManager(context);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public synchronized void onEnteredRegion(Region region, List<Beacon> list) {

                Notification ActualBeacon = new Notification();
                int size = 0;
                boolean sendNotification = false;
                Log.d(TAG, "onEnteredRegion: " + region.getIdentifier());
                String message = enterMessages.get(region.getIdentifier());

                DateFormat myDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String currentDate = myDate.format(new Date());

                Log.e("FLAG_BEACON","This is my Beacon Minor "+region.getMinor()+" , size "+MyListBeaconNotif.size());

                //Validate if the notification was send
                if(MyListBeaconNotif.isEmpty()){

                    sendNotification = true;
                    addBeacon = true;
                }else {
                    sendNotification = sendBeaconNotification(region);
                }

                Log.e("FLAG_BEACON","Send Notification: "+sendNotification);

                if (message != null && sendNotification == true) {
                    if (addBeacon) {

                        size = MyListBeaconNotif.size();
                        ActualBeacon.setDateSend(currentDate);
                        ActualBeacon.setUUID(region.getProximityUUID().toString());
                        ActualBeacon.setMajor(region.getMajor());
                        ActualBeacon.setMinor(region.getMinor());


                        if (MyListBeaconNotif.isEmpty()) {

                            MyListBeaconNotif.add(0, ActualBeacon);

                        } else {

                            MyListBeaconNotif.add(size, ActualBeacon);

                        }
                        Log.e("FLAG_BEACON" , "Size ArrayList" + MyListBeaconNotif.size());
                    }

                    showNotification(message,tittleMessages.get(region.getIdentifier()),region.getIdentifier());
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.d(TAG, "onExitedRegion: " + region.getIdentifier());
                String message = exitMessages.get(region.getIdentifier());
                if (message != null) {
                    //showNotification(message);
                }
            }
        });
    }

    public boolean sendBeaconNotification(Region region){
        DateFormat myDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String currentDate = myDate.format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        boolean beaconExist = false;
        boolean beaconExistSend = false;

        boolean sendNotification;

        //Get Stores
        int time = 1;

        for (int i = 0; i < MyListBeaconNotif.size(); i++) {

            //Compare UUID
            Log.e("FLAG_BEACON", "This is my Region Beacon Minor " + region.getMinor() + " , My List Minor " + MyListBeaconNotif.get(i).getMinor());
            if (MyListBeaconNotif.get(i).getUUID().toString().equals(region.getProximityUUID().toString())
                    && MyListBeaconNotif.get(i).getMajor() == region.getMajor()
                    && MyListBeaconNotif.get(i).getMinor() == region.getMinor()) {

                beaconExist = true;
                //Compare Dates and parse
                Log.e("FLAG_BEACON", "Pass conditionals, Date List: " + MyListBeaconNotif.get(i).getDateSend().toString() + ", Fecha Actual: " + currentDate);
                try {
                    d1 = format.parse(MyListBeaconNotif.get(i).getDateSend().toString());
                    d2 = format.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long diff = d2.getTime() - d1.getTime();
                long diffMinutes = diff / (60 * 1000);

                Log.e("FLAG_BEACON", "Time in minutes: " + diffMinutes + ". Difference: D2: " + d2.getTime() + ", D1: " + d1.getTime());

                Log.e("BEACON_NOTF_TIME", String.valueOf(MainActivity.time));

                if (diffMinutes >= time) {
                    MyListBeaconNotif.get(i).setDateSend(currentDate);
                    beaconExistSend = true;
                }

            }
        }
        Log.e("BEACON_SEND_NOTIFY","Existe el beacon: "+beaconExist+" ,Existe, pero lo envio: "+beaconExistSend);
        if (beaconExist) {
            addBeacon = false;
            if (beaconExistSend) {
                return true;
            } else {
                return false;
            }
        } else {
            addBeacon = true;
            return true;
        }
    }

    public void addNotification(com.EPDev.PromotionAppIcc.estimote.BeaconID beaconID, String enterMessage, String tittleMessage, String promotionId, String storeId) {
        Region region = beaconID.toBeaconRegion();
        enterMessages.put(region.getIdentifier(), enterMessage);
        //exitMessages.put(region.getIdentifier(), exitMessage);
        tittleMessages.put(region.getIdentifier(), tittleMessage);

        //Put preferences Region id, PromotionId and storeId
        storeIdPrefereces.put(region.getIdentifier(),storeId );
        promotionIdPrefereces.put(region.getIdentifier(),promotionId );

        regionsToMonitor.add(region);
    }

    public void startMonitoring(List<Store> StoreDataParam) {
        this.StoreData=StoreDataParam;
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (Region region : regionsToMonitor) {
                    beaconManager.startMonitoring(region);
                }
            }
        });
    }

    private synchronized void showNotification(String message, String Tittle, String beaconIdentifier) {

            Intent resultIntent = new Intent(context, PromotionDetailActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("promotion_selected_id",String.valueOf(promotionIdPrefereces.get(beaconIdentifier)));
            bundle.putString("store_selected_id",String.valueOf(storeIdPrefereces.get(beaconIdentifier)));

            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtras(bundle);

            int requestID = (int) System.currentTimeMillis();
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    context, requestID, resultIntent, 0);

            Log.e("PUT_EXTRAS_NOTIFICATION","Promotion id: "+String.valueOf(promotionIdPrefereces.get(beaconIdentifier))
                                            +", Store id: "+String.valueOf(storeIdPrefereces.get(beaconIdentifier))
                                            +", notificationId: "+String.valueOf(notificationID));

            Log.e("GET_BUNDLE",bundle.getString("promotion_selected_id"));

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(Tittle)
                    .setContentText(message)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(resultPendingIntent)
                    .setExtras(bundle)
                    .setWhen(System.currentTimeMillis()).setTicker(Tittle)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManager.notify(1, builder.build());

            //notificationManager.notify(random.nextInt(100), builder.build());
            notificationManager.notify(random.nextInt(1000), builder.build());

    }
}

