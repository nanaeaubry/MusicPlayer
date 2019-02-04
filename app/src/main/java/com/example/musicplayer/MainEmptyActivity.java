package com.example.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainEmptyActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Save the internal storage manager
		Session.filesDir = getFilesDir();

		// Prepare local storage if necessary
		loadUsers();

		Intent activityIntent = new Intent(this, LoginActivity.class);
		startActivity(activityIntent);

		finish();
	}

	void loadUsers() {


		try {

			String path = getFilesDir().getAbsolutePath() + "/users.json";
			File file = new File(path);
			if (file.exists()) {
				InputStream inputStream = new FileInputStream(file);
				Session.users = loadUsersFromStream(inputStream);
				inputStream.close();
				return;
			}

			InputStream inputStream = getAssets().open("users.json");
			Session.users = loadUsersFromStream(inputStream);
			Session.saveUsers();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<User> loadUsersFromStream(InputStream inputStream) {

		try {
			Gson gson = new Gson();

			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			JsonReader jsonReader = new JsonReader(inputStreamReader);

			ArrayList<User> users = new ArrayList<>();
			jsonReader.beginArray();
			while (jsonReader.hasNext()) {
				User user = gson.fromJson(jsonReader, User.class);
				users.add(user);
			}
			jsonReader.close();

			return users;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
