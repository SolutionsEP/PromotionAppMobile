package com.EPDev.PromotionAppIcc;


/**
 * Created by Edwin Alvarado on 10-08-2016.
 */
public class PromotionMain {

    private String id;
    private String title;
    private String about;
    private Boolean isActive;
    private String picture;
    private Float price;
    private String endDate;
    private String storeName;
    private String storeId;

    public PromotionMain(String id, String title, String about, Boolean isActive, String picture,
                         Float price, String endDate, String storeName, String storeId) {
        this.id = id;
        this.title = title;
        this.about = about;
        this.isActive = isActive;
        this.picture = picture;
        this.price = price;
        this.endDate = endDate;
        this.storeName = storeName;
        this.storeId = storeId;

    }

    //Getters and setters
    //---------------------------id-----------------------------------------
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //Getters and setters
    //---------------------------title-----------------------------------------
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //Getters and setters
    //---------------------------about-----------------------------------------
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    //---------------------------isActive-----------------------------------------
    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    //---------------------------picture-----------------------------------------
    public String getPicture() {
        return picture;
    }

    public void setpicture(String picture) {
        this.picture = picture;
    }

    //---------------------------price-----------------------------------------
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    //---------------------------endDate-----------------------------------------
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    //---------------------------storeName-----------------------------------------

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    //---------------------------storeId-----------------------------------------

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

}
