package com.example.musicplayer;

import android.support.v4.app.Fragment;

public class PlaylistFragment extends Fragment {
	public interface PlaylistFragmentListener{
		void onGoBack();
	}

	private PlaylistFragmentListener listener;

	public void setPlaylistFragmentListener(PlaylistFragmentListener listener){
		this.listener = listener;
	}
}
