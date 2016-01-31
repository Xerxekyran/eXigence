package eXigence.domain.entities;

import java.security.InvalidKeyException;
import java.util.HashMap;

import eXigence.core.Simulation;
import eXigence.domain.NonInteractiveObject;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Bank;
import eXigence.domain.entities.buildings.Bathhouse;
import eXigence.domain.entities.buildings.Cinema;
import eXigence.domain.entities.buildings.EBuildingTypes;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.domain.entities.buildings.Graveyard;
import eXigence.domain.entities.buildings.Gym;
import eXigence.domain.entities.buildings.Hospital;
import eXigence.domain.entities.buildings.Hotel;
import eXigence.domain.entities.buildings.Library;
import eXigence.domain.entities.buildings.Police;
import eXigence.domain.entities.buildings.Restaurant;
import eXigence.domain.entities.buildings.Toilet;
import eXigence.domain.map.WorldMap;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

public class EntityManager
{
	// Eine Liste von Entities, welche erzeugt wurden
	private HashMap<Integer, BaseEntity>	entities;

	// Singleton Instanz
	private static EntityManager			instance	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	private EntityManager()
	{
		// Hashmap initialisieren
		entities = new HashMap<Integer, BaseEntity>();
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	/**
	 * Liefert ein Entity zu der entsprechende übergebenen ID. Null wenn keine
	 * Entity gefunden wurde
	 * 
	 * @param id
	 *            ID des Entities
	 * @return Referenz auf ein von BaseEntity abgeleitet Objekt mit der
	 *         entsprechenden ID
	 */
	public BaseEntity getEntityForID(int id)
	{
		return entities.get(id);
	}

	/**
	 * Registriert das Entity mit dem Manager. Es werden keine doppelten
	 * Einträge zugelassen
	 * 
	 * @param newEnt
	 *            das zu registrierende Entity
	 * @throws InvalidKeyException
	 *             Wirft eine entsprechende Exception, wenn ein Element schon
	 *             mit dieser ID existiert
	 */
	public void registerNewEntity(BaseEntity newEnt) throws InvalidKeyException
	{
		// Überprüfen ob es schon ein Entity mit dieser ID gibt
		if (getEntityForID(newEnt.getID()) != null)
			throw new InvalidKeyException(
					"Cannot register new Entity: Entity with this ID allready exists ("
							+ newEnt.getID() + ")");

		// Das neue Entity registrieren
		entities.put(newEnt.getID(), newEnt);
	}

	/**
	 * Erstellt aus den übergebenen Daten einen NPC und meldet ihn beim
	 * EntitiyManager an
	 * 
	 * @param firstName
	 *            Vorname des NPC
	 * @param lastName
	 *            Nachname des NPC
	 * @param simulation
	 *            Die Simulations, auf der der NPC existieren soll
	 * @return Den neu erstellten Charakter
	 * @throws InvalidKeyException
	 *             Wenn Vorname und Nachname so schon existieren (dies ist die
	 *             Grundlage für die eindeutige ID im System)
	 */
	public NonPlayerCharacter createNewNPC(	String firstName,
											String lastName,
											Simulation simulation)
			throws InvalidKeyException
	{
		// Neues NPC Objekt anlegen
		NonPlayerCharacter newChar = new NonPlayerCharacter(simulation.getMap());
		newChar.setFirstName(firstName);
		newChar.setLastName(lastName);
		newChar.setModelName("spielfigur");
		newChar.setPosition(new Vector2D(1.0, 1.0));
		newChar.setHeading(new Vector2D(1.0, 0.0));
		newChar.setMale(true);
		newChar.setHomeSimulation(simulation);

		// ID anhand des Namens generieren
		newChar.setID((firstName.concat(lastName)).hashCode());

		// NPC anmelden
		registerNewEntity(newChar);

		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Created new NPC: ID[" + newChar.getID() + "], FirstName["
						+ newChar.getFirstName() + "], LastName["
						+ newChar.getLastName() + "]");
		return newChar;
	}

	/**
	 * Erstellt ein neues Gebäudeobjekt vom angegeben Typ und registriert es
	 * beim EntityManager
	 * 
	 * @param position
	 *            position des Gebäudes
	 * @param heading
	 *            Blickrichtung des Gebäudes
	 * @param buildingType
	 *            Art des Gebäudes
	 * @param homeMap
	 *            Karte auf der das Gebäude stehen soll
	 * @return Referenz auf die neue Instanz des angeforderten GEbäudes
	 * @throws InvalidKeyException
	 */
	public ExigenceBuilding createNewBuilding(	Vector2D position,
												Vector2D heading,
												EBuildingTypes buildingType,
												WorldMap homeMap)
			throws InvalidKeyException
	{
		// Neues Objekt anhand des Typs anlegen
		ExigenceBuilding newBuilding;

		switch (buildingType)
		{
		case Restaurant:
			newBuilding = new Restaurant(position, heading);
			break;
		case Friedhof:
			newBuilding = new Graveyard(position, heading);
			break;
		case Hotel:
			newBuilding = new Hotel(position, heading);
			break;
		case Kino:
			newBuilding = new Cinema(position, heading);
			break;
		case Krankenhaus:
			newBuilding = new Hospital(position, heading);
			break;
		case Police:
			newBuilding = new Police(position, heading);
			break;
		case Toilet:
			newBuilding = new Toilet(position, heading);
			break;
		case Bank:
			newBuilding = new Bank(position, heading);
			break;
		case Bathhouse:
			newBuilding = new Bathhouse(position, heading);
			break;
		case Gym:
			newBuilding = new Gym(position, heading);
			break;
		case Library:
			newBuilding = new Library(position, heading);
			break;

		default:
			newBuilding = null;
		}

		// Wenn kein Gebäude erstellt werden konnte abbrechen
		if (newBuilding == null)
			throw new InvalidKeyException(
					"This Type of Building is not yet supported");

		// Eindeutige ID berechnen (Es darf an einer Position nur ein Gebäude
		// geben
		newBuilding.setID((position.getX() + "_" + position.getY()).hashCode());

		newBuilding.setHomeMap(homeMap);

		// Anmelden
		registerNewEntity(newBuilding);

		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Created new Building: ID[" + newBuilding.getID()
						+ "], Modelname[" + newBuilding.getModelName() + "]");

		return newBuilding;
	}

	/**
	 * Erstellt ein neues nicht interaktives Objekt und registriert es mit dem
	 * EntityManager
	 * 
	 * @param position
	 *            Position des Objekts
	 * @param heading
	 *            Blickrichtung des Objekts
	 * @param modelName
	 *            Modelnamen des Objekts
	 * @return Die Referenz zur erstellten Instanz des NonInteractiveObject
	 */
	public NonInteractiveObject createNewNonInteractiveObject(	Vector2D position,
																Vector2D heading,
																String modelName,
																WorldMap homeMap)
			throws InvalidKeyException
	{
		// Neues Objekt anlegen
		NonInteractiveObject newObjekt = new NonInteractiveObject();
		newObjekt.setModelName(modelName);
		newObjekt.setPosition(position);
		newObjekt.setHeading(heading);
		newObjekt.setHomeMap(homeMap);

		// Eindeutige ID berechnen (Es darf an einer Position nur ein Objekt
		// geben
		newObjekt.setID((position.getX() + "_" + position.getY()).hashCode());

		// anmelden
		registerNewEntity(newObjekt);

		return newObjekt;
	}

	/**
	 * Entfernt das entsprechende Entity aus der Liste des EntityManagers
	 * 
	 * @param ent
	 *            Das zu entfernende Entity
	 */
	public void removeEntity(BaseEntity ent)
	{
		entities.remove(ent);
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	/**
	 * Liefert die Singleton Instanz
	 * 
	 * @return Einzige Referenz auf einen EntityManager
	 */
	public static EntityManager getInstance()
	{
		if (instance == null)
			instance = new EntityManager();

		return instance;
	}

}
