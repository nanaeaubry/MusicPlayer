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
import android.widget.Toast;

import java.util.ArrayList;

public class ScoresAdapter extends ArrayAdapter<Score> {

	private Context context;
	private Boolean removeButtonVisible = false;
	private Boolean addButtonVisible = true;

	public ScoresAdapter(Context context, ArrayList<Score> scores) {
		super(context, 0, scores);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get data item for this position
		final Score score = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_score, parent, false);
		}

		TextView songName = convertView.findViewById(R.id.songName);
		TextView artistName = convertView.findViewById(R.id.artistName);
		TextView albumName = convertView.findViewById(R.id.albumName);

		songName.setText(score.song.title);
		artistName.setText(score.artist.name);
		albumName.setText(score.release.name);

		Button addToPlaylistButton = convertView.findViewById(R.id.addToPlaylist);
		addToPlaylistButton.setVisibility(addButtonVisible ? View.VISIBLE : View.GONE);
		addToPlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Session.getPlaylists().size() <= 0) {
					Toast.makeText(context, "First create at least one playlist in your profile", Toast.LENGTH_SHORT).show();
					return;
				}

				// Create dialog to pick up playlist
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Pick a playlist");
				builder.setItems(Session.getPlaylistNames(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Add the song to the playlist
						Playlist playlist = Session.getPlaylists().get(which);
						playlist.scoreIds.add(score.song.id);
						Toast.makeText(context, "Added: " + score.song.title + " to " + playlist.name, Toast.LENGTH_SHORT).show();
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
		});

		Button removeFromPlaylistButton = convertView.findViewById(R.id.removeFromPlaylist);
		removeFromPlaylistButton.setVisibility(removeButtonVisible ? View.VISIBLE : View.GONE);
		removeFromPlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Playlist playlist = Session.getCurrentPlaylist();
				String songId = score.song.id;
				playlist.scoreIds.remove(songId);
				Session.getCurrentPlaylistScores().remove(score);

				notifyDataSetChanged();

				Toast.makeText(context, "Removed: " + score.song.title, Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}

	public void setRemoveButtonVisible(Boolean isVisible) {
		removeButtonVisible = isVisible;
	}

	public void setAddButtonVisible(Boolean isVisible) {
		addButtonVisible = isVisible;
	}

}
