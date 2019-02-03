package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	ArrayList<Score> scores;
	ArrayList<Playlist> playlists;

	Bundle bundle;
	AlertDialog.Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Loading users and getting the select user playlists
		ArrayList<User> users = loadUsers();
		playlists = users.get(0).playlists;

		builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
		builder.setTitle("Item Selected")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert);

		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

		// Reading scores and loading into list view
		scores = loadScores();
		bundle = new Bundle();
		bundle.putParcelableArrayList("scores", scores);
		bundle.putParcelableArrayList("playlists", playlists);

		MusicFragment musicFragment = new MusicFragment();
		musicFragment.setArguments(bundle);
		musicFragment.setMusicFragmentListener(musicFragmentListener);

		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, musicFragment).commit();
	}

	MusicFragment.MusicFragmentListener musicFragmentListener = new MusicFragment.MusicFragmentListener() {
		@Override
		public void onAddScoreToPlaylist(Playlist playlist, Score score) {
			builder.setMessage(" " + score.song.title);
			builder.show();
		}

		@Override
		public void onRemoveScoreFromPlaylist(Playlist playlist, Score score) {
			builder.setMessage("Add " + score.song.title);
			builder.show();
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
					selectedFragment.setArguments(bundle);
					break;
				case R.id.navigation_playlists:
					selectedFragment = new PlaylistFragment();
					break;
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
			return true;
		}
	};
}
