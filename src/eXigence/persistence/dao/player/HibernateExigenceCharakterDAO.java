package eXigence.persistence.dao.player;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import eXigence.domain.NonPlayerCharacter;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public class HibernateExigenceCharakterDAO implements IExigenceCharakterDAO
{
	private EntityManager	em	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void saveExistingExigenceCharacter(NonPlayerCharacter character)
	{
		// Entitymanager überprüfen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.merge(character);
			em.flush();
			//em.refresh(character);

			em.getTransaction().commit();
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Collection<NonPlayerCharacter> loadAllCharacters()
	{
		Collection<NonPlayerCharacter> retVal = new Vector<NonPlayerCharacter>();

		// Entitymanager überprüfen
		if (checkEntityManager())
		{
			// Alle Charaktere auslesen
			try
			{
				// Query ausführen
				Query q = em.createNamedQuery("getAllCharacters");				
				retVal = (Collection<NonPlayerCharacter>)q.getResultList();
			}
			catch (Exception e)
			{
				LogWriter.getInstance().logToFile(LogLevel.Error,
						"Error while loading the ExigenceCharacters: "
								+ e.toString());
			}
		}
		return retVal;
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
	 * Überprüft das vorhandensein des EntityManagers Schreibt ggf einen
	 * Logeintrag und gibt entsprechend einen boolischen Wert zurück true: Alles
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
