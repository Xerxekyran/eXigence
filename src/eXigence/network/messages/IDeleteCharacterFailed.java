package eXigence.network.messages;

public interface IDeleteCharacterFailed extends IOutgoingMessage
{
	/**
	 * Grund f�r das scheitern setzen
	 * @param reason Grund warum der Character nicht gel�scht werden konnte
	 */
	public void setReason(String reason);
}
