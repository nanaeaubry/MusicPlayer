package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	private Boolean playingFromCurrentPlaylist = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Listen to play score event on Session
		Session.setSessionListener(sessionListener);

		// Loading users and getting the select user playlists
		ArrayList<User> users = loadUsers();
		Session.currentUser =users.get(0);

		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

		// Reading scores and loading into list view
		Session.scores = loadScores();

		MusicFragment musicFragment = new MusicFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, musicFragment).commit();
	}

	Session.SessionListener sessionListener = new Session.SessionListener() {
		@Override
		public void playScore(Score score, Boolean currentPlaylist) {
			playingFromCurrentPlaylist = currentPlaylist;
			TextView playSong= findViewById(R.id.playSong);
			playSong.setText("Play: " + score.song.title);
		}
	};

	private ArrayList<Score> loadScores() {

		ArrayList<Score> scores = new ArrayList<>();
		try {
			InputStream is = getAssets().open("music.json");
			JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));

			Gson gson = new Gson();

			jsonReader.beginArray();

			while (jsonReader.hasNext()) {
				Score score = gson.fromJson(jsonReader, Score.class);
				scores.add(score);
			}
			jsonReader.close();
			return scores;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<User> loadUsers() {

		ArrayList<User> users = new ArrayList<>();
		try {
			InputStream is = getAssets().open("users.json");
			JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));

			Gson gson = new Gson();

			jsonReader.beginArray();

			while (jsonReader.hasNext()) {
				User user = gson.fromJson(jsonReader, User.class);
				users.add(user);
			}
			jsonReader.close();
			return users;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment selectedFragment = null;

			switch (item.getItemId()) {
				case R.id.navigation_musicList:
					selectedFragment = new MusicFragment();
					break;
				case R.id.navigation_playlists:
					selectedFragment = new ProfileFragment();
					break;
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
			return true;
		}
	};
}
