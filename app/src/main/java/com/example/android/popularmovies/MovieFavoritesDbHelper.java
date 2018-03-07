package com.example.android.popularmovies;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.popularmovies.MovieFavoritesDb.SQL_CREATE_DB;
import static com.example.android.popularmovies.MovieFavoritesDb.SQL_DELETE_DB;


/**
 * Created by dneum on 3/2/2018.
 */ 

public class MovieFavoritesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MovieFavorites.db";

    public MovieFavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(this.getDatabaseName(), "helper - here i am");
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(this.getDatabaseName(), "create_db " + SQL_CREATE_DB);
        try {
            db.execSQL(SQL_CREATE_DB);
        } catch(SQLException exeption) {
            Log.d(getDatabaseName(),exeption.getMessage());
        }
    }

    public void onDestroy(SQLiteDatabase db) {
        Log.d("onDestroy", getDatabaseName());
        db.execSQL(SQL_DELETE_DB);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_DB);
        onCreate(db);
    }
}
