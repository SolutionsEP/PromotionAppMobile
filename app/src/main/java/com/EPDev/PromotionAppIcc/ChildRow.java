package com.EPDev.PromotionAppIcc;

/**
 * Created by soluciones on 12-08-2016.
 */
public class ChildRow {
    private String icon;
    private String text;
    private String description;
    private String storeId;


    public ChildRow(String icon, String text, String description, String storeId) {
        this.icon = icon;
        this.text = text;
        this.description = description;
        this.storeId = storeId;

    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
