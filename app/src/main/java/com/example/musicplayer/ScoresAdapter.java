package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;


/**
 * Adapter for scores that are in the music fragment and playlist fragment
 */
public class ScoresAdapter extends ArrayAdapter<Score> {

	private Context context;

	private Boolean removeButtonVisible = false;
	private Boolean addButtonVisible = true;

	private Filter filter;
	private ArrayList<Score> scores;

	public ScoresAdapter(Context context, ArrayList<Score> scores) {
		super(context, 0, scores);
		this.context = context;
		this.scores = scores;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//Get data item for this position
		final Score score = getItem(position);

		// Load scores if they have not been loaded
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_score, parent, false);
		}

		// Set TextViews to appropriate attributes of score
		TextView songName = convertView.findViewById(R.id.songName);
		TextView artistName = convertView.findViewById(R.id.artistName);
		TextView albumName = convertView.findViewById(R.id.albumName);

		// Set text to attribute names
		songName.setText(score.song.title);
		artistName.setText(score.artist.name);
		albumName.setText(score.release.name);


		// Button to add score to a playlist of choice
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
						Session.saveUsers();
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

		// Button to remove a song from a playlist when it is added to playlist
		Button removeFromPlaylistButton = convertView.findViewById(R.id.removeFromPlaylist);
		removeFromPlaylistButton.setVisibility(removeButtonVisible ? View.VISIBLE : View.GONE);
		removeFromPlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Playlist playlist = Session.getCurrentPlaylist();
				String songId = score.song.id;
				playlist.scoreIds.remove(songId);
				Session.getCurrentPlaylistScores().remove(score);
				Session.saveUsers();

				notifyDataSetChanged();

				Toast.makeText(context, "Removed: " + score.song.title, Toast.LENGTH_SHORT).show();
			}
		});

		return convertView;
	}

	// Set visibility of remove song button
	public void setRemoveButtonVisible(Boolean isVisible) {
		removeButtonVisible = isVisible;
	}

	// Set visibility of add song button
	public void setAddButtonVisible(Boolean isVisible) {
		addButtonVisible = isVisible;
	}

	/**
	 * Get filter results to be listed on music fragment
	 *
	 * @return Filter list view of results
	 */
	@Override
	public Filter getFilter() {
		if (this.filter == null) {
			this.filter = new ScoreFilter(this.scores);
		}
		return this.filter;
	}

	private class ScoreFilter extends Filter {
		private ArrayList<Score> sourceScores;

		ScoreFilter(ArrayList<Score> scores) {
			sourceScores = new ArrayList<>();
			sourceScores.addAll(scores);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			ArrayList<Score> filteredScore = (ArrayList<Score>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0, l = filteredScore.size(); i < l; i++) {
				add(filteredScore.get(i));
			}
			notifyDataSetInvalidated();
		}

		/**
		 * Perform filtering on list view of scores as user enters constraint text
		 *
		 * @param constraint input from user
		 * @return Results of the scores adhering to the constraint
		 */
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			FilterResults results = new FilterResults();

			// perform your search here using the searchConstraint String.
			if (TextUtils.isEmpty(constraint)) {
				results.count = sourceScores.size();
				results.values = sourceScores;
			} else {

				ArrayList<Score> filteredScores = new ArrayList<>();
				String filter = constraint.toString().toLowerCase();
				for (Score score : sourceScores) {
					String songTitle = score.song.title.toLowerCase();
					if (songTitle.indexOf(filter) >= 0) {
						filteredScores.add(score);
						continue;
					}
					String artistName = score.artist.name.toLowerCase();
					if (artistName.indexOf(filter) >= 0) {
						filteredScores.add(score);
						continue;
					}
					String albumName = score.release.name.toLowerCase();
					if (albumName.indexOf(filter) >= 0) {
						filteredScores.add(score);
						continue;
					}
				}
				results.count = filteredScores.size();
				results.values = filteredScores;
			}

			return results;
		}
	}
}
