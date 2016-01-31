package eXigence.domain.entities.buildings;

import javax.persistence.Entity;

import eXigence.domain.ENecessities;
import eXigence.domain.ETelegramType;
import eXigence.domain.ExigenceCharacter;
import eXigence.domain.PlayerExitsBuildingTelegram;
import eXigence.domain.TelegramDispatcher;
import eXigence.util.math.Vector2D;

@Entity
public class Gym extends ExigenceBuilding
{
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public Gym(Vector2D position, Vector2D heading)
	{		
		super(position, heading, "", 0, 0);
		
		loadValuesFromConfig("Gym");
	}
	
	/**
	 * Standardkonstruktor für Hibernate
	 */
	public Gym() 
	{
		loadValuesFromConfig("Gym");		
	}
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public ENecessities getNecessityType()
	{
		return ENecessities.Fitness;
	}
	
	@Override
	public boolean enterBuilding(ExigenceCharacter interactingPlayer)
	{
		if(currentlyFreeSlots <= 0 || interactingPlayer.getMoney() < getCostMoneyAsCustomer())
			return false;
		
		// Die entsprechenden Bedürfnisse verändern
		interactingPlayer.setNecessitiyFitness(interactingPlayer.getNecessitiyFitness() - 40);
		interactingPlayer.setMoney(interactingPlayer.getMoney() - getCostMoneyAsCustomer());
		interactingPlayer.setFitness(interactingPlayer.getFitness()+1);
		
		// Platz besetzen
		currentlyFreeSlots--;
		
		// Zeitlich versetztes Telegram zum verlassen des Gebäudes versenden
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
		if(currentlyFreeEmployeeSlots <= 0)
			return false;
		
		// Geld verdienen
		interactingPlayer.setMoney(interactingPlayer.getMoney() + gainMoneyAsEmployee);
		
		// Arbeiten ist langweilig und macht müde
		interactingPlayer.setNecessitiyEnjoyment(interactingPlayer.getNecessitiyEnjoyment() + 10);
		interactingPlayer.setNecessitiySleep(interactingPlayer.getNecessitiySleep() + 10);
		
		// Arbeitsplatz besetzen
		currentlyFreeEmployeeSlots--;
		
		// Zeitlich versetztes Telegram zum verlassen des Gebäudes versenden
		TelegramDispatcher.getInstance().DispatchTelegram(employeeStayTimeMS, 
				TelegramDispatcher.SENDER_ID_IRRELEVANT, 
				interactingPlayer.getID(), 
				ETelegramType.ExitingBuilding, 
				new PlayerExitsBuildingTelegram(this, true));
		
		return true;
		
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

}
