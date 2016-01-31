package eXigence.network.messages;

import eXigence.domain.ExigenceCharacter;

public interface ICreateCharacterRequest extends IIncomingMessage
{
	/**
	 * Getter f�r einen Character der erstellt werden soll
	 * @return Der zu erstellende Character
	 */
	public ExigenceCharacter getCharacter();
}
