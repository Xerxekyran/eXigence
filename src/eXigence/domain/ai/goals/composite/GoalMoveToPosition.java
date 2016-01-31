package eXigence.domain.ai.goals.composite;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.Telegram;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.util.math.Vector2D;

/**
 * Highlevel Ziel um zu einer bestimmten Position zu gelangen. Es wird mit einem
 * Suchalgorithmus ein Weg (Liste von Kanten) berechnet und dieser dann
 * abgelaufen
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public class GoalMoveToPosition extends GoalComposite
{
	private Vector2D	destination;

	public GoalMoveToPosition(NonPlayerCharacter owner, Vector2D position)
	{
		super(owner, EGoalTypes.GoalMoveToPosition);
		this.destination = position;
	}

	@Override
	public void activate()
	{
		// Status auf aktiv setzen
		status = EGoalStatus.active;

		// Kindziele entfernen
		removeAllSubGoals();

		// Die Wegberechnung geschieht über mehrere Updatezyklen
		if (owner.getPathPlanner().requestPathToPosition(destination))
		{
			// TODO: Charakter ggf schon einmal einfach in Richtung Ziel gehen
			// lassen
		}

	}

	@Override
	public EGoalStatus process()
	{
		// aktivieren
		activateIfInactive();

		// Kindziele ausführen
		status = processSubGoals();

		// Wenn etwas schief gelaufen ist, neu initialisieren
		reactivateIfFailed();

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
				// Kindziele entfernen
				removeAllSubGoals();
				addSubGoal(new GoalFollowPath(owner,
						owner.getPathPlanner().getPath()));
				handled = true;
				break;

			// Wenn kein Weg gefunden werden konnte
			case NoPathAvailable:
				status = EGoalStatus.failed;
				handled = true;
				break;

			default:
				handled = false;
			}
		}
		return handled;
	}
}
