package eXigence.network.messages;

import java.util.Collection;

import eXigence.domain.NonPlayerCharacter;

public interface IUpdateCharacters extends IOutgoingMessage
{
	/**
	 * Setter f�r die zu aktualisierenden Charaktere
	 * @param characters Array von Characteren
	 */
	public void setCharacters(NonPlayerCharacter characters[]);
	
	/**
	 * Setter f�r die eigenen Charactere des Benutzers (diese bekommen auch die Priorit�ten �bertragen)
	 * @param playerCharacters Liste der Charactere des Benutzers
	 */
	public void setOwnCharacters(Collection<NonPlayerCharacter>	playerCharacters);
}
