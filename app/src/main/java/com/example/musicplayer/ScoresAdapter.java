package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoresAdapter extends ArrayAdapter<Score> {

	public interface ScoresAdapterListener {
		public void onAddScore(Score score);
		public void onRemoveScore(Score score);
	}

	private ScoresAdapterListener listener;
	private Boolean removeButtonVisible = false;
	private Boolean addButtonVisible = true;

	public ScoresAdapter(Context context, ArrayList<Score> scores) {
		super(context, 0, scores);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get data item for this position
		final Score score = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_score, parent, false);
		}

		TextView songName = (TextView) convertView.findViewById(R.id.songName);
		TextView artistName = (TextView) convertView.findViewById(R.id.artistName);
		TextView albumName = (TextView) convertView.findViewById(R.id.albumName);

		songName.setText(score.song.title);
		artistName.setText(score.artist.name);
		albumName.setText(score.release.name);

		Button addToPlaylistButton = convertView.findViewById(R.id.addToPlaylist);
		addToPlaylistButton.setVisibility(addButtonVisible ? View.VISIBLE : View.GONE);
		addToPlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onAddScore(score);
			}
		});

		Button removeFromPlaylistButton = convertView.findViewById(R.id.removeFromPlaylist);
		removeFromPlaylistButton.setVisibility(removeButtonVisible ? View.VISIBLE : View.GONE);
		removeFromPlaylistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onRemoveScore(score);
			}
		});

		return convertView;
	}

	public void setRemoveButtonVisible(Boolean isVisible)
	{
		removeButtonVisible = isVisible;
	}

	public void setAddButtonVisible(Boolean isVisible)
	{
		addButtonVisible = isVisible;
	}

	public void SetScoresAdapterListener(ScoresAdapterListener listener) {
		this.listener = listener;
	}
}
