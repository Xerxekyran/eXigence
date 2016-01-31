package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Restaurant;

public class GoalEvaluatorHunger extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorHunger(double prioriyValue)
	{
		super(prioriyValue);		
	}
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public double calculateDesirability(NonPlayerCharacter npc)
	{	
		// Wenn er nicht genug Geld hat
		if(npc.getMoney() < Restaurant.costMoneyAsCustomer)
			return 0;
		
		return (npc.getNecessitiyHunger() / NonPlayerCharacter.getMaxNecessitiy()) * priorety;
	}

	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Hunger);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

}
