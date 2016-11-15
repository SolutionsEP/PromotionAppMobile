package com.EPDev.PromotionAppIcc;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinderListener;

public class StoreDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    ImageView imageUrl;
    private GoogleMap mMap;
    private Location Loc;
    public static double Lat;
    public static double Lng;
    private Stores StoresData = null;
    private Store StoreSelected = null;
    Bitmap BitMapUrl;

    private List<Promotion> promotions = new ArrayList<>();

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        Bundle bundle = getIntent().getExtras();
        String storeSelectedId = bundle.getString("store_selected_id");

        //Get Stores
        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);
        String jsonStoresSaved = preferencesStores.getString("key_StoresObject", null);

        StoresData = Stores.createStoresFromJson(jsonStoresSaved);

        for (int i = 0; i < StoresData.getStoresData().size(); i++) {

            Log.e("STORE_DETAIL",StoresData.getStoresData().get(i).getShopID().equals(storeSelectedId)
                    +", storeId Object: "+StoresData.getStoresData().get(i).getShopID()
                    +", storeId bundle: "+storeSelectedId);

            if (StoresData.getStoresData().get(i).getShopID().equals(storeSelectedId)) {
                //need compare an identifier to get store selected
                Log.e("STORE_DETAIL","Pass if and set storeSelected "+String.valueOf(StoresData.getStoresData().get(i)));
                StoreSelected = StoresData.getStoresData().get(i);
            }
        }

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

        FloatingActionButton call = (FloatingActionButton) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llamar(StoreSelected.getPhone(),StoreSelected.getName());
            }
        });

        ImageButton buttonBack = (ImageButton)findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.store_map);
        mapFragment.getMapAsync(this);

        //Set name store
        TextView nameStore = (TextView) findViewById(R.id.store_name);
        nameStore.setText(maskName(StoreSelected.getName()));


        //Set Picture
        ImageView imageStore = (ImageView) findViewById(R.id.imageTestUrl);
        Picasso.with(this).load(StoreSelected.getPicture()).into(imageStore);

        //Set Description
        TextView aboutStore = (TextView) findViewById(R.id.about_store);
        aboutStore.setText(StoreSelected.getAbout());

        //Set Category
        TextView categoryStore = (TextView) findViewById(R.id.category_store);
        categoryStore.setText(StoreSelected.getCategory());

        //Set Mail
        TextView mailStore = (TextView) findViewById(R.id.email_store);
        mailStore.setText(StoreSelected.getEmail());

        //Set Phone
        TextView phoneStore = (TextView) findViewById(R.id.phone_store);
        phoneStore.setText(StoreSelected.getPhone());

        //Set direction
        TextView directionStore = (TextView) findViewById(R.id.direction_store);
        directionStore.setText(StoreSelected.getAddress());

        //Add promotions
        Promotions((ArrayList<Promotion>) StoreSelected.getPromotions());


    }

    public void llamar(final String tel, final String storeName) {
        final Context context = this;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("¿Esta seguro que desea llamar a "+storeName+"?");
        alertDialogBuilder
                .setMessage("Aceptar para llamar!")
                .setCancelable(false)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                try {
                                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel)));
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
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

    public String maskName(String storeName){

        if(storeName.length() > 25){

            storeName.substring(0,20);
            storeName = storeName.concat("...");
            return storeName;
        }else{
            return storeName;
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng storeLocation;
        storeLocation = new LatLng(Double.parseDouble(StoreSelected.getLatitude()),
                                    Double.parseDouble(StoreSelected.getLongitude()));

        // Add a marker in store and move the camera
        mMap.addMarker(new MarkerOptions()
                .position(storeLocation)
                .title(StoreSelected.getName())
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 16));
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

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

    @Override
    public void onBackPressed() {
        Log.e("BACK_PRESSED","Back pressed before");
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        ///noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(StoreDetailActivity.this, Pop.class));
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
            Intent intent = new Intent(StoreDetailActivity.this,MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_stores_layout) {
            // Handle the stores action
            Intent intent = new Intent(StoreDetailActivity.this, StoresActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about_layout) {
            // Handle the about action
            Intent intent = new Intent(StoreDetailActivity.this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(StoreDetailActivity.this, Pop.class));

        } else if (id == R.id.nav_share) {

        }else if (id == R.id.nav_near_layout) {
            // Handle the stores action
            Intent intent = new Intent(StoreDetailActivity.this, NearActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_close){
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Agregar Promociones
    private void Promotions(ArrayList<Promotion> storePromotions){

        for (int i = 0; i < storePromotions.size(); i++) {
            Log.e("ADD_PROMOTIONS","i: "+i+", and size: "+storePromotions.size());

            promotions.add(new Promotion(storePromotions.get(i).getId(),
                    storePromotions.get(i).getTitle(),
                    storePromotions.get(i).getAbout(),
                    storePromotions.get(i).getIsActive(),
                    storePromotions.get(i).getPicture(),
                    storePromotions.get(i).getPrice(),
                    storePromotions.get(i).getEndDate()));
        }

        PromotionsView();
        onClickRow();

    }

    private void PromotionsView(){

        ArrayAdapter<Promotion> adapter = new MyListAdapter();
        ListView list = (ListView)findViewById(R.id.ListPromotions);
        list.setAdapter(adapter);

    }

    private void onClickRow(){

        ListView list = (ListView)findViewById(R.id.ListPromotions);
        assert list != null;
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Promotion promotionClicked = promotions.get(position);
                Intent intent = new Intent(StoreDetailActivity.this,PromotionDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("promotion_selected_id",promotions.get(position).getId().toString());
                bundle.putString("store_selected_id",StoreSelected.getShopID());
                intent.putExtras(bundle);

                StoreDetailActivity.this.startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Promotion> {

        @Override
        public int getCount() {
            return promotions == null ? 0 : promotions.size();
        }

        public MyListAdapter() {
            super(StoreDetailActivity.this, R.layout.item_promotion_view, promotions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.e("GET_VIEW","View and position: "+position);
            View itemView = convertView;

            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_promotion_view, parent, false);
            }

            Promotion currentPromotion = promotions.get(position);

            ImageView promotionImg = (ImageView)itemView.findViewById(R.id.promotion_img);
            Picasso.with(getContext()).load(currentPromotion.getPicture()).into(promotionImg);

            TextView promotionTitle = (TextView) itemView.findViewById(R.id.promotion_title);
            promotionTitle.setText(currentPromotion.getTitle());

            TextView promotionDescr = (TextView) itemView.findViewById(R.id.promotion_description);
            promotionDescr.setText(maskDescription(currentPromotion.getAbout()));

            TextView promotionValid = (TextView) itemView.findViewById(R.id.promotion_valid);
            promotionValid.setText(formatDate(currentPromotion.getEndDate()));


            return itemView;
        }

        public String formatDate(String date){

            String separator = "/";

            String[] currentDate = date.split(separator);

            String year = currentDate[2];
            String month = currentDate[1];
            String day = currentDate[0];

            Log.e("FORMAT_DATE_PROM","Day: "+day+" , month: "+month+", year:"+year);

            String completeDate = "Valid until: ".concat(day)
                    .concat(" ")
                    .concat(getMonth(month))
                    .concat(" ")
                    .concat(year);

            return completeDate;

        }

        public String getMonth(String number){

            switch (number){
                case "01":
                    return "January";

                case "02":
                    return "February";

                case "03":
                    return "March";

                case "04":
                    return "April";

                case "05":
                    return "May";

                case "06":
                    return "June";

                case "07":
                    return "July";

                case "08":
                    return "August";

                case "09":
                    return "September";

                case "10":
                    return "October";

                case "11":
                    return "November";

                case "12":
                    return "December";

                default:
                    return "SIN MES";
            }
        }

        public String maskDescription(String about){

            if (about.length() <= 40){
                return about;

            }else{
                about = about.substring(0,40);
                about = about.concat("...");
                return about;

            }



        }

    }
}
