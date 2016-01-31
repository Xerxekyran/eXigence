package eXigence.persistence.dao;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eXigence.persistence.dao.map.HibernateExigenceMapDAO;
import eXigence.persistence.dao.map.IExigenceMapDAO;
import eXigence.persistence.dao.player.HibernateExigenceCharakterDAO;
import eXigence.persistence.dao.player.HibernatePlayerProfileDAO;
import eXigence.persistence.dao.player.IExigenceCharakterDAO;
import eXigence.persistence.dao.player.IPlayerProfileDAO;

/**
 * 
 * @author Lars George
 * 
 */
public class DAOFactory
{
	@SuppressWarnings("unchecked")
	private HashMap<Class, IDAO>	daos		= new HashMap<Class, IDAO>();
	private EntityManagerFactory	emFactory;
	private EntityManager			em;
	private static DAOFactory		instance	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Privater Konstruktor, um das Singleton Pattern zu realisieren
	 */
	private DAOFactory()
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
			
		emFactory = Persistence.createEntityManagerFactory("eXigence");
		em = emFactory.createEntityManager();

		// ProfilDAO erstellen
		HibernatePlayerProfileDAO profil = new HibernatePlayerProfileDAO();
		profil.setEntityManager(em);
		
		// CharacterDAO erstellen
		HibernateExigenceCharakterDAO charakterDAO = new HibernateExigenceCharakterDAO();
		charakterDAO.setEntityManager(em);
		
		// WorldMapDAO erstellen
		HibernateExigenceMapDAO mapDAO = new HibernateExigenceMapDAO();
		mapDAO.setEntityManager(em);
		
		// DAOs in die Liste einfügen
		addDao(IPlayerProfileDAO.class, profil);
		addDao(IExigenceCharakterDAO.class, charakterDAO);
		addDao(IExigenceMapDAO.class, mapDAO);
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * 
	 * @param newClass
	 * @param dao
	 */
	@SuppressWarnings("unchecked")
	private void addDao(Class newClass, IDAO dao)
	{
		daos.put(newClass, dao);
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode um die Singleton Instanz zu erhalten
	 * 
	 * @return Liefert die Referenz zu der DAOFactory
	 */
	public static DAOFactory getInstance()
	{
		if (instance == null)
			instance = new DAOFactory();

		return instance;
	}

	/**
	 * 
	 * @param type
	 *            Klasse des DAOS welches angefordert werden soll
	 * @return Konkretes DAO-Objekt vom Typ der übergebenen Klasse
	 */
	@SuppressWarnings("unchecked")
	public IDAO getDaoForClass(Class type)
	{
		return daos.get(type);
	}
}
