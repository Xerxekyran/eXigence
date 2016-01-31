package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.NonPlayerCharacter;

/**
 * Basisklasse für Klassen welche Ziele bewerten können
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public abstract class GoalEvaluator
{

	// Persönliche Gewichtung des Ziels (für verschiedene
	// Charactereigenschaften)
	protected double	priorety;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluator(double priorety)
	{	
		if(priorety <= 0)
			priorety = 1;
		this.priorety = priorety;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Liefert ein Ergebnis zwischen 0 und 1 welches die Notwendigkeit des
	 * Erreichens angibt
	 * 
	 * @param npc
	 * @return Die Notwendigkeit
	 */
	public abstract double calculateDesirability(NonPlayerCharacter npc);

	/**
	 * Dem NPC das entsprechende Ziel setzen
	 * 
	 * @param npc
	 */
	public abstract void setGoal(NonPlayerCharacter npc);

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
