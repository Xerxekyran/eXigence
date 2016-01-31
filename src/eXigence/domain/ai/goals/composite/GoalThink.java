package eXigence.domain.ai.goals.composite;

import java.util.Iterator;
import java.util.Vector;

import eXigence.domain.ENecessities;
import eXigence.domain.ExigenceCharacter;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.Goal;
import eXigence.domain.ai.goals.evaluation.GoalEvaluator;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorEducation;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorEnjoyment;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorFitness;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorHunger;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorHygiene;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorMoney;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorSleep;
import eXigence.domain.ai.goals.evaluation.GoalEvaluatorStrangury;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

/**
 * DAS Highlevel Ziel eines NPC, er besitzt eine Instanz dieser
 * Klasse und ruft periodisch deren process Methode auf um die verschiedene
 * Ziele zu erfüllen
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public class GoalThink extends GoalComposite
{

	private Vector<GoalEvaluator>	goalEvaluators	= new Vector<GoalEvaluator>();
	private GoalEvaluator memory = null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalThink(NonPlayerCharacter owner)
	{
		super(owner, EGoalTypes.GoalThink);		
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode welche die GoalEvaluatoren anlegt
	 */
	public void initGoalEvaluators()
	{
		// Maximale Priorität holen, damit die übergebenen Werte zwischen 0 und
		// 1 sind
		double maxPriorityValue = ExigenceCharacter.getMaxPriority();

		// Die Evaluatoren erstellen
		goalEvaluators.add(new GoalEvaluatorHunger(owner.getPriorHunger() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorEducation(owner.getPriorEducation() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorEnjoyment(owner.getPriorEnjoyment() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorFitness(owner.getPriorFitness() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorHygiene(owner.getPriorHygiene() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorMoney(owner.getPriorMoney() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorSleep(owner.getPriorSleep() / maxPriorityValue));
		goalEvaluators.add(new GoalEvaluatorStrangury(owner.getPriorStrangury() / maxPriorityValue));
	}
	
	/**
	 * Toplevelziel MoveToPosition wird mit dem entsprechenden Ziel gesetzt
	 * 
	 * @param pos
	 *            Position zu der sich der NPC bewegen soll
	 */
	public void addGoal_MoveToPosition(Vector2D pos)
	{
		LogWriter.getInstance().logToFile(LogLevel.Info,
				owner.getFirstName() + " " + owner.getLastName()
						+ " entscheidet sich zu der Position " + pos.getX()
						+ " | " + pos.getY() + " kommen zu wollen.");

		// Wenn dieses Ziel nicht eh schon am Anfang der Liste steht
		if (notPresent(EGoalTypes.GoalMoveToPosition))
		{
			// Die alten Ziele entfernen
			removeAllSubGoals();

			// Das neue Ziel einrichten
			addSubGoal(new GoalMoveToPosition(owner, pos));
		}
	}

	/**
	 * Highlevelziel FullfillNecessetie mit entsprechender Angabe zum Bedürfnis
	 * welches erfüllt werden soll setzen
	 */
	public void addGoal_FullfillNecessetie(ENecessities necessetie)
	{		
		// Wenn dieses Ziel nicht eh schon am Anfang der Liste steht
		if (notPresent(EGoalTypes.GoalFullfillNecessetie))
		{
			LogWriter.getInstance().logToFile(LogLevel.Info,
					owner.getFirstName()
							+ " "
							+ owner.getLastName()
							+ " entscheidet sich dazu ein Bedürfnis ["+ necessetie.toString() +"] befriedigen zu wollen.");
	
			// Die alten Ziele entfernen
			removeAllSubGoals();
	
			// Das neue Ziel einrichten
			addSubGoal(new GoalFullfillNecessetie(owner, necessetie));
		}
	}

	/**
	 * Diese Methode läuft durch alle GoalEvaluatoren und selektiert den mit der
	 * höchsten Score als das aktuelle Ziel
	 */
	public void arbitrate()
	{
		double best = 0.0;
		double desirability;
		GoalEvaluator mostDesirable = null;
		GoalEvaluator tmpEvaluator = null;

		// Alle Evaluatoren durchgehen und den mit dem höchsten Wert auswählen
		Iterator<GoalEvaluator> it = goalEvaluators.iterator();
		while (it.hasNext())
		{			
			
			tmpEvaluator = it.next();
			
			if(memory != null && tmpEvaluator.getClass().equals(memory.getClass()))
			{
				continue;
			}
			
			desirability = tmpEvaluator.calculateDesirability(owner);
			
			
			// Wenn der Wert besser ist, dieses Ziel wählen
			if (desirability >= best)
			{
				best = desirability;
				mostDesirable = tmpEvaluator;
			}
		}

		// Wenn ein Ziel mit höchstem Wert gefunden wurde
		if (mostDesirable != null)
		{
			// Diesen Evaluator merken, damit er das nächste mal nicht gleich wieder benutzt wird
			// Für den Fall das es fehlschlägt soll er sich nicht an dem einen Ziel aufhängen
			memory = mostDesirable;

			// Dieses Ziel beim Besitzer setzen
			mostDesirable.setGoal(owner); 
		}
	}

	/**
	 * 
	 * @param goalType
	 * @return true wenn der übergeben Zieltyp nicht am Anfang der Unterziele
	 *         steht, ansonsten false
	 */
	public boolean notPresent(EGoalTypes goalType)
	{
		// Wenn es Unterziele gibt
		if (!subGoals.isEmpty())
		{
			return (!subGoals.getFirst().getGoalType().equals(goalType));
		}

		return true;
	}

	@Override
	public void activate()
	{
		arbitrate();
		status = EGoalStatus.active;
	}

	@Override
	public EGoalStatus process()
	{
		activateIfInactive();

		EGoalStatus subGoalStatus = processSubGoals();

		if (subGoalStatus.equals(EGoalStatus.completed)
				|| subGoalStatus.equals(EGoalStatus.failed))
		{
			status = EGoalStatus.inactive;
		}

		return status;
	}

	@Override
	public void terminate()
	{
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
