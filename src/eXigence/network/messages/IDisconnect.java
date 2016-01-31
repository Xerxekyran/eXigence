package eXigence.network.messages;

public interface IDisconnect extends IIncomingMessage, IOutgoingMessage
{
	/**
	 * Setter f�r den Grund des Disconnects
	 * @param reason Textnachricht der den Grund der Disconnect Nachricht angibt
	 */
	public void setReason(String reason);
	
	/**
	 * Getter f�r den Grund der Disconnect Nachricht
	 * @return Den Grund f�r diese Nachricht
	 */
	public String getReason();
}
