package com.example.android.popularmoviestagetwo.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.URL;

/**
 * Created on 19-12-2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String API_KEY_PARAM = "api_key";

    public static final String TRAILER_REQUEST = "videos";
    public static final String REVIEW_REQUEST = "reviews";


    public static URL buildUrl(String queryParam, String apiKey) {

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(queryParam)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        Log.v(TAG, "Build url : " + (url != null ? url.toString() : null));
        return url;
    }

    public static URL buildUrl(String queryParam, String apiKey,String requestType) {

        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(queryParam)
                .appendPath(requestType)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        Log.v(TAG, "Build url : " + (url != null ? url.toString() : null));
        return url;
    }



}
