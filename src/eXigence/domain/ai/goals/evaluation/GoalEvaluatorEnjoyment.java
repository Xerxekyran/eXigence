package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Cinema;

public class GoalEvaluatorEnjoyment extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorEnjoyment(double prioriyValue)
	{
		super(prioriyValue);
	}

	@Override
	public double calculateDesirability(NonPlayerCharacter npc)
	{
		// Wenn er nicht genug Geld hat
		if(npc.getMoney() < Cinema.costMoneyAsCustomer)
			return 0;
		
		return (npc.getNecessitiyEnjoyment() / NonPlayerCharacter.getMaxNecessitiy()) * priorety;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Enjoyment);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
