package com.EPDev.PromotionAppIcc;

/**
 * Created by Edwin Alvarado on 04-08-2016.
 */

//On this class you need to declare methods get and set to take some services
public class Post {

    //Variables that are in our json
    private int userId;
    private int id;
    private String title;
    private String body;

    //Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int postId) {
        this.userId = userId;
    }

    public int getId() {
        return userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setPostTitle(String title) {
        this.title = title;
    }

    public String getPostBody() {
        return body;
    }

    public void setPostBody(String body) {
        this.body = body;
    }
}
