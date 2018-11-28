package com.example.tio.callnumber;


import android.net.Uri;

public class DataModel {

    String name;
    String number;
    Uri image;

    public DataModel(String name, String number, Uri image) {
        this.name = name;
        this.number = number;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public Uri getImage() {
        Uri imgUri = Uri.parse(String.valueOf(image));

        return imgUri;
    }
}
