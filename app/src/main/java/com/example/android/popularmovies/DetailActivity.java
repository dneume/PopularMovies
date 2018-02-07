package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static com.example.android.popularmovies.Utils.buildUrlForReviews;
import static com.example.android.popularmovies.Utils.readStream;
import static java.lang.Boolean.TRUE;


public class DetailActivity extends AppCompatActivity {

    private Context mContext;
    private String mNumberOfReviews = "none";
    private MovieDetail mMovieDetail;
    private TextView mMovieReview;

    public class MovieReviewAndVideoDbQueryTask extends AsyncTask<Void, Void, String> {

        String mId;
        String[] mContent;

        // use the constructor to pass multiple parameters to the task
        public MovieReviewAndVideoDbQueryTask(String Id,String[] content) {
            this.mId = Id;
            this.mContent = content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            movies.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            int i = 0;
            int number_of_reviews = 0;
            URL reviewUrl;

            Context context = getBaseContext();

            try {
//                get the list of reviews
                reviewUrl = buildUrlForReviews(mId, context);
                number_of_reviews = getMovieReviews(mContent, reviewUrl);
                Log.d("number of reviews", String.valueOf(number_of_reviews));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return String.valueOf(number_of_reviews);
        }

        @Override
        protected void onPostExecute(String numberOfReviews) {
            mNumberOfReviews = numberOfReviews;
            String hold = mMovieReview.getText() + "(" + mNumberOfReviews + ")";
            mMovieReview.setText(hold);

            // set the content in the MovieDetail
            int num = 0;
            for(num = 0; num < Integer.valueOf(mNumberOfReviews); num++) {
                mMovieDetail.movie_review[num] = mContent[num];
            }
            // ensure the next item in the array is explicitly null
            mMovieDetail.movie_review[num] = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] mDetail = new String[7];
        mMovieDetail = new MovieDetail(String.valueOf(0));

        String ldetail = "detail";
        int lengthOfRating;

        mContext = getApplicationContext();
        super.onCreate(savedInstanceState);

        // get the current Intent and retrieve the movie detail
        Intent intent = getIntent();

        mDetail = intent.getStringArrayExtra(ldetail);
        mMovieDetail.movie_id = mDetail[0];
        mMovieDetail.movie_original_title = mDetail[1];
        mMovieDetail.movie_thumbnail_path = mDetail[2];
        mMovieDetail.movie_overview = mDetail[3];

        //Execute the MovieDbQuery by instantiating the MovieDbQueryTask in a background task
        String mId =  mMovieDetail.movie_id;
        String mMovieReviews[] = mMovieDetail.movie_review;
        new MovieReviewAndVideoDbQueryTask(mId,mMovieReviews).execute();

        // get the first 4 digits of the rating
        String mMovieRating = mDetail[4];
        lengthOfRating = mMovieRating.length();
        if(lengthOfRating < 4)
            mMovieDetail.move_user_rating = mMovieRating.substring(0, lengthOfRating);
        else
            mMovieDetail.move_user_rating = mMovieRating.substring(0, 4);

        //format the date
        Date date = null;
        String holdDate = mDetail[5];
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            date = df.parse(holdDate);
            df = new SimpleDateFormat("dd MMM, yyyy");
            holdDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mMovieDetail.release_date = holdDate;

        mMovieDetail.movie_complete_path = mDetail[6];

        //display the xml file for the detail window view
        setContentView(R.layout.activity_detail);

       // Display the MovieDetail
        ImageView mImageView = new ImageView(mContext);
        mImageView = findViewById(R.id.display_detail_image);
        Picasso mPicasso = null;
        Picasso.with(mContext).load(mMovieDetail.movie_complete_path).fit().into(mImageView);


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

//        TextView mMovieReview is declared as public to this class so it can
//         can be updated on PostExecute of the AsyncTask;
        mMovieReview = new TextView(mContext);
        mMovieReview = findViewById(R.id.display_reviews);
        mMovieReview.setOnClickListener(new View.OnClickListener() {
                  public void onClick(View view) {
                             Log.d(" detail activity ", " onItemClick" );
                             launchMovieReviewsActivity();
                  }
                });

        TextView mMovieTrailers = new TextView(mContext);
        mMovieTrailers = findViewById(R.id.display_videos);
    }

