package eXigence.network.messages;

public interface IControllingCharacterBeginFailed extends IOutgoingMessage
{
	/**
	 * Den Grund des Fehlschlagens setzen
	 * @param reason Der Grund
	 */
	public void setReason(String reason);
}
