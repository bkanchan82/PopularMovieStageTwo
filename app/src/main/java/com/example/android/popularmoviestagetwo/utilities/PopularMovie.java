package com.example.android.popularmoviestagetwo.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 21-12-2017.
 */

public class PopularMovie implements Parcelable{

    private final int movieId;
    private final String title;
    private final int voteCount;
    private final double voteAverage;
    private final double popularity;
    private final String posterPath;
    private final String overview;
    private final String releaseDate;
    private final String originalTitle;
    private final String originalLanguage;


    public PopularMovie(String title,
                        int voteCount,
                        double popularity,
                        String posterPath,
                        String overview,
                        String releaseDate,
                        String originalTitle,
                        int movieId,
                        double voteAverage,
                        String originalLanguage) {
        this.title = title;
        this.voteCount = voteCount;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.originalTitle = originalTitle;
        this.movieId = movieId;
        this.voteAverage = voteAverage;
        this.originalLanguage = originalLanguage;
    }

    private PopularMovie(Parcel in) {
        movieId = in.readInt();
        title = in.readString();
        voteCount = in.readInt();
        voteAverage = in.readDouble();
        popularity = in.readDouble();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        originalTitle = in.readString();
        originalLanguage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(title);
        dest.writeInt(voteCount);
        dest.writeDouble(voteAverage);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(originalTitle);
        dest.writeString(originalLanguage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PopularMovie> CREATOR = new Creator<PopularMovie>() {
        @Override
        public PopularMovie createFromParcel(Parcel in) {
            return new PopularMovie(in);
        }

        @Override
        public PopularMovie[] newArray(int size) {
            return new PopularMovie[size];
        }
    };

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPosterPath(String posterSize) {
        return TheMovieDbJsonUtils.POSTER_BASE_URL+posterSize+this.posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

}
