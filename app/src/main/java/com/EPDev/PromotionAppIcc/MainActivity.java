package com.EPDev.PromotionAppIcc;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.EPDev.PromotionAppIcc.MyApplication;
import com.EPDev.PromotionAppIcc.Post;
import com.EPDev.PromotionAppIcc.RetrofitArrayAPI;
import com.EPDev.PromotionAppIcc.estimote.AppBaseActivity;
import com.estimote.sdk.SystemRequirementsChecker;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.client.cache.Resource;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList titulo = new ArrayList();
    ArrayList descripcion = new ArrayList();
    ArrayList imagen = new ArrayList();
    Button boton;
    Button ButtonArray;
    EditText texto;
    ImageButton imgBtnRest;
    ImageButton imgBtnShop;
    ImageButton imgBtnDisco;
    public static List<Store> StoreData = null;
    private Stores StoresData = null;

    List<Promotion> storePromotions = new ArrayList<>();
    List<PromotionMain> promotions = new ArrayList<>();

    //public static List<Notification> MyListBeaconNotif;

    //unit minutes to set notifications
    public static int time = 1;

    //variable to set the app update
    public static String update = "EVER";

    private static final String TAG = "MainActivity";
    static boolean CurrentlyRunning = false;
    MyApplication app = (MyApplication) getApplication();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("HOME_FRAGMENT","Before main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReplaceFonts.replaceDefaultFonts(this, "DEFAULT", "FutuBk_.ttf");

        //boton = (Button)findViewById(R.id.btn_go_next);
        //texto = (EditText)findViewById(R.id.editText);
        //ButtonArray = (Button)findViewById(R.id.button_service);

        //Get Stores
        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);
        String jsonStoresSaved = preferencesStores.getString("key_StoresObject", null);

        StoresData = Stores.createStoresFromJson(jsonStoresSaved);
        StoreData = StoresData.getStoresData();

        //Set Menu and Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.text_share));
                startActivity(Intent.createChooser(intent, "Compartir con"));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Resources res = getResources();
        //Declare tabs
        TabHost tabs = (TabHost)findViewById(R.id.tabHost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tabHome1");
        spec.setContent(R.id.tab_home1);
        spec.setIndicator("THE MOST OUTSTANDING",
                            res.getDrawable(R.drawable.ic_menu_share));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tabHome2");
        spec.setContent(R.id.tab_home2);
        spec.setIndicator("CATEGORIES",
                            res.getDrawable(R.drawable.ic_media_play));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        fillFirstTab();
        fillSecondTab();

        tabs.setOnTabChangedListener(new AnimatedTabHostListener(getContext(), tabs));


        //Get initial values to time and update
        String stringTime = preferencesStores.getString("key_time_notifications", null);
        update = preferencesStores.getString("key_update", null);

        Log.e("AFTER_GET_CONF"," Time: "+stringTime);
        Log.e("AFTER_GET_CONF"," Update: "+update);

        if (stringTime == null || update == null){
            time = 1;
            update = "EVER";
        } else {
            time = Integer.parseInt(stringTime);
            update = preferencesStores.getString("key_update", null);
            
        }
    }

    public void fillFirstTab(){
        //Add promotions
        addPromotions();
    }

    public void fillSecondTab(){
        //Set Images Buttons

        //Image Button Restaurant
        //http://200.74.223.98:8001/CompensarPDFs/Promotion/pics/restaurante.png
        imgBtnRest = (ImageButton)findViewById(R.id.imageButtonRest);
        Picasso.with(getContext()).load("http://sj.uploads.im/U1c4g.jpg").into(imgBtnRest);
        imgBtnRest.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Here u need to call the restaurants services
                goStoresDetail("0");
            }
        });

        //Image Button Shop
        //http://200.74.223.98:8001/CompensarPDFs/Promotion/pics/sports_shop.png
        imgBtnShop = (ImageButton)findViewById(R.id.imageButtonShop);
        Picasso.with(getContext()).load("http://sk.uploads.im/YS2Fv.jpg").into(imgBtnShop);
        imgBtnShop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Here u need to call the restaurants services
                goStoresDetail("1");
            }
        });

        //Image Button Disco
        //http://200.74.223.98:8001/CompensarPDFs/Promotion/pics/disco.png
        imgBtnDisco = (ImageButton)findViewById(R.id.imageButtonDisco);
        Picasso.with(getContext()).load("http://sk.uploads.im/hDC8f.jpg").into(imgBtnDisco);
        imgBtnDisco.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Here u need to call the restaurants services
                goStoresDetail("2");
            }
        });

        ImageButton nextBtn = (ImageButton)findViewById(R.id.btnNextRest);
        nextBtn.setImageResource(R.mipmap.siguiente_pagina_dos);

        nextBtn = (ImageButton)findViewById(R.id.btnNextStores);
        nextBtn.setImageResource(R.mipmap.siguiente_pagina_dos);

        nextBtn = (ImageButton)findViewById(R.id.btnNexDisco);
        nextBtn.setImageResource(R.mipmap.siguiente_pagina_dos);


    }

    //Open fragment
    public void goStoresDetail(String category){
        // Handle the stores action
        Intent intent = new Intent(MainActivity.this, StoresActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("category_selected_home",category);
        intent.putExtras(bundle);

        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();


        MyApplication app = (MyApplication) getApplication();
        Log.e(TAG,"Preshow Frm MainActivity");
        //texto = (EditText)findViewById(R.id.editText);
        //texto.setText("");

        if (!SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            Log.e(TAG, "Can't scan for beacons, some pre-conditions were not met");
            Log.e(TAG, "Read more about what's required at: http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html");
            Log.e(TAG, "If this is fixable, you should see a popup on the app's screen right now, asking to enable what's necessary");
        } else if (!app.isBeaconNotificationsEnabled()) {
            Log.d(TAG, "Enabling beacon notifications");

            //Calling service
            Intent serviceIntent = new Intent(this, BeaconBackgroundService.class);
            //serviceIntent.setAction("com.EPDev.PromotionAppIcc.BeaconBackgroundService");
            startService(serviceIntent);

            Log.e("MAIN_STORES",StoresData.getStoresData().get(0).getName());

            //Call Service?
            app.enableBeaconNotifications(StoresData);

        }
    }

    public void onStart() {
        super.onStart();
        CurrentlyRunning= true; //Store status of Activity somewhere like in shared //preference
    }

    public void onStop() {
        super.onStop();
        CurrentlyRunning= false;//Store status of Activity somewhere like in shared //preference
    }

    protected void SendAlert(String s){
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(s)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public Context getContext() {
        return this.getApplicationContext();
    }


    public static void FillNotifications(MyApplication app, Stores StoresData){
        Log.e("FLAG_","Im in FillNotifications ");

        //texto = (EditText)findViewById(R.id.editText);
        //texto.setText("");
        MainActivity.StoreData = StoreData;
        app.enableBeaconNotifications(StoresData);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, Pop.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_home_layout) {
            // Handle the home action

        } else if (id == R.id.nav_stores_layout) {
            // Handle the stores action
            Intent intent = new Intent(MainActivity.this, StoresActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("category_selected_home","none");
            intent.putExtras(bundle);

            startActivity(intent);

        } else if (id == R.id.nav_about_layout) {
            // Handle the about action
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            //Open the menu main
            startActivity(new Intent(MainActivity.this, Pop.class));

        } else if (id == R.id.nav_share) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.text_share));
            startActivity(Intent.createChooser(intent, "Compartir con"));

        } else if (id == R.id.nav_near_layout) {
            // Handle the stores action
            Intent intent = new Intent(MainActivity.this, NearActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_close){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Esta seguro que desea salir de ProMotion App?");
            alertDialogBuilder
                    .setMessage("Aceptar para salir!")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                }
                            })

                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Agregar Promociones MainListPromotions
    private void addPromotions(){

        for (int j = 0; j < StoresData.getStoresData().size();j++) {

            storePromotions = StoresData.getStoresData().get(j).getPromotions();

            for (int i = 0; i < storePromotions.size(); i++) {

                Log.e("ADD_PROMOTIONS", "i: " + i + ", and size: " + storePromotions.size());
                Log.e("ADD_PROMOTIONS", "Name: " + StoresData.getStoresData().get(j).getName()
                                  + ", and id: " + StoresData.getStoresData().get(j).getShopID());

                promotions.add(new PromotionMain(storePromotions.get(i).getId(),
                        storePromotions.get(i).getTitle(),
                        storePromotions.get(i).getAbout(),
                        storePromotions.get(i).getIsActive(),
                        storePromotions.get(i).getPicture(),
                        storePromotions.get(i).getPrice(),
                        storePromotions.get(i).getEndDate(),
                        StoresData.getStoresData().get(j).getName(),
                        StoresData.getStoresData().get(j).getShopID()
                        ));
            }
        }

        PromotionsView();
        onClickRow();

    }

    private void PromotionsView(){

        ArrayAdapter<PromotionMain> adapter = new MyListAdapter();
        ListView list = (ListView)findViewById(R.id.main_list_adapter);
        list.setAdapter(adapter);

    }

    private void onClickRow(){

        ListView list = (ListView)findViewById(R.id.main_list_adapter);
        assert list != null;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PromotionMain promotionClicked = promotions.get(position);
                Intent intent = new Intent(MainActivity.this,PromotionDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("promotion_selected_id",promotions.get(position).getId().toString());
                bundle.putString("store_selected_id",promotions.get(position).getStoreId());
                intent.putExtras(bundle);

                MainActivity.this.startActivity(intent);
            }
        });
    }


    private class MyListAdapter extends ArrayAdapter<PromotionMain> {

        @Override
        public int getCount() {
            return promotions == null ? 0 : promotions.size();
        }

        public MyListAdapter() {
            super(MainActivity.this, R.layout.item_promotion_main_view, promotions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.e("GET_VIEW","View and position: "+position);
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_promotion_main_view, parent, false);
            }

            PromotionMain currentPromotion = promotions.get(position);

            ImageView promotionImg = (ImageView)itemView.findViewById(R.id.promotion_img);
            Picasso.with(getContext()).load(currentPromotion.getPicture()).into(promotionImg);

            TextView promotionTitle = (TextView) itemView.findViewById(R.id.promotion_title);
            promotionTitle.setText(maskString(currentPromotion.getTitle(),45));

            TextView promotionDescr = (TextView) itemView.findViewById(R.id.promotion_description);
            promotionDescr.setText(maskDescription(currentPromotion.getAbout()));

            TextView promotionValid = (TextView) itemView.findViewById(R.id.promotion_valid);
            promotionValid.setText(formatDate(currentPromotion.getEndDate()));

            TextView storeName = (TextView) itemView.findViewById(R.id.promotion_store_name);
            storeName.setText(maskString(currentPromotion.getStoreName(),23));

            TextView storeId = (TextView) itemView.findViewById(R.id.promotion_store_id);
            storeId.setText(currentPromotion.getStoreId());

            return itemView;
        }

        public String maskString(String s, int limite){

            if (s.length() < limite){
                return s;

            }else {
                s = s.substring(0, limite);
                s = s.concat("...");
                return s;

            }
        }

        public String formatDate(String date){

            String separator = "/";

            String[] currentDate = date.split(separator);

            String year = currentDate[2];
            String month = currentDate[1];
            String day = currentDate[0];

            Log.e("FORMAT_DATE_PROM","Day: "+day+" , month: "+month+", year:"+year);

            String completeDate = "Valido hasta el ".concat(day)
                    .concat(" de ")
                    .concat(getMonth(month))
                    .concat(" del ")
                    .concat(year);

            return completeDate;

        }

        public String getMonth(String number){

            switch (number){
                case "01":
                    return "Enero";

                case "02":
                    return "Febrero";

                case "03":
                    return "Marzo";

                case "04":
                    return "Abril";

                case "05":
                    return "Mayo";

                case "06":
                    return "Junio";

                case "07":
                    return "Julio";

                case "08":
                    return "Agosto";

                case "09":
                    return "Septiembre";

                case "10":
                    return "Octubre";

                case "11":
                    return "Noviembre";

                case "12":
                    return "Diciembre";

                default:
                    return "SIN MES";
            }
        }

        public String maskDescription(String about){

            if (about.length() < 40){

                return about;
            }else {
                about = about.substring(0, 40);
                about = about.concat("...");
                return about;
            }

        }

    }

}
