package eXigence.services;

import java.security.InvalidKeyException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import eXigence.core.Simulation;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.ai.PathPlanner;
import eXigence.domain.ai.SteeringBehaviour;
import eXigence.domain.entities.EntityManager;
import eXigence.persistence.dao.DAOFactory;
import eXigence.persistence.dao.map.IExigenceMapDAO;
import eXigence.persistence.dao.player.IExigenceCharakterDAO;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Implementierun des Service für Simulationsrelevante Vorgänge
 * 
 * @author Lars George
 * 
 */
public class SimulationService implements ISimulationService
{
	private IExigenceCharakterDAO	characterDAO	= null;
	private IExigenceMapDAO	mapDAO		= null;

	public SimulationService()
	{
		// Hole und setze ein PlayerProfilDAO
		setExigenceCharacterDAO((IExigenceCharakterDAO) DAOFactory.getInstance().getDaoForClass(IExigenceCharakterDAO.class));
		setExigenceBuildingDAO((IExigenceMapDAO) DAOFactory.getInstance().getDaoForClass(IExigenceMapDAO.class));
	}

	@Override
	public boolean loadWholeWorldFromDB(Simulation sim)
	{
		LogWriter.getInstance().logToFile(LogLevel.Debug, "Loading World from persistence...");		
		
		// Die WorldMap einladen
		sim.setMap(mapDAO.loadMapWithID(1));		
		
		if(sim.getMap() == null)
			return false;
		
		LogWriter.getInstance().logToFile(LogLevel.Debug, "Successfully loaded " + sim.getMap().getBuildings().size() + " buildings.");
		
		// Alle Charactere auslesen lassen
		Collection<NonPlayerCharacter> characters = characterDAO.loadAllCharacters();

		// Liste von NPCs anlegen
		ConcurrentLinkedQueue<NonPlayerCharacter> npcs = new ConcurrentLinkedQueue<NonPlayerCharacter>();		
		
		// Über alle Charactere iterieren
		Iterator<NonPlayerCharacter> it = characters.iterator();
		while (it.hasNext())
		{
			// Nächstes Element
			NonPlayerCharacter tmpChar = it.next();

			try
			{
				// Der Liste hinzufügen
				npcs.add(tmpChar);

				// Steeringbehaviour erzeugen und setzen
				tmpChar.setSteering(new SteeringBehaviour(sim.getMap(), tmpChar));
				
				// PathPlanner Instanz erzeugen und setzen
				tmpChar.setPathPlanner(new PathPlanner(tmpChar, sim.getMap()));
				
				// Simulation zu der er gehört setzen
				tmpChar.setHomeSimulation(sim);
				
				// GoalEvaluatoren initialisieren
				tmpChar.getBrain().initGoalEvaluators();
				
				// Den NPC mit dem EntityManager registrieren
				EntityManager.getInstance().registerNewEntity(tmpChar);
	
				
			}
			catch (InvalidKeyException e)
			{
				LogWriter.getInstance().logToFile(LogLevel.Error,
						"Error while loading the npcs from persistence. Loading an Entity with an already used ID: "
								+ e.toString());
				return false;
			}
		}
		LogWriter.getInstance().logToFile(LogLevel.Debug, "Successfully loaded " + npcs.size() + " characters.");
		
		// NPCs bei der Simulation anmelden
		sim.setNpcs(npcs);
		
		return true;
	}

	@Override
	public boolean saveCurrentWorldData(Simulation simulation)
	{
		// Alle NPCs abspeichern
		Iterator<NonPlayerCharacter> it = simulation.getNpcs().iterator();
		while (it.hasNext())
		{
			NonPlayerCharacter tmpChar = it.next();		
			
			// Den Character persistent abspeichern
			characterDAO.saveExistingExigenceCharacter(tmpChar);
		}

		// Die Karte abspeichern (mit allen Gebäuden und nicht interactiven Elementen)
		mapDAO.saveExistingWorldMap(simulation.getMap());		

		return true;
	}

	@Override
	public void setExigenceCharacterDAO(IExigenceCharakterDAO characterDAO)
	{
		this.characterDAO = characterDAO;
	}

	@Override
	public void setExigenceBuildingDAO(IExigenceMapDAO buildingDAO)
	{
		this.mapDAO = buildingDAO;
	}

}
