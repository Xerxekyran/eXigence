package eXigence.domain.entities.buildings;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.jdom.Element;

import eXigence.core.ConfigLoader;
import eXigence.domain.ENecessities;
import eXigence.domain.ExigenceCharacter;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.VisibleEntity;
import eXigence.domain.map.WorldMap;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

@Entity
public abstract class ExigenceBuilding extends VisibleEntity
{
	@Basic
	private ENecessities	buildingType				= ENecessities.Education;

	@Basic
	protected double		width						= 0;

	@Basic
	protected double		length						= 0;

	@Transient
	protected int			maximumSlots				= 0;

	@Transient
	protected int			currentlyFreeSlots			= 0;

	@Transient
	protected int			maximumEmployeeSlots		= 0;

	@Transient
	protected int			currentlyFreeEmployeeSlots	= 0;

	@ManyToOne
	private WorldMap		homeMap						= null;

	@Transient
	protected Vector2D		entrancePos					= new Vector2D(0, 0);

	@Transient
	protected Vector2D		employeePos					= new Vector2D(0, 0);
	
	@Transient
	public static int		costMoneyAsCustomer			= 0;
	
	@Transient
	public static int		gainMoneyAsEmployee			= 0;
	
	@Transient
	protected int			stayTimeMS					= 0;
	
	@Transient
	protected int			employeeStayTimeMS			= 0;
	

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public ExigenceBuilding(Vector2D position,
			Vector2D heading,
			String modelName,
			double width,
			double length)
	{
		super(position, heading, modelName);
		this.width = width;
		this.length = length;
	}

