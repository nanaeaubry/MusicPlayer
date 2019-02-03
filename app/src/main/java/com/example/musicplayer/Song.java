package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable{
	public String id;
	public String title;

	public Song(Parcel in) {
		super();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
		public Song createFromParcel(Parcel in) {
			return new Song(in);
		}

		public Song[] newArray(int size) {
			return new Song[size];
		}
	};

	public void readFromParcel(Parcel in) {
		id = in.readString();
		title = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(id);
		dest.writeString(title);

	}
}
