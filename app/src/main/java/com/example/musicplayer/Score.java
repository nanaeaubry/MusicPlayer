package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Score implements Parcelable {
	public Release release;
	public Artist artist;
	public Song song;

	public Score(Parcel in) {
		super();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {
		public Score createFromParcel(Parcel in) {
			return new Score(in);
		}

		public Score[] newArray(int size) {
			return new Score[size];
		}
	};

	public void readFromParcel(Parcel in) {
		release = in.readParcelable(Release.class.getClassLoader());
		song = in.readParcelable(Song.class.getClassLoader());
		artist = in.readParcelable(Artist.class.getClassLoader());

	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags){
		dest.writeParcelable(release, flags);
		dest.writeParcelable(song, flags);
		dest.writeParcelable(artist, flags);

	}

}
