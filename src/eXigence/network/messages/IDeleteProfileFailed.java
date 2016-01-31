package eXigence.network.messages;

public interface IDeleteProfileFailed extends IOutgoingMessage
{
	/**
	 * Setter für den Grund des Fehlers
	 * @param reason Grund für das Fehlschlagen des löschens eines Profils
	 */
	public void setReason(String reason);
}
