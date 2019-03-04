package com.example.musicplayer;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

public class UDPConnection {

	private static final int MAX_DATAGRAM_SIZE = 60000;
	private static final int LOCAL_PORT = 2049;

	private static Object sync = new Object();

	// Attributes
	// ----------------------------------------------
	private String serverIp;
	private int serverPort;

	// Methods
	// ----------------------------------------------

	// Constructor
	public UDPConnection(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	// Opens the socket for listening
	public JsonObject execute(JsonObject request) {

		do {
			long id = Instant.now().getEpochSecond();
			JsonObject json = new JsonObject();
			json.addProperty("id", id);
			json.add("execute", request);
			byte[] requestData = json.toString().getBytes();

			DatagramSocket socket = null;
			try {
				InetAddress serverAddress = InetAddress.getByName(serverIp);
				DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, serverPort);

				DatagramPacket responsePacket;
				synchronized (sync) {

					socket = new DatagramSocket(LOCAL_PORT);
					socket.send(requestPacket);

					byte[] responseData = new byte[MAX_DATAGRAM_SIZE];
					responsePacket = new DatagramPacket(responseData, responseData.length);

					socket.setSoTimeout(10000);
					socket.receive(responsePacket);
				}

				String responseString = new String(responsePacket.getData()).substring(0, responsePacket.getLength());

				JsonParser parser = new JsonParser();
				JsonObject responseJson = parser.parse(responseString).getAsJsonObject();

				long responseId = responseJson.get("id").getAsLong();
				if (responseId != id) {
					return null;
				}

				return responseJson.get("execute").getAsJsonObject();

			} catch (SocketTimeoutException e) {
				Log.d("UDPConnection", "Try again: " + request.toString());
				continue;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return null;
			} catch (SocketException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		} while (true);

	}
}// End Class

