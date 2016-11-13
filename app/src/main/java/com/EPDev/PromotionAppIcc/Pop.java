package com.EPDev.PromotionAppIcc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by soluciones on 23-08-2016.
 */
public class Pop extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_settings);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.9), (int)(height*.7));

        Button btnClose = (Button)findViewById(R.id.close_window);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);

        int time;

        String stringTime = preferencesStores.getString("key_time_notifications", null);
        String update = preferencesStores.getString("key_update", null);

        Log.e("AFTER_GET_CONF_POP"," Time: "+stringTime);
        Log.e("AFTER_GET_CONF_POP"," Update: "+update);

        if (stringTime == null || update == null){
            time = 1;
            update = "EVER";
        } else {
            time = Integer.parseInt(stringTime);
            update = preferencesStores.getString("key_update", null);

        }

        RadioGroup rbN = (RadioGroup)findViewById(R.id.radio_notify);
        RadioButton rbnD =(RadioButton)findViewById(R.id.radioNotifyDay);
        RadioButton rbnH =(RadioButton)findViewById(R.id.radioNotifyHour);
        RadioButton rbnM =(RadioButton)findViewById(R.id.radioNotifyMinute);

        RadioGroup rbU = (RadioGroup)findViewById(R.id.radio_update);
        RadioButton rbuE =(RadioButton)findViewById(R.id.radioUpdatesEver);
        RadioButton rbuN =(RadioButton)findViewById(R.id.radioUpdatesNever);
        RadioButton rbuW =(RadioButton)findViewById(R.id.radioUpdatesWifi);

        switch (time){
            case 1:
                rbnD.setChecked(false);
                rbnH.setChecked(false);
                rbnM.setChecked(true);
                break;

            case 60:
                rbnD.setChecked(false);
                rbnM.setChecked(false);
                rbnH.setChecked(true);
                break;

            case 1440:
                rbnM.setChecked(false);
                rbnH.setChecked(false);
                rbnD.setChecked(true);
                break;

        }

        switch (update){
            case "EVER":
                rbuN.setChecked(false);
                rbuW.setChecked(false);
                rbuE.setChecked(true);
                break;

            case "NEVER":
                rbuW.setChecked(false);
                rbuE.setChecked(false);
                rbuN.setChecked(true);
                break;

            case "WIFI":
                rbuE.setChecked(false);
                rbuN.setChecked(false);
                rbuW.setChecked(true);
                break;

        }
    }

    public void onRadioNotifyClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //Set values to sharePreferences
        SharedPreferences preferencesStores = getApplicationContext().getSharedPreferences("PreferencesStores", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesStores.edit();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioNotifyDay:
                if (checked)
                    // Set MainActivity.time = 1440
                    editor.putString("key_time_notifications", "1440");
                    editor.commit();
                    break;
            case R.id.radioNotifyHour:
                if (checked)
                    // Set MainActivity.time = 60
                    editor.putString("key_time_notifications", "60");
                    editor.commit();
                    break;
            case R.id.radioNotifyMinute:
                if (checked)
                    // Set MainActivity.time = 1
                    editor.putString("key_time_notifications", "1");
                    editor.commit();
                    break;
        }
    }

    public void onRadioUpdatesClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //Set values to sharePreferences
        SharedPreferences preferencesStores = getApplicationContext().getSharedPreferences("PreferencesStores", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesStores.edit();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioUpdatesEver:
                if (checked)
                    // Update ever
                    editor.putString("key_update", "EVER");
                    editor.commit();
                    break;
            case R.id.radioUpdatesNever:
                if (checked)
                    // Update never
                    editor.putString("key_update", "NEVER");
                    editor.commit();
                    break;
            case R.id.radioUpdatesWifi:
                if (checked)
                    // Update WIFI
                    editor.putString("key_update", "WIFI");
                    editor.commit();
                    break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
