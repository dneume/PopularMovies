package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.android.popularmovies.Utils.buildUrlForDetail;
import static java.lang.Boolean.FALSE;


public class MainActivity extends AppCompatActivity implements ImageAdapter.ItemClickListener {
    private ImageAdapter adapter;

    private Boolean networkIsAvailable = FALSE;
    private Context context;
    public TextView mSearchResultsTextView;

    public static List<MovieDetail> movies;

    public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {

        //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=78891bb3d2c1b1ee69109f6f46b23ead

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieDbResults = null;
            URL detailUrl;

            try {
                movieDbResults = getResponseFromHttpUrl(searchUrl);
                for (int i = 0; i < movies.size(); i++) {
                    //Log.d("array list: ", movies.get(i).movie_id);
                    detailUrl = buildUrlForDetail(movies.get(i).movie_id);
                    getMovieDetail(detailUrl,i);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
              return movieDbResults;
        }

        @Override
        protected void onPostExecute(String movieDbResults) {
            Log.d("array size: ", String.valueOf(movies.size()));
            for (int i = 0; i < movies.size(); i++) {
                Log.d("array list ", movies.get(i).movie_original_title);
                Log.d("array list ", movies.get(i).movie_overview);
            }
            showJsonDataView();
            mSearchResultsTextView.setText(movieDbResults);
            Log.d("onPost show JsonData","getting ready to exit");
        }
     }

    private void showJsonDataView() {
        mSearchResultsTextView.setVisibility(View.VISIBLE);
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

        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "34", "35", "36", "37", "39"
        };

        numberOfColumns = 2;
        context = getApplicationContext();

        //hide the extra text view until it is needed
        mSearchResultsTextView = findViewById(R.id.r_error);
        hideJsonDataView();

        // is the network available
        utils = new Utils();
        networkIsAvailable = utils.isNetworkAvailable(context);

        // set up the recycler view with the layout and the adapter
        recyclerView = findViewById(R.id.rvImages);
        recyclerView_LayoutManager = new GridLayoutManager(context, numberOfColumns);
        if (recyclerView_LayoutManager != null) {
            recyclerView.setLayoutManager(recyclerView_LayoutManager);
        }

        url = Utils.buildUrl();
        // create the an arraylist of the moviedetail based on the id from the moviedb
        movies = new ArrayList<MovieDetail>();

        //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
        //Log.d("1 process",url.toString());
        new MovieDbQueryTask().execute(url);

        // check to make sure the ids are stored properly
//        Log.d("array size: ", String.valueOf(movies.size()));
//        for (int i = 0; i < movies.size(); i++) {
//            Log.d("array list: ", movies.get(i).movie_id);
//        }


        recyclerView_Adapter = new ImageAdapter(context, data);
        if (recyclerView_Adapter != null) {
            recyclerView.setAdapter(recyclerView_Adapter);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //IF the network is not available...do something
        if(!networkIsAvailable) {
            Toast.makeText(this, "'Popular Movies' requires a network, but one cannot be found!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Try again once a network is available!", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String local_input = null;
        int index = 0;
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
                }
                hasInput = scanner.hasNext();

            }
            return local_input;

        } finally {
            urlConnection.disconnect();
        }
    }

    public boolean getMovieDetail(URL url, int i){

        HttpURLConnection urlConnection = null;
        String local_input = null;
        String copy_of_local_input = null;
        int index = 0;
        Scanner[] scanner;
        MovieDetail mMovieDetail = new MovieDetail("1");

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mMovieDetail.movie_id = movies.get(i).movie_id;

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
            //Log.d("end of detail"," ");

            movies.add(mMovieDetail);
            movies.remove(i);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return true;
    }
    private String scanInput(String delimiter, String input) {
        int index = 0;
        String local_id = null;
        Scanner scanner = new Scanner(input);
        scanner.useDelimiter(delimiter);
        boolean hasInput = scanner.hasNext();
        input = scanner.next();
        input = scanner.next();
        if(hasInput){
            index = input.indexOf("\",");
            if (index > 0) {
                local_id = input.substring(0, index);
                //Log.d(delimiter, local_id);
            }
        }
        return local_id;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

}


