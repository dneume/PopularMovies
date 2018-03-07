package com.example.android.popularmovies;

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
    String movie_review[];
    String movie_trailer_id[];
    String movie_trailer_name[];
    // movie_review and movie_trailers are placeholders for an unknown number of reviews and trailers
    // the assumption is that 20 trailers and 20 movie reviews are plenty


    public MovieDetail(String id)
    {
        this.movie_id = id;
        this.movie_original_title = "tbd";
        this.movie_thumbnail_path = "tbd";
        this.movie_overview = "tbd";
        this.move_user_rating = "tbd";
        this.release_date = "tbd";
        this.movie_complete_path = "tbd";
        this.movie_review = new String[21];
        this.movie_trailer_id = new String[21];
        this.movie_trailer_name = new String[21];
    }
}


