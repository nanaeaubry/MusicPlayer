package com.example.musicplayer;

import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Session {
	public interface SessionListener {
		void playScore(Score score, Boolean currentPlaylist);
	}

	private static SessionListener listener;

	public static File filesDir;

	public static ArrayList<User> users;
	private static User currentUser;

	public static ArrayList<Score> scores;

	private static Playlist currentPlaylist;
	private static ArrayList<Score> playlistScores;

	public static void setCurrentUser(User user){
		Session.currentUser = user;
	}

	public static User getCurrentUser(){
		return Session.currentUser;
	}

	public static ArrayList<Playlist> getPlaylists() {
		return Session.currentUser.playlists;
	}

	public static String[] getPlaylistNames() {
		String[] items = new String[Session.getPlaylists().size()];
		for (int i = 0; i < Session.getPlaylists().size(); i++) {
			items[i] = Session.getPlaylists().get(i).name;
		}
		return items;
	}

	public static Playlist getCurrentPlaylist() {
		return Session.currentPlaylist;
	}

	public static void setCurrentPlaylist(Playlist playlist) {
		Session.currentPlaylist = playlist;
		Session.playlistScores = null;
	}

	public static ArrayList<Score> getCurrentPlaylistScores() {
		if (Session.playlistScores != null) {
			return Session.playlistScores;
		}

		Session.playlistScores = new ArrayList<>();
		if (currentPlaylist == null) {
			return Session.playlistScores;
		}
		for (int i = 0; i < currentPlaylist.scoreIds.size(); i++) {
			String songId = currentPlaylist.scoreIds.get(i);
			for (int j = 0; j < scores.size(); j++) {
				Score score = scores.get(j);
				if (score.song.id.equals(songId)) {
					Session.playlistScores.add(score);
					break;
				}
			}

		}
		return Session.playlistScores;
	}

	public static void setSessionListener(SessionListener listener) {
		Session.listener = listener;
	}

	public static void playScore(Score score, Boolean currentPlaylist) {
		Session.listener.playScore(score, currentPlaylist);
	}

	public static void saveUsers() {

		Gson gson = new Gson();
		String json = gson.toJson(Session.users);

		try {

			String filePath = Session.filesDir.getAbsolutePath() + "/users.json";
			File file = new File(filePath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(json.getBytes());
			fileOutputStream.flush();
			fileOutputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

