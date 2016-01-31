package eXigence.domain.ai.goals.composite;

import java.util.Iterator;
import java.util.LinkedList;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.Telegram;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.Goal;

/**
 * Composite Goals sind Ziele mit einer beliebigen Anzahl von Unterzielen
 * (Composite Pattern)
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public abstract class GoalComposite extends Goal
{
	protected LinkedList<Goal>	subGoals;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalComposite(NonPlayerCharacter owner, EGoalTypes type)
	{
		super(owner, type);
		subGoals = new LinkedList<Goal>();
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public boolean handleTelegram(Telegram tlg)
	{
		return forwardTelegramToFrontMostSubgoal(tlg);
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Entfernt und beendet alle Unterziele
	 */
	public void removeAllSubGoals()
	{
		Iterator<Goal> it = subGoals.iterator();
		while (it.hasNext())
		{
			// Ziel beenden
			it.next().terminate();
		}

		// Liste löschen
		subGoals.clear();
	}

	/**
	 * Diese Methode entfernt Ziele die am Anfang der Liste stehen, welche
	 * fertig sind und ruft entsprechend ggf das nächste auf
	 * 
	 * @return den aktuellen Status
	 */
	public EGoalStatus processSubGoals()
	{
		// Alle Ziele entfernen die vorne sind und complete oder failed
		while (!subGoals.isEmpty()
				&& (subGoals.getFirst().getStaus().equals(EGoalStatus.completed) || subGoals.getFirst().getStaus().equals(EGoalStatus.failed)))
		{
			subGoals.getFirst().terminate();
			subGoals.pollFirst();
		}

		// Wenn es noch Ziele gibt
		if (!subGoals.isEmpty())
		{
			// Vorderstes Ziel ausführen und status holen
			EGoalStatus subgoalStatus = subGoals.getFirst().process();

			// Sonderfall beachten, wo das erste Ziel completed ist, jedoch noch
			// weitere Unterziele existieren
			if (subgoalStatus.equals(EGoalStatus.completed) && subGoals.size() > 1)
			{
				return EGoalStatus.active;
			}

			return subgoalStatus;
		}
		else
		{
			// Keine weiteren Ziele zu bearbeiten
			return EGoalStatus.completed;
		}
	}

	/**
	 * Fügt ein neues Ziel am Anfang der Liste ein
	 * 
	 * @param newGoal
	 *            das neue Ziel
	 */
	public void addSubGoal(Goal newGoal)
	{
		// Neues Ziel am Anfang einfügen
		subGoals.addFirst(newGoal);
	}

	/**
	 * Gibt das Telegram an das Unterziel weiter, welches an erster Stelle in
	 * der Liste steht
	 * 
	 * @param tlg
	 *            Das zu bearbeitende Telegram
	 * @return True wenn das Telegram bearbeitet werden konnte, ansonsten false
	 */
	protected boolean forwardTelegramToFrontMostSubgoal(Telegram tlg)
	{
		// Telegram an das erste Unterziel weiterleiten
		if (!subGoals.isEmpty())
			return subGoals.getFirst().handleTelegram(tlg);

		// Wenn es keine Unterziele gibt, Nachricht nicht bearbeiten
		return false;

	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
