package eXigence.services;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.persistence.dao.player.IPlayerProfileDAO;

/**
 * Interface f�r Services, welche die Simulationsbedingten Methoden
 * bereitstellen
 * 
 * @author Lars George
 * 
 */
public interface IUserservice extends IService
{
	/**
	 * Methode zur �berpr�fung des Logins
	 * 
	 * @param loginname
	 *            Benutzername f�r das Profil
	 * @param password
	 *            Passwort des Benutzers
	 * @return Gibt bei korrekten Logindaten das dazugeh�rige Profil zur�ck,
	 *         wenn nicht wird null zur�ckgegeben
	 */
	public PlayerProfile validateLogin(String loginname, String password);

	/**
	 * �berpr�ft die Daten der Parameter auf g�ltigkeit in Bezug auf die
	 * Registrierung eines neuen Profils auf dem Server.<br />
	 * Wenn es mindestens eine Stelle gibt, an der die Daten der Registrierung
	 * nicht gen�gen, werden die Gr�nde hierf�r zur�ckgegeben.
	 * 
	 * @param loginname
	 *            Benutzername f�r das Profil
	 * @param password
	 *            Gew�nschtes Passwort des Benutzers
	 * @param email
	 *            EMail des Benutzers
	 * @return null wenn die Registrierung erfolgreich war, ansonsten ein
	 *         String[] mit den Fehlermeldungen
	 * 
	 */
	public String[] registerNewPlayerProfile(	String loginname,
												String password,
												String email);

	/**
	 * Speichert f�r ein schon vorhandenes Profil die Daten neu ab (bei evt
	 * �nderungen)
	 * 
	 * @param profile
	 *            das zu speichernde Profil
	 * @return angabe ob das speichern erfolgreich war (true) oder nicht (false)
	 */
	public boolean savePlayerProfile(PlayerProfile profile);

	/**
	 * Setter f�r das PlayerProfilDAO
	 * 
	 * @param profilDAO
	 *            profilDAO zum verwalten von PlayerProfilen auf der
	 *            Persistenzschicht
	 */
	public void setPlayerProfileDAO(IPlayerProfileDAO profilDAO);

	/**
	 * Loescht ein persistiertes Spielerprofil aus der Datenbank
	 * 
	 * @param profile
	 *            das zu loeschende Profil
	 * @return True wenn das Profile erfolgreich geloescht werden konnte,
	 *         ansonsten false
	 */
	public boolean deletePlayerProfile(PlayerProfile profile);

	/**
	 * L�scht einen Character aus der Persistenzschicht
	 * 
	 * @param character
	 *            der zu l�schende Character
	 * @return true wenn der character gel�scht werden konnte, ansonsten false
	 */
	public boolean deleteCharacter(NonPlayerCharacter character);
}
