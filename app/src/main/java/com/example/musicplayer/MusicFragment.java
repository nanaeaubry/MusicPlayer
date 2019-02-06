package com.example.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
/**
 * Class for music list page
 */
public class MusicFragment extends Fragment  {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);
		// Get scores to load into listview
		final ScoresAdapter scoresAdapter = new ScoresAdapter(getActivity(), Session.scores);

		ListView listView = view.findViewById(R.id.musicListView);
		listView.setAdapter(scoresAdapter);
		listView.setOnItemClickListener(listItemListener);

		// Set up filter for score list
		EditText inputFilter = view.findViewById(R.id.inputFilter);
		inputFilter.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// NO OP
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				scoresAdapter.getFilter().filter(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				//NO OP
			}
		});

		return view;
	}

	/**
	 * When score is clicked or tapped a song will play
	 */
	ListView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Session.playScore(Session.scores.get(position), false);
		}
	};

}
