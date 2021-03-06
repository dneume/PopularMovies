package com.example.android.popularmovies;


import android.content.Intent;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;

import android.text.Layout;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import static com.example.android.popularmovies.Utils.buildUrlForDetail;
import static com.example.android.popularmovies.Utils.readStream;
import static com.example.android.popularmovies.Utils.scanInput;
import static com.example.android.popularmovies.Utils.buildUrlForThumbNailFile;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class  MainActivity extends AppCompatActivity {

    private Boolean networkIsAvailable = FALSE;
    private Context context;
    public TextView mSearchResultsTextView;
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
            Context context;

            int i = 0;

            context = getApplicationContext();
            try {
                // the buildUrlfor is adding and deleting members
                movieDbResults = getResponseFromHttpUrl(searchUrl);

                mIterator = movies.listIterator(0);
                while(mIterator.hasNext()){
                    mMovieDetail = mIterator.next();
                    mMovie_Id = mMovieDetail.movie_id;
                    detailUrl = buildUrlForDetail(mMovie_Id,context);
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
                i++;
            }
            showJsonDataView();
            Log.d("onPost Execute","getting ready to exit");
        }

     }

    private void showJsonDataView() {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_Adapter.notifyDataSetChanged();
     }

    private void hideJsonDataView() {
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        URL url;
        Utils utils;
        RecyclerView.Adapter recyclerView_Adapter;
        RecyclerView.LayoutManager recyclerView_LayoutManager;
        RecyclerView recyclerView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        url = Utils.buildUrl("Popular",context);

        //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
        new MovieDbQueryTask().execute(url);

        Log.d("onCreate ", " ");
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


//    @Override
    public void onItemClick(View view, int position) {
        Log.d(" main activity ", " onItemClick" + " " + String.valueOf(position));
    }



    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //IF the network is not available...do something
        if(!networkIsAvailable) {
            Toast.makeText(this, "'Movies to Watch!' requires a network, but one cannot be found!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Try again once a network is available!", Toast.LENGTH_LONG).show();
            finish();
        }

        recyclerView = findViewById(R.id.rvImages);

        // create the movie adapter and set the custom listener to return the click value
        recyclerView_Adapter = new MovieAdapter(context,
                movies,
                new MovieAdapter.onCustomItemClickListener() {
                    @Override
                    public void onCustomItemClick(View view, int position) {

                        Log.d("onclick view ", " in main activity " + String.valueOf(position));
                        launchDetailActivity(position);
                    }
                }
        );

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

            // Retrieve all the movie IDs and put them into List Array
            while (hasInput) {

                local_input = scanner.next();
                index = local_input.indexOf(",");
                if (index > 0) {
                    String local_id = local_input.substring(0, index);
                    mMovieDetail = new MovieDetail(local_id);
                    movies.add(mMovieDetail);
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
       // Within onCreateOptionsMenu, use getMenuInflater().inflate to inflate the menu
        getMenuInflater().inflate(R.menu.menus,menu);
      //  Return true to display your menu
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
                url = Utils.buildUrl("Popular",context);
                //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
                new MovieDbQueryTask().execute(url);
                return true;
            case R.id.highest:
                menuItem.setChecked(TRUE);
                if(sort_option == R.id.highest)
                    return true;
                Toast.makeText(context, "Display list of highest rated movies " , Toast.LENGTH_LONG).show();
                sort_option = R.id.highest;

                // call this function to build the query to return one page of movies
                url = Utils.buildUrl("Highest",context);
                //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
                new MovieDbQueryTask().execute(url);
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public boolean launchDetailActivity(int position) {
        Intent i;
        String ldetail = "detail";
        MovieDetail mMovieDetail;
        String[] mMovieDetailExtras = new String[7];

        // position is the index of the item clicked in the grid array
        // get the position in the array and pass the movie detail in the intent
        mMovieDetail = movies.get(position);
        mMovieDetailExtras[0] = mMovieDetail.movie_id;
        mMovieDetailExtras[1] = mMovieDetail.movie_original_title;
        mMovieDetailExtras[2] = mMovieDetail.movie_thumbnail_path;
        mMovieDetailExtras[3] = mMovieDetail.movie_overview;
        mMovieDetailExtras[4] = mMovieDetail.move_user_rating;
        mMovieDetailExtras[5] = mMovieDetail.release_date;
        mMovieDetailExtras[6] = mMovieDetail.movie_complete_path;

        // need to rewrite the code to get the menu to inflate on the child menu.
        // see Menu API documentation for more info
        invalidateOptionsMenu();

        i = new Intent(this, DetailActivity.class);
        i.putExtra(ldetail,mMovieDetailExtras);
        startActivity(i);
        return TRUE;
    }

}



