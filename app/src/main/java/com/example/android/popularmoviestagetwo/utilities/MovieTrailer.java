package com.example.android.popularmoviestagetwo.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 25-01-2018.
 */

public class MovieTrailer implements Parcelable {

    private static final String VIDEO_THUMBNAIL_PREFIX = "http://img.youtube.com/vi/";

    private final String id;
    private final String key;
    private final String name;
    private final String type;

    public MovieTrailer(String id, String key, String name, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.type = type;
    }

    private MovieTrailer(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getVideoThumbnailUrl() {
        return VIDEO_THUMBNAIL_PREFIX + key + "/0.jpg";
    }

    public String getYoutubeVideoUrl() {
        return "http://www.youtube.com/watch?v=" + key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
