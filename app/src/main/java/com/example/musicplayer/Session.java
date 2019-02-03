package com.example.musicplayer;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Session {
	public interface SessionListener {
		void playScore(Score score, Boolean currentPlaylist);
	}

	private static SessionListener listener;

	public static User currentUser;
	public static ArrayList<Score> scores;

	private static Playlist currentPlaylist;
	private static ArrayList<Score> playlistScores;

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

	public static Playlist getCurrentPlaylist(){
		return Session.currentPlaylist;
	}

	public static void setCurrentPlaylist(Playlist playlist){
		Session.currentPlaylist = playlist;
		Session.playlistScores = null;
	}

	public static ArrayList<Score> getCurrentPlaylistScores() {
		if (Session.playlistScores != null){
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
				if (score.song.id == songId) {
					Session.playlistScores.add(score);
					break;
				}
			}

		}
		return Session.playlistScores;
	}

	public static void setSessionListener(SessionListener listener){
		Session.listener = listener;
	}

	public static void playScore(Score score, Boolean currentPlaylist){
		Session.listener.playScore(score, currentPlaylist);
	}
}
