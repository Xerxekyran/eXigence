package eXigence.domain.ai;

import eXigence.util.math.Vector2D;

/**
 * Eine Klasse für Kanten die für die Wegfindung benutzt wird. Es werden anders
 * als in der MapEdge nicht indizes auf Knoten gespeichert, sondern die
 * Koordinaten in der Welt
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public class PathEdge
{
	private Vector2D	source;
	private Vector2D	destination;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public PathEdge(Vector2D source, Vector2D destination)
	{
		this.source = source;
		this.destination = destination;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public Vector2D getSource()
	{
		return source;
	}

	public void setSource(Vector2D source)
	{
		this.source = source;
	}

	public Vector2D getDestination()
	{
		return destination;
	}

	public void setDestination(Vector2D destination)
	{
		this.destination = destination;
	}

}
