package com.example.musicplayer;

import java.util.ArrayList;

public class Session {
	public static User currentUser;
	public static ArrayList<Score> scores;

	public static ArrayList<Playlist> getPlaylists(){
		return Session.currentUser.playlists;
	}

	public static String[] getPlaylistNames(){
		String[] items = new String[Session.getPlaylists().size()];
		for (int i = 0; i < Session.getPlaylists().size(); i++) {
			items[i] = Session.getPlaylists().get(i).name;
		}
return items;
	}
}
