package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoresAdapter extends ArrayAdapter<Score> {
	public ScoresAdapter(Context context, ArrayList<Score> scores){
		super(context, 0, scores);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get data item for this position
		Score score = getItem(position);

		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_score, parent, false);
		}

		TextView songName = (TextView) convertView.findViewById(R.id.songName);
		TextView artistName = (TextView) convertView.findViewById(R.id.artistName);
		TextView albumName = (TextView) convertView.findViewById(R.id.albumName);

		songName.setText(score.song.title);
		artistName.setText(score.artist.name);
		albumName.setText(score.release.name);

		return convertView;
	}
}
