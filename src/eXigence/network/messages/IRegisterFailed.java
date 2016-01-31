package eXigence.network.messages;

public interface IRegisterFailed extends IOutgoingMessage
{
	/**
	 * Setter f�r die Gr�nde, warum die Registrierung nicht funktionier hat
	 * 
	 * @param reasons
	 *            Auflistung der Fehler, die die Registrierung verhindert haben
	 */

	public void setReasons(String[] reasons);

}
