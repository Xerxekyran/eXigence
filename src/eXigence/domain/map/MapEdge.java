package eXigence.domain.map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 
 * @author Lars George
 *
 */
@Entity
public class MapEdge
{
	@Id
	@GeneratedValue
	private int MapEdgeID;
	
	@Basic
	private int fromNodeIndex;
	
	@Basic
	private int toNodeIndex;
	
	@Basic
	private double cost;
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public MapEdge(int from, int to, double cost)
	{
		this.fromNodeIndex = from;
		this.toNodeIndex = to;
		this.cost = cost;
	}
	
	public MapEdge()
	{}
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public int getFromNodeIndex()
	{
		return fromNodeIndex;
	}

	public void setFromNodeIndex(int startNode)
	{
		this.fromNodeIndex = startNode;
	}

	public int getToNodeIndex()
	{
		return toNodeIndex;
	}

	public void setToNodeIndex(int endNode)
	{
		this.toNodeIndex = endNode;
	}

	public double getCost()
	{
		return cost;
	}

	public void setCost(double cost)
	{
		this.cost = cost;
	}

	public int getMapEdgeID()
	{
		return MapEdgeID;
	}

	public void setMapEdgeID(int mapEdgeID)
	{
		MapEdgeID = mapEdgeID;
	}	
	
}
