package com.example.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_profile, container, false);


		PlaylistsFragment playlistsFragment = new PlaylistsFragment();
		playlistsFragment.setPlaylistsFragmentListener(playlistsFragmentListener);

		getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, playlistsFragment).commit();
		return view;
	}

	PlaylistsFragment.PlaylistsFragmentListener playlistsFragmentListener = new PlaylistsFragment.PlaylistsFragmentListener() {
		@Override
		public void onShowPlaylist(Playlist playlist) {
			PlaylistFragment playlistFragment = new PlaylistFragment();
			playlistFragment.setPlaylistFragmentListener(playlistFragmentListener);
			getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, playlistFragment).commit();
		}
	};


	PlaylistFragment.PlaylistFragmentListener playlistFragmentListener = new PlaylistFragment.PlaylistFragmentListener() {
		@Override
		public void onGoBack() {
			PlaylistsFragment playlistsFragment = new PlaylistsFragment();
			playlistsFragment.setPlaylistsFragmentListener(playlistsFragmentListener);

			getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, playlistsFragment).commit();
		}
	};
}
