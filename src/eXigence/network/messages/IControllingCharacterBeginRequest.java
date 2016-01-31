package eXigence.network.messages;

public interface IControllingCharacterBeginRequest  extends IIncomingMessage
{
	/**
	 * Ermittelt die ID des zu steurnden Characters
	 * @return die ID des Characters
	 */
	public int getCharacterID();
}
