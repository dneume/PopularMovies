package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DetailActivity extends Activity {

    private Context mContext;
    private MovieDetail mLocalMovieDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] mDetail = new String[7];
        MovieDetail mMovieDetail = new MovieDetail(String.valueOf(0));
        String ldetail = "detail";

        mContext = getApplicationContext();
        super.onCreate(savedInstanceState);

        // get the current and Intent and retrieve the movie detail
        Intent intent = getIntent();
        mDetail = intent.getStringArrayExtra(ldetail);
        mMovieDetail.movie_id = mDetail[0];
        mMovieDetail.movie_original_title = mDetail[1];
        mMovieDetail.movie_thumbnail_path = mDetail[2];
        mMovieDetail.movie_overview = mDetail[3];
        mMovieDetail.move_user_rating = mDetail[4];
        mMovieDetail.release_date = mDetail[5];
        mMovieDetail.movie_complete_path = mDetail[6];

        //display the xml file for the detail window view
        setContentView(R.layout.activity_detail);

       // Display the MovieDetail
        ImageView mImageView = new ImageView(mContext);
        mImageView = findViewById(R.id.display_detail_image);
        Picasso mPicasso = null;
        mPicasso.with(mContext).load(mMovieDetail.movie_complete_path).fit().into(mImageView);


        TextView mTitle = new TextView(mContext);
        mTitle = findViewById(R.id.display_movie_title);
        mTitle.setText(mMovieDetail.movie_original_title);
//
        TextView mOverview = new TextView(mContext);
        mOverview = findViewById(R.id.display_overview);
        mOverview.setText(mMovieDetail.movie_overview);
//
        TextView mReleaseDate = new TextView(mContext);
        mReleaseDate = findViewById(R.id.display_release_date);
        mReleaseDate.setText("Released On: " + mMovieDetail.release_date);

        TextView mUserRating = new TextView(mContext);
        mUserRating = findViewById(R.id.display_user_rating);
        mUserRating.setText("Average User Rating: " + mMovieDetail.move_user_rating);
    }

  }
