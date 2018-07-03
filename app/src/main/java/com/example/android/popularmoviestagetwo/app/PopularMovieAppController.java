package com.example.android.popularmoviestagetwo.app;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created on 22-01-2018.
 */

public class PopularMovieAppController extends Application {

    private static final String TAG = PopularMovieAppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static PopularMovieAppController mInstance;

    public static synchronized PopularMovieAppController getInstance(){
        return mInstance;
    }

    private RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
