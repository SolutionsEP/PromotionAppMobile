package com.EPDev.PromotionAppIcc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PromotionDetailActivity extends AppCompatActivity {

    private Promotion promotionSelected = null;
    private Stores StoresData = null;
    private Store StoreSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_detail);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        String promotionSelectedId = bundle.getString("promotion_selected_id");
        String storeSelectedId = bundle.getString("store_selected_id");

        //Log.e("PROMOTION_DETAIL","Mi minor selected: "+minor);

        //Get Stores
        SharedPreferences preferencesStores = getSharedPreferences("PreferencesStores", Context.MODE_PRIVATE);
        String jsonStoresSaved = preferencesStores.getString("key_StoresObject", null);

        ImageButton buttonBack = (ImageButton)findViewById(R.id.back_button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Log.e("PROMOTION_DETAIL","storeId bundle: "+promotionSelectedId);

        StoresData = Stores.createStoresFromJson(jsonStoresSaved);

        for (int i = 0; i < StoresData.getStoresData().size(); i++) {


            if (StoresData.getStoresData().get(i).getShopID().equals(storeSelectedId)) {
                //need compare an identifier to get store selected
                Log.e("STORE_DETAIL","Pass if and set storeSelected "+String.valueOf(StoresData.getStoresData().get(i)));
                StoreSelected = StoresData.getStoresData().get(i);

                for (int j = 0; j < StoreSelected.getPromotions().size(); j++){

                    if (StoreSelected.getPromotions().get(j).getId().equals(promotionSelectedId)) {
                        Log.e("PROMOTION_SELECTED","Fill Promotion Selected");
                        promotionSelected = StoreSelected.getPromotions().get(j);
                    }
                }
            }
        }
        fillScreen();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PromotionDetailActivity.this.finish();
    }

    public void fillScreen(){

        //Set name store
        TextView nameStore = (TextView) findViewById(R.id.store_name);
        nameStore.setText(maskName(StoreSelected.getName()));

        //Set image detail promotion
        ImageView promotionImg = (ImageView)findViewById(R.id.promotion_detail_img);
        Picasso.with(this).load(promotionSelected.getPicture()).into(promotionImg);

        //Set title detail promotion
        TextView promotionTitle = (TextView)findViewById(R.id.promotion_detail_title);
        promotionTitle.setText(promotionSelected.getTitle());

        //Set about detail promotion
        TextView promotionAbout = (TextView)findViewById(R.id.promotion_detail_about);
        promotionAbout.setText(promotionSelected.getAbout());

        //Set category
        TextView promotionCategory = (TextView)findViewById(R.id.category_store);
        promotionCategory.setText(StoreSelected.getCategory());

        //Set Price
        TextView promotionPrice = (TextView)findViewById(R.id.promotion_detail_price);
        promotionPrice.setText(promotionSelected.getPrice().toString());

        //Set Valid Date
        TextView promotionDate = (TextView)findViewById(R.id.promotion_valid_date);
        promotionDate.setText(formatDate(promotionSelected.getEndDate()));

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

    public String maskName(String storeName){

        if(storeName.length() > 25){

            storeName.substring(0,20);
            storeName = storeName.concat("...");
            return storeName;
        }else{
            return storeName;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_settings) {
            startActivity(new Intent(PromotionDetailActivity.this, Pop.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
