package com.example.musicplayer;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Class showing list of playlists attainable from navigation bar.
 */
public class PlaylistsFragment extends Fragment {

	private PlaylistsAdapter playlistsAdapter;
	private ArrayList<String> playlists;
	RecyclerView recyclerView;
	LinearLayout userPlaylists;
	ProgressBar progressBar;


	public interface PlaylistsFragmentListener {
		void onShowPlaylist(String playlist);
	}

	private PlaylistsFragmentListener listener;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_playlists, container, false);

		Button addPlaylistButton = view.findViewById(R.id.addPlaylist);
		addPlaylistButton.setOnClickListener(addPlaylistListener);

		playlists = new ArrayList<>();
		playlistsAdapter = new PlaylistsAdapter(playlists, itemClickListener);

		recyclerView = view.findViewById(R.id.playlistsRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(playlistsAdapter);

		userPlaylists = view.findViewById(R.id.userPlaylists);
		progressBar = view.findViewById(R.id.playlists_progress);

		LoadPlaylistsTask task = new LoadPlaylistsTask();
		task.execute();

		return view;
	}

	/**
	 * Get playlist from sessions. List of playlists depends on user
	 */
	RecyclerViewItemClickListener itemClickListener = new RecyclerViewItemClickListener() {
		@Override
		public void onClick(View v, int position) {
			String playlistName = playlists.get(position);
			listener.onShowPlaylist(playlistName);
		}
	};

	/**
	 * Add a playlist by tapping plus icon. User will enter the name of the playlist
	 * and be prompted to accept or cancel.
	 */
	View.OnClickListener addPlaylistListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Enter Playlist Name");

			final EditText input = new EditText(v.getContext());
			input.setInputType(InputType.TYPE_CLASS_TEXT);

			builder.setView(input);

			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString();
					if (name.length() <= 0) {
						dialog.cancel();
						return;
					}
					SavePlaylistTask task = new SavePlaylistTask(name);
					task.execute();

				}
			});

			builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();


		}
	};

	public void setPlaylistsFragmentListener(PlaylistsFragmentListener listener) {
		this.listener = listener;
	}

	private void playlistsLoaded(ArrayList<String> playlists) {
		this.playlists.addAll(playlists);
		playlistsAdapter.notifyDataSetChanged();
		progressBar.setVisibility(View.GONE);
		userPlaylists.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.VISIBLE);
	}


	class LoadPlaylistsTask extends AsyncTask<Void, Void, ArrayList<String>> {


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

			Gson gson = new Gson();
			ArrayList<String> playlistNames = new ArrayList<>();
			for (int i = 0; i < arr.size(); i++) {
				playlistNames.add(arr.get(i).getAsJsonObject().get("name").getAsString());
			}
			return playlistNames;
		}

		@Override
		protected void onPostExecute(final ArrayList<String> playlistNames) {
			playlistsLoaded(playlistNames);
		}
	}

	private void playlistSaved(String playlist) {
		if (this.playlists.contains(playlist)) {
			return;
		}
		this.playlists.add(0, playlist);
		playlistsAdapter.notifyDataSetChanged();
	}

	class SavePlaylistTask extends AsyncTask<Void, Void, String> {

		private String playlistName;

		public SavePlaylistTask(String playlistName) {
			this.playlistName = playlistName;
		}

		@Override
		protected String doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "UserService");
			request.addProperty("methodName", "createPlaylist");
			JsonObject param = new JsonObject();
			param.addProperty("userId", Session.userId);
			param.addProperty("name", playlistName);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);
			if (response.get("ret").getAsBoolean()) {
				return playlistName;
			}

			return null;
		}

		@Override
		protected void onPostExecute(String playlistName) {
			if (playlistName != null) {
				playlistSaved(playlistName);
			}
		}
	}

}
