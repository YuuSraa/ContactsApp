package com.example.phonebook;

public class modelContact {

    //varaiables
    String Fname,Lname,phone,uid,id,categoryId;

    long timestamp;

    boolean favorite ;

    //constructor

    public modelContact() {
    }

    public modelContact(String fname, String lname, String phone, String uid, String id, String categoryId, long timestamp, boolean favorite) {
        Fname = fname;
        Lname = lname;
        this.phone = phone;
        this.uid = uid;
        this.id = id;
        this.categoryId = categoryId;
        this.timestamp = timestamp;
        this.favorite = favorite;
    }


    //getter & setters

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}

