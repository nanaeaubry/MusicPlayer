package com.example.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class MusicFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);

		Bundle scoresBundle = getArguments();
		ArrayList<Score> scores = scoresBundle.getParcelableArrayList("scores");

		ScoresAdapter scoresAdapter = new ScoresAdapter(getActivity(), scores);
		ListView listView = view.findViewById(R.id.musicListView);

		listView.setAdapter(scoresAdapter);

		return view;
	}


}
