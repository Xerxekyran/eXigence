package eXigence.network.messages;

public interface IDeleteCharacterFailed extends IOutgoingMessage
{
	/**
	 * Grund für das scheitern setzen
	 * @param reason Grund warum der Character nicht gelöscht werden konnte
	 */
	public void setReason(String reason);
}
