package de.developup.roboterarm.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler {
	private Socket clientSocket;
	ISocketMessageListiner listiner;
	public boolean connected = false;
	public ClientHandler(String host,int port, ISocketMessageListiner listiner) throws IOException {
        try {
            this.clientSocket = new Socket(host,port);

			if (clientSocket.isConnected()) {
				this.connected = true;
				System.out.println("ist verbunden");
			}
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
        this.listiner=listiner;
	}
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
}