    public boolean launchMovieReviewsActivity() {
        Intent i;
        int num = 0;
        String mMovieId = "MovieReviewDetail";
        String[] content = new String[20];

        int lNumberOfReviews = Integer.valueOf(mNumberOfReviews);
        //pass the movie title in the first array

        for(num = 0 ; num < lNumberOfReviews; num++){
            content[num] = mMovieDetail.movie_review[num];
//           the assumption is that you will not have nore than 20 reviews
            if(num > 20) {
                Log.d("More than 20 reviews","You should not be here!");
                break;
            }
        }
        content[lNumberOfReviews] = mMovieDetail.movie_original_title;
        i = new Intent(this, MovieReviewActivity.class);
        i.putExtra(mMovieId,content);
        startActivity(i);
        return TRUE;
    }

    public int getMovieReviews_old(URL url[], URL urlToTheReviews) throws IOException {

        String local_input = null;
        String checkForUrl = null;
        int index = 0;
        int count = 0;
        int attempts = 0;

        HttpURLConnection urlConnection = (HttpURLConnection) urlToTheReviews.openConnection();

        if (urlConnection.getResponseCode() != 200) {

            Log.d("urlreview in method", String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage());
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();
                if (headerName == null)
                    headerName = "NULL";
                Log.d("Header Name:", headerName);
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    Log.d("Header value:", value);
                }
            }
            urlConnection.disconnect();
            return urlConnection.getResponseCode();
        }


        try {
            InputStream in = urlConnection.getInputStream();
            local_input = readStream(in);

            //scanner for the url string
            Scanner scanner = new Scanner(local_input);
            scanner.useDelimiter("\"url\":\"");

            // not all movies have a review so need to check that there is at least one
            checkForUrl = scanner.findInLine("\"url\":\"");
            if (checkForUrl == null) {
                urlConnection.disconnect();
                return count;
            }

            // Retrieve each url for the movie review url Array
            while (scanner.hasNext()) {
                local_input = scanner.next();
                index = local_input.indexOf("\"");
                if (index > 0) {
                    String local_id = local_input.substring(0, index);
                    url[count] = new URL(local_id);
                    count++;
                }
            }

        } finally {
            urlConnection.disconnect();
        }
        return count;
    }
    public int getMovieReviews(String content[], URL urlToTheReviews) throws IOException {

        String local_input = null;
        String checkForUrl = null;
        int index = 0;
        int count = 0;
        int attempts = 0;

        HttpURLConnection urlConnection = (HttpURLConnection) urlToTheReviews.openConnection();

        if (urlConnection.getResponseCode() != 200) {

            Log.d("urlreview in method", String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage());
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();
                if (headerName == null)
                    headerName = "NULL";
                Log.d("Header Name:", headerName);
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    Log.d("Header value:", value);
                }
            }
            urlConnection.disconnect();
            return urlConnection.getResponseCode();
        }


        try {
            InputStream in = urlConnection.getInputStream();
            local_input = readStream(in);

            Log.d("local_input",local_input);
            //scanner for the url string
            Scanner scanner = new Scanner(local_input);
            scanner.useDelimiter("\"author\":\"");

            // not all movies have a review so need to check that there is at least one
            checkForUrl = scanner.findInLine("\"author\":\"");
            if (checkForUrl == null) {
                urlConnection.disconnect();
                return count;
            }

            // Retrieve each url for the movie review url Array
            while (scanner.hasNext()) {
                local_input = scanner.next();
                index = local_input.indexOf("url\":");
                if (index > 0) {
                    String local_id = local_input.substring(0, index);
                    content[count] = local_id;
                    Log.d("local_review", local_id);
                    count++;
                }
            }

        } finally {
            urlConnection.disconnect();
        }
        return count;
    }
}
