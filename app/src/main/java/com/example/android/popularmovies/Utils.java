package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by dneum on 1/11/2018.
 */

public class Utils {

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
}
