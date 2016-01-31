package eXigence.services;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.persistence.dao.player.IPlayerProfileDAO;

/**
 * Interface für Services, welche die Simulationsbedingten Methoden
 * bereitstellen
 * 
 * @author Lars George
 * 
 */
public interface IUserservice extends IService
{
	/**
	 * Methode zur Überprüfung des Logins
	 * 
	 * @param loginname
	 *            Benutzername für das Profil
	 * @param password
	 *            Passwort des Benutzers
	 * @return Gibt bei korrekten Logindaten das dazugehörige Profil zurück,
	 *         wenn nicht wird null zurückgegeben
	 */
	public PlayerProfile validateLogin(String loginname, String password);

	/**
	 * Überprüft die Daten der Parameter auf gültigkeit in Bezug auf die
	 * Registrierung eines neuen Profils auf dem Server.<br />
	 * Wenn es mindestens eine Stelle gibt, an der die Daten der Registrierung
	 * nicht genügen, werden die Gründe hierfür zurückgegeben.
	 * 
	 * @param loginname
	 *            Benutzername für das Profil
	 * @param password
	 *            Gewünschtes Passwort des Benutzers
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
	 * Speichert für ein schon vorhandenes Profil die Daten neu ab (bei evt
	 * Änderungen)
	 * 
	 * @param profile
	 *            das zu speichernde Profil
	 * @return angabe ob das speichern erfolgreich war (true) oder nicht (false)
	 */
	public boolean savePlayerProfile(PlayerProfile profile);

	/**
	 * Setter für das PlayerProfilDAO
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
	 * Löscht einen Character aus der Persistenzschicht
	 * 
	 * @param character
	 *            der zu löschende Character
	 * @return true wenn der character gelöscht werden konnte, ansonsten false
	 */
	public boolean deleteCharacter(NonPlayerCharacter character);
}
