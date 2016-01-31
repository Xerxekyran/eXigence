package eXigence.services;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.persistence.dao.DAOFactory;
import eXigence.persistence.dao.player.IPlayerProfileDAO;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Implementierung eines Userservice. Bietet Methode zum verarbeiten von
 * Benutzern
 * 
 * @author Lars George
 * 
 */
public class Userservice implements IUserservice
{
	private IPlayerProfileDAO	profilDAO	= null;

	public Userservice()
	{
		// Hole und setze ein PlayerProfilDAO
		setPlayerProfileDAO((IPlayerProfileDAO) DAOFactory.getInstance().getDaoForClass(IPlayerProfileDAO.class));
	}

	@Override
	public PlayerProfile validateLogin(String username, String password)
	{
		PlayerProfile playerProfil = profilDAO.loadPlayerProfil(username);

		// Wenn die Eingaben übereinstimmen, das dazugehörige Profil
		// zurückliefern
		if (playerProfil != null && playerProfil.getPassword().equals(password))
			return playerProfil;

		return null;
	}

	@Override
	public void setPlayerProfileDAO(IPlayerProfileDAO profilDAO)
	{
		this.profilDAO = profilDAO;
	}

	@Override
	public String[] registerNewPlayerProfile(	String loginname,
												String password,
												String email)
	{
		ArrayList<String> reasons = new ArrayList<String>(0);

		// Überprüfen ob es diesen Benutzer schon gibt
		PlayerProfile tmpProfil = this.profilDAO.loadPlayerProfil(loginname);

		if (tmpProfil != null)
			reasons.add("Diesen Benutzernamen gibt es bereits.\n");

		// Wenn nicht alle Informationen vollständig sind
		if (loginname == "" || password == "" || email == "")
		{
			reasons.add("Bitte füllen Sie alle Felder komplett aus.\n");
		}

		// EMail Format validieren
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);
		if (!m.matches())
		{
			reasons.add("Kein gültiges EMail-Format.\n");
		}

		// Wenn es keine Fehler gab Spieler registrieren
		if (reasons.size() == 0)
		{
			// Profil anhand der Daten erstellen
			PlayerProfile newProfile = new PlayerProfile();
			newProfile.setEMail(email);
			newProfile.setIsGameMaster(false);
			newProfile.setLoginName(loginname);
			newProfile.setPassword(password);
			newProfile.setPlayerCharacters(null);

			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"Userservice::setPlayerProfilDAO() -> Saving new PlayerProfile "
							+ "[loginname: " + loginname + "], "
							+ "[password: " + password + "], " + "[email: "
							+ email + "]");

			// Profil abspeichern
			this.profilDAO.saveNewPlayerProfil(newProfile);
			return null;
		}
		// Ansonsten die Fehler
		else
			return reasons.toArray(new String[0]);
	}

	@Override
	public boolean savePlayerProfile(PlayerProfile profile)
	{
		boolean retVal = false;
		try
		{
			profilDAO.saveExistingPlayerProfil(profile);
			retVal = true;
		}
		catch (Exception exc)
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"Userservice::saveExistingPlayerProfil() -> Error while saving an existing Profile");
			retVal = false;
		}

		return retVal;
	}

	@Override
	public boolean deletePlayerProfile(PlayerProfile profile)
	{		
		boolean retVal = false;
		try
		{
			profilDAO.deleteProfil(profile);
			retVal = true;
		}
		catch (Exception exc)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"Userservice::deletePlayerProfile() -> Error while deleting an existing profile");
			retVal = false;
		}

		return retVal;
	}

	@Override
	public boolean deleteCharacter(NonPlayerCharacter character)
	{
		boolean retVal = false;
		try
		{
			profilDAO.deleteExigenceCharacter(character);
			retVal = true;
		}
		catch (Exception exc)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"Userservice::deleteCharacter() -> Error while deleting character");
			retVal = false;
		}

		return retVal;
	}
}
