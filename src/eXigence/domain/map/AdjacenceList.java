package eXigence.domain.map;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Datenstruktur einer Adjazenzliste (Darstellung der Knoten und Kanten eines
 * Graphen)
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
@Entity
public class AdjacenceList
{
	@Id
	@GeneratedValue
	private int					AdjacenceID;

	// Die Knoten der Liste
	@OneToMany(cascade = CascadeType.ALL)
	private List<MapNode>		nodes;

	// Eine Liste von Kantenlisten (An der Stelle i des Vectors liegt die Liste
	// von Kanten die vom dem Knoten i aus erreichbar sind)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<MapEdgeList>	edges;

	// Ob es sich um einen gerichteten Graphen handelt
	@Basic
	private boolean				isDigraph;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Konstruktor
	 * 
	 * @param isDigraph
	 *            Handelt es sich um einen gerichteten Graphen?
	 */
	public AdjacenceList(boolean isDigraph)
	{
		this.isDigraph = isDigraph;
		nodes = new Vector<MapNode>();
		edges = new Vector<MapEdgeList>();
	}

	/**
	 * Standardkonstruktor für Hibernate
	 */
	public AdjacenceList()
	{
		isDigraph = false;
		nodes = new Vector<MapNode>();
		edges = new Vector<MapEdgeList>();
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Liefert die Kante, die von den Knoten mit den übergebenen Indexen führt.
	 * null wenn es keine Kante gibt
	 * 
	 * @param fromIndex
	 *            Index des Knoten von dem aus die Kante gehen soll
	 * @param toIndex
	 *            Index des Knoten zu dem die Kante gehen soll
	 * @return Die Kante zwischen den Knoten mit den Indices. null wenn es dort
	 *         keine Kante gibt
	 */
	public MapEdge getEdge(int fromIndex, int toIndex)
	{
		throw new NotImplementedException();
		// return null;
	}

	/**
	 * Fügt die neue Kante dem Graphen hinzu Doppelte Kanten werden nicht
	 * zugelassen (wenn es schon eine von A nach B gibt wird keine neue für
	 * diese Strecke eingefügt)
	 * 
	 * @param newEdge
	 *            die neue Kante
	 */
	public void addEdge(MapEdge newEdge)
	{
		// die liste der kanten suchen an die die neue gehangen wird
		List<MapEdge> edgeList = edges.get(newEdge.getFromNodeIndex()).getEdges();
		Iterator<MapEdge> it = edgeList.iterator();
		while (it.hasNext())
		{
			MapEdge tmpEl = it.next();
			if (newEdge.getFromNodeIndex() == tmpEl.getFromNodeIndex()
					&& newEdge.getToNodeIndex() == tmpEl.getToNodeIndex())
				return;
		}

		// In der Liste einfügen, von wo die Kante aus startet
		edges.get(newEdge.getFromNodeIndex()).getEdges().add(newEdge);
	}

	/**
	 * Liefert den Knoten mit dem übergebenen Index
	 * 
	 * @param index
	 *            der Index des gewünschten Knoten im vector der Adjazenzliste
	 * @return Den Knoten am index
	 */
	public MapNode getNode(int index)
	{
		return nodes.get(index);
	}

	/**
	 * Fügt einen neuen Knoten der Liste hinzu
	 * 
	 * @param newNode
	 *            Der neue Knoten
	 * @return True wenn der Knoten hinzugefügt wurde ansonsten false
	 */
	public boolean addNode(MapNode newNode)
	{
		edges.add(new MapEdgeList());
		return nodes.add(newNode);
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

	public boolean isDigraph()
	{
		return isDigraph;
	}

	public List<MapNode> getNodes()
	{
		return nodes;
	}

	public void setNodes(List<MapNode> nodes)
	{
		this.nodes = nodes;
	}

	public void setDigraph(boolean isDigraph)
	{
		this.isDigraph = isDigraph;
	}

	public int getAdjacenceID()
	{
		return AdjacenceID;
	}

	public void setAdjacenceID(int adjacenceID)
	{
		AdjacenceID = adjacenceID;
	}

	public List<MapEdgeList> getEdges()
	{
		return edges;
	}

	public void setEdges(List<MapEdgeList> edges)
	{
		this.edges = edges;
	}

}
