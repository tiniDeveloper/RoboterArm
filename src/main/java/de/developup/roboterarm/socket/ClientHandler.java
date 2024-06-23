package de.developup.roboterarm.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Klasse zum Verwalten der Client-Verbindung und Kommunikation mit dem Server.
 */
public class ClientHandler {
	private Socket clientSocket;
	ISocketMessageListiner listiner;
	public boolean connected = false;

	/**
	 * Konstruktor zum Erstellen einer Verbindung zum Server.
	 *
	 * @param host Hostname des Servers.
	 * @param port Port des Servers.
	 * @param listiner Listener für Nachrichten vom Server.
	 * @throws IOException wenn ein Verbindungsfehler auftritt.
	 */
	public ClientHandler(String host,int port, ISocketMessageListiner listiner) throws IOException {
        this.clientSocket = new Socket(host,port);

		if (clientSocket.isConnected()) {
			this.connected = true;
			System.out.println("ist verbunden");
		}
        this.listiner=listiner;
	}

	/**
	 * Sendet eine Nachricht an den Server und wartet auf eine Antwort.
	 *
	 * @param data Die zu sendende Nachricht als Byte-Array.
	 */
	public void sendAndWaitForResponse(byte[] data) {
		try {
			if(!clientSocket.isClosed()){
				InputStream in = clientSocket.getInputStream();
				OutputStream out = clientSocket.getOutputStream();
				out.write(data);
				byte[] buff;
				buff = in.readNBytes(9);
				if (buff.length>1) {

					this.listiner.onByteMessage(buff);
				}
			}else{
				this.connected=false;
				listiner.onConnecctionClosed("Socket closed");
			}
		}
		catch (Exception e) {
			try {
				this.clientSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				listiner.onConnecctionClosed(e1.getMessage());
			}
		}
	}
	public void closeConnection() throws IOException {
		this.clientSocket.close();
	}
}
