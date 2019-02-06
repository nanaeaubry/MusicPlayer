package com.example.musicplayer;

import java.util.ArrayList;
/**
 * Class stores attributes of the Playlist which are composed of a name and
 * an arraylist of scoreIds taken from the song id.
 */
public class Playlist {
	String name;
	ArrayList<String> scoreIds;

	Playlist() {
		scoreIds = new ArrayList<>();
	}
}