package eXigence.network.messages;

public interface IDisconnect extends IIncomingMessage, IOutgoingMessage
{
	/**
	 * Setter für den Grund des Disconnects
	 * @param reason Textnachricht der den Grund der Disconnect Nachricht angibt
	 */
	public void setReason(String reason);
	
	/**
	 * Getter für den Grund der Disconnect Nachricht
	 * @return Den Grund für diese Nachricht
	 */
	public String getReason();
}
