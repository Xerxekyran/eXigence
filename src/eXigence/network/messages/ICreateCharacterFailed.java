package eXigence.network.messages;

public interface ICreateCharacterFailed extends IOutgoingMessage
{
	/**
	 * Setter für den Grund des Fehlschlagens.
	 * @param reason Warum konnte der Character nicht erstellt werden
	 */
	public void setReason(String reason);
}
