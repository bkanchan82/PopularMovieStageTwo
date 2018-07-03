package com.example.android.popularmoviestagetwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviestagetwo.utilities.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created on 25-01-2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private ArrayList<MovieTrailer> movieTrailers;
    private Context context = null;
    private final TrailerAdapterOnItemClickListener TrailerAdapterOnItemClickListener;

    public TrailerAdapter(Context ctx, TrailerAdapterOnItemClickListener trailerAdapterOnItemClickListener){
        this.TrailerAdapterOnItemClickListener = trailerAdapterOnItemClickListener;
        context = ctx;
    }


    public interface TrailerAdapterOnItemClickListener {
        void onMovieItemClickListener(MovieTrailer trailerObject);
    }

    @Override
    public TrailerAdapter.TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutId = R.layout.movie_trailer_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutId,parent, false);

        return new TrailerAdapter.TrailerHolder(view);

    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {

        MovieTrailer trailerObject = movieTrailers.get(position);

        Picasso.with(context).load(trailerObject.getVideoThumbnailUrl()).into(holder.trailerPosterImageView);
        holder.trailerTypeTextView.setText(trailerObject.getType());
        holder.trailerTitleTextView.setText(trailerObject.getName());

    }

    @Override
    public int getItemCount() {
        if(movieTrailers !=null) {
            return movieTrailers.size();
        }else{
            return 0;
        }
    }

    public void setMovieTrailerData(ArrayList<MovieTrailer> movieData){
        this.movieTrailers = movieData;
        notifyDataSetChanged();
    }

    final class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView trailerPosterImageView;
        final TextView trailerTypeTextView;
        final TextView trailerTitleTextView;

        public TrailerHolder(View itemView) {
            super(itemView);
            trailerPosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            trailerTypeTextView = itemView.findViewById(R.id.trailer_type_tv);
            trailerTitleTextView = itemView.findViewById(R.id.trailer_title_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedItemPosition = getAdapterPosition();
            TrailerAdapterOnItemClickListener.onMovieItemClickListener(movieTrailers.get(clickedItemPosition));
        }
    }

}
