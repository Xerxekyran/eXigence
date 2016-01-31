package eXigence.persistence.dao.map;

import javax.persistence.EntityManager;

import eXigence.domain.map.WorldMap;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public class HibernateExigenceMapDAO implements IExigenceMapDAO
{
	private EntityManager	em	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void saveExistingWorldMap(WorldMap map)
	{
		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.merge(map);
			em.flush();
			em.refresh(map);

			em.getTransaction().commit();
		}
	}

	@Override
	public void setEntityManager(EntityManager entityManager)
	{
		this.em = entityManager;
	}
	
	@Override
	public void saveNewWorldMap(WorldMap map)
	{
		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			em.getTransaction().begin();

			em.persist(map);
			em.flush();
			em.refresh(map);

			em.getTransaction().commit();
		}
	}
	
	@Override
	public WorldMap loadMapWithID(int id)
	{
		WorldMap retMap = null;
		
		// Entitymanager �berpr�fen
		if (checkEntityManager())
		{
			// Versuchen die Karte zu lesen
			try
			{
				// Query ausf�hren
				retMap = em.find(WorldMap.class, id);				
			}
			catch (Exception e)
			{
				LogWriter.getInstance().logToFile(LogLevel.Error,
						"Error while loading the WorldMap: "
								+ e.toString());
			}
		}
		return retMap;
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
