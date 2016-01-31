package eXigence.domain.map;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

import eXigence.domain.ai.PathEdge;
import eXigence.util.math.Vector2D;

/**
 * Klasse zum Durchsuchen eines Graphen anhand des A* Algorithmus
 * 
 * @author Lars George
 * 
 */
public class GraphSearchAStarTimeSliced
{
	private AdjacenceList	graph;

	private Vector<MapEdge>	shortestPathTree;
	private Vector<MapEdge>	searchFrontier;

	private Vector<Double>	fCosts;
	private Vector<Double>	gCosts;

	private int				sourceNodeIndex	= -1;
	private int				targetNodeIndex	= -1;

	PriorityQueue<Integer>	priorQueue;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public GraphSearchAStarTimeSliced(AdjacenceList graph,
			int sourceNodeIndex,
			int targetNodeIndex)
	{
		this.graph = graph;
		this.sourceNodeIndex = sourceNodeIndex;
		this.targetNodeIndex = targetNodeIndex;

		int numNodes = graph.getNodes().size();

		// Vectoren initialisieren
		shortestPathTree = new Vector<MapEdge>(numNodes);
		searchFrontier = new Vector<MapEdge>(numNodes);
		fCosts = new Vector<Double>(numNodes);
		gCosts = new Vector<Double>(numNodes);

		for (int i = 0; i < numNodes; i++)
		{
			fCosts.add(i, 0.0);
			gCosts.add(i, 0.0);
			shortestPathTree.add(null);
			searchFrontier.add(null);
		}

		// PriorityQueue mit dem eigenen Comparator erstellen (sortiert nach
		// gCosts)
		priorQueue = new PriorityQueue<Integer>(20,
				new GraphSearchAStarComparator<Integer>(this));

		// Startknoten auf die Queue packen
		priorQueue.add(sourceNodeIndex);
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Liefert eine Liste der Kanten die benutzt werden müssen um den kürzesten
	 * Weg vom Startpunkt zum Ziel zu gehen
	 * 
	 * @return Der berechnete kürzeste Weg anhand von PathEdge Objekten
	 */
	public LinkedList<PathEdge> getShortestPathAsPathEdges()
	{
		LinkedList<PathEdge> pathEdges = new LinkedList<PathEdge>();

		// Wenn nix angegeben wurde
		if (targetNodeIndex < 0)
			return pathEdges;

		// Das Ziel ans Ende der Liste tun
		int nd = targetNodeIndex;

		// Den Weg rückwärts ablaufen
		while ((nd != sourceNodeIndex) && (shortestPathTree.get(nd) != null))
		{
			pathEdges.push(new PathEdge(
					graph.getNode(shortestPathTree.get(nd).getFromNodeIndex()).getPosition(),
					graph.getNode(shortestPathTree.get(nd).getToNodeIndex()).getPosition()));
			
			nd = shortestPathTree.get(nd).getFromNodeIndex();
		}	

		return pathEdges;
	}

	/**
	 * Liefert eine Liste von Indices der Knoten die man besuchen muss um zum
	 * Zielknoten zu gelangen
	 * 
	 * @return Liste von Indices der Knoten
	 */
	public LinkedList<Integer> getIndexedPathToTarget()
	{
		LinkedList<Integer> indexedPath = new LinkedList<Integer>();

		// Wenn nix angegeben wurde
		if (targetNodeIndex < 0)
			return indexedPath;

		// Das Ziel ans Ende der Liste tun
		int nd = targetNodeIndex;
		indexedPath.add(nd);

		// Den Weg rückwärts ablaufen
		while ((nd != sourceNodeIndex) && (shortestPathTree.get(nd) != null))
		{
			nd = shortestPathTree.get(nd).getFromNodeIndex();
			indexedPath.push(nd);
		}

		return indexedPath;
	}

	/**
	 * Die Methode führt einen Zyklus der Wegberechnung durch. Der Rückgabetyp
	 * gibt an ob noch weitere Aufrufe notwendig sind.
	 * 
	 * @return Entweder wurde das Zeil gefunden, es konnte nicht erreicht werden
	 *         oder es wurde nur noch nicht gefunden (weitere aufrufe notwendig)
	 */
	public ESearchResult cycleOnce()
	{	
		// Wenn nichts weiter untersucht werden kann, Suche nicht weiter möglich
		if (priorQueue.isEmpty())
			return ESearchResult.target_not_found;

		// Knoten mit den geringsten Kosten von der Queue holen
		int nextClosestNodeIndex = priorQueue.remove();

		// Den Knoten zu der Liste der schon bearbeiteten hinzutun
		shortestPathTree.set(nextClosestNodeIndex,
				searchFrontier.get(nextClosestNodeIndex));

		// Wenn das Ziel gefunden wurde abbrechen
		if (nextClosestNodeIndex == targetNodeIndex)
			return ESearchResult.target_found;

		// Alle Kanten testen, die mit diesem Knoten verbunden sind
		MapEdge edgeTo = null;
		MapEdgeList neighbourEdges = graph.getEdges().get(nextClosestNodeIndex);
		for (int i = 0; i < neighbourEdges.getEdges().size(); i++)
		{
			// Aktuelle Kante zwischenspeichern
			edgeTo = neighbourEdges.getEdges().get(i);

			// die heuristischen Kosten von diesem Knoten zum Zielknoten
			// berechnen
			double hCost = calculateHeuristicCost(edgeTo.getToNodeIndex());

			// die "echten" Kosten zu diesem Knoten von der Quelle aus berechnen
			double gCost = gCosts.get(nextClosestNodeIndex) + edgeTo.getCost();

			// Wenn die Kante noch nicht in der "Suchfront" vorhanden ist
			if (searchFrontier.get(edgeTo.getToNodeIndex()) == null)
			{
				fCosts.set(edgeTo.getToNodeIndex(), gCost + hCost);
				gCosts.set(edgeTo.getToNodeIndex(), gCost);

				priorQueue.add(edgeTo.getToNodeIndex());

				searchFrontier.set(edgeTo.getToNodeIndex(), edgeTo);
			}
			else if (gCost < gCosts.get(edgeTo.getToNodeIndex())
					&& (shortestPathTree.get(edgeTo.getToNodeIndex()) == null))
			{
				fCosts.set(edgeTo.getToNodeIndex(), gCost + hCost);
				gCosts.set(edgeTo.getToNodeIndex(), gCost);

				// TODO: EVT priorQueue zum neu sortieren zwingen

				searchFrontier.set(edgeTo.getToNodeIndex(), edgeTo);
			}
		}

		return ESearchResult.search_incomplete;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	private double calculateHeuristicCost(int fromNodeIndex)
	{
		return (new Vector2D(graph.getNode(fromNodeIndex).getPosition().getX()
				- graph.getNode(targetNodeIndex).getPosition().getX(),
				graph.getNode(fromNodeIndex).getPosition().getY()
						- graph.getNode(targetNodeIndex).getPosition().getY()).length());
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public Vector<Double> getFCosts()
	{
		return fCosts;
	}

	public Vector<Double> getGCosts()
	{
		return gCosts;
	}

}
