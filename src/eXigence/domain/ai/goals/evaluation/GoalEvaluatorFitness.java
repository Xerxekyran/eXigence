package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.entities.buildings.Gym;

public class GoalEvaluatorFitness extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorFitness(double prioriyValue)
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
		if(npc.getMoney() < Gym.costMoneyAsCustomer)
			return 0;
		
		return (npc.getNecessitiyFitness() / NonPlayerCharacter.getMaxNecessitiy()) * priorety;
	}

	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Fitness);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

}
