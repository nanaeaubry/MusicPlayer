package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicFragment extends Fragment {

	public interface MusicFragmentListener {
		void onPlayScore(Score score);
	}

	private MusicFragmentListener listener;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);

		ScoresAdapter scoresAdapter = new ScoresAdapter(getActivity(), Session.scores);

		ListView listView = view.findViewById(R.id.musicListView);
		listView.setAdapter(scoresAdapter);
		listView.setOnItemClickListener(listItemListener);

		return view;
	}

	ListView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			listener.onPlayScore(Session.scores.get(position));
		}
	};

	public void setMusicFragmentListener(MusicFragmentListener listener) {
		this.listener = listener;
	}

}
