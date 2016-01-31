package eXigence.network.messages;

public interface IOutgoingMessage extends IMessage
{
	/**
	 * 
	 * @return Liefert einen String der die Nachricht repräsentiert um über das Netzwerk versendet werden zu können
	 */
	public String toNetworkString();
}
