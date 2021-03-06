package com.example.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Class that holds the profiles of each user
 */
public class ProfileFragment extends Fragment {

	FragmentManager fragmentManager;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		fragmentManager = getFragmentManager();

		PlaylistsFragment playlistsFragment = new PlaylistsFragment();
		playlistsFragment.setPlaylistsFragmentListener(new PlaylistsFragment.PlaylistsFragmentListener() {
			@Override
			public void onShowPlaylist(String playlistName) {
				showPlaylist(playlistName);
			}
		});

		fragmentManager.beginTransaction().replace(R.id.fragment_container, playlistsFragment).commit();
		return view;
	}

	/**
	 * When a playlist is tapped it will go to the playlist fragment and a back button arrow
	 * will give users option to go back
	 * @param playlistName
	 */
	private void showPlaylist(String playlistName) {
		PlaylistFragment playlistFragment = new PlaylistFragment();
		playlistFragment.setPlaylistName(playlistName);
		playlistFragment.setPlaylistFragmentListener(new PlaylistFragment.PlaylistFragmentListener() {
			@Override
			public void onGoBack() {
				showPlaylists();
			}
		});
		fragmentManager.beginTransaction().replace(R.id.fragment_container, playlistFragment).commit();
	}

	/**
	 * Show list of playlists
	 */
	private void showPlaylists() {
		PlaylistsFragment playlistsFragment = new PlaylistsFragment();
		playlistsFragment.setPlaylistsFragmentListener(new PlaylistsFragment.PlaylistsFragmentListener() {
			@Override
			public void onShowPlaylist(String playlist) {
				showPlaylist(playlist);
			}
		});
		fragmentManager.beginTransaction().replace(R.id.fragment_container, playlistsFragment).commit();
	}

}
