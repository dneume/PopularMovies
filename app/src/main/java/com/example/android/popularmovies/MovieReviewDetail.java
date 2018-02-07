package com.example.android.popularmovies;

/**
 * Created by dneum on 2/5/2018.
 */

public class MovieReviewDetail {
    String movie_original_title;
    String movie_trailers_increment;
    String movie_review_increment;
    String movie_review;
    String movie_trailer;
    String movie_review_author;
    // movie_review and movie_trailers are placeholders for an unknown number of reviews and trailers
    // the assumption is that 20 trailers and 20 movie reviews are plenty


    public MovieReviewDetail()
    {
        this.movie_original_title = "tbd";
        this.movie_trailers_increment = "tbd";
        this.movie_review_increment = "tbd";
        this.movie_review = "tbd";
        this.movie_trailer = "tbd";
        this.movie_review_author="tbd";
    }
}
