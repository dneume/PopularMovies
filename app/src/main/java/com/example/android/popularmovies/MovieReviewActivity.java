package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import static com.example.android.popularmovies.Utils.scanInput;

/**
 * Created by dneum on 2/3/2018.
 */

    public class MovieReviewActivity extends AppCompatActivity {

        RecyclerView.Adapter recyclerView_Adapter;
        RecyclerView.LayoutManager recyclerView_LayoutManager;
        RecyclerView recyclerView;

        public List<MovieReviewDetail> reviewList;
        public ListIterator<MovieReviewDetail> mIterateReview;
        public MovieReviewDetail mRd;


        @Override
        protected void onCreate(Bundle savedInstanceState) {

            Context mContext;
            String mIntentId = "MovieReviewDetail";
            String movie_title = null;

            String auth_review_string[] = new String[21];
            int num = 0;
            int index = 0;
            String input = null;
            int total_number_reviews = 0;

            // always execute the next three steps in this order for recyclerview
            super.onCreate(savedInstanceState);

            // get the current Intent and retrieve the movie detail
            Intent intent = getIntent();
            auth_review_string = intent.getStringArrayExtra(mIntentId);

            // let's set the content view
            setContentView(R.layout.review_recycler_view);
            mContext = getApplicationContext();

 //         the assumption is that you should not have more than 20 reviews, this is debug code
//          also - this is to get a quick count for the MovieReviewDetail
            while(auth_review_string[num] != null && num < 21) {
                Log.d("view_intent",auth_review_string[num]);
                num++;
            }
            //the last entry in the array of strings is the movie_title, so lets decrement this
            // just to make life easier
            num--;
            movie_title = auth_review_string[num];

            reviewList = new ArrayList<MovieReviewDetail>();
            total_number_reviews = num;
            for(num = 0; num < total_number_reviews; num++) {
                mRd = new MovieReviewDetail();

                // the author of each review is prefixed to the content, so need to pull this off
                input = auth_review_string[num];
//                Log.d("scan_input", input);
                index = input.indexOf("\"");
                if (index > 0)
                    mRd.movie_review_author = input.substring(0, index);

                mRd.movie_review = auth_review_string[num];
                mRd.movie_review = scanInput("content\":\"", auth_review_string[num]);
                String mBuf = "Review " + Integer.toString(num + 1) + " of " + Integer.toString(total_number_reviews);
                mRd.movie_review_increment = mBuf;
                mRd.movie_original_title = movie_title;
                reviewList.add(mRd);
            }
         }

        @Override
        public void onPostCreate(Bundle savedInstanceState) {
            MovieReviewDetail mRd;
            Context mContext;
            super.onPostCreate(savedInstanceState);

            mContext = getApplicationContext();
            //display the xml file for the detail window view
            recyclerView = findViewById(R.id.review_recycler_view);

    //      lets iterate over the list to ensure that we built the list properly
            Log.d("PostCreate","PostCreate");
            mIterateReview = reviewList.listIterator(0);
            while(mIterateReview.hasNext()){
                mRd = mIterateReview.next();
                Log.d(mRd.movie_review_increment, mRd.movie_review);
            }

    //        // create the movie adapter and set the custom listener to return the click value
            recyclerView_Adapter = new MovieReviewAdapter(mContext, reviewList);
            if (recyclerView_Adapter != null) {
                recyclerView.setAdapter(recyclerView_Adapter);
                recyclerView.setVisibility(View.VISIBLE);
            }

            recyclerView_LayoutManager = new LinearLayoutManager(mContext);
            if (recyclerView_LayoutManager != null) {
                recyclerView.setLayoutManager(recyclerView_LayoutManager);
            }

            recyclerView.setVisibility(View.VISIBLE);
        }
}