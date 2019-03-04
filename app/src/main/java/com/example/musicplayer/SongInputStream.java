package com.example.musicplayer;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import com.google.gson.JsonObject;

import java.util.concurrent.Semaphore;


public class SongInputStream extends InputStream {
	/**
	 * Total number of bytes in the file
	 */
	protected int total = 0;
	/**
	 * Marker
	 */
	protected int mark = 0;
	/**
	 * Current reading position
	 */
	protected int pos = 0;
	/**
	 * It stores a buffer with FRAGMENT_SIZE bytes for the current reading.
	 * This variable is useful for UDP sockets. Thus bur is the datagram
	 */
	protected byte buf[];
	/**
	 * It prepares for the next buffer. In UDP sockets you can read nextbufer
	 * while buf is in use
	 */
	protected byte nextBuf[];
	/**
	 * It is used to read the buffer
	 */
	protected int fragment = 0;
	protected static final int FRAGMENT_SIZE = 8192;
	/**
	 * File name to stream
	 */
	protected String filename;

	protected SongInputStreamListener listener;

	Semaphore sem;


	public SongInputStream(String filename, SongInputStreamListener listener) throws IOException {
		sem = new Semaphore(1);
		try {
			sem.acquire();
		} catch (InterruptedException exc) {
			System.out.println(exc);
		}

		this.listener = listener;
		this.filename = filename;

		this.buf = new byte[FRAGMENT_SIZE];
		this.nextBuf = new byte[FRAGMENT_SIZE];

		GetFileSizeTask task = new GetFileSizeTask(filename);
		task.execute();
	}

	/**
	 * Reads the next byte of data from the input stream.
	 */
	@Override
	public synchronized int read() throws IOException {

		if (pos >= total) {
			pos = 0;
			return -1;
		}
		int posmod = pos % FRAGMENT_SIZE;
		if (posmod == 0) {
			try {
				sem.acquire();
			} catch (InterruptedException exc) {
				System.out.println(exc);
			}
			for (int i = 0; i < FRAGMENT_SIZE; i++)
				buf[i] = nextBuf[i];


			GetSongChunkTask task = new GetSongChunkTask(filename, fragment);
			task.execute();
			fragment++;
		}
		int p = pos % FRAGMENT_SIZE;
		pos++;
		return buf[p] & 0xff;
	}

	/**
	 * Reads some number of bytes from the input stream and stores them
	 * into the buffer array b.
	 */
	@Override
	public synchronized int read(byte b[], int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		}

		if (pos >= total) {
			return -1;
		}
		int avail = total - pos;
		if (len > avail) {
			len = avail;
		}
		if (len <= 0) {
			return 0;
		}
		for (int i = off; i < off + len; i++)
			b[i] = (byte) read();
		return len;
	}

	/**
	 * Skips over and discards n bytes of data from this input stream.
	 */
	@Override
	public synchronized long skip(long n) throws IOException {
		long k = total - pos;
		if (n < k) {
			k = n < 0 ? 0 : n;
		}

		pos += k;
		fragment = (int) Math.floor(pos / FRAGMENT_SIZE);

		GetSongChunkTask task = new GetSongChunkTask(filename, fragment);
		task.execute();

		fragment++;
		task = new GetSongChunkTask(filename, fragment);
		task.execute();

		return k;
	}

	/**
	 * Returns an estimate of the number of bytes that can be read
	 * (or skipped over) from this input stream without blocking by
	 * the next invocation of a method for this input stream.
	 */
	@Override
	public synchronized int available() {
		return total - pos;
	}

	/**
	 * Tests if this input stream supports the mark and reset methods.
	 */
	@Override
	public boolean markSupported() {
		return true;
	}

	/**
	 * Marks the current position in this input stream.
	 */
	@Override
	public void mark(int readAheadLimit) {
		mark = pos;
	}

	/**
	 * Repositions this stream to the position at the time the
	 * mark method was last called on this input stream.
	 */
	@Override
	public synchronized void reset() throws IOException {
		pos = mark;
		fragment = (int) Math.floor(pos / FRAGMENT_SIZE);

		GetSongChunkTask task = new GetSongChunkTask(filename, fragment);
		task.execute();

		fragment++;
		task = new GetSongChunkTask(filename, fragment);
		task.execute();
	}

	/**
	 * Closes this input stream and releases any system resources
	 * associated with the stream.
	 */
	@Override
	public void close() throws IOException {
	}

	private void gotFileSize(Integer size) {
		this.total = size;
		GetSongChunkTask task = new GetSongChunkTask(filename, 0);
		task.execute();
	}

	class GetFileSizeTask extends AsyncTask<Void, Void, Integer> {

		private String filename;

		public GetFileSizeTask(String filename) {
			this.filename = filename;
		}

		@Override
		protected Integer doInBackground(Void... voids) {

			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "SongService");
			request.addProperty("methodName", "getFileSize");
			JsonObject param = new JsonObject();
			param.addProperty("key", filename);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);
			return response.get("ret").getAsInt();
		}

		@Override
		protected void onPostExecute(Integer size) {
			gotFileSize(size);
		}
	}

	private void gotSongChunk(Integer fragment, byte[] buf) {
		nextBuf = buf;
		sem.release();

		if (fragment == 0) {
			this.listener.onReady(this);
			fragment++;
		}
	}

	class GetSongChunkTask extends AsyncTask<Void, Void, byte[]> {

		private String filename;
		private Integer fragment;

		public GetSongChunkTask(String filename, Integer fragment) {
			this.filename = filename;
			this.fragment = fragment;
		}

		@Override
		protected byte[] doInBackground(Void... voids) {

			JsonObject request = new JsonObject();
			request.addProperty("serviceName", "SongService");
			request.addProperty("methodName", "getSongChunk");
			JsonObject param = new JsonObject();
			param.addProperty("key", filename);
			param.addProperty("fragment", fragment);
			request.add("param", param);

			UDPConnection connection = new UDPConnection(Session.serverIp, Session.serverPort);
			JsonObject response = connection.execute(request);

			String s = response.get("ret").getAsString();
			return Base64.getDecoder().decode(s);
		}

		@Override
		protected void onPostExecute(byte[] buf) {
			gotSongChunk(fragment, buf);
		}
	}
}