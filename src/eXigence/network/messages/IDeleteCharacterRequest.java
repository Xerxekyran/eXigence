package eXigence.network.messages;

public interface IDeleteCharacterRequest extends IIncomingMessage
{
	/**
	 * Getter f�r die ID des zu l�schenden Characters
	 * @return ID des Characters
	 */
	public int getCharacterID();
}
