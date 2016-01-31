package eXigence.network.messages;

import eXigence.domain.ExigenceCharacter;

public interface IEditCharacterRequest extends IIncomingMessage
{
	/**
	 * Liefert ein ExigenceCharacter Objekt mit den zu ändernden Daten
	 * @return Doe Referenz auf ein Characterobjekt, welches die neuen Daten hält
	 */
	public ExigenceCharacter getNewCharacterData();
}
