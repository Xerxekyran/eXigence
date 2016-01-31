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
	 * L�dt ein PlayerProfil Objekt aus der Datenbank, zu dem �bergeben
	 * Benutzername passt
	 * 
	 * @param usrName
	 *            Benutzername des zu suchenden Profils
	 * @return Null wenn es keinen Benutzer mit diesem Namen gibt, ansonsten das
	 *         dazu geh�rige PlayerProfil Objekt
	 */
	public PlayerProfile loadPlayerProfil(String usrName);

	/**
	 * L�scht ein Profil aus der Datenbank
	 * 
	 * @param pProfil
	 *            Das zu l�schende Profil
	 */
	public void deleteProfil(PlayerProfile pProfil);
	
	/**
	 * L�scht einen Character aus der Persistenzschicht
	 * 
	 * @param character
	 *            der zu l�schenden Character
	 */
	public void deleteExigenceCharacter(NonPlayerCharacter character);
}
