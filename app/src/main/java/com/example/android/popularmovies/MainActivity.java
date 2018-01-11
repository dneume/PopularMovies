package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity implements ImageAdapter.ItemClickListener {

    private ImageAdapter adapter;
    private Boolean networkIsAvailable = FALSE;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int numberOfColumns = 0;

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


        // is the network available
        utils = new Utils();
        networkIsAvailable = utils.isNetworkAvailable(context);

        recyclerView = findViewById(R.id.rvImages);
        recyclerView_LayoutManager = new GridLayoutManager(context, numberOfColumns);
        if (recyclerView_LayoutManager != null) {
            recyclerView.setLayoutManager(recyclerView_LayoutManager);
        }

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


