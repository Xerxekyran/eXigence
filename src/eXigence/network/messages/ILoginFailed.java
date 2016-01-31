package eXigence.network.messages;

public interface ILoginFailed extends IOutgoingMessage
{
	/**
	 * Setter f�r den Grund des ung�ltigen Logins
	 * @param reason Grund f�r den Fehlschlag des Logins
	 */
	public void setReason(String reason);
}
