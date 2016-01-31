package eXigence.persistence.dao.player;

import javax.persistence.EntityManager;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * DAOKlasse f�r PlayerProfile als Hibernate implementierung
 * 
 * @author Lars George
 * 
 */
public class HibernatePlayerProfileDAO implements IPlayerProfileDAO
{
	private EntityManager	em	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	@Override
	public PlayerProfile loadPlayerProfil(String loginName)
	{
		PlayerProfile retVal = null;

		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{			
			// Den Benutzer versuchen zu finden
			try
			{
				retVal = em.find(PlayerProfile.class, loginName);
			}
			catch (Exception e)
			{
				LogWriter.getInstance().logToFile(LogLevel.Error,
						"Fehler beim laden des Player-Profils: " + e.toString());
			}
		}
		return retVal;
	}

	@Override
	public void saveNewPlayerProfil(PlayerProfile newProfil)
	{
		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.persist(newProfil);
			em.flush();
			em.refresh(newProfil);

			em.getTransaction().commit();
		}
	}

	@Override
	public void saveExistingPlayerProfil(PlayerProfile pProfil)
	{

		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.merge(pProfil);
			em.flush();
			em.refresh(pProfil);

			em.getTransaction().commit();
		}
	}

	@Override
	public void deleteProfil(PlayerProfile pProfil)
	{

		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.remove(pProfil);
			em.flush();

			em.getTransaction().commit();
		}
	}
	
	@Override
	public void deleteExigenceCharacter(NonPlayerCharacter character)
	{
		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.remove(character);
			em.flush();			

			em.getTransaction().commit();
		}
	}

	@Override
	public void setEntityManager(EntityManager entityManager)
	{
		this.em = entityManager;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * �berpr�ft das vorhandensein des EntityManagers Schreibt ggf einen
	 * Logeintrag und gibt entsprechend einen boolischen Wert zur�ck true: Alles
	 * OK false: Etwas stimmt nicht mit der Persistenzeinheit
	 */
	private boolean checkEntityManager()
	{
		// Wenn der Entitymanager nicht gesetzt wurde
		if (em == null)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"Es existiert keine EntityManager.");
			return false;
		}
		return true;
	}
	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

}
