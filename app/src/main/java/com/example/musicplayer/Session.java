package com.example.musicplayer;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Session {

	public static String serverIp;
	public static int serverPort = 2048;

	public static String userId;

	// PLay score api
	public interface PlayScoreListener {
		void onPlayScore(Score score);
	}

	private static PlayScoreListener playScoreListener;

	public static void setPlayScoreListener(PlayScoreListener listener) {
		Session.playScoreListener = listener;
	}

	public static void playScore(Score score) {
		Session.playScoreListener.onPlayScore(score);
	}

}

