package eXigence.network.messages;

public interface IChangeProfilFailed extends IOutgoingMessage
{
	/**
	 * Setter f�r den Grund des Scheiterns des Speichervorgangs
	 * @param reason Grund f�r den Fehlschlag der Speicherung
	 */
	public void setReason(String reason);
}
