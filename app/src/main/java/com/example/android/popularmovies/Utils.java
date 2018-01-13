package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import android.net.Uri;
import android.util.Log;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by dneum on 1/11/2018.
 */

public class Utils {

    final static String BASE_URL =
            "http://api.themoviedb.org/3/discover/movie/";
    final static String KEY_ID1 = "sort_by";
    final static String PARAM_POPULAR = "popularity.desc";
    final static String KEY_ID2 = "api_key";
    final static String PARAM_API_KEY = "78891bb3d2c1b1ee69109f6f46b23ead";


    public void Utils() {

    }

    public Boolean isNetworkAvailable(Context context) {
        Boolean Network_is_available = TRUE;
        Boolean Network_is_not_available = FALSE;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            return Network_is_available;
        }
        else
        {
            return Network_is_not_available;
        }

    }


    public static URL buildUrl(){
        URL url = null;
        Uri.Builder uriBuilder = null;
        Uri uri;

        uri = Uri.parse(BASE_URL);
        uriBuilder = uri.buildUpon();
        uriBuilder.appendQueryParameter(KEY_ID1, PARAM_POPULAR);
        uriBuilder.appendQueryParameter(KEY_ID2, PARAM_API_KEY);
        uri = uriBuilder.build();

        String local = uriBuilder.toString();

        try{
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("build url", local);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

