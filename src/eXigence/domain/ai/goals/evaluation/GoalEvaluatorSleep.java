package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Hotel;

public class GoalEvaluatorSleep extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorSleep(double prioriyValue)
	{
		super(prioriyValue);
	}

	@Override
	public double calculateDesirability(NonPlayerCharacter npc)
	{
		// Wenn er nicht genug Geld hat
		if(npc.getMoney() < Hotel.costMoneyAsCustomer)
			return 0;
		
		return (npc.getNecessitiySleep() / NonPlayerCharacter.getMaxNecessitiy()) * priorety;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Sleep);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
