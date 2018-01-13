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

import java.io.IOException;
import java.net.URL;

import static com.example.android.popularmovies.Utils.getResponseFromHttpUrl;
import static java.lang.Boolean.FALSE;


public class MainActivity extends AppCompatActivity implements ImageAdapter.ItemClickListener {
    private ImageAdapter adapter;

    private Boolean networkIsAvailable = FALSE;
    private Context context;
    public TextView mSearchResultsTextView;


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

            try {
                movieDbResults = getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
              return movieDbResults;
        }

        @Override
        protected void onPostExecute(String movieDbResults) {
            Log.d("onPostExecute",movieDbResults);
            showJsonDataView();
            Log.d("Post show JsonData","Post show JsonData");
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

        //Execute the MovieDbQuery by instantiating the MovieDbQueryTask
        Log.d("1 process",url.toString());
        new MovieDbQueryTask().execute(url);
        Log.d("2 process",url.toString());

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

}