	/**
	 * Standardkonstruktor für Hibernate
	 */
	public ExigenceBuilding()
	{
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Gibt an welches Bedürfnis durch dieses Gebäude befriedigt wird
	 * 
	 * @return Das Bedürfnis welches befriedigt wird
	 */
	public ENecessities getNecessityType()
	{
		return ENecessities.Nothing;
	}

	/**
	 * Methode welche den Character entsprechend der Eigenschaften des Gebäudes
	 * manipuliert (zB Bedürfnisse befriedigen). Sobald der Vorgang
	 * abgeschlossen ist, erhält der Character ein entsprechendes Telegram
	 * 
	 * @param interactingPlayer
	 *            Der Character, welcher das Gebäude betreten möchte
	 * @return False wenn das Gebäude nicht betreten werden kann (keine freien
	 *         Plätze mehr) ansonsten true
	 */
	public abstract boolean enterBuilding(ExigenceCharacter interactingPlayer);

	/**
	 * Methode welche den Character als Arbeiter in dem Gebäude behandelt und
	 * entsprechend seine Werte verändert. Sobald der Vorgang abgeschlossen ist,
	 * erhält der Character ein entsprechendes Telegram
	 * 
	 * @param interactingPlayer
	 *            Der Character, welcher in dem Gebäude arbeiten möchte
	 * @return False wenn das Gebäude nicht betreten werden kann (keine freien
	 *         Plätze mehr) ansonsten true
	 */
	public abstract boolean enterBuildingAsEmployee(ExigenceCharacter interactingPlayer);

	/**
	 * Methode welche aufgerufen wird, wenn ein Character das Gebäude verlässt
	 */
	public void exitBuilding()
	{
		setCurrentlyFreeSlots(currentlyFreeSlots +1);		
	}
	
	public void exitBuildingAsEmployee()
	{
		setCurrentlyFreeEmployeeSlots(currentlyFreeEmployeeSlots+1);		
	}
	
	/**
	 * Liefert die Position des Arbeiter-Eingangs in Weltkoordinaten
	 * 
	 * @return Position des EmployeeEntrance
	 */
	public Vector2D getAbsolutePositionOfEmployeeEntrance()
	{
		return (getPosition().add(getEmployeePos())).rotateBy(getHeading().getAngleInRad());
	}

	/**
	 * Liefert die Positino des Eingangs in Weltkoordinaten
	 * 
	 * @return Position des Entrance
	 */
	public Vector2D getAbsolutePositionOfEntrance()
	{
		return (getPosition().add(getEntrancePos())).rotateBy(getHeading().getAngleInRad());
	}

	/**
	 * Versucht den Character mit dem Gebäude interagieren zu lassen (testet
	 * beide Eingänge gegen die Position des Spielers) Wird ein Eingang gefunden
	 * so betritt der Character das Gebäude (wenn noch Platz ist)
	 * 
	 * @param interactingPlayer
	 *            Der Character, welcher mit dem Gebäude interagieren möchte
	 * @return True wenn es möglich ist zu interagieren, ansonsten false
	 */
	public boolean tryInteractWithBuilding(NonPlayerCharacter interactingPlayer)
	{
		boolean interacted = false;
		
		// Überprüfen ob er sich am normalen Eingang befindet
		if (interactingPlayer.isAtPosition(getAbsolutePositionOfEntrance(), 400))
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"[" + interactingPlayer.getFirstName()
							+ "] is at normal Entrance of " + getModelName());

			// Versuchden den Eingang zu benutzen
			if (enterBuilding(interactingPlayer))
			{
				// Der Eingang wurde erfolgreich betreten
				interacted = true;
			}
			
		}
		// Wenn nicht, dann noch den Arbeiterenigang überprüfen
		else if (interactingPlayer.isAtPosition(getAbsolutePositionOfEmployeeEntrance()))
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"[" + interactingPlayer.getFirstName()
							+ "] is at employee Entrance of " + getModelName());

			// Versuchden den Eingang zu benutzen
			if (enterBuildingAsEmployee(interactingPlayer))
			{
				// Der Eingang wurde erfolgreich betreten
				interacted = true;
			}
		}

		return interacted;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	protected void loadValuesFromConfig(String buildingName)
	{
		try
		{
			// Das entsprechende Konfigurationselement auslesen
			Element xmlConfigValues = ConfigLoader.getInstance().getConfigElement(buildingName);
			
			this.width = Double.parseDouble(xmlConfigValues.getChildText("Width"));
			this.length = Double.parseDouble(xmlConfigValues.getChildText("Length"));
			this.modelName = xmlConfigValues.getChildText("Modelname");
			this.maximumSlots = Integer.parseInt(xmlConfigValues.getChildText("Slots"));
			this.currentlyFreeSlots = maximumSlots;
			this.maximumEmployeeSlots = Integer.parseInt(xmlConfigValues.getChildText("EmployeeSlots"));
			this.currentlyFreeEmployeeSlots = maximumEmployeeSlots;
			this.entrancePos.setX(Double.parseDouble(xmlConfigValues.getChildText("EntranceX")));
			this.entrancePos.setY(Double.parseDouble(xmlConfigValues.getChildText("EntranceY")));
			this.employeePos.setX(Double.parseDouble(xmlConfigValues.getChildText("EmployeeEntranceX")));
			this.employeePos.setY(Double.parseDouble(xmlConfigValues.getChildText("EmployeeEntranceY")));			
			
			ExigenceBuilding.costMoneyAsCustomer = Integer.parseInt(xmlConfigValues.getChildText("CostMoneyAsCustomer"));
			ExigenceBuilding.gainMoneyAsEmployee = Integer.parseInt(xmlConfigValues.getChildText("GainMoneyAsEmployee"));
			this.stayTimeMS = Integer.parseInt(xmlConfigValues.getChildText("StaytimeSeconds"));
			this.stayTimeMS *= 1000;
			this.employeeStayTimeMS = Integer.parseInt(xmlConfigValues.getChildText("EmployeeStaytimeSeconds"));
			this.employeeStayTimeMS *= 1000;
			
		}
		catch(Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error, "Can not read config values for Building: "+ e.toString());
		}
	}
	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

	public ENecessities getBuildingType()
	{
		return buildingType;
	}

	public void setBuildingType(ENecessities buildingType)
	{
		this.buildingType = buildingType;
	}

	public double getWidth()
	{
		return width;
	}

	public void setWidth(double width)
	{
		this.width = width;
	}

	public double getLength()
	{
		return length;
	}

	public void setLength(double length)
	{
		this.length = length;
	}

	public WorldMap getHomeMap()
	{
		return homeMap;
	}

	public void setHomeMap(WorldMap homeMap)
	{
		this.homeMap = homeMap;
	}
	
	public Vector2D getEntrancePos(ENecessities necessetie)
	{
		if(necessetie.equals(ENecessities.Money))
			return getEmployeePos();
		else
			return getEntrancePos();
	}

	public Vector2D getEntrancePos()
	{
		return entrancePos;
	}

	public void setEntrancePos(Vector2D entrancePos)
	{
		this.entrancePos = entrancePos;
	}

	public Vector2D getEmployeePos()
	{
		return employeePos;
	}

	public void setEmployeePos(Vector2D employeePos)
	{
		this.employeePos = employeePos;
	}

	public int getMaximumSlots()
	{
		return maximumSlots;
	}

	public void setMaximumSlots(int maximumSlots)
	{
		this.maximumSlots = maximumSlots;
	}

	public int getCurrentlyFreeSlots()
	{
		return currentlyFreeSlots;
	}

	public void setCurrentlyFreeSlots(int currentlyFreeSlots)
	{
		// Nicht mehr als Max zulassen
		if(currentlyFreeSlots > maximumSlots)
			currentlyFreeSlots = maximumSlots;
		
		this.currentlyFreeSlots = currentlyFreeSlots;
	}

	public int getMaximumEmployeeSlots()
	{
		return maximumEmployeeSlots;
	}

	public void setMaximumEmployeeSlots(int maximumEmployeeSlots)
	{
		this.maximumEmployeeSlots = maximumEmployeeSlots;
	}

	public int getCurrentlyFreeEmployeeSlots()
	{
		return currentlyFreeEmployeeSlots;
	}

	public void setCurrentlyFreeEmployeeSlots(int currentlyFreeEmployeeSlots)
	{
		// Nicht mehr als Max zulassen
		if(currentlyFreeEmployeeSlots > maximumEmployeeSlots)
			currentlyFreeEmployeeSlots = maximumEmployeeSlots;
		
		this.currentlyFreeEmployeeSlots = currentlyFreeEmployeeSlots;
	}

	public int getCostMoneyAsCustomer()
	{
		return costMoneyAsCustomer;
	}

	public void setCostMoneyAsCustomer(int costMoneyAsCustomer)
	{
		ExigenceBuilding.costMoneyAsCustomer = costMoneyAsCustomer;
	}

	public int getGainMoneyAsEmployee()
	{
		return gainMoneyAsEmployee;
	}

	public void setGainMoneyAsEmployee(int gainMoneyAsEmployee)
	{
		ExigenceBuilding.gainMoneyAsEmployee = gainMoneyAsEmployee;
	}

}
