package eXigence.persistence.dao.map;

import eXigence.domain.map.WorldMap;
import eXigence.persistence.dao.IHibernateDAO;

public interface IExigenceMapDAO extends IHibernateDAO
{
	/**
	 * Speichert die Welt persistent ab
	 * 
	 * @param map
	 *            Die zu speichernde Karte
	 */
	public void saveExistingWorldMap(WorldMap map);

	/**
	 * Speichert ein zuvor noch nicht in der Persistenzschicht geschriebenes
	 * Objekt ab
	 * 
	 * @param map
	 *            das zu persistierende Objekt
	 */
	public void saveNewWorldMap(WorldMap map);

	/**
	 * Lädt die Map mit der entsprechenden ID aus der Datenbank
	 * 
	 * @param id
	 *            id der Karte
	 * @return Die geladene Map, null wenn keine entsprechende gefunden wurde
	 */
	public WorldMap loadMapWithID(int id);
}
