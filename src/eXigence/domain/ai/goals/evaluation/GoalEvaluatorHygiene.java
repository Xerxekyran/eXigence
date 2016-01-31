package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Bathhouse;

public class GoalEvaluatorHygiene extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorHygiene(double prioriyValue)
	{
		super(prioriyValue);
	}

	@Override
	public double calculateDesirability(NonPlayerCharacter npc)
	{
		// Wenn er nicht genug Geld hat
		if(npc.getMoney() < Bathhouse.costMoneyAsCustomer)
			return 0;
		
		return (npc.getNecessitiyHygiene() / NonPlayerCharacter.getMaxNecessitiy()) * priorety;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Hygiene);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
