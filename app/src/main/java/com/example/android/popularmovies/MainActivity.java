package com.example.android.popularmovies;


import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;

import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import static com.example.android.popularmovies.MovieFavoritesDb.CONTENT_URI;
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
    // default the sort option to null on the first time this is instantiated
    public static String sort_option = null;

    public RecyclerView.Adapter recyclerView_Adapter;
    public RecyclerView.LayoutManager recyclerView_LayoutManager;
    public RecyclerView recyclerView;

    public static List<MovieDetail> movies;
    public ListIterator<MovieDetail> mIterator;

    public static SQLiteDatabase db;


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
            ContentProviderMovieApp cpma;
            Cursor cursor;

            int i = 0;
            Context context = getBaseContext();
            cpma = new ContentProviderMovieApp();
            try {
                // check and create the db the first time thru
                MovieFavoritesDbHelper mDbHelper = new MovieFavoritesDbHelper(getApplicationContext());
                db = mDbHelper.getWritableDatabase();
                mDbHelper.onCreate(db);

                //get the proper source for movies list to display
                if(sort_option == "Favorites") {
                    Log.d(getLocalClassName(), "switch_favorites");
                    cpma = new ContentProviderMovieApp();
                    // return all rows from the database
                    cursor = cpma.query(CONTENT_URI,null,null,null,null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast() ){
                        String local_id = cursor.getString(cursor.getColumnIndex(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_MOVIE_ID));
                        String movie_title = cursor.getString(cursor.getColumnIndex(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_MOVIE_ORIGINAL_TITLE));
                        mMovieDetail = new MovieDetail(local_id);
                        mMovieDetail.movie_original_title = movie_title;
                        movies.add(mMovieDetail);
                        cursor.moveToNext();
                    }
                    cursor.close();
                }
                else {
                     Log.d(getLocalClassName(),"switch_other");
                     // the get the json data from the web service
                     movieDbResults = getResponseFromHttpUrl(searchUrl);
                }

                mIterator = movies.listIterator(0);
                while(mIterator.hasNext()){
                    mMovieDetail = mIterator.next();
                    mMovie_Id = mMovieDetail.movie_id;
                    detailUrl = buildUrlForDetail(mMovie_Id,context);
                    getMovieDetail(detailUrl,i,mMovieDetail);
                    Log.d(getLocalClassName(), "List_Movie_id: " + mMovieDetail.movie_id + " " + String.valueOf(i) );

                    i++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }



//            db.execSQL(SQL_DELETE_DB);
//             if( db != null) {
//                 Log.d(getLocalClassName(),"calling onDestroy");
//                 mDbHelper.onDestroy(db);
//             }
//            Log.d(getLocalClassName(),db.getPath());
//            db.execSQL(SQL_CREATE_DB);
            // Create a new map of values, where column names are the keys
//            ContentValues values = new ContentValues();
//            values.put(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_MOVIE_ID, "Movie title");
//            values.put(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_MOVIE_ORIGINAL_TITLE, "original title");
//            values.put(MovieFavoritesDb.MovieFavoritesColumns.COLUMN_THUMBNAIL_PATH,"thumbnail path");
//// Insert the new row, returning the primary key value of the new row
//            long newRowId = db.insert(MovieFavoritesDb.MovieFavoritesColumns.TABLE_NAME, null, values);
//            Log.d(getLocalClassName(), Long.toString(newRowId));
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
                Log.d(getLocalClassName(), "onPostExecute " + mMovieDetail.movie_id + " " + String.valueOf(i) + mMovieDetail.movie_original_title );
                i++;
            }
            showJsonDataView();
            Log.d(getLocalClassName(), "onPost Execute: getting ready to exit");
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

        URL url = null;
        Utils utils;

        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);

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
        // first time thru, the sort_option is set to the default
        //otherwise, the sort_option is a global variable
        Log.d(getLocalClassName(),"OnCreate sort_optionVal: " + String.valueOf(sort_option));

        if(sort_option == null) {
            Log.d(getLocalClassName(),"sort_option is set to popular");
            sort_option = "Popular";
        }
        //Menus have not been created yet so tracking the sort option manually
        if(sort_option != "Favorites") {
            url = Utils.buildUrl(sort_option, context);
        }
        //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
        new MovieDbQueryTask().execute(url);
        if(movies.isEmpty() == FALSE) {
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
                new MovieAdapter.OnCustomItemClickListener() {
                    @Override
                    public void onCustomItemClick(View view, int position) {

                        Log.d(getLocalClassName(),"onclick view, in main activity " + String.valueOf(position));
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
       savedInstanceState.putString("Sort_option",sort_option);
       // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        sort_option = savedInstanceState.getString("Sort_option");
   }


//     query the database for popular movies, and then scan the stream for every movie_id in order
//     to save in the arraylist of MovieDetail
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d("getResponseFromHttpUrl",url.toString());
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

    @Override
    public void onStart() {
        super.onStart();
     }

    //pass the url of the movie_id, along with the mMovieDetail instance from the arraylist
    public boolean getMovieDetail(URL url, int i, MovieDetail mMovieDetail) {

        HttpURLConnection urlConnection = null;
        String local_input = null;
        String copy_of_local_input = null;
        URL mUrl = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
            return false;
        } finally {
            urlConnection.disconnect();
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // Within onCreateOptionsMenu, use getMenuInflater().inflate to inflate the menu
        getMenuInflater().inflate(R.menu.menus,menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem menuItem;
        if(sort_option == "Favorites") {
            menuItem = menu.findItem(R.id.favorites);
            menuItem.setChecked(TRUE);
        }
        if(sort_option == "Popular") {
            menuItem = menu.findItem(R.id.popular);
            menuItem.setChecked(TRUE);
        }
        if(sort_option == "Highest") {
            menuItem = menu.findItem(R.id.highest);
            menuItem.setChecked(TRUE);
        }
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
                if(sort_option == "Popular")
                        return true;
                Toast.makeText(context,  "Display list of popular movies " , Toast.LENGTH_LONG).show();

                sort_option = "Popular";
                // call this function to build the query to return one page of movies
                url = Utils.buildUrl(sort_option,context);
                //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
                new MovieDbQueryTask().execute(url);
                return true;
            case R.id.highest:
                menuItem.setChecked(TRUE);
                if(sort_option == "Highest")
                    return true;
                Toast.makeText(context, "Display list of highest rated movies " , Toast.LENGTH_LONG).show();
                sort_option = "Highest";

                // call this function to build the query to return one page of movies
                url = Utils.buildUrl(sort_option,context);
                //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
                new MovieDbQueryTask().execute(url);
                return true;
            case R.id.favorites:
                menuItem.setChecked(TRUE);
                if(sort_option == "Favorites")
                    return true;
                Toast.makeText(context, "Display list of favorite movies " , Toast.LENGTH_LONG).show();
                sort_option = "Favorites";
                // call this function to build the query to return one page of favorite movies
                url = null;
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



