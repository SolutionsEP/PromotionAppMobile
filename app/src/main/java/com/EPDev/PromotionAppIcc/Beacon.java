package com.EPDev.PromotionAppIcc;

/**
 * Created by soluciones on 10-08-2016.
 */
public class Beacon {

    String UUID;
    String Major;
    String Minor;

    //Getters and setters
    //---------------------------UUID-----------------------------------------
    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    //---------------------------Mayor-----------------------------------------
    public String getMajor() {
        return Major;
    }

    public void setMajor(String Major) {
        this.Major = Major;
    }

    //---------------------------Minor-----------------------------------------
    public String getMinor() {
        return Minor;
    }

    public void setMinor(String Minor) {
        this.Minor = Minor;
    }

}
