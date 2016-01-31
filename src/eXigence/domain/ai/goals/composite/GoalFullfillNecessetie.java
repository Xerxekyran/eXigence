package eXigence.domain.ai.goals.composite;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.Telegram;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.atomic.GoalInteractWithBuilding;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Highlevel Ziel um das Bedürfnis Hunger zu befriedigen
 * 
 * @author Lars George
 * 
 */
public class GoalFullfillNecessetie extends GoalComposite
{

	private ENecessities	necessetie;
	private ExigenceBuilding destinationBuilding;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalFullfillNecessetie(NonPlayerCharacter owner,
			ENecessities necessetie)
	{
		super(owner, EGoalTypes.GoalFullfillNecessetie);

		this.necessetie = necessetie;		
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void activate()
	{
		status = EGoalStatus.active;

		// Einen Weg zum nächsten Gebäude anfordern, welches das gewünschte
		// Bedürfnis unterstützt
		destinationBuilding = owner.getPathPlanner().requestPathToBuilding(necessetie);
		if (destinationBuilding == null)
		{
			LogWriter.getInstance().logToFile(LogLevel.Warn,
					"GoalFullfillNecessetie ["+ owner.getFirstName() +", "+ owner.getLastName() +"]: No Path to Building found.");

			// Wenn das Gebäude nicht gefunden werden kann
			status = EGoalStatus.failed;
		}
		else
			addSubGoal(new GoalIdle(owner));
	}

	@Override
	public EGoalStatus process()
	{		
		activateIfInactive();

		status = processSubGoals();

		return status;
	}

	@Override
	public void terminate()
	{
	}

	@Override
	public boolean handleTelegram(Telegram tlg)
	{		
		// Nachricht erst mal nach untern reichen und gucken ob sie abgearbeitet
		// wird
		boolean handled = forwardTelegramToFrontMostSubgoal(tlg);

		// Wenn sie nicht behandelt, selber versuchen
		if (!handled)
		{
			switch (tlg.getType())
			{
			// Wenn der Weg fertig berechnet wurde
			case PathReady:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
					"GoalFullfillNecessetie ["+ owner.getFirstName() +", "+ owner.getLastName() +"]: Path ready to walk on.");
				
				
				// Kindziele entfernen
				removeAllSubGoals();
				
				// Am Ende dann das Ziel "Interagiere mit dem Gebäude" erstellen
				addSubGoal(new GoalInteractWithBuilding(owner, destinationBuilding, necessetie));
				
				// Das Ziel zum gehen auf dem Weg erstellen
				addSubGoal(new GoalFollowPath(owner,
						owner.getPathPlanner().getPath()));				
				
				// Nachricht konnte abgearbeitet werden
				handled = true;
				break;

			// Wenn kein Weg gefunden werden konnte
			case NoPathAvailable:
				LogWriter.getInstance().logToFile(LogLevel.Warn,
						"GoalFullfillNecessetie ["+ owner.getFirstName() +", "+ owner.getLastName() +"]: No Path available.");
				
				status = EGoalStatus.failed;
				handled = true;
				break;

			default:
				handled = false;
			}
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
