package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import static com.example.android.popularmovies.MainActivity.db;

public class ContentProviderMovieApp extends ContentProvider {


    public ContentProviderMovieApp() {
        Log.d("content_provider","instantiate");
    }

    @Override
    public int delete(Uri delete_uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        String buffer = delete_uri.getPath();
        //remove the forward slash
        String tableName = buffer.substring(1);
        int local_delete_id = db.delete(tableName,selection, selectionArgs);
        return local_delete_id;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri insert_uri, ContentValues values) {
    // TODO: Implement this to handle requests to insert a new row.
        Uri uri = null;
        String buffer = insert_uri.getPath();
        //remove the forward slash
        String tableName = buffer.substring(1);
        long newRowId = db.insert(tableName, null, values);
        if(newRowId != -1)
            uri = Uri.withAppendedPath(insert_uri, Long.toString(newRowId));
        return uri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Log.d("content_provider","onCreate");
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        //projection = columnNames,
        // selection = where clause - if null all rows are include
        // selectionArgs = values to replace ? in the where clause
        // sort_order = null
        Cursor cursor;
        String buffer = uri.getPath();
        //remove the forward slash
        String tableName = buffer.substring(1);
        cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
