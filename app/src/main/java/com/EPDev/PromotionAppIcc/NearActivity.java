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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.*;

public class NearActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, DirectionFinderListener{

    private GoogleMap mMap;
    private Location Loc;
    TextView mensaje1;
    TextView mensaje2;
    public static double Lat;
    public static double Lng;
    private Stores StoresData = null;
    Bitmap BitMapUrl;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Set Menu and Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.text_share));
                startActivity(Intent.createChooser(intent, "Compartir con"));
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Setear Localizacion Lat Lng
        Localizacion mlocListener = new Localizacion(this);

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
        double nearStore = 0;
        double nearStoreAux = 0;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(Lat, Lng);
        LatLng storesLocation = null;
        LatLng nearStoreLatLng = null;
        String StroreDirection = "";

        /*mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("Estas aqui"));
        */
        Log.e("LATLNG_NEAR_ACTIVITY","Mine: "+myLocation+", Near store: "+nearStoreLatLng);

        //Get Stores
        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);
        String jsonStoresSaved = preferencesStores.getString("key_StoresObject", null);

        StoresData = Stores.createStoresFromJson(jsonStoresSaved);
        Log.e("NEAR_STORE_LIST", StoresData.getStoresData().get(0).getLatitude());

        //Set firts position to near store
        nearStore = distanciaCoord(Lat,Lat,
                                    Double.parseDouble(StoresData.getStoresData().get(0).getLatitude()),
                                    Double.parseDouble(StoresData.getStoresData().get(0).getLongitude()));

        nearStoreLatLng = new LatLng(
                Double.parseDouble(StoresData.getStoresData().get(0).getLatitude()),
                Double.parseDouble(StoresData.getStoresData().get(0).getLongitude()));

        for (int i = 0; i < StoresData.getStoresData().size(); i++) {
            if (Boolean.parseBoolean(StoresData.getStoresData().get(i).getisActive())) {

                new RetrieveImageAsync(StoresData.getStoresData().get(i).getPicture()).execute();

                // Add a marker in Sydney and move the camera
                storesLocation = new LatLng(Double.parseDouble(StoresData.getStoresData().get(i).getLatitude()),
                        Double.parseDouble(StoresData.getStoresData().get(i).getLongitude()));

                Log.e("c", StoresData.getStoresData().get(i).getLatitude()+
                        " , "+StoresData.getStoresData().get(i).getLongitude());

                mMap.addMarker(new MarkerOptions()
                        .position(storesLocation)
                        .title(StoresData.getStoresData().get(i).getName())
                );

                nearStoreAux = distanciaCoord(Lat,Lng,
                        Double.parseDouble(StoresData.getStoresData().get(i).getLatitude()),
                        Double.parseDouble(StoresData.getStoresData().get(i).getLongitude()));

                if(i != 0) {
                    if (nearStore >= nearStoreAux) {
                        nearStore = nearStoreAux;
                        nearStoreLatLng = new LatLng(
                                Double.parseDouble(StoresData.getStoresData().get(i).getLatitude()),
                                Double.parseDouble(StoresData.getStoresData().get(i).getLongitude()));
                        StroreDirection = StoresData.getStoresData().get(i).getAddress();
                    }
                }

            }
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        /*
            mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .add(
                            myLocation,
                            nearStoreLatLng
                    )
                    .width(10)
                    .color(Color.RED)

            );
        */

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16));
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        String direction = getDirection(Loc);
        Log.e("LOG_ADDRESS","Mi direcci—n es: \n" + direction);

        sendRequest(myLocation,nearStoreLatLng);
    }

    private void sendRequest(LatLng myDirection,LatLng storeDirection){

        try {
            new DirectionFinder(this, myDirection, storeDirection).execute();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }


    public static double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }

    public String getDirection(Location loc) {
        //Obtener la direcci—n de la calle a partir de la latitud y la longitud
        if (Lat != 0.0 && Lng != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<android.location.Address> list = geocoder.getFromLocation(Lat, Lng, 1);
                if (!list.isEmpty()) {
                    android.location.Address address = list.get(0);

                    String direction = "";
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                        direction = direction.concat(address.getAddressLine(i));
                        direction = direction.concat(" ");
                    }
                    return direction;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap getBitMapFromURL(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            //Log exception
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            startActivity(new Intent(NearActivity.this, Pop.class));
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
            Intent intent = new Intent(NearActivity.this,MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_stores_layout) {
            // Handle the stores action
            // Handle the about action
            Intent intent = new Intent(NearActivity.this, StoresActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("category_selected_home","none");
            intent.putExtras(bundle);

            startActivity(intent);

        } else if (id == R.id.nav_about_layout) {
            // Handle the about action
            Intent intent = new Intent(NearActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(NearActivity.this, Pop.class));

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.text_share));
            startActivity(Intent.createChooser(intent, "Share With"));

        }else if (id == R.id.nav_near_layout) {


        }else if (id == R.id.nav_close){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getText(R.string.leave_app));
            alertDialogBuilder
                    .setMessage(getResources().getText(R.string.ok_leave))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getText(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                }
                            })

                    .setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
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

    public class RetrieveImageAsync extends AsyncTask<Void, Void, Bitmap>
    {

        private String url;

        public RetrieveImageAsync(String imageUrl)
        {
            this.url = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... params)
        {
            Bitmap retVal = null;
            try
            {
                URL imageUrl = new URL(url);
                return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            }
            catch (Exception ex)
            {
            }
            return retVal;
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            try
            {
                //Do something with the result Bitmap
                BitMapUrl = result;
            }
            catch (Exception e)
            {
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait,",
                "Looking for directions...", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }


    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        Log.e("FINDER_SUCCESS","FLAG");
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            /*((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distances.text);*/

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
        }

    }


}
