package com.example.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainEmptyActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent activityIntent;

		// go straight to main if a token is stored
		if (Session.currentUser != null) {
			activityIntent = new Intent(this, MainActivity.class);
		} else {
			activityIntent = new Intent(this, LoginActivity.class);
		}

		startActivity(activityIntent);
		finish();
	}
}
