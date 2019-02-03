package com.example.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MusicFragment extends Fragment {

	public interface MusicFragmentListener {
		public void onAddScoreToPlaylist(Playlist playlist, Score score);
		public void onRemoveScoreFromPlaylist(Playlist playlist, Score score);
	}

	private MusicFragmentListener listener;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);

		Bundle bundle = getArguments();
		ArrayList<Score> scores = bundle.getParcelableArrayList("scores");

		ScoresAdapter scoresAdapter = new ScoresAdapter(getActivity(), scores);
		scoresAdapter.SetScoresAdapterListener(new ScoresAdapter.ScoresAdapterListener() {
			@Override
			public void onAddScore(Score score) {
			}

			@Override
			public void onRemoveScore(Score score) {

			}
		});

		ListView listView = view.findViewById(R.id.musicListView);

		listView.setAdapter(scoresAdapter);
		listView.setOnItemClickListener(listItemListener);


		return view;
	}

	ListView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int a = 1;
		}
	};

	public void setMusicFragmentListener(MusicFragmentListener listener) {
		this.listener = listener;
	}

}
