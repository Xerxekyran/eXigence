package eXigence.domain.ai.goals.atomic;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.Telegram;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.Goal;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public class GoalInteractWithBuilding extends Goal
{
	private ExigenceBuilding	building;
	private ENecessities 		necessities;
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * 
	 * @param owner Der Besitzer des Ziels
	 * @param building Das Gebäude mit dem interagiert werden soll
	 */
	public GoalInteractWithBuilding(NonPlayerCharacter owner, ExigenceBuilding building, ENecessities necessetie)
	{
		super(owner, EGoalTypes.GoalInteractWithBuilding);
		this.building = building;
		this.necessities = necessetie;
	}
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void activate()
	{
		status = EGoalStatus.active;
		
		LogWriter.getInstance().logToFile(LogLevel.Debug, "["+ owner.getFirstName() +"] Trying to interact with Building ["+ building.getModelName() +"]");
		
		// Versuchen das Gebäude zu betreten
		if(!building.tryInteractWithBuilding(owner))
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug, "["+ owner.getFirstName() +"] interacting failed.");
			
			// Wenn kein Eingang gefunden werden konnte oder alle voll sind
			status = EGoalStatus.failed;
		}
		else
		{
			// Unsichtbar machen, weil er ja im Gebäude nun ist
			owner.setVisible(false);
			LogWriter.getInstance().logToFile(LogLevel.Debug, "["+ owner.getFirstName() +"] successfully interacted with building, now waiting for ExitingBuilding Telegram.");			
		}
	}

	@Override
	public EGoalStatus process()
	{
		// Aktivieren wenn inaktiv
		activateIfInactive();
				  
		return status;
	}

	@Override
	public void terminate()
	{
	}
	
	@Override
	public boolean handleTelegram(Telegram tlg)
	{
		boolean handled = false;
		
		switch(tlg.getType())
		{
			case ExitingBuilding:
				LogWriter.getInstance().logToFile(LogLevel.Debug, "["+ owner.getFirstName() +"] exiting Building Telegram received.");
				
				// Das Gebäude entsprechend verlassen
				if(necessities.equals(ENecessities.Money))
				{
					building.exitBuildingAsEmployee();
				}
				else
				{
					building.exitBuilding();
				}
				
				// Wieder sichtbar machen
				owner.setVisible(true);
				
				// Ziel ist komplett erfüllt worden
				status = EGoalStatus.completed;
				
				// Nachricht wurde abgearbeitet
				handled = true;				
			break;
		
		}
		return handled;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

}
