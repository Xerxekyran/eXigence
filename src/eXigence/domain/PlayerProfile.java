package eXigence.domain;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Repräsentiert einen Spieler in einem Spiel
 * 
 * @author Lars George
 * 
 */
@Entity
public class PlayerProfile
{
	@Id
	@Column(name = "ID_loginName", nullable = false, length = 30)
	private String						loginName			= "";

	@Basic
	@Column(name = "password", nullable = false, length = 30)
	private String						password			= "";

	@Basic
	@Column(name = "eMail", nullable = false, length = 50)
	private String						eMail				= "";

	@Basic
	private Boolean						isGameMaster		= false;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Collection<NonPlayerCharacter>	playerCharacters	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Konstruktor zum anlegen von Playerobjekten
	 */
	public PlayerProfile(String loginName,
			String pw,
			String eMail,
			boolean isGameMaster,
			Collection<NonPlayerCharacter> characters)
	{

		this.loginName = loginName;
		this.password = pw;
		this.eMail = eMail;
		this.isGameMaster = isGameMaster;
		this.playerCharacters = characters;
	}

	/**
	 * Standardkonstruktor für das Persistenzframework
	 */
	public PlayerProfile()
	{
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public String getLoginName()
	{
		return loginName;
	}

	public void setLoginName(String loginName)
	{
		this.loginName = loginName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEMail()
	{
		return eMail;
	}

	public void setEMail(String mail)
	{
		eMail = mail;
	}

	public Boolean getIsGameMaster()
	{
		return isGameMaster;
	}

	public void setIsGameMaster(Boolean isGameMaster)
	{
		this.isGameMaster = isGameMaster;
	}

	public Collection<NonPlayerCharacter> getPlayerCharacters()
	{
		return playerCharacters;
	}

	public void setPlayerCharacters(Collection<NonPlayerCharacter> playerCharacters)
	{
		this.playerCharacters = playerCharacters;
	}
}
