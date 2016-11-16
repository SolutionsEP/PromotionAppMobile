package com.EPDev.PromotionAppIcc;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class StoresActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, NavigationView.OnNavigationItemSelectedListener{

    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<ParentRow>();
    private ArrayList<ParentRow> showThisParentList = new ArrayList<ParentRow>();
    private MenuItem searchItem;
    private Stores StoresData = null;
    private String categorySelectedHome = "none";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        parentList = new ArrayList<ParentRow>();
        showThisParentList = new ArrayList<ParentRow>();

        //the app will crash if display list not called here
        displayList();

        //this expands the list of continents
        Bundle bundle = getIntent().getExtras();
        categorySelectedHome = bundle.getString("category_selected_home");
        expandAll(categorySelectedHome);

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
                startActivity(Intent.createChooser(intent, "Share With"));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    protected void onResume() {
        super.onResume();
        //this expands the list of continents
        Bundle bundle = getIntent().getExtras();
        categorySelectedHome = bundle.getString("category_selected_home");
        expandAll(categorySelectedHome);
    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        expandAll("none");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        expandAll("none");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listAdapter.filterData(newText);
        expandAll("none");
        return false;
    }

    private void loadData(){
        ArrayList<ChildRow> childRowsRest = new ArrayList<ChildRow>();
        ArrayList<ChildRow> childRowsSports = new ArrayList<ChildRow>();
        ArrayList<ChildRow> childRowsDisco = new ArrayList<ChildRow>();
        ParentRow parentRowRestaurants = null;
        ParentRow parentRowSports = null;
        ParentRow parentRowDiscos = null;

        String catId = null;
        //Get Stores
        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);
        String jsonStoresSaved = preferencesStores.getString("key_StoresObject", null);

        StoresData = Stores.createStoresFromJson(jsonStoresSaved);
        Log.e("STORES_LIST",StoresData.getStoresData().get(0).getName());


        for (int i = 0; i < StoresData.getStoresData().size(); i++) {
            if (Boolean.parseBoolean(StoresData.getStoresData().get(i).getisActive())) {

                switch(StoresData.getStoresData().get(i).getCategoryId())
                {
                    case "1":
                        childRowsRest.add(new ChildRow(StoresData.getStoresData().get(i).getPicture(),
                                StoresData.getStoresData().get(i).getName(),
                                maskAddress(StoresData.getStoresData().get(i).getAddress()),
                                StoresData.getStoresData().get(i).getShopID()));
                        break;
                    case "5":
                        childRowsSports.add(new ChildRow(StoresData.getStoresData().get(i).getPicture(),
                                StoresData.getStoresData().get(i).getName(),
                                maskAddress(StoresData.getStoresData().get(i).getAddress()),
                                StoresData.getStoresData().get(i).getShopID()));
                        break;
                    case "4":
                        childRowsDisco.add(new ChildRow(StoresData.getStoresData().get(i).getPicture(),
                                StoresData.getStoresData().get(i).getName(),
                                maskAddress(StoresData.getStoresData().get(i).getAddress()),
                                StoresData.getStoresData().get(i).getShopID()));
                        break;
                }
            }
        }
        parentRowRestaurants = new ParentRow("RESTAURANTS", childRowsRest);
        parentList.add(parentRowRestaurants);

        parentRowSports = new ParentRow("CLOTHING STORES", childRowsSports);
        parentList.add(parentRowSports);

        parentRowDiscos = new ParentRow("TECHNOLOGY", childRowsDisco);
        parentList.add(parentRowDiscos);

        /*
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Pizza Hut Los Simbolos","Ven y disfrua de las mejores pizzas","000000"));
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Arturo's Sabana Grande","El mejor pollo frito","000001"));
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Fridays Los Palos Grandes","Ven con tus panas a comer y beber","000002"));
        parentRow = new ParentRow("Restaurantes", childRows);
        parentList.add(parentRow);

        childRows = new ArrayList<ChildRow>();
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Tenis Shop","Lider en accesorios deportivos","000003"));
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Pro Player","Los mejores en zapatos deportivos","000004"));
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Adiddas Lider","Tu mejor outfit","000005"));
        parentRow = new ParentRow("Tiendas deportivas", childRows);
        parentList.add(parentRow);

        childRows = new ArrayList<ChildRow>();
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Teatro Bar","Disco y Teatro al mismo tiempo","000006"));
        childRows.add(new ChildRow(R.mipmap.generic_icon,"La Quinta Bar","Ven y disfruta en la quinta con tus panas","000007"));
        childRows.add(new ChildRow(R.mipmap.generic_icon,"Fig's Altamira","El mejor lugar para pasarla bien","000008"));
        parentRow = new ParentRow("Discotecas", childRows);
        parentList.add(parentRow);
        */
    }

    public String maskAddress(String address){

        String mask;

        if (address.length() < 40){
            mask = address;
        } else {
            mask = address.substring(0,40);
        }
        return mask.concat("...");
    }

    private void expandAll(String categorySelectedHome){
        int count = listAdapter.getGroupCount();

        Log.e("STORE_EXPAND","This is the categorySelectedValue "+categorySelectedHome);

        for (int i = 0; i < count; i++){

            if (categorySelectedHome.equals(String.valueOf(i)) || categorySelectedHome.equals("none")) {
                myList.expandGroup(i);
            }
        }//end  for (int i = 0; i < count; i++)
    }

    private void displayList(){
        loadData();

        myList = (ExpandableListView) findViewById(R.id.expandableListViewSearch);
        listAdapter = new MyExpandableListAdapter(StoresActivity.this, parentList);

        myList.setAdapter(listAdapter);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();

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

        ///noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(StoresActivity.this, Pop.class));
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
            Intent intent = new Intent(StoresActivity.this,MainActivity.class);
            startActivity(intent);
            
        } else if (id == R.id.nav_stores_layout) {
            // Handle the stores action

        } else if (id == R.id.nav_about_layout) {
            // Handle the about action
            Intent intent = new Intent(StoresActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(StoresActivity.this, Pop.class));

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.text_share));
            startActivity(Intent.createChooser(intent, "Share With"));

        }else if (id == R.id.nav_near_layout) {
            // Handle the stores action
            Intent intent = new Intent(StoresActivity.this, NearActivity.class);
            startActivity(intent);

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

}
