package com.example.musicplayer;

import java.io.InputStream;

public interface SongInputStreamListener {
	void onReady(InputStream stream);
}
