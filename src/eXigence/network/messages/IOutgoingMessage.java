package eXigence.network.messages;

public interface IOutgoingMessage extends IMessage
{
	/**
	 * 
	 * @return Liefert einen String der die Nachricht repr�sentiert um �ber das Netzwerk versendet werden zu k�nnen
	 */
	public String toNetworkString();
}
