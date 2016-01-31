package eXigence.domain.entities.buildings;

import javax.persistence.Entity;

import eXigence.domain.ENecessities;
import eXigence.domain.ETelegramType;
import eXigence.domain.ExigenceCharacter;
import eXigence.domain.PlayerExitsBuildingTelegram;
import eXigence.domain.TelegramDispatcher;
import eXigence.util.math.Vector2D;

@Entity
public class Toilet extends ExigenceBuilding
{
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public Toilet(Vector2D position, Vector2D heading)
	{		
		super(position, heading, "", 0, 0);
		
		loadValuesFromConfig("Toilet");
	}
	
	/**
	 * Standardkonstruktor f�r Hibernate
	 */
	public Toilet() 
	{
		loadValuesFromConfig("Toilet");		
	}
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public ENecessities getNecessityType()
	{
		return ENecessities.Strangury;
	}
	
	@Override
	public boolean enterBuilding(ExigenceCharacter interactingPlayer)
	{
		if(currentlyFreeSlots <= 0 || interactingPlayer.getMoney() < getCostMoneyAsCustomer())
			return false;
		
		// Die entsprechenden Bed�rfnisse ver�ndern
		interactingPlayer.setNecessitiyStrangury(0);
		interactingPlayer.setMoney(interactingPlayer.getMoney() - getCostMoneyAsCustomer());
		
		// Platz besetzen
		currentlyFreeSlots--;
		
		// Zeitlich versetztes Telegram zum verlassen des Geb�udes versenden
		TelegramDispatcher.getInstance().DispatchTelegram(stayTimeMS, 
				TelegramDispatcher.SENDER_ID_IRRELEVANT, 
				interactingPlayer.getID(), 
				ETelegramType.ExitingBuilding, 
				new PlayerExitsBuildingTelegram(this, false));
		
		return true;
		
	}
	@Override
	public boolean enterBuildingAsEmployee(ExigenceCharacter interactingPlayer)
	{
		return false;
		/*
		if(currentlyFreeEmployeeSlots <= 0)
			return false;
		
		// Geld verdienen
		interactingPlayer.setMoney(interactingPlayer.getMoney() + gainMoneyAsEmployee);
		
		// Arbeiten ist langweilig und macht m�de
		interactingPlayer.setNecessitiyEnjoyment(interactingPlayer.getNecessitiyEnjoyment() + 10);
		interactingPlayer.setNecessitiySleep(interactingPlayer.getNecessitiySleep() + 10);
		
		// Arbeitsplatz besetzen
		currentlyFreeEmployeeSlots--;
		
		// Zeitlich versetztes Telegram zum verlassen des Geb�udes versenden
		TelegramDispatcher.getInstance().DispatchTelegram(employeeStayTimeMS, 
				TelegramDispatcher.SENDER_ID_IRRELEVANT, 
				interactingPlayer.getID(), 
				ETelegramType.ExitingBuilding, 
				null);
		
		return true;*/
		
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
