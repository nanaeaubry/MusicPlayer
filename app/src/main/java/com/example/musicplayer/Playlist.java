package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Playlist implements Parcelable {
	String name;
	ArrayList<String> scoreIds;

	public Playlist(Parcel in) {
		super();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
		public Playlist createFromParcel(Parcel in) {
			return new Playlist(in);
		}

		public Playlist[] newArray(int size) {
			return new Playlist[size];
		}
	};

	public void readFromParcel(Parcel in) {
		name = in.readString();
		scoreIds = (ArrayList<String>) in.readSerializable();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(name);
		dest.writeSerializable(scoreIds);

	}

}