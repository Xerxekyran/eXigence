package eXigence.network.messages;

public interface IControllingCharacterEndFailed extends IOutgoingMessage
{
	/**
	 * Den Grund des Fehlschlagens setzen
	 * @param reason Der Grund
	 */
	public void setReason(String reason);
}
