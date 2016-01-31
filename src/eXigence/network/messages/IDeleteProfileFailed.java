package eXigence.network.messages;

public interface IDeleteProfileFailed extends IOutgoingMessage
{
	/**
	 * Setter f�r den Grund des Fehlers
	 * @param reason Grund f�r das Fehlschlagen des l�schens eines Profils
	 */
	public void setReason(String reason);
}
