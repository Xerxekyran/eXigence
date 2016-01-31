package eXigence.network.messages;

public interface IDeleteCharacterRequest extends IIncomingMessage
{
	/**
	 * Getter für die ID des zu löschenden Characters
	 * @return ID des Characters
	 */
	public int getCharacterID();
}
