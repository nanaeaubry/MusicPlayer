package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Class for music list page
 */
public class MusicFragment extends Fragment {

	MusicAdapter musicAdapter;
	ArrayList<Score> scores;
	ArrayList<String> playlistNames;

	EditText inputFilter;

	String filter = null;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);

		scores = new ArrayList<>();
		playlistNames = new ArrayList<>();
		musicAdapter = new MusicAdapter(
				scores,
				playlistNames,
				itemClickListener,
				addSongToPlaylistListener,
				null);

		RecyclerView recyclerView = view.findViewById(R.id.musicRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(musicAdapter);

		// Set up filter for score list
		inputFilter = view.findViewById(R.id.inputFilter);
		inputFilter.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// NO OP
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filter = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				//NO OP
			}
		});

		Button buttonFilter = view.findViewById(R.id.btnFilter);
		buttonFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				inputFilter.setEnabled(false);

				LoadScoresTask scoresTask = new LoadScoresTask(filter);
				scoresTask.execute();

			}
		});

		inputFilter.setEnabled(false);
		LoadPlaylistNamesTask task = new LoadPlaylistNamesTask();
		task.execute();

		return view;
	}

	private void playlistNamesLoaded(ArrayList<String> playlistNames) {
		this.playlistNames.addAll(playlistNames);
		LoadScoresTask scoresTask = new LoadScoresTask(null);
		scoresTask.execute();
	}

	private void scoresLoaded(ArrayList<Score> scores) {
		this.scores.clear();
		this.scores.addAll(scores);
		musicAdapter.notifyDataSetChanged();
		inputFilter.setEnabled(true);
	}

	RecyclerViewItemClickListener itemClickListener = new RecyclerViewItemClickListener() {
		@Override
		public void onClick(View v, int position) {
			Score score = scores.get(position);
			Session.playScore(score);
		}
	};

	ScoreItemActionListener addSongToPlaylistListener = new ScoreItemActionListener() {
		@Override
		public void onClick(View v, final Score score) {
			final Context context = v.getContext();
			if (playlistNames.size() <= 0) {
				Toast.makeText(context, "First create at least one playlist in your profile", Toast.LENGTH_SHORT).show();
				return;
			}

			// Create dialog to pick up playlist
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Pick a playlist");
			String[] arr = new String[playlistNames.size()];
			playlistNames.toArray(arr);
			builder.setItems(arr, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Add the song to the playlist
					String playlistName = playlistNames.get(which);
					AddSongToPlaylistTask task = new AddSongToPlaylistTask(context, score, playlistName);
					task.execute();
				}
			});
			builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	class LoadPlaylistNamesTask extends AsyncTask<Void, Void, ArrayList<String>> {


		@Override
		protected ArrayList<String> doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "UserService");
			request.addProperty("methodName", "getPlaylists");
			JsonObject param = new JsonObject();
			param.addProperty("userId", Session.userId);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);
			JsonArray arr = response.get("ret").getAsJsonArray();

			ArrayList<String> playlistNames = new ArrayList<>();
			for (int i = 0; i < arr.size(); i++) {
				playlistNames.add(arr.get(i).getAsJsonObject().get("name").getAsString());
			}
			return playlistNames;
		}

		@Override
		protected void onPostExecute(final ArrayList<String> playlistNames) {
			playlistNamesLoaded(playlistNames);
		}
	}

	class LoadScoresTask extends AsyncTask<Void, Void, ArrayList<Score>> {

		String filter;

		public LoadScoresTask(String filter) {
			this.filter = filter;
		}

		@Override
		protected ArrayList<Score> doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "CatalogService");
			request.addProperty("methodName", "getSongs");
			JsonObject param = new JsonObject();
			param.addProperty("startIndex", 0);
			param.addProperty("count", 100);
			if (filter != null) {
				param.addProperty("filter", filter);
			}
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);
			JsonArray arr = response.get("ret").getAsJsonArray();

			Gson gson = new Gson();
			ArrayList<Score> scores = new ArrayList<>();
			for (int i = 0; i < arr.size(); i++) {
				scores.add(gson.fromJson(arr.get(i), Score.class));
			}
			return scores;
		}

		@Override
		protected void onPostExecute(final ArrayList<Score> scores) {
			scoresLoaded(scores);
		}
	}

	private void songAddedToPlaylist(Context context, Score score, String playlistName) {
		Toast.makeText(context, "Added: " + score.title + " to " + playlistName, Toast.LENGTH_SHORT).show();
	}

	class AddSongToPlaylistTask extends AsyncTask<Void, Void, Boolean> {
		Context context;
		Score score;
		String playlistName;

		public AddSongToPlaylistTask(Context context, Score score, String playlistName) {
			this.context = context;
			this.score = score;
			this.playlistName = playlistName;
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "UserService");
			request.addProperty("methodName", "addSongToPlaylist");
			JsonObject param = new JsonObject();
			param.addProperty("userId", Session.userId);
			param.addProperty("name", playlistName);
			JsonObject jSong = new JsonObject();
			jSong.addProperty("id", score.id);
			jSong.addProperty("title", score.title);
			jSong.addProperty("album", score.album);
			jSong.addProperty("artist", score.artist);
			param.add("song", jSong);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);
			return response.get("ret").getAsBoolean();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				songAddedToPlaylist(context, score, playlistName);
			}
		}

	}
}
