package com.example.android.popularmoviestagetwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviestagetwo.utilities.PopularMovie;
import com.example.android.popularmoviestagetwo.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created on 22-01-2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder>{

    private ArrayList<PopularMovie> movieData;
    private Context context = null;
    private final MovieAdapterOnItemClickListener movieAdapterOnItemClickListener;

    public MovieAdapter(Context ctx, MovieAdapterOnItemClickListener movieAdapterOnItemClickListener){
        this.movieAdapterOnItemClickListener = movieAdapterOnItemClickListener;
        context = ctx;
    }


    public interface MovieAdapterOnItemClickListener {
        void onMovieItemClickListener(PopularMovie movieObject);
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutId,parent, false);

        return new MovieHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {

        PopularMovie movieObject = movieData.get(position);
        Picasso.with(context).load(movieObject.getPosterPath(TheMovieDbJsonUtils.POSTER_LOGO_SIZE)).into(holder.posterImageView);
     /*   Picasso.with(context)
                .load(movieObject.getPosterPath())
                .resize(50, 50)
                .centerCrop()
                .into(holder.posterImageView);*/
        holder.movieTitleTextView.setText(movieObject.getTitle());

    }

    @Override
    public int getItemCount() {
        if(movieData!=null) {
            return movieData.size();
        }else{
            return 0;
        }
    }

    public void setMovieData(ArrayList<PopularMovie> movieData){
        this.movieData = movieData;
        notifyDataSetChanged();
    }

    final class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView posterImageView;
        final TextView movieTitleTextView;

        public MovieHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.iv_display_poster);
            movieTitleTextView = itemView.findViewById(R.id.tv_display_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedItemPosition = getAdapterPosition();
            movieAdapterOnItemClickListener.onMovieItemClickListener(movieData.get(clickedItemPosition));
        }
    }

}
