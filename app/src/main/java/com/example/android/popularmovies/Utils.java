package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;



import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
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

    private final static String BASE_URL =
            "http://api.themoviedb.org/3/movie/";
    private final static String PARAM_POPULAR = "popular";
    private final static String PARAM_HIGHEST = "top_rated";
    private final static String KEY_API = "api_key";

    private final static String KEY_LANG = "LANGUAGE";
    private final static String PARAM_LANG = "en-US";
    private final static String KEY_PAGE = "page";
    private final static String PARAM_PAGE = "1";

    private final static String BASE_IMAGE_URL =
            "http://image.tmdb.org/t/p";
    private final static String IMAGE_SIZE = "w185";
    private final static String PARAM_REVIEWS = "reviews";
    private final static String PARAM_VIDEOS = "videos";

    // https://api.themoviedb.org/3/movie/popular?api_key=<<api_key>>&language=en-US&page=1

    public void utils() {

    }

    public Boolean isNetworkAvailable(Context context) {
        Boolean Network_is_available = TRUE;
        Boolean Network_is_not_available = FALSE;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            return Network_is_available;
        } else {
            return Network_is_not_available;
        }

    }

    public static URL  buildUrl(String sort_order, Context context) {
        URL url = null;
        Uri.Builder uriBuilder = null;
        Uri uri;

        uri = Uri.parse(BASE_URL);
        uriBuilder = uri.buildUpon();

        if(sort_order == "Popular")
            uriBuilder.appendPath(PARAM_POPULAR);
        else
            uriBuilder.appendPath(PARAM_HIGHEST);
        uriBuilder.appendQueryParameter(KEY_API, context.getResources().getString(R.string.movie_api_key_dtn));
        uriBuilder.appendQueryParameter(KEY_LANG, PARAM_LANG);
        uriBuilder.appendQueryParameter(KEY_PAGE, PARAM_PAGE);
        uri = uriBuilder.build();

        String local = uriBuilder.toString();

        Log.d("buildUrl: ", local);

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static URL buildUrlForDetail(String movie_id, Context context) {
        URL url = null;
        Uri.Builder uriBuilder = null;
        Uri uri;

        uri = Uri.parse(BASE_URL);
        uriBuilder = uri.buildUpon();
        uriBuilder.appendPath(movie_id);
        uriBuilder.appendQueryParameter(KEY_API, context.getResources().getString(R.string.movie_api_key_dtn));
        uri = uriBuilder.build();

        String local = uriBuilder.toString();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("Return from detail url", local);
        return url;
    }

    public static URL buildUrlForReviews(String movie_id, Context context) {
        URL url = null;
        Uri.Builder uriBuilder = null;
        Uri uri;

        uri = Uri.parse(BASE_URL);
        uriBuilder = uri.buildUpon();
        uriBuilder.appendPath(movie_id);
        uriBuilder.appendPath(PARAM_REVIEWS);
        uriBuilder.appendQueryParameter(KEY_API, context.getResources().getString(R.string.movie_api_key_dtn));
        uri = uriBuilder.build();

//        String local = uriBuilder.toString();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForVideos(String movie_id, Context context) {
        URL url = null;
        Uri.Builder uriBuilder = null;
        Uri uri;

        uri = Uri.parse(BASE_URL);
        uriBuilder = uri.buildUpon();
        uriBuilder.appendPath(movie_id);
        uriBuilder.appendPath(PARAM_VIDEOS);
        uriBuilder.appendQueryParameter(KEY_API, context.getResources().getString(R.string.movie_api_key_dtn));
        uri = uriBuilder.build();

//        String local = uriBuilder.toString();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlForThumbNailFile(String movie_thumbnail_path) {

        URL url = null;
        Uri.Builder uriBuilder = null;
        Uri uri;

        uri = Uri.parse(BASE_IMAGE_URL);
        uriBuilder = uri.buildUpon();
        uriBuilder.appendPath(IMAGE_SIZE);
        String local = movie_thumbnail_path.substring(1);
        uriBuilder.appendPath(local);
        uri = uriBuilder.build();
        Log.d("path to jpg",uriBuilder.toString());

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    // read the http input stream into a buffer so that the buffer can be used by multiple scanners
    public static String readStream(InputStream is) {
        try {
            int backslash;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {

                // trapping the newline carriage return in an input stream
                // and replacing with a space, also
                // forward slashes in general are being bypassed
                if (i == 92) {
                    backslash = 92;
                    try {
                        i = is.read();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (i == 114 || i == 110) {
                        i = 32;
                        backslash = 0;
                    }
                }
                bo.write(i);
                i = is.read();
            }
            return bo.toString();

        } catch (IOException e) {
            Log.d("Exception_msg",e.getMessage());
            return "";
        }
    }
    // a generic scan function so that multiple delimiters can be pulled from a string
    public static String scanInput(String delimiter, String input) {
        int index = 0;
        String local_id = null;
        Scanner scanner = new Scanner(input);
        scanner.useDelimiter(delimiter);
        boolean hasInput = scanner.hasNext();
        input = scanner.next();
        input = scanner.next();
        if(hasInput) {
            //this will get the value of a \" delimited string
            index = input.indexOf("\",");
            if (index > 0) {
                local_id = input.substring(0, index);
            } else {
                // this will get the value of a numeric/integer value
                index = input.indexOf(",");
                if (index > 0) {
                    local_id = input.substring(0, index);
                }
            }
        }
        return local_id;
    }
}
