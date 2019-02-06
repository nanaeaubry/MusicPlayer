package com.example.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Class showing list of playlists attainable from navigation bar.
 */
public class PlaylistsFragment extends Fragment {

	public interface PlaylistsFragmentListener {
		void onShowPlaylist(Playlist playlist);
	}

	private PlaylistsFragmentListener listener;
	private Context context;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		this.context = container.getContext();

		View view = inflater.inflate(R.layout.fragment_playlists, container, false);

		TextView currentUserName = view.findViewById(R.id.currentUserName);
		User user = Session.getCurrentUser();
		currentUserName.setText(user.firstName + " " + user.lastName);

		Button addPlaylistButton = view.findViewById(R.id.addPlaylist);
		addPlaylistButton.setOnClickListener(addPlaylistListener);

		PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(getActivity(), Session.getPlaylists());

		ListView listView = view.findViewById(R.id.playlistsView);
		listView.setAdapter(playlistsAdapter);
		listView.setOnItemClickListener(listItemListener);

		return view;
	}

	/**
	 * Get playlist from sessions. List of playlists depends on user
	 */
	ListView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			listener.onShowPlaylist(Session.getPlaylists().get(position));
		}
	};

	/**
	 * Add a playlist by tapping plus icon. User will enter the name of the playlist
	 * and be prompted to accept or cancel.
	 */
	View.OnClickListener addPlaylistListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Enter Playlist Name");

			final EditText input = new EditText(context);
			input.setInputType(InputType.TYPE_CLASS_TEXT);

			builder.setView(input);

			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = input.getText().toString();
					if (name.length() <= 0) {
						dialog.cancel();
						return;
					}

					Playlist playlist = new Playlist();
					playlist.name = name;
					Session.getPlaylists().add(playlist);
					Session.saveUsers();
				}
			});

			builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();


		}
	};

	public void setPlaylistsFragmentListener(PlaylistsFragmentListener listener) {
		this.listener = listener;
	}

}
