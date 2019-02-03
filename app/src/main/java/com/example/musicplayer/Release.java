package com.example.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class Release implements Parcelable{
	int id;
	String name;

	public Release(Parcel in) {
		super();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Release> CREATOR = new Parcelable.Creator<Release>() {
		public Release createFromParcel(Parcel in) {
			return new Release(in);
		}

		public Release[] newArray(int size) {
			return new Release[size];
		}
	};

	public void readFromParcel(Parcel in) {
		id = in.readInt();
		name = in.readString();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags){
		dest.writeInt(id);
		dest.writeString(name);

	}
}
