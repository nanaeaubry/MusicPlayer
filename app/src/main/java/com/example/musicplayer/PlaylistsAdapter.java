package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Adapter for playlists
 */
public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder> {

	ArrayList<String> playlists;
	RecyclerViewItemClickListener itemClickListener;

	public PlaylistsAdapter(ArrayList<String> playlists, RecyclerViewItemClickListener itemClickListener) {
		this.playlists = playlists;
		this.itemClickListener = itemClickListener;
	}

	@NonNull
	@Override
	public PlaylistsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		View playlistsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
		return new PlaylistsViewHolder(playlistsView, itemClickListener);
	}

	@Override
	public void onBindViewHolder(@NonNull PlaylistsViewHolder playlistsViewHolder, int i) {
		final String playlist = playlists.get(i);
		playlistsViewHolder.playlistName.setText(playlist);

		Button deletePlaylistButton = playlistsViewHolder.itemView.findViewById(R.id.deletePlaylist);
		deletePlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Create dialog to pick up playlist
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setTitle("Delete Playlist");
				builder.setMessage("Are you sure?");
				builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						DeletePlaylistTask task = new DeletePlaylistTask(playlist);
						task.execute();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	@Override
	public int getItemCount() {
		return playlists.size();
	}

	class PlaylistsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		public TextView playlistName;
		private RecyclerViewItemClickListener itemClickListener;

		public PlaylistsViewHolder(View view, RecyclerViewItemClickListener itemClickListener ) {
			super(view);
			playlistName = view.findViewById(R.id.playlistName);
			this.itemClickListener = itemClickListener;
			view.setOnClickListener(this);
		}

		public void onClick(View v){
			itemClickListener.onClick(v, getAdapterPosition());
		}
	}

	private void playlistDeleted(String playlist){
		playlists.remove(playlist);
		notifyDataSetChanged();
	}

	class DeletePlaylistTask extends AsyncTask<Void, Void, Boolean>{

		private String playlist;

		public DeletePlaylistTask(String playlist){
			this.playlist = playlist;
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "UserService");
			request.addProperty("methodName", "deletePlaylist");
			JsonObject param = new JsonObject();
			param.addProperty("userId", Session.userId);
			param.addProperty("name", playlist);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);

			return response.get("ret").getAsBoolean();

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				playlistDeleted(playlist);
			}
		}
	}
}
