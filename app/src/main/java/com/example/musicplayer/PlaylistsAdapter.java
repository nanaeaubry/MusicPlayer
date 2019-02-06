package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for playlists
 */
public class PlaylistsAdapter extends ArrayAdapter<Playlist> {

	private Context context;


	public PlaylistsAdapter(Context context, ArrayList<Playlist> playlists) {
		super(context, 0, playlists);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get data item for this position
		final Playlist playlist = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_playlist, parent, false);
		}

		TextView playlistName = convertView.findViewById(R.id.playlistName);

		playlistName.setText(playlist.name);


		// Delete a playlist with trash icon button
		Button deletePlaylistButton = convertView.findViewById(R.id.deletePlaylist);
		deletePlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Create dialog to pick up playlist
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Delete Playlist");
				builder.setMessage("Are you sure?");
				builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Session.getPlaylists().remove(playlist);
						Session.saveUsers();
						notifyDataSetChanged();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});


		return convertView;
	}
}
