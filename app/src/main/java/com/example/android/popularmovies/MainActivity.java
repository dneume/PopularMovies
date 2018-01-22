package com.example.android.popularmovies;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import static com.example.android.popularmovies.Utils.buildUrlForDetail;
import static com.example.android.popularmovies.Utils.readStream;
import static com.example.android.popularmovies.Utils.scanInput;
import static com.example.android.popularmovies.Utils.buildUrlForThumbNailFile;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {
    private MovieAdapter adapter;
    private Boolean networkIsAvailable = FALSE;
    private Context context;
    public TextView mSearchResultsTextView;
    //public ImageView mImageView;
    public Menu menu;
    private int sort_option;

    public RecyclerView.Adapter recyclerView_Adapter;
    public RecyclerView.LayoutManager recyclerView_LayoutManager;
    public RecyclerView recyclerView;

    public static List<MovieDetail> movies;
    public ListIterator<MovieDetail> mIterator;

    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            movies.clear();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieDbResults = null;
            URL detailUrl;
            MovieDetail mMovieDetail;
            String mMovie_Id;

            int i = 0;
            ImageView mImageView;

            mImageView = (ImageView) findViewById(R.id.display_image);
            try {
                // the buildUrlfor is adding and deleting members
                movieDbResults = getResponseFromHttpUrl(searchUrl);

                mIterator = movies.listIterator(0);
                while(mIterator.hasNext()){
                    mMovieDetail = mIterator.next();
                    mMovie_Id = mMovieDetail.movie_id;
                    detailUrl = buildUrlForDetail(mMovie_Id);
                    getMovieDetail(detailUrl,i,mMovieDetail);
                    Log.d("List_Movie_id ", mMovieDetail.movie_id + " " + String.valueOf(i) );
                    i++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
              return movieDbResults;
        }

        @Override
        protected void onPostExecute(String movieDbResults) {
            Log.d("onPostExecute ", String.valueOf(movies.size()));

            mIterator = movies.listIterator(0);
            int i = 0;
            MovieDetail mMovieDetail;
            while(mIterator.hasNext()){
                mMovieDetail = mIterator.next();
                Log.d("onPostExecute ", mMovieDetail.movie_id + " " + String.valueOf(i) + mMovieDetail.movie_original_title );
//                Log.d("continued...complete path. ", mMovieDetail.movie_complete_path);
                i++;
            }

            showJsonDataView();
           // mSearchResultsTextView.setText(movieDbResults);
            Log.d("onPost show JsonData","getting ready to exit");
        }

     }

    private void showJsonDataView() {
//        Context context = getApplicationContext();
//        String mMovie_complete_path = null;
//        ImageView mImageView = findViewById(R.id.display_image);
//        Picasso mPicasso = null;
//
//        Iterator<MovieDetail> mIterator = movies.iterator();
//        int i = 0;
//        MovieDetail mMovieDetail;
//        while(mIterator.hasNext()){
//            mMovieDetail = mIterator.next();
//            mMovie_complete_path = mMovieDetail.movie_complete_path;
//            Log.d("show Jason data...complete path. ", mMovieDetail.movie_complete_path);
//            mPicasso.with(context).load(mMovie_complete_path).into(mImageView);
//            i++;
//        }
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_Adapter.notifyDataSetChanged();
     }

    private void hideJsonDataView() {
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int numberOfColumns = 0;
        URL url;
        Utils utils;
        RecyclerView.Adapter recyclerView_Adapter;
        RecyclerView.LayoutManager recyclerView_LayoutManager;
        RecyclerView recyclerView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         numberOfColumns = 2;
        context = getApplicationContext();

        //hide the extra text view until it is needed
        mSearchResultsTextView = findViewById(R.id.r_error);
        hideJsonDataView();

        // is the network available
        utils = new Utils();
        networkIsAvailable = utils.isNetworkAvailable(context);



        // create the an arraylist of the moviedetail based on the id from the moviedb
        movies = new ArrayList<MovieDetail>();

        // call this function to build the query to return one page of movies
        url = Utils.buildUrl("Popular");

        //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
        new MovieDbQueryTask().execute(url);

        Log.d("onCreate ", " ");
        //movies.listIterator() = new ListIterator<MovieDetail>;
        if(movies.isEmpty() == FALSE) {
            Log.d("on create ", " NOT EMPTY" );
            mIterator = movies.listIterator(0);
            int i = 0;
            MovieDetail mMovieDetail;
            while(mIterator.hasNext()) {
                mMovieDetail = mIterator.next();
                Log.d("onCreate ", mMovieDetail.movie_id + " " + String.valueOf(i) + mMovieDetail.movie_original_title);
                i++;
            }
         }
        else {
            Log.d("on create ", " IS EMPTY" );
        }

    }


    @Override
    public void onItemClick(View view, int position) { }



    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //IF the network is not available...do something
        if(!networkIsAvailable) {
            Toast.makeText(this, "'Popular Movies' requires a network, but one cannot be found!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Try again once a network is available!", Toast.LENGTH_LONG).show();
            finish();
        }

        recyclerView = findViewById(R.id.rvImages);
        recyclerView_Adapter = new MovieAdapter(context, movies);
        if (recyclerView_Adapter != null) {
            recyclerView.setAdapter(recyclerView_Adapter);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        int numberOfColumns = 2;
        recyclerView_LayoutManager = new GridLayoutManager(context, numberOfColumns);
        if (recyclerView_LayoutManager != null) {
            recyclerView.setLayoutManager(recyclerView_LayoutManager);
        }

    }


//     query the database for popular movies, and then scan the stream for every movie_id in order
//     to save in the arraylist of MovieDetail
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String local_input = null;
        int index = 0;
        int count = 0;
        MovieDetail mMovieDetail;

        try {
            InputStream in = urlConnection.getInputStream();

            //scanner for the id
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\"id\":");
            boolean hasInput = scanner.hasNext();
            local_input = scanner.next();

            while (hasInput) {

                local_input = scanner.next();
                index = local_input.indexOf(",");
                if (index > 0) {
                    String local_id = local_input.substring(0, index);
                    mMovieDetail = new MovieDetail(local_id);
                    movies.add(mMovieDetail);
                    //Log.d("Parse_the_first_http_response ", local_id + " " + movies.get(count).movie_id + " " + String.valueOf(count));
                    count++;
                }
                hasInput = scanner.hasNext();

            }
            return local_input;

        } finally {
            urlConnection.disconnect();
        }
    }

    //pass the url of the movie_id, along with the mMovieDetail instance from the arraylist
    public boolean getMovieDetail(URL url, int i, MovieDetail mMovieDetail){

        HttpURLConnection urlConnection = null;
        String local_input = null;
        String copy_of_local_input = null;
        URL mUrl = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
           // get the json file for the movie_id
           // and then parse the jason using Scanners

            InputStream in = urlConnection.getInputStream();
            local_input = readStream(in);
            copy_of_local_input = local_input;

            String original_title = scanInput("original_title\":\"", local_input);
            local_input = copy_of_local_input;
             mMovieDetail.movie_original_title = original_title;

            String poster_path = scanInput("poster_path\":\"", local_input);
            local_input = copy_of_local_input;
            mMovieDetail.movie_thumbnail_path = poster_path;

            String overview = scanInput("overview\":\"", local_input);
            local_input = copy_of_local_input;
            mMovieDetail.movie_overview = overview;

            String vote_average = scanInput("vote_average\":", local_input);
            local_input = copy_of_local_input;
            mMovieDetail.move_user_rating = vote_average;

            String release_date = scanInput("release_date\":\"", local_input);
            local_input = copy_of_local_input;
            mMovieDetail.release_date = release_date;

            mUrl = buildUrlForThumbNailFile(mMovieDetail.movie_thumbnail_path);
            mMovieDetail.movie_complete_path = mUrl.toString();



        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Within onCreateOptionsMenu, use getMenuInflater().inflate to inflate the menu
        getMenuInflater().inflate(R.menu.menus,menu);
//      //  Return true to display your menu

        sort_option = R.id.popular;

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        return TRUE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id_of_item_checked = menuItem.getItemId();
        URL url;

        switch (id_of_item_checked){
            case R.id.popular:
                // checkable behavior is single so no need to uncheck the other menu item
                menuItem.setChecked(TRUE);
                if(sort_option == R.id.popular)
                        return true;
                Toast.makeText(context,  "Display list of popular movies " , Toast.LENGTH_LONG).show();

                sort_option = R.id.popular;
                // call this function to build the query to return one page of movies
                url = Utils.buildUrl("Popular");
                //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
                new MovieDbQueryTask().execute(url);
                return true;
            case R.id.highest:
                menuItem.setChecked(TRUE);
                if(sort_option == R.id.highest)
                    return true;
                Toast.makeText(context, "Get list of highest rated movies " , Toast.LENGTH_LONG).show();
                sort_option = R.id.highest;

                // call this function to build the query to return one page of movies
                url = Utils.buildUrl("Highest");
                //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
                new MovieDbQueryTask().execute(url);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}



