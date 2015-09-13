package com.example.mhacks6.androidrecommender;

import android.location.Location;

/**
 * Created by temandr on 9/12/15.
 */
public class Item {
    public int timelog;
    public int dayy;
    public String app;
    public String location;
    public int userid;

    public Item(String packageName, String location, int hourInt, int weekDay)
    {
        this.app = packageName;
        this.location = location;
        this.timelog = hourInt;
        this.dayy = weekDay;
        this.userid = 1;
    }

}
