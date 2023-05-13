package com.example.phonebook;

public class modelCategory {

    //make sure to use same spelling for model variables as in firebase

        String id,uid,category;
        long timestamp;

        //constructor empty required for firebase


    public modelCategory() {

    }

    //parametriezed constructor
    public modelCategory(String id, String uid, String category, long timestamp) {
        this.id = id;
        this.uid = uid;
        this.category = category;
        this.timestamp = timestamp;
    }

    // getter/setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
