package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
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

import static com.example.android.popularmovies.MovieFavoritesDb.CONTENT_URI;
import static com.example.android.popularmovies.Utils.buildUrlForReviews;
import static com.example.android.popularmovies.Utils.buildUrlForVideos;
import static com.example.android.popularmovies.Utils.readStream;
import static java.lang.Boolean.TRUE;


public class DetailActivity extends AppCompatActivity {

    private String mNumberOfReviews = "0";
    private String mNumberOfVideos = "0";
    private MovieDetail mMovieDetail;
    private TextView mMovieReview;
    private TextView mMovieVideo;

    // the following are needed to play the trailer
    private DetailVideoFragment videoFragment;
    private View videoBox;

    private ContentProviderMovieApp cpma;

    public class MovieReviewAndVideoDbQueryTask extends AsyncTask<Void, Void, String[]> {

        String mId;
        String[] mReviewContent;
        String[] mVideoId;
        String[] mVideoName;

        // use the constructor to pass multiple parameters to the task
        public MovieReviewAndVideoDbQueryTask(String Id,String[] reviewContent, String[] VideoId, String[] VideoName ){
            this.mId = Id;
            this.mReviewContent = reviewContent;
            this.mVideoId = VideoId;
            this.mVideoName = VideoName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {

            int number_of_reviews = 0;
            int number_of_videos = 0;
            URL reviewUrl;
            URL videoUrl;
            String buf[] = new String[2];

            Context context = getBaseContext();

            try {
//                get the list of reviews
                reviewUrl = buildUrlForReviews(mId, context);
                number_of_reviews = getMovieReviews(mReviewContent, reviewUrl);
                Log.d(getLocalClassName(),"number_of_reviews " + String.valueOf(number_of_reviews));

//                get the list of videos
                videoUrl = buildUrlForVideos(mId, context);
                number_of_videos = getMovieVideos(mVideoId, mVideoName, videoUrl);
                Log.d(getLocalClassName(),"number_of_videos " + String.valueOf(number_of_videos));

            } catch (IOException e) {
                e.printStackTrace();
            }
            buf[0] = String.valueOf(number_of_reviews);
            buf[1] = String.valueOf(number_of_videos);
            return buf;
        }

        @Override
        protected void onPostExecute(String[] buf ) {
            mNumberOfReviews = buf[0];
            String hold = mMovieReview.getText() + "(" + mNumberOfReviews + ")";
            mMovieReview.setText(hold);

            // set the content in the MovieDetail
            int num;
            for(num = 0; num < Integer.valueOf(mNumberOfReviews); num++) {
                mMovieDetail.movie_review[num] = mReviewContent[num];
                Log.d(getLocalClassName(),"on_Post_execute " + mReviewContent[num]);
                Log.d(getLocalClassName(), "on_Post_execute " + mMovieDetail.movie_review[num]);
            }
            // ensure the next item in the array is explicitly null
            mMovieDetail.movie_review[num] = null;

            // set the content for the number of videos
            mNumberOfVideos = buf[1];
            hold = mMovieVideo.getText() + "(" + mNumberOfVideos + ")";
            mMovieVideo.setText(hold);
            // set the content in the MovieDetail
            for(num = 0; num < Integer.valueOf(mNumberOfVideos); num++) {
                mMovieDetail.movie_trailer_id[num] = mVideoId[num];
            }
            // ensure the next item in the array is explicitly null
            mMovieDetail.movie_trailer_id[num] = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] mDetail = new String[7];
        mMovieDetail = new MovieDetail(String.valueOf(0));
        Context mContext;

        //create the content provider
        cpma = new ContentProviderMovieApp();

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
        String mMovieVideos_id[] = mMovieDetail.movie_trailer_id;
        String mMovieVideo_name[] = mMovieDetail.movie_trailer_name;
        new MovieReviewAndVideoDbQueryTask(mId,mMovieReviews, mMovieVideos_id,mMovieVideo_name).execute();

        // get the first 4 digits of the rating
        String mMovieRating = mDetail[4];
        lengthOfRating = mMovieRating.length();
        if(lengthOfRating < 4)
            mMovieDetail.move_user_rating = mMovieRating.substring(0, lengthOfRating);
        else
            mMovieDetail.move_user_rating = mMovieRating.substring(0, 4);

        //format the date
        Date date;
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

        mMovieVideo = new TextView(mContext);
        mMovieVideo = findViewById(R.id.display_videos);

        // add a CheckedTextView box in order to make it a favorite
        final CheckedTextView chkBox = findViewById(R.id.checkedTextView);

        //query the db to check if this is a MovieFavorite
        Cursor cursor;
        String[] local_movie_id = new String[1];
        local_movie_id[0] = mMovieDetail.movie_id;
        cursor = cpma.query(CONTENT_URI,null,"MovieId = ?",local_movie_id,null);

        if(cursor.getCount() > 0){
            chkBox.setChecked(true);
            chkBox.setText("This is a Favorite!");
        }
        else{
            chkBox.setChecked(false);
            chkBox.setText("Make this a Favorite?");
        }
        cursor.close();

        //        we have the data, now lets create the fragments needed to support the playing of the video
        videoBox = findViewById(R.id.detail_video_container_fragment);
        String videoId = mMovieDetail.movie_trailer_id[0];
        videoFragment =
                (DetailVideoFragment) getFragmentManager().findFragmentById(R.id.detail_video_container_fragment);
        videoFragment.setVideoId(videoId);
        videoBox.setVisibility(View.VISIBLE);


        chkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
               if(((CheckedTextView) v).isChecked()){
                    ((CheckedTextView) v).setChecked(false);
                    chkBox.setText("Make this a Favorite?");
                    String[] localstr = new String[1];
                    localstr[0] = mMovieDetail.movie_id;
                    int rows_deleted = cpma.delete(CONTENT_URI,"MovieId = ?",localstr);
                    Log.d(getLocalClassName(),"deleted MovieId " + mMovieDetail.movie_id + ", " + Integer.toString(rows_deleted) + " instances");
                }else{
                    ((CheckedTextView) v).setChecked(true);
                    chkBox.setText("This is a Favorite!");

         // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_MOVIE_ID, mMovieDetail.movie_id);
                    values.put(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_MOVIE_ORIGINAL_TITLE, mMovieDetail.movie_original_title);
                    values.put(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_THUMBNAIL_PATH,mMovieDetail.movie_thumbnail_path);
         // insert the new row using the content provider
                    Uri uri;
                    uri = cpma.insert(CONTENT_URI,values);
                    Log.d(getLocalClassName(), uri.toString());
                }
            }
        });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mMovieVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchMovieVideosActivity();
            }
        });

        mMovieReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchMovieReviewsActivity();
            }
        });
    }


    public boolean launchMovieReviewsActivity() {
        Intent i;
        int num;
        String mMovieId = "MovieReviewDetail";
        String[] content = new String[20];

        int lNumberOfReviews = Integer.valueOf(mNumberOfReviews);
        //pass the movie title in the first array

        Log.d("set_content", Integer.toString(lNumberOfReviews));
        for(num = 0 ; num < lNumberOfReviews; num++){
            content[num] = mMovieDetail.movie_review[num];
            Log.d(getLocalClassName(), "set_content " + Integer.toString(num));
            Log.d(getLocalClassName(), "set_content " + mMovieDetail.movie_review[num]);

//           the assumption is that you will not have more than 20 reviews
            if(num > 20) {
                Log.d( getLocalClassName(),"More than 20 reviews. You should not be here!");
                break;
            }
        }
        content[lNumberOfReviews] = mMovieDetail.movie_original_title;

        Log.d("launch_movie_review", Integer.toString(lNumberOfReviews));
        for(num = 0 ; num < lNumberOfReviews; num++){
            Log.d(getLocalClassName(), "before_movie_review " + content[num] );
        }

        i = new Intent(this, MovieReviewActivity.class);
        i.putExtra(mMovieId,content);
        startActivity(i);
        return TRUE;
    }

    public boolean     launchMovieVideosActivity() {
        Intent i;
        int num;
        String mIntentId = "MovieVideoDetail";
        String[] content = new String[42];

        int lNumberOfVideos = Integer.valueOf(mNumberOfVideos);
        //pass the movie title in the first array

        Log.d(this.getLocalClassName(), "reviews = " + mNumberOfReviews);
        Log.d(this.getLocalClassName(), "videos = " + Integer.toString(lNumberOfVideos));
        for(num = 0 ; num < lNumberOfVideos; num++){
            content[num] = mMovieDetail.movie_trailer_id[num] + ";" + mMovieDetail.movie_trailer_name[num];
            Log.d(this.getLocalClassName(),"Prepare Intent = " + content[num]);
//           the assumption is that you will not have more than 20 videos
            if(num > 20) {
                Log.d(getLocalClassName(), "More than 20 videos. You should not be here!");
                break;
            }
        }
        content[lNumberOfVideos] = mMovieDetail.movie_original_title;
        i = new Intent(this, MovieVideoActivity.class);
        i.putExtra(mIntentId,content);
        startActivity(i);
        return TRUE;
    }

    public int getMovieVideos(String video_id[],String video_name[], URL urlToTheVideos) throws IOException {

        String local_input;
        String checkForVideo;
        String copy_of_local_input;
        int index;
        int count = 0;
        int number_of_videos;

        HttpURLConnection urlConnection = (HttpURLConnection) urlToTheVideos.openConnection();

        if (urlConnection.getResponseCode() != 200) {

            Log.d(this.getLocalClassName(), String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage());
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();
                if(headerName == null)
                    headerName = "NULL";
                Log.d(this.getLocalClassName(), "headerName = " + headerName);
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    Log.d(this.getLocalClassName(),"Header value = " + value);
                }
            }
            urlConnection.disconnect();
            return urlConnection.getResponseCode();
        }


        try {
            InputStream in = urlConnection.getInputStream();
            local_input = readStream(in);
            copy_of_local_input = local_input;

            //***********   scanner for the key string *********************
            Scanner scanner = new Scanner(local_input);
            scanner.useDelimiter("\"key\":\"");

            // not all movies have a review so need to check that there is at least one
            checkForVideo = scanner.findInLine("\"key\":\"");
            if(checkForVideo == null) {
                urlConnection.disconnect();
                return count;
            }

            // Retrieve each url for the movie review url Array
            while(scanner.hasNext()) {
                local_input = scanner.next();
                index = local_input.indexOf("\",");
                if (index > 0) {
                    String local_buf = local_input.substring(0, index);
                    String local_id = local_buf.replace("\\","");
                    video_id[count] = local_id;
                    Log.d(this.getLocalClassName(), "Video id = " + local_id);
                    count++;
                }
            }
            number_of_videos = count;


            //***********   scanner for the name string *********************
            count = 0;
            local_input = copy_of_local_input;
            scanner = new Scanner(local_input);
            scanner.useDelimiter("\"name\":\"");
            checkForVideo = scanner.findInLine("\"name\":\"");

            // Retrieve each video name
            while(scanner.hasNext()) {
                local_input = scanner.next();
                index = local_input.indexOf("\",");
                if (index > 0) {
                    String local_buf = local_input.substring(0, index);
                    String local_name = local_buf.replace("\\","");
                    video_name[count] = local_name;
                    Log.d(this.getLocalClassName(), "Video name = " + local_name);
                    count++;
                }
            }
        } finally {
            urlConnection.disconnect();
        }
        return number_of_videos;
    }



    public int getMovieReviews(String content[], URL urlToTheReviews) throws IOException {

        String local_input;
        String checkForUrl;
        int index;
        int count = 0;

        HttpURLConnection urlConnection = (HttpURLConnection) urlToTheReviews.openConnection();

        if (urlConnection.getResponseCode() != 200) {

            Log.d(this.getLocalClassName(), "urlreview in method = " + String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage());
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();
                if (headerName == null)
                    headerName = "NULL";
                Log.d(this.getLocalClassName(),"HeaderName = " + headerName);
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    Log.d(this.getLocalClassName(), "HeaderValue = :" + value);
                }
            }
            urlConnection.disconnect();
            return urlConnection.getResponseCode();
        }


        try {
            InputStream in = urlConnection.getInputStream();
            local_input = readStream(in);

            Log.d(this.getLocalClassName(),"local_input= " + local_input);
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
                    Log.d(this.getLocalClassName(), "local_review= " + local_id);
                    count++;
                }
            }

        } finally {
            urlConnection.disconnect();
        }
        return count;
    }

//    private void layout() {
//        boolean isPortrait =
//                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
//
//        videoBox = findViewById(R.id.detail_video_container_fragment);
//
//        if (isFullscreen) {
//            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
//            setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
//            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
//        } else if (isPortrait) {
//            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
//            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
//        } else {
//            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
//            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
//            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
//            setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
//            setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
//                    Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//        }
//    }
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
//    private static void setLayoutSize(View view, int width, int height) {
//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        params.width = width;
//        params.height = height;
//        view.setLayoutParams(params);
//    }
//
//    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
//        params.width = width;
//        params.height = height;
//        params.gravity = gravity;
//        view.setLayoutParams(params);
//    }


}
