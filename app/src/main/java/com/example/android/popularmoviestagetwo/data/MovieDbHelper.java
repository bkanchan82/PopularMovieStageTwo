package com.example.android.popularmoviestagetwo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created on 23-01-2018.
 */

class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_FAVORITE_MOVIE = "CREATE TABLE "+MovieContract.FavoriteMovieEntry.TABLE_NAME+" ("+
                MovieContract.FavoriteMovieEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID+" INTEGER UNIQUE NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT+" INTEGER NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE+" REAL NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_TITLE+" TEXT NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_POPULARITY+" REAL NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH+" TEXT NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE+" TEXT NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_LANGUAGE+" TEXT NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW+" TEXT NOT NULL, "+
                MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE+" TEXT NOT NULL"+
                ");";

        sqLiteDatabase.execSQL(CREATE_FAVORITE_MOVIE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MovieContract.FavoriteMovieEntry.TABLE_NAME+";");
        onCreate(sqLiteDatabase);
    }
}
