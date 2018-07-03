package com.example.android.popularmoviestagetwo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created on 23-01-2018.
 */

public class MovieProvider extends ContentProvider {

    private static final int CODE_FAVORITE_MOVIE = 199;
    private static final int CODE_FAVORITE_MOVIE_WITH_ID = 200;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper movieDbHelper;

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.FAVORITE_MOVIE_PATH,CODE_FAVORITE_MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.FAVORITE_MOVIE_PATH+"/#",CODE_FAVORITE_MOVIE_WITH_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return false;
    }

    /*
    * Handles Query requests from clients. We will use this method in popular movie to query all of your
     * favorite movie data.
     *
     * @param uri   The uri to query data
     * @param projection    The list of columns to put into the cursor. If null, then all columns are included
     * @param selection     A selection criteria to apply when filtering rows. If null then all rows are included
     * @param selectionArgs You may include ?s in selection, which will be replaced by the value from selection args,
     *                      in order that the appear in the selection
      * @param shortOrder   How the rows in the cursor should be shorted
      * @return A cursor containing the result of the query. In our implementation*/

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_FAVORITE_MOVIE:
                return db.query(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case CODE_FAVORITE_MOVIE_WITH_ID:

                String movieId = uri.getLastPathSegment();
                String mSelection = MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" = ?";
                String[] mSelectionArgs = new String[]{movieId};

                return db.query(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                default:
                    throw new UnsupportedOperationException("Unknown uri");

        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_FAVORITE_MOVIE:
                long id =  db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        null,
                        contentValues);
                return MovieContract.FavoriteMovieEntry.CONTENT_URI.
                        buildUpon().
                        appendPath(String.valueOf(id)).
                        build();
            default:
                throw new UnsupportedOperationException("Unknown uri");
        }

    }


    @Override
    public int delete(@NonNull Uri uri,
                      String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_FAVORITE_MOVIE_WITH_ID:

                String movieId = uri.getLastPathSegment();
                String mSelection = MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" = ?";
                String[] mSelectionArgs = new String[]{movieId};

                return db.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri");

        }
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      String selection,
                      String[] selectionArgs) {

        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_FAVORITE_MOVIE_WITH_ID:

                String movieId = uri.getLastPathSegment();
                String mSelection = MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" = ?";
                String[] mSelectionArgs = new String[]{movieId};

                return db.update(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        contentValues,
                        mSelection,
                        mSelectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri");

        }
    }
}
