package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Restaurant;
import eXigence.domain.entities.buildings.Toilet;

public class GoalEvaluatorStrangury extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorStrangury(double prioriyValue)
	{
		super(prioriyValue);
	}

	@Override
	public double calculateDesirability(NonPlayerCharacter npc)
	{
		// Wenn er nicht genug Geld hat
		if(npc.getMoney() < Toilet.costMoneyAsCustomer)
			return 0;
		
		return (npc.getNecessitiyStrangury() / NonPlayerCharacter.getMaxNecessitiy()) * priorety;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Strangury);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
