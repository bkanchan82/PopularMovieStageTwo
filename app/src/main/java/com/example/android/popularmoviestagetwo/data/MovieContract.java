package com.example.android.popularmoviestagetwo.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created on 23-01-2018.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviestagetwo";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String FAVORITE_MOVIE_PATH = "favorite_movie";


    public static final class FavoriteMovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITE_MOVIE_PATH).build();

        public static final String TABLE_NAME = "favorite_movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "movie_vote_count";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_POPULARITY = "movie_popularity";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }

}
