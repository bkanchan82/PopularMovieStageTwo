package com.example.android.popularmoviestagetwo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmoviestagetwo.app.PopularMovieAppController;
import com.example.android.popularmoviestagetwo.data.MovieContract;
import com.example.android.popularmoviestagetwo.utilities.NetworkUtils;
import com.example.android.popularmoviestagetwo.utilities.PopularMovie;
import com.example.android.popularmoviestagetwo.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MOVIE_ARRAY_LIST = "movie_array_list";

    //mDisplayErrorTV will display error message if there is any error in loading the movie list
    private TextView mDisplayErrorTV;

    //mDisplayMoviesRV is used to display the movie list on the entire screen
    private RecyclerView mDisplayMoviesRV;

    //loadingMovieListPb will show progress bar while it will load movie list from network
    private ProgressBar loadingMovieListPb;

    private MovieAdapter adapter;

    private static ArrayList<PopularMovie> mPopularMovies;

    private static final String[] mProjection = new String[]{
            MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoriteMovieEntry.COLUMN_TITLE,
            MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT,
            MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW,
            MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.FavoriteMovieEntry.COLUMN_POPULARITY,
            MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH,
            MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_LANGUAGE,

    };

    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_TITLE = 1;
    private static final int INDEX_MOVIE_VOTE_COUNT = 2;
    private static final int INDEX_ORIGINAL_TITLE = 3;
    private static final int INDEX_OVERVIEW = 4;
    private static final int INDEX_RELEASE_DATE = 5;
    private static final int INDEX_VOTE_AVERAGE = 6;
    private static final int INDEX_POPULARITY = 7;
    private static final int INDEX_POSTER_PATH = 8;
    private static final int INDEX_ORIGINAL_LANGUAGE = 9;


    private NetworkReceiver mNetworkReceiver;
    private IntentFilter mNetworkIntentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the java object from xml layout resource
        mDisplayErrorTV = findViewById(R.id.tv_display_error);
        mDisplayMoviesRV = findViewById(R.id.rv_display_movies);
        loadingMovieListPb = findViewById(R.id.pb_loading_movies);

        final int columns = getResources().getInteger(R.integer.gallery_column);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);

        mDisplayMoviesRV.setLayoutManager(gridLayoutManager);
        mDisplayMoviesRV.setHasFixedSize(true);

        adapter = new MovieAdapter(this, this);

        mDisplayMoviesRV.setAdapter(adapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState != null) {
            ArrayList<PopularMovie> data = savedInstanceState.getParcelableArrayList(MOVIE_ARRAY_LIST);
            if (data != null && data.size() > 0) {
                loadingMovieListPb.setVisibility(View.INVISIBLE);
                adapter.setMovieData(data);
                showMovies();
            } else {
                loadMovies(sharedPreferences);
            }
        } else {
            ConnectivityManager connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            Log.v(TAG,"Is Network connected "+isConnected);
            if(isConnected){
                loadMovies(sharedPreferences);
            }else{
                Toast.makeText(this,"No Network",Toast.LENGTH_SHORT).show();
                loadMoviesFromDatabase();
            }


        }


        mNetworkReceiver = new NetworkReceiver();
        mNetworkIntentFilter = new IntentFilter();
        mNetworkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

    }


    private String getShortingOrder(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(getString(R.string.pref_movie_shorting_key),
                getString(R.string.pref_default_movie_shorting_value));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(mNetworkReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int selectedId = item.getItemId();
        if (selectedId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMovies(SharedPreferences sharedPreferences) {
        showMovies();
        adapter.setMovieData(null);

        String shortingOrder = getShortingOrder(sharedPreferences);

        if (shortingOrder.equals(getString(R.string.pref_favorite_movie_value))) {
            loadMoviesFromDatabase();
        } else {

            try {
                String movieListString = NetworkUtils.buildUrl(shortingOrder, getString(R.string.THE_MOVIE_API_KEY)).toString();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        movieListString,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.v(TAG, "MOVIE Response : " + response.toString());
                                loadingMovieListPb.setVisibility(View.INVISIBLE);
                                try {
                                    ArrayList<PopularMovie> data = TheMovieDbJsonUtils.getSimpleMovieArrayListFromJson(response);
                                    if (data != null && data.size() > 0) {
                                        mPopularMovies = data;
                                        adapter.setMovieData(data);
                                        showMovies();
                                    } else {
                                        showErrorMessage(getString(R.string.error_message));
                                    }
                                } catch (JSONException jsonException) {
                                    jsonException.printStackTrace();
                                    showErrorMessage(getString(R.string.error_message));
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingMovieListPb.setVisibility(View.INVISIBLE);
                        showErrorMessage(getString(R.string.error_message));
                        error.printStackTrace();
                    }
                });
                PopularMovieAppController.getInstance().addRequestQueue(jsonObjectRequest);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
        }

    }

    private void loadMoviesFromDatabase(){
        new AsyncTask<Void,Void,ArrayList<PopularMovie>>(){
            @Override
            protected ArrayList<PopularMovie> doInBackground(Void... voids) {

                Cursor movieCursor =  getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                        mProjection,
                        null,
                        null,
                        null);
                if(movieCursor.moveToFirst()){
                    ArrayList<PopularMovie> popularMovieArrayList = new ArrayList<>();
                    do{
                        PopularMovie popularMovie =  new PopularMovie(movieCursor.getString(INDEX_TITLE),
                                movieCursor.getInt(INDEX_MOVIE_VOTE_COUNT),
                                movieCursor.getDouble(INDEX_POPULARITY),
                                movieCursor.getString(INDEX_POSTER_PATH),
                                movieCursor.getString(INDEX_OVERVIEW),
                                movieCursor.getString(INDEX_RELEASE_DATE),
                                movieCursor.getString(INDEX_ORIGINAL_TITLE),
                                movieCursor.getInt(INDEX_MOVIE_ID),
                                movieCursor.getDouble(INDEX_VOTE_AVERAGE),
                                movieCursor.getString(INDEX_ORIGINAL_LANGUAGE));
                        popularMovieArrayList.add(popularMovie);
                    }while (movieCursor.moveToNext());
                    movieCursor.close();
                    return popularMovieArrayList;
                }else{
                    movieCursor.close();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ArrayList<PopularMovie> popularMovieArrayList) {
                loadingMovieListPb.setVisibility(View.INVISIBLE);
                if(popularMovieArrayList==null) {
                    showErrorMessage(getString(R.string.favorite_movie_error_message));
                }else{
                    showMovies();
                    if(mPopularMovies!=null) {
                        mPopularMovies.clear();
                    }else{
                        mPopularMovies = new ArrayList<>();
                    }
                    mPopularMovies.addAll(popularMovieArrayList);
                    adapter.setMovieData(popularMovieArrayList);
                }

            }

        }.execute();

    }


    //This is called when we will have a valid data
    private void showMovies() {
        mDisplayErrorTV.setVisibility(View.INVISIBLE);
        mDisplayMoviesRV.setVisibility(View.VISIBLE);
    }


    //This is called when an error occurred in loading movie list
    private void showErrorMessage(String errorMessage) {
        mDisplayErrorTV.setVisibility(View.VISIBLE);
        mDisplayErrorTV.setText(errorMessage);
        mDisplayMoviesRV.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMovieItemClickListener(PopularMovie movieObject) {

        Context context = this;
        Class staticClass = MovieDetailActivity.class;
        Intent intent = new Intent(context, staticClass);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_DATA, movieObject);
        startActivity(intent);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_movie_shorting_key))) {
            String shortBy = sharedPreferences.getString(getString(R.string.pref_movie_shorting_key), getString(R.string.pref_default_movie_shorting_value));
            Log.d(TAG, "On Shared Preference Change Listener short by : " + shortBy);

            loadMovies(sharedPreferences);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_ARRAY_LIST, mPopularMovies);
    }

    @Override
    protected void onStart() {
        registerReceiver(mNetworkReceiver, mNetworkIntentFilter);
        super.onStart();
    }

    private class NetworkReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            NetworkInfo info = extras
                    .getParcelable("networkInfo");

            NetworkInfo.State state = info.getState();
            Log.d("TEST Internet", info.toString() + " "
                    + state.toString());

            if (state == NetworkInfo.State.CONNECTED) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                loadMovies(sharedPreferences);
            }
        }
    }

}
