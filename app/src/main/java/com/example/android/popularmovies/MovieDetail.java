package com.example.android.popularmovies;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by dneum on 1/15/2018.
 */

public class MovieDetail {

    String movie_id;
    String movie_original_title;
    String movie_thumbnail_path;
    String movie_overview;
    String move_user_rating;
    String release_date;
    String movie_complete_path;
    BitmapDrawable movie_image;

    public MovieDetail(String id)
    {
        this.movie_id = id;
        this.movie_original_title = "tbd";
        this.movie_thumbnail_path = "tbd";
        this.movie_overview = "tbd";
        this.move_user_rating = "tbd";
        this.release_date = "tbd";
        this.movie_complete_path = "tbd";
        this.movie_image = null;
    }

}


