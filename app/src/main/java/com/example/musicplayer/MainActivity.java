package com.example.musicplayer;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Base64;

/**
 * The main page.
 */

public class MainActivity extends AppCompatActivity {

	private static String[] songs = {"dancingqueen.mp3", "happy.mp3"};

	// Song playing properties
	private LinearLayout playControls;

	private MediaPlayer mediaPlayer = new MediaPlayer();
	private BufferSongTask bufferSongTask = null;

	private String songPlayed = "dancingqueen.mp3";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Hide keyboard when app launches
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// Listen to play score event on Session
		Session.setPlayScoreListener(playScoreListener);

		// Load bottom navigation bar
		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

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
	Session.PlayScoreListener playScoreListener = new Session.PlayScoreListener() {
		@Override
		public void onPlayScore(Score score) {

			mediaPlayer.reset();

			// Allow play and pause button to be seen
			playControls.setVisibility(View.VISIBLE);

			// Set text fields to show what song is being played
			TextView playSong = findViewById(R.id.playSong);
			playSong.setText(score.title);

			TextView playArtist = findViewById(R.id.playArtist);
			playArtist.setText(score.artist);

			TextView playAlbum = findViewById(R.id.playAlbum);
			playAlbum.setText(score.album);

			if (songPlayed == songs[0]){
				songPlayed = songs[1];
			}
			else {
				songPlayed = songs[0];
			}

			if (bufferSongTask != null) {
				bufferSongTask.cancel(true);
			}

			bufferSongTask = new BufferSongTask(getApplicationContext(), songPlayed);
			bufferSongTask.execute();
		}
	};

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

	class BufferSongTask extends AsyncTask<Void, Integer, Boolean> {

		private Context context;
		private String filename;
		private File tmpFile = null;
		private FileOutputStream tmpOutputStream = null;
		private FileInputStream tmpInputStream = null;
		private Boolean playerInitialized = false;

		public BufferSongTask(Context context, String filename) {
			this.context = context;
			this.filename = filename;
		}

		@Override
		protected Boolean doInBackground(Void... voids) {

			try {
				tmpFile = File.createTempFile("song", ".mp3", context.getCacheDir());
				tmpFile.deleteOnExit();

				tmpInputStream = new FileInputStream(tmpFile);
				tmpOutputStream = new FileOutputStream(tmpFile);

				UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
				int fragment = 0;
				do {
					if (this.isCancelled()) {
						return false;
					}

					JsonObject request = new JsonObject();
					request.addProperty("serviceName", "SongService");
					request.addProperty("methodName", "getSongChunk");
					JsonObject param = new JsonObject();
					param.addProperty("key", filename);
					param.addProperty("fragment", fragment);
					request.add("param", param);

					JsonObject response = connection.execute(request);

					String s = response.get("ret").getAsString();
					if (TextUtils.isEmpty(s)) {
						return true;
					}

					byte[] buff = Base64.getDecoder().decode(s);
					tmpOutputStream.write(buff);
					tmpOutputStream.flush();

					publishProgress(fragment);

					fragment++;
				} while (true);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					// Not closing the inputStream, the mediaPlayer is using it
//					if (tmpInputStream != null) {
//						tmpInputStream.close();
//					}
					if (tmpOutputStream != null) {
						tmpOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... args) {
			final int fragment = args[0];
			if (fragment < 200) {
				return;
			}

			initializePlayer();
		}

//		@Override
//		protected void onPostExecute(Boolean aBoolean) {
//			Log.d("MainActivity", "Size: " + tmpFile.length());
//			initializePlayer();
//		}

		void initializePlayer() {
			if (playerInitialized) {
				return;
			}

			playerInitialized = true;
			try {
				mediaPlayer.setDataSource(tmpInputStream.getFD());
				mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						Log.d("MainActivity", "Player Start");
						mp.start();
					}
				});
				mediaPlayer.prepareAsync();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
