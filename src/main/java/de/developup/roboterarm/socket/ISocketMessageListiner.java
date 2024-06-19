package de.developup.roboterarm.socket;

/**
 * Abstrakte Klasse zur Behandlung von Socket-Nachrichten.
 * Muss implementiert werden, wenn auf Nachrichten vom Socket-Server reagieren werden soll.
 */
public abstract class ISocketMessageListiner {

	/**
	 * Abstrakte Methode zur Verarbeitung von empfangenen Byte-Nachrichten.
	 *
	 * @param incommengByteArray Das empfangene Byte-Array (Nachricht vom Server).
	 */
	public abstract void onByteMessage(byte[] incommengByteArray);

	/**
	 * Abstrakte Methode mit Aufruf bei Verbindungsabbruch
	 *
	 * @param reason Grund für den Verbindungsabbruch.
	 */
	public abstract void onConnecctionClosed(String reason);
}
