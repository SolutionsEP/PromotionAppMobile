package com.EPDev.PromotionAppIcc;

import com.estimote.sdk.cloud.internal.User;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;

import java.util.List;

/**
 * Created by Edwin Alvarado on 10-08-2016.
 */
//On this class you need to declare methods get and set to take some services
public class Store {

    //Variables that are in our json
    private String shopID;
    private String isActive;
    private String picture;
    private String name;
    private String categoryId;
    private String category;
    private String company;
    private String email;
    private String phone;
    private String address;
    private String about;
    private String registered;
    private String latitude;
    private String longitude;
    private List<Promotion> promotions;
    private List<Beacon> beaconInfo;

    public Store(String shopID, String isActive, String picture, String name, String categoryId, String category, String company, String email, String phone, String address, String about, String registered, String latitude, String longitude, List<Promotion> promotions, List<Beacon> beaconInfo) {
        this.shopID = shopID;
        this.isActive = isActive;
        this.picture = picture;
        this.name = name;
        this.categoryId = categoryId;
        this.category = category;
        this.company = company;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.about = about;
        this.registered = registered;
        this.latitude = latitude;
        this.longitude = longitude;
        this.promotions = promotions;
        this.beaconInfo = beaconInfo;
    }

    //Getters and setters
    //---------------------------ShopID-----------------------------------------
    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    //---------------------------isActive-----------------------------------------
    public String getisActive() {
        return isActive;
    }

    public void setisActive(Boolean shopID) {
        this.isActive = isActive;
    }

    //---------------------------picture-----------------------------------------
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    //---------------------------name-----------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //---------------------------categoryId-----------------------------------------
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    //---------------------------category-----------------------------------------
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    //---------------------------company-----------------------------------------
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    //---------------------------ShopID-----------------------------------------
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //---------------------------phone-----------------------------------------
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    //---------------------------address-----------------------------------------
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //---------------------------about-----------------------------------------
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    //---------------------------registered-----------------------------------------
    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    //---------------------------latitude-----------------------------------------
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.about = latitude;
    }

    //---------------------------longitude-----------------------------------------
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    //---------------------------about-----------------------------------------
    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    //---------------------------beaconInfo-----------------------------------------
    public List<Beacon> getBeaconInfo() {
        return beaconInfo;
    }

    public void setBeaconInfo(List<Beacon> beaconInfo) {
        this.beaconInfo = beaconInfo;
    }




}
