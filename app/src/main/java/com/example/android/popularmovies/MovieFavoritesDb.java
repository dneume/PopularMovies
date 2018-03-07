package com.example.android.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dneum on 3/2/2018.
 */

public class MovieFavoritesDb {

    public static final String AUTHORITY = "com.examples.android.popularmovies";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,"MovieFavoriteTbl");

    // to prevent someone from accidentally instantiating the class
    private MovieFavoritesDb() {}

    public static class MovieFavoritesColumns implements BaseColumns {
        // String _Count defined in BaseColumns
        // String _ID defined in BaseColumns
        public static final String TABLE_NAME = "MovieFavoriteTbl";
        public static final String COLUMN_MOVIE_ID = "MovieId";
        public static final String COLUMN_MOVIE_ORIGINAL_TITLE = "MovieOriginalTitle";
        public static final String COLUMN_THUMBNAIL_PATH = "MovieThumbNailPath";
    }

    public static final String SQL_CREATE_DB =
            "CREATE TABLE " + MovieFavoritesColumns.TABLE_NAME + " (" +
                    MovieFavoritesColumns.COLUMN_MOVIE_ID + " TEXT," +
                    MovieFavoritesColumns.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT," +
                    MovieFavoritesColumns.COLUMN_THUMBNAIL_PATH + " TEXT)";

    public static final String SQL_DELETE_DB =
            "DROP TABLE IF EXISTS " + MovieFavoritesColumns.TABLE_NAME;

    public static final String SQL_QUERY_FAVORITES =
            "Select " + MovieFavoritesColumns.COLUMN_MOVIE_ID + ", " +
                    MovieFavoritesColumns.COLUMN_MOVIE_ORIGINAL_TITLE + ", " +
                    MovieFavoritesColumns.COLUMN_THUMBNAIL_PATH + " From " +
                    MovieFavoritesColumns.TABLE_NAME;

}
