package eXigence.network.messages;

public interface ICreateCharacterFailed extends IOutgoingMessage
{
	/**
	 * Setter f�r den Grund des Fehlschlagens.
	 * @param reason Warum konnte der Character nicht erstellt werden
	 */
	public void setReason(String reason);
}
