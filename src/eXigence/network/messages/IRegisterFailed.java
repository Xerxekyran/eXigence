package eXigence.network.messages;

public interface IRegisterFailed extends IOutgoingMessage
{
	/**
	 * Setter für die Gründe, warum die Registrierung nicht funktionier hat
	 * 
	 * @param reasons
	 *            Auflistung der Fehler, die die Registrierung verhindert haben
	 */

	public void setReasons(String[] reasons);

}
