package eXigence.domain.ai.goals.evaluation;

import eXigence.domain.ENecessities;
import eXigence.domain.NonPlayerCharacter;

public class GoalEvaluatorMoney extends GoalEvaluator
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GoalEvaluatorMoney(double prioriyValue)
	{
		super(prioriyValue);
	}

	@Override
	public double calculateDesirability(NonPlayerCharacter npc)
	{
		return ( 1 - (npc.getMoney() / NonPlayerCharacter.getMaxMoney())  ) * priorety;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void setGoal(NonPlayerCharacter npc)
	{
		npc.getBrain().addGoal_FullfillNecessetie(ENecessities.Money);
	}
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
