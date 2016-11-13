package com.EPDev.PromotionAppIcc;

import java.util.Date;

/**
 * Created by soluciones on 10-08-2016.
 */
public class Notification {

    String UUID;
    int Major;
    int Minor;
    String dateSend;
    String Tittle;

    //Getters and setters
    //---------------------------UUID-----------------------------------------
    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    //---------------------------Mayor-----------------------------------------
    public int getMajor() {
        return Major;
    }

    public void setMajor(int Major) {
        this.Major = Major;
    }

    //---------------------------Minor-----------------------------------------
    public int getMinor() {
        return Minor;
    }

    public void setMinor(int Minor) {
        this.Minor = Minor;
    }

    //---------------------------dateSend-----------------------------------------
    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }


}
