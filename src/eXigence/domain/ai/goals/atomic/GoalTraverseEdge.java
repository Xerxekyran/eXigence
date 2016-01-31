package eXigence.domain.ai.goals.atomic;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.ai.PathEdge;
import eXigence.domain.ai.goals.EGoalStatus;
import eXigence.domain.ai.goals.EGoalTypes;
import eXigence.domain.ai.goals.Goal;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public class GoalTraverseEdge extends Goal
{
	private PathEdge	edge;
	private boolean		isLastEdge;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * 
	 * @param owner Der Besitzer des Ziels
	 * @param edge Die Kante die benutzt werden soll
	 * @param lastEdge ist es die letzte Kante eines Pfades? (ergibt ein anderes Ankommensverhalten)
	 */
	public GoalTraverseEdge(NonPlayerCharacter owner,
			PathEdge edge,
			boolean lastEdge)
	{
		super(owner, EGoalTypes.GoalTraverseEdge);
		this.edge = edge;
		this.isLastEdge = lastEdge;
	}
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void activate()
	{
		status = EGoalStatus.active;
		
		// Das Ziel dem Bewegungsapparat geben
		owner.getSteering().setTarget(edge.getDestination());
		
		if(isLastEdge)
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug, "GoalTraverseEdge: activate() mit arriveOn() von ["+ edge.getSource() +"] nach ["+ edge.getDestination() +"]");
			owner.getSteering().arriveOn();
		}
		else
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug, "GoalTraverseEdge: activate() mit seekOn() von ["+ edge.getSource() +"] nach ["+ edge.getDestination() +"]");
			owner.getSteering().seekOn();
		}
	}

	@Override
	public EGoalStatus process()
	{
		// Aktivieren wenn inaktiv
		activateIfInactive();
				  
		// Überprüfen ob das Ziel schon erreicht wurde
		if(owner.isAtPosition(edge.getDestination()))
		{
			status = EGoalStatus.completed;
		}
		
		return status;
	}

	@Override
	public void terminate()
	{
//		LogWriter.getInstance().logToFile(LogLevel.Debug, "GoalTraverseEdge: terminate()");

	}	

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

}
