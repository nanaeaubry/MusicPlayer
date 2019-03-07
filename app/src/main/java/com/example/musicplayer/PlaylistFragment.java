package com.example.musicplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.util.ArrayList;

/**
 * Class showing list of scores in selected playlist
 */

public class PlaylistFragment extends Fragment {
	RecyclerView recyclerView;
	LinearLayout playlists;
	ProgressBar progressBar;

	public interface PlaylistFragmentListener {
		void onGoBack();
	}

	private String playlistName;
	private PlaylistFragmentListener listener;
	private ArrayList<Score> scores;
	private MusicAdapter musicAdapter;

	public void setPlaylistName(String name) {
		this.playlistName = name;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_playlist, container, false);

		TextView playlistName = view.findViewById(R.id.playlistName);
		playlistName.setText(this.playlistName);

		Button goBackButton = view.findViewById(R.id.goBackButton);
		goBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onGoBack();
			}
		});

		scores = new ArrayList<>();
		musicAdapter = new MusicAdapter(
				scores,
				null,
				itemClickListener,
				null,
				deleteSongFromPlaylistListener);

		recyclerView = view.findViewById(R.id.musicRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(musicAdapter);

		playlists = view.findViewById(R.id.playlist);
		progressBar = view.findViewById(R.id.playlist_progress);

		LoadScoresTask task = new LoadScoresTask();
		task.execute();

		return view;
	}

	RecyclerViewItemClickListener itemClickListener = new RecyclerViewItemClickListener() {
		@Override
		public void onClick(View v, int position) {
			Score score = scores.get(position);
			Session.playScore(score);
		}
	};

	ScoreItemActionListener deleteSongFromPlaylistListener = new ScoreItemActionListener() {
		@Override
		public void onClick(View v, Score score) {
			DeleteSongFromPlaylistTask task = new DeleteSongFromPlaylistTask(v.getContext(), playlistName, score);
			task.execute();
		}
	};

	public void setPlaylistFragmentListener(PlaylistFragmentListener listener) {
		this.listener = listener;
	}

	private void scoresLoaded(ArrayList<Score> scores) {
		this.scores.addAll(scores);
		musicAdapter.notifyDataSetChanged();
		progressBar.setVisibility(View.GONE);
		playlists.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.VISIBLE);
	}

	class LoadScoresTask extends AsyncTask<Void, Void, ArrayList<Score>> {

		@Override
		protected ArrayList<Score> doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "UserService");
			request.addProperty("methodName", "getPlaylistSongs");
			JsonObject param = new JsonObject();
			param.addProperty("userId", Session.userId);
			param.addProperty("name", playlistName);
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

	private void songDeletedFromPlaylist(Context context, Score score) {
		scores.remove(score);
		musicAdapter.notifyDataSetChanged();
		Toast.makeText(context, "Removed: " + score.title, Toast.LENGTH_SHORT).show();
	}

	class DeleteSongFromPlaylistTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;
		private final String playlistName;
		private final Score score;

		public DeleteSongFromPlaylistTask(Context context, String playlistName, Score score) {
			this.context = context;
			this.playlistName = playlistName;
			this.score = score;
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "UserService");
			request.addProperty("methodName", "deletePlaylistSong");
			JsonObject param = new JsonObject();
			param.addProperty("userId", Session.userId);
			param.addProperty("name", playlistName);
			param.addProperty("songId", score.id);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);
			return response.get("ret").getAsBoolean();

		}

		@Override
		protected void onPostExecute(final Boolean result) {
			if (result) {
				songDeletedFromPlaylist(context, score);
			}
		}

	}

}
