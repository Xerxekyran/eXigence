package eXigence.persistence.dao.player;

import java.util.Collection;

import eXigence.domain.NonPlayerCharacter;
import eXigence.persistence.dao.IHibernateDAO;

/**
 * 
 * @author Lars George
 * 
 */
public interface IExigenceCharakterDAO extends IHibernateDAO
{
	/**
	 * L�d alle Charaktere die es in der Persistenzschicht existieren und gibt
	 * diese zur�ck
	 * 
	 * @return Eine Sammlung von ExigenceCharacteren (NPC und Spieler)
	 */
	public Collection<NonPlayerCharacter> loadAllCharacters();

	/**
	 * Speichert einen schon existierenden Character persistent ab
	 * 
	 * @param character
	 *            Der zu speichernde Character
	 */
	public void saveExistingExigenceCharacter(NonPlayerCharacter character);


}
