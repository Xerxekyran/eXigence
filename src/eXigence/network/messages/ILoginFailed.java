package eXigence.network.messages;

public interface ILoginFailed extends IOutgoingMessage
{
	/**
	 * Setter für den Grund des ungültigen Logins
	 * @param reason Grund für den Fehlschlag des Logins
	 */
	public void setReason(String reason);
}
