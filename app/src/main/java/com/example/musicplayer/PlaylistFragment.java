package com.example.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Class showing list of scores in selected playlist
 */

public class PlaylistFragment extends Fragment {
	public interface PlaylistFragmentListener {
		void onGoBack();
	}

	private PlaylistFragmentListener listener;
	private ArrayList<Score> scores;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_playlist, container, false);

		TextView playlistName = view.findViewById(R.id.playlistName);
		playlistName.setText(Session.getCurrentPlaylist().name);

		Button goBackButton = view.findViewById(R.id.goBackButton);
		goBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onGoBack();
			}
		});

		scores = Session.getCurrentPlaylistScores();
		ScoresAdapter scoresAdapter = new ScoresAdapter(getActivity(), scores);
		scoresAdapter.setAddButtonVisible(false);
		scoresAdapter.setRemoveButtonVisible(true);

		ListView listView = view.findViewById(R.id.musicListView);
		listView.setAdapter(scoresAdapter);
		listView.setOnItemClickListener(listItemListener);

		return view;
	}

	public void setPlaylistFragmentListener(PlaylistFragmentListener listener) {
		this.listener = listener;
	}

	ListView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Session.playScore(scores.get(position), true);
		}
	};

}
