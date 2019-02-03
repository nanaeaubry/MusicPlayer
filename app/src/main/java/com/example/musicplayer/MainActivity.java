package com.example.musicplayer;

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

	Bundle scoresBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		// Reading scores and loading into list view
		ArrayList<Score> scores = loadScores();
		scoresBundle = new Bundle();
		scoresBundle.putParcelableArrayList("scores", scores);

		MusicFragment musicFragment = new MusicFragment();
		musicFragment.setArguments(scoresBundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, musicFragment).commit();
	}

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

	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment selectedFragment = null;

			switch (item.getItemId()) {
				case R.id.navigation_musicList:
					selectedFragment = new MusicFragment();
					selectedFragment.setArguments(scoresBundle);
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
