package eXigence.domain.ai.goals.composite;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.Goal;

public class GoalIdle extends Goal
{
	public GoalIdle(NonPlayerCharacter owner)
	{
		super(owner, EGoalTypes.GoalSeekToPosition);
	}

	@Override
	public void activate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public EGoalStatus process()
	{
		return EGoalStatus.active;
	}

	@Override
	public void terminate()
	{
		// TODO Auto-generated method stub

	}

}
