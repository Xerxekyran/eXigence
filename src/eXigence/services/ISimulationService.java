package eXigence.services;

import eXigence.core.Simulation;
import eXigence.persistence.dao.map.IExigenceMapDAO;
import eXigence.persistence.dao.player.IExigenceCharakterDAO;

public interface ISimulationService extends IService
{
	/**
	 * Lädt die Objekte für die Simulation aus der Persistenzschicht und meldet
	 * alles entsprechend beim EntityManager an
	 * 
	 * @param sim
	 *            Eine Simulationsinstanz, welche die geladenen Objekte aus der
	 *            Persistenzschicht bekommen soll
	 * @return True wenn der Ladevorgang erfolgreich war
	 */
	public boolean loadWholeWorldFromDB(Simulation sim);

	/**
	 * Speichert die aktuellen Objekte der Simulation persistent ab
	 * 
	 * @param simulation
	 *            Referenz auf die Simulation die abgespeichert werden soll
	 * @return True wenn der Speichervorgang erfolgreich war, ansonsten false
	 */
	public boolean saveCurrentWorldData(Simulation simulation);

	/**
	 * Setter für das ExigenceCharacterDAO
	 * 
	 * @param characterDAO
	 *            characterDAO zum verwalten von PlayerProfilen auf der
	 *            Persistenzschicht
	 */
	public void setExigenceCharacterDAO(IExigenceCharakterDAO characterDAO);

	/**
	 * Setter für das ExigenceBuildingDAO
	 * 
	 * @param buildingDAO
	 *            buildingDAO zum verwalten von ExigenceBuildingObjekten auf der
	 *            Persistenzschicht
	 */
	public void setExigenceBuildingDAO(IExigenceMapDAO buildingDAO);
}
