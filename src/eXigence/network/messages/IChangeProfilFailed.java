package eXigence.network.messages;

public interface IChangeProfilFailed extends IOutgoingMessage
{
	/**
	 * Setter für den Grund des Scheiterns des Speichervorgangs
	 * @param reason Grund für den Fehlschlag der Speicherung
	 */
	public void setReason(String reason);
}
