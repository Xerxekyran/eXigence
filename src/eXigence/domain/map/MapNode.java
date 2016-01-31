package eXigence.domain.map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import eXigence.util.math.Vector2D;

/**
 * Eine Knoten des Karten-Graphen
 * 
 * @author Lars George
 * @version 1.0
 *
 */
@Entity
public class MapNode
{
	@Id
	@GeneratedValue
	private int MapNodeID;
	
	@Basic
	private Vector2D position = new Vector2D(0,0);
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public MapNode(Vector2D position)
	{
		this.position = position;
	}

	public MapNode()
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
	public Vector2D getPosition()
	{
		return position;
	}

	public void setPosition(Vector2D position)
	{
		this.position = position;
	}

	public int getMapNodeID()
	{
		return MapNodeID;
	}

	public void setMapNodeID(int mapNodeID)
	{
		MapNodeID = mapNodeID;
	}
	
	
}
