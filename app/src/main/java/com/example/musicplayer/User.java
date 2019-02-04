package com.example.musicplayer;

import java.util.ArrayList;

public class User {
	String id;
	String lastName;
	String firstName;
	String password;
	ArrayList<Playlist> playlists;

	void User() {
		playlists = new ArrayList<>();
	}
}
