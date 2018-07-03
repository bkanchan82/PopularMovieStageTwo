package com.example.android.popularmoviestagetwo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.popularmoviestagetwo.app.PopularMovieAppController;
import com.example.android.popularmoviestagetwo.data.MovieContract;
import com.example.android.popularmoviestagetwo.utilities.MovieReview;
import com.example.android.popularmoviestagetwo.utilities.MovieTrailer;
import com.example.android.popularmoviestagetwo.utilities.NetworkUtils;
import com.example.android.popularmoviestagetwo.utilities.PopularMovie;
import com.example.android.popularmoviestagetwo.utilities.TheMovieDbJsonUtils;
import com.example.android.popularmoviestagetwo.utilities.VerticalSpaceItemDecoration;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnItemClickListener {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_DATA = "extra_movie_object";

    private static final String TRAILER_ARRAY_LIST = "trailer_array_list";
    private static final String REVIEW_ARRAY_LIST = "review_array_list";

    private PopularMovie mPopularMovie;

    private ImageView mMarkAsFavorite;

    private ProgressBar mTrailerLoadingPb;
    private TextView mTrailerLoadingErrorTv;
    private RecyclerView mTrailerRecyclerView;

    private ProgressBar mReviewLoadingPb;
    private TextView mReviewLoadingErrorTv;
    private RecyclerView mReviewRecyclerView;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private static ArrayList<MovieTrailer> mMoviesTrailer;
    private static ArrayList<MovieReview> mMoviesReview;
    private static final int VERTICAL_ITEM_SPACE = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if(getActionBar()!=null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView mPosterImageView = findViewById(R.id.iv_movie_poster);
        mMarkAsFavorite = findViewById(R.id.markAsFavorite);
        TextView mTitleTextView = findViewById(R.id.tv_title);
        TextView mRatingTextView = findViewById(R.id.tv_user_rating);
        TextView mReleaseDateTextView = findViewById(R.id.tv_release_date);
        TextView mOverviewTextView = findViewById(R.id.tv_overview);

        mTrailerLoadingPb =  findViewById(R.id.trailer_loading_pb);
        mTrailerLoadingErrorTv =  findViewById(R.id.trailer_loading_error_tv);
        mTrailerRecyclerView = findViewById(R.id.movie_trailer_rv);
        mReviewLoadingPb = findViewById(R.id.review_loading_pb);
        mReviewLoadingErrorTv = findViewById(R.id.review_loading_error_tv);
        mReviewRecyclerView = findViewById(R.id.movie_review_rv);

        LinearLayoutManager trailerLinearLM =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mTrailerRecyclerView.setLayoutManager(trailerLinearLM);

        //add ItemDecoration
        mTrailerRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        mTrailerRecyclerView.setHasFixedSize(true);

        mTrailerAdapter = new TrailerAdapter(this, this);

        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        LinearLayoutManager reviewLinearLM =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        mReviewRecyclerView.setLayoutManager(reviewLinearLM);

        //add ItemDecoration
        mReviewRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        mReviewRecyclerView.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();

        mReviewRecyclerView.setAdapter(mReviewAdapter);


        Intent intentThatInvokeTheActivity = getIntent();
        if (intentThatInvokeTheActivity.hasExtra(EXTRA_MOVIE_DATA)) {
            mPopularMovie = intentThatInvokeTheActivity.getParcelableExtra(EXTRA_MOVIE_DATA);

            if (mPopularMovie != null) {
                Picasso.with(this).load(mPopularMovie.getPosterPath(TheMovieDbJsonUtils.POSTER_SIZE)).into(mPosterImageView);
                if (isFavorite()) {
                    mMarkAsFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                } else {
                    mMarkAsFavorite.setImageResource(R.drawable.ic_favorite_gray_24dp);
                }
                mTitleTextView.setText(mPopularMovie.getOriginalTitle());
                mRatingTextView.setText(String.valueOf(mPopularMovie.getVoteCount()));
                mReleaseDateTextView.setText(getString(R.string.releasing_on, mPopularMovie.getReleaseDate()));
                mOverviewTextView.setText(mPopularMovie.getOverview());
            }

        }

        if (savedInstanceState != null) {
            ArrayList<MovieTrailer> data = savedInstanceState.getParcelableArrayList(TRAILER_ARRAY_LIST);
            if (data != null && data.size() > 0) {
                mTrailerLoadingPb.setVisibility(View.INVISIBLE);
                mTrailerAdapter.setMovieTrailerData(data);
                showTrailer();
            } else {
                loadTrailer();
            }
            ArrayList<MovieReview> reviewData = savedInstanceState.getParcelableArrayList(REVIEW_ARRAY_LIST);
            if (reviewData != null && reviewData.size() > 0) {
                mReviewLoadingPb.setVisibility(View.INVISIBLE);
                mReviewAdapter.setMovieReviewData(reviewData);
                showReview();
            } else {
                loadReviews();
            }
        } else {
            loadTrailer();
            loadReviews();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuId = item.getItemId();

        switch (selectedMenuId) {
            case R.id.action_share:

                if(mMoviesTrailer!=null && mMoviesTrailer.size()>0){
                    Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                            .setType("text/plain")
                            .setChooserTitle(getString(R.string.share_movie_title))
                            .setText(mMoviesTrailer.get(0).getYoutubeVideoUrl())
                            .getIntent();

                    if(shareIntent.resolveActivity(getPackageManager())!=null){
                        startActivity(shareIntent);
                    }

                }else{
                    Toast.makeText(this,"There is no trailer",Toast.LENGTH_SHORT).show();
                }

                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isFavorite() {
        Cursor movieCursor = getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                new String[]{MovieContract.FavoriteMovieEntry._ID},
                MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(mPopularMovie.getMovieId())},
                null);

        if (movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            movieCursor.close();
            return false;
        }
    }

    public void markAsFavorite(View view) {
        if (isFavorite()) {
            Uri uriTODeleteRow = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mPopularMovie.getMovieId())).build();
            int deletedRowCount = getContentResolver().delete(uriTODeleteRow,
                    null,
                    null);
            if (deletedRowCount > 0) {
                mMarkAsFavorite.setImageResource(R.drawable.ic_favorite_gray_24dp);
            }
            Log.v(TAG, "Deleted Row Count " + deletedRowCount);
        } else {
            if (mPopularMovie != null) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, mPopularMovie.getMovieId());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_VOTE_COUNT, mPopularMovie.getVoteCount());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, mPopularMovie.getVoteAverage());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, mPopularMovie.getTitle());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_POPULARITY, mPopularMovie.getPopularity());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, mPopularMovie.getPosterPath());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_LANGUAGE, mPopularMovie.getOriginalLanguage());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE, mPopularMovie.getOriginalTitle());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, mPopularMovie.getOverview());
                contentValues.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mPopularMovie.getReleaseDate());
                try {
                    Uri uri = getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                            contentValues);
                    Log.v(TAG, "Returned uri " + uri.toString() + " \\n And Returned id " + uri.getLastPathSegment());
                    mMarkAsFavorite.setImageResource(R.drawable.ic_favorite_red_24dp);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TRAILER_ARRAY_LIST, mMoviesTrailer);
        outState.putParcelableArrayList(REVIEW_ARRAY_LIST, mMoviesReview);
    }

    private void loadTrailer() {
        showTrailer();
        mTrailerAdapter.setMovieTrailerData(null);

        try {
            String trailerListUrl = NetworkUtils.buildUrl(String.valueOf(mPopularMovie.getMovieId()), getString(R.string.THE_MOVIE_API_KEY), NetworkUtils.TRAILER_REQUEST).toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    trailerListUrl,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v(TAG, "Trailer Response : " + response.toString());
                            mTrailerLoadingPb.setVisibility(View.INVISIBLE);
                            try {
                                ArrayList<MovieTrailer> data = TheMovieDbJsonUtils.getSimpleMovieTrailerArrayListFromJson(response);
                                if (data != null && data.size() > 0) {
                                    mMoviesTrailer = data;
                                    mTrailerAdapter.setMovieTrailerData(data);
                                    showTrailer();
                                } else {
                                    showTrailerErrorMessage();
                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                                showTrailerErrorMessage();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mTrailerLoadingPb.setVisibility(View.INVISIBLE);
                    showTrailerErrorMessage();
                    error.printStackTrace();
                }
            });
            PopularMovieAppController.getInstance().addRequestQueue(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

        }

    }


    //This is called when we will have a valid data
    private void showTrailer() {
        mTrailerLoadingErrorTv.setVisibility(View.INVISIBLE);
        mTrailerRecyclerView.setVisibility(View.VISIBLE);
    }


    //This is called when an error occurred in loading movie list
    private void showTrailerErrorMessage() {
        mTrailerLoadingErrorTv.setVisibility(View.VISIBLE);
        mTrailerRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void loadReviews() {
        showReview();
        mTrailerAdapter.setMovieTrailerData(null);

        try {
            String reviewListUrl = NetworkUtils.buildUrl(String.valueOf(mPopularMovie.getMovieId()), getString(R.string.THE_MOVIE_API_KEY), NetworkUtils.REVIEW_REQUEST).toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    reviewListUrl,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v(TAG, "Review Response : " + response.toString());
                            mReviewLoadingPb.setVisibility(View.INVISIBLE);
                            try {
                                ArrayList<MovieReview> data = TheMovieDbJsonUtils.getSimpleMovieReviewArrayListFromJson(response);
                                if (data != null && data.size() > 0) {
                                    mMoviesReview = data;
                                    mReviewAdapter.setMovieReviewData(data);
                                    showReview();
                                } else {
                                    showReviewErrorMessage();
                                }
                            } catch (JSONException jsonException) {
                                jsonException.printStackTrace();
                                showReviewErrorMessage();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mReviewLoadingPb.setVisibility(View.INVISIBLE);
                    showReviewErrorMessage();
                    error.printStackTrace();
                }
            });
            PopularMovieAppController.getInstance().addRequestQueue(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

        }

    }


    //This is called when we will have a valid data
    private void showReview() {
        mReviewLoadingErrorTv.setVisibility(View.INVISIBLE);
        mReviewRecyclerView.setVisibility(View.VISIBLE);
    }


    //This is called when an error occurred in loading movie list
    private void showReviewErrorMessage() {
        mReviewLoadingErrorTv.setVisibility(View.VISIBLE);
        mReviewRecyclerView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onMovieItemClickListener(MovieTrailer trailerObject) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(trailerObject.getYoutubeVideoUrl()));
        Log.v(TAG,trailerObject.getYoutubeVideoUrl());
        startActivity(intent);

    }
}
