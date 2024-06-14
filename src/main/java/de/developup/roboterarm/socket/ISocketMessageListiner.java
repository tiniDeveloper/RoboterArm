package de.developup.roboterarm.socket;


public abstract class ISocketMessageListiner {
	public abstract void onByteMessage(byte[] incommengByteArray);
	public abstract void onConnecctionClosed(String reason);
}
