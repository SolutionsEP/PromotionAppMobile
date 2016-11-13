package com.EPDev.PromotionAppIcc;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;

import java.util.List;

/**
 * Created by soluciones on 14-08-2016.
 */
public class Stores {

    List<Store> StoresData;

    public Stores(List<Store> storesData) {
        StoresData = storesData;
    }

    public List<Store> getStoresData() {
        return StoresData;
    }

    public void setStoresData(List<Store> storesData) {
        StoresData = storesData;
    }

    /**
     * Serialización del objeto
     * que será devuelto como un String en formato jSon
     */
    public String serializeStore(){
        Gson StoreJson = new Gson();
        return StoreJson.toJson(this);
    }

    /**
     * Creación del objeto desde el jSon
     */
    public static Stores createStoresFromJson(String storeJson){
        Gson objectJson = new Gson();
        return objectJson.fromJson(storeJson, Stores.class);
    }
}
