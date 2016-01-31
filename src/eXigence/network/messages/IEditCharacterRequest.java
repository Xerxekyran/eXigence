package eXigence.network.messages;

import eXigence.domain.ExigenceCharacter;

public interface IEditCharacterRequest extends IIncomingMessage
{
	/**
	 * Liefert ein ExigenceCharacter Objekt mit den zu �ndernden Daten
	 * @return Doe Referenz auf ein Characterobjekt, welches die neuen Daten h�lt
	 */
	public ExigenceCharacter getNewCharacterData();
}
