package com.example.musicplayer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Adapter for scores that are in the music fragment and playlist fragment
 */
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

	private RecyclerViewItemClickListener itemClickListener;
	private ScoreItemActionListener addSongToPlaylistListener;
	private ScoreItemActionListener removeSongFromPlaylistListener;

	private ArrayList<Score> scores;

	private ArrayList<String> playlistNames;

	public MusicAdapter(
			ArrayList<Score> scores,
			ArrayList<String> playlistNames,
			RecyclerViewItemClickListener itemClickListener,
			ScoreItemActionListener addSongToPlaylistListener,
			ScoreItemActionListener removeSongFromPlaylistListener) {
		this.playlistNames = playlistNames;
		this.scores = scores;
		this.itemClickListener = itemClickListener;
		this.addSongToPlaylistListener = addSongToPlaylistListener;
		this.removeSongFromPlaylistListener = removeSongFromPlaylistListener;
	}

	@Override
	public MusicViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		View scoreView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
		return new MusicViewHolder(scoreView, itemClickListener);
	}

	@Override
	public void onBindViewHolder(@NonNull MusicViewHolder musicViewHolder, int position) {
		final Score score = scores.get(position);
		musicViewHolder.songName.setText(score.title);
		musicViewHolder.artistName.setText(score.artist);
		musicViewHolder.albumName.setText(score.album);

		View scoreView = musicViewHolder.itemView;

		Button addToPlaylistButton = scoreView.findViewById(R.id.addToPlaylist);
		if (addSongToPlaylistListener != null) {
			addToPlaylistButton.setVisibility(View.VISIBLE);

			addToPlaylistButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addSongToPlaylistListener.onClick(v, score);
				}
			});
		}

		Button removeFromPlaylistButton = scoreView.findViewById(R.id.removeFromPlaylist);
		if (removeSongFromPlaylistListener != null) {
			removeFromPlaylistButton.setVisibility(View.VISIBLE);
			removeFromPlaylistButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeSongFromPlaylistListener.onClick(v, score);
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return scores.size();
	}


	class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		public TextView songName, artistName, albumName;
		private RecyclerViewItemClickListener itemClickListener;

		public MusicViewHolder(View view, RecyclerViewItemClickListener itemClickListener) {
			super(view);
			songName = view.findViewById(R.id.songName);
			artistName = view.findViewById(R.id.artistName);
			albumName = view.findViewById(R.id.albumName);
			this.itemClickListener=itemClickListener;
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			itemClickListener.onClick(v, getAdapterPosition());
		}
	}
}
