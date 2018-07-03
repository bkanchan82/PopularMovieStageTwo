package com.example.android.popularmoviestagetwo.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 25-01-2018.
 */

public class MovieReview implements Parcelable {

    private final String user;
    private final String review;

    public MovieReview(String user, String review) {
        this.user = user;
        this.review = review;
    }

    private MovieReview(Parcel in) {
        user = in.readString();
        review = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(review);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public String getUser() {
        return user;
    }

    public String getReview() {
        return review;
    }

}
