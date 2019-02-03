package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable{
	String name;

	public Artist(Parcel in) {
		super();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
		public Artist createFromParcel(Parcel in) {
			return new Artist(in);
		}

		public Artist[] newArray(int size) {
			return new Artist[size];
		}
	};

	public void readFromParcel(Parcel in) {
		name = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(name);

	}
}
