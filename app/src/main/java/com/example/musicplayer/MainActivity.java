package com.example.musicplayer;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * The main page.
 */

public class MainActivity extends AppCompatActivity {

	// Song playing properties
	private MediaPlayer mediaPlayer;
	private LinearLayout playControls;
	private int currentSongId = R.raw.happy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Hide keyboard when app launches
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// Prepare MediaPlayer
		mediaPlayer = new MediaPlayer();

		// Listen to play score event on Session
		Session.setSessionListener(sessionListener);

		// Load bottom navigation bar
		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

		// Reading scores and loading into list view
		Session.scores = loadScores();

		// Music Fragment will open first
		MusicFragment musicFragment = new MusicFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, musicFragment).commit();

		// Song play controls
		playControls = findViewById(R.id.songControls);
		final Button playSongButton = findViewById(R.id.playButton);
		playSongButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					playSongButton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
				} else {
					mediaPlayer.start();
					playSongButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
				}
			}
		});

		// Set seek bar for volume
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		SeekBar volumeBar = findViewById(R.id.skbVolume);
		volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// NO OP
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// NO OP
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
			}
		});


	}

	// Operations to enable music playing
	Session.SessionListener sessionListener = new Session.SessionListener() {
		@Override
		public void playScore(Score score, Boolean currentPlaylist) {

			// Allow play and pause button to be seen
			playControls.setVisibility(View.VISIBLE);

			// Set text fields to show what song is being played
			TextView playSong = findViewById(R.id.playSong);
			playSong.setText(score.song.title);

			TextView playArtist = findViewById(R.id.playArtist);
			playArtist.setText(score.artist.name);

			TextView playAlbum = findViewById(R.id.playAlbum);
			playAlbum.setText(score.release.name);

			// Toggle between two songs
			if (currentSongId == R.raw.happy) {
				currentSongId = R.raw.dancingqueen;
			} else {
				currentSongId = R.raw.happy;
			}
			//Read in song and play
			AssetFileDescriptor afd = getResources().openRawResourceFd(currentSongId);
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * Read artist, song, and album into a Score and create an arraylist of all scores
	 * to be displayed.
	 * @return List of scores
	 */
	private ArrayList<Score> loadScores() {

		ArrayList<Score> scores = new ArrayList<>();
		try {
			// Read in Json file with Gson
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

	/**
	 * Enable navigation on bottom bar.
	 */
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
