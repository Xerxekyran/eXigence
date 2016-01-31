package eXigence.domain.ai.goals.composite;

import java.util.LinkedList;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.ai.PathEdge;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.atomic.GoalTraverseEdge;

/**
 * Ein Ziel welches den NPC entlang eines Pfades bewegt
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public class GoalFollowPath extends GoalComposite
{
	private LinkedList<PathEdge>	path;

	public GoalFollowPath(NonPlayerCharacter owner, LinkedList<PathEdge> path)
	{
		super(owner, EGoalTypes.GoalFollowPath);		
		this.path = path;		
	}

	@Override
	public void activate()
	{		
		// status aktivieren
		status = EGoalStatus.active;

		// Erste Kante von der Liste nehmen
		PathEdge edge = path.pollFirst();

		//LogWriter.getInstance().logToFile(LogLevel.Debug, "GoalFollowPath: activate() mit der Kante von["+ edge.getSource() +"] zu["+ edge.getDestination() +"]");
		
		// Ein Kindziel erstellen, welches den NPC entlang der nächsten (ersten)
		// Kante bewegt
		addSubGoal(new GoalTraverseEdge(owner, edge, path.isEmpty()));
	}

	@Override
	public EGoalStatus process()
	{
		activateIfInactive();

		status = processSubGoals();

		// Wenn das Kindziel erfüllt ist jedoch noch weitere Kanten vorhanden
		// sind, die nächste Kante als Ziel setzen (mit activate())
		if (status.equals(EGoalStatus.completed) && !path.isEmpty())
		{
			activate();
		}

		return status;
	}

	@Override
	public void terminate()
	{
	}

}
