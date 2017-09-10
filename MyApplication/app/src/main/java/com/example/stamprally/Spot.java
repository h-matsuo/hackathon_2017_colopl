package com.example.stamprally;

import android.graphics.Bitmap;

/**
 * Created by mb-347 on 2017/09/10.
 */

public class Spot {
    String spotName;
    int userId;
    int images[];
    double lattitude;
    double longtitude;
    String prefecture;
    String city;
    String description;
    String hint;
    int favorites;

    public Spot(String spotName,int images[],String description) {

        {
            this.spotName = spotName;
            this.images = images;
            this.description = description;
        }
    }

}
