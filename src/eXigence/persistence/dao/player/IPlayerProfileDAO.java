package eXigence.persistence.dao.player;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.persistence.dao.IHibernateDAO;

/**
 * 
 * @author Lars George
 * 
 */
public interface IPlayerProfileDAO extends IHibernateDAO
{
	/**
	 * Speichert ein noch nicht in der Datenbank existierendes PlayerProfil.
	 * 
	 * @param newProfil
	 *            Das neu zu speichernde PlayerProfil<br />
	 *            Achtung: Es darf dieses Profil so noch nicht in der Datenbank
	 *            geben!
	 */
	public void saveNewPlayerProfil(PlayerProfile newProfil);

	/**
	 * Speichert ein Spielerprofil
	 * 
	 * @param pProfil
	 *            Objekt das es zu persisieren gilt
	 */
	public void saveExistingPlayerProfil(PlayerProfile pProfil);

	/**
	 * Lädt ein PlayerProfil Objekt aus der Datenbank, zu dem übergeben
	 * Benutzername passt
	 * 
	 * @param usrName
	 *            Benutzername des zu suchenden Profils
	 * @return Null wenn es keinen Benutzer mit diesem Namen gibt, ansonsten das
	 *         dazu gehörige PlayerProfil Objekt
	 */
	public PlayerProfile loadPlayerProfil(String usrName);

	/**
	 * Löscht ein Profil aus der Datenbank
	 * 
	 * @param pProfil
	 *            Das zu löschende Profil
	 */
	public void deleteProfil(PlayerProfile pProfil);
	
	/**
	 * Löscht einen Character aus der Persistenzschicht
	 * 
	 * @param character
	 *            der zu löschenden Character
	 */
	public void deleteExigenceCharacter(NonPlayerCharacter character);
}
