package eXigence.domain.map;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import eXigence.core.Simulation;
import eXigence.domain.NonInteractiveObject;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.IntersectionTests;
import eXigence.util.math.Vector2D;

/**
 * Repr�sentiert die Karte auf der eine Simulation stattfindet Ist unter anderem
 * Grundlage f�r die Wegfindung der KI
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
@Entity
public class WorldMap
{
	@Id
	@GeneratedValue
	private int									worldID;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private AdjacenceList						navGraph				= new AdjacenceList(
																				false);

	// Zu welcher Simulation geh�rt diese Map
	@Transient
	private Simulation							simulation;

	/**
	 * Die Geb�ude mit denen interagiert werden kann
	 */
	@OneToMany(mappedBy = "homeMap", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Collection<ExigenceBuilding>		buildings				= new ConcurrentLinkedQueue<ExigenceBuilding>();

	/**
	 * Liste von statischen Elementen in der Simulation (ohne
	 * Interaktionsm�glichkeit)
	 */
	@OneToMany(mappedBy = "homeMap", cascade = CascadeType.ALL)
	private Collection<NonInteractiveObject>	nonInteractiveObjects	= new ConcurrentLinkedQueue<NonInteractiveObject>();

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public WorldMap(Simulation sim)
	{
		navGraph = new AdjacenceList(false);
		this.simulation = sim;
	}

	/**
	 * Standardkonstruktor f�r Hibernate
	 */
	public WorldMap()
	{
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Testet ob ein Objekt mit einem Boundingradius von Punkt a nach b kommt
	 * ohne gegen unbewegliche Objekte zu sto�en
	 * 
	 * @param a
	 *            Startpunkt
	 * @param b
	 *            Zielpunkt
	 * @param boundingRadius
	 *            Boundingradius gr��e zu Kollisions�brepr�fung
	 * @return true wenn der Weg versperrt ist ansonsten false
	 */
	public boolean isPathObstructed(Vector2D a,
									Vector2D b,
									double boundingRadius)
	{
		// System.out.println("isPathObstructed "+ a + " und "+ b + " mit radius
		// " + boundingRadius);

		// Richtungsvektor errechnen
		Vector2D toB = b.sub(a).normalize();
		Vector2D curPos = new Vector2D(a.getX(), a.getY());

		// So lange die Tests nicht so nah dran sind wie das Quadrat des
		// boundingRadius
		while (curPos.distanceSquare(b) > boundingRadius * boundingRadius)
		{
			// System.out.println(curPos);
			// Die zu untersuchende Position weiter setzen
			curPos.addToThis(toB.mul(0.5 * boundingRadius));

			// Alle Objekte gegen den imagin�ren Kreis testen
			if (doObjectsIntersectCircle(curPos, boundingRadius))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Erstellt eine quadratische Karte mit
	 * 
	 * @param numXCells
	 *            wieviele Zellen in x-richtung soll es geben
	 * @param numYCells
	 *            wieviele Zellen in y-richtung soll es geben
	 * @param mapWidth
	 *            die gesamtbreite der Karte
	 * @param mapHeight
	 *            die gesamth�he der Karte
	 */
	public void createGridMap(	int numXCells,
								int numYCells,
								int mapWidth,
								int mapHeight)
	{
		Vector2D newNodePosition;

		double cellWidth = mapWidth / numXCells;
		double cellHeight = mapHeight / numYCells;

		double midX = cellWidth / 2;
		double midY = cellHeight / 2;

		// Alle Knoten anlegen
		for (int y = 0; y < numYCells; y++)
		{
			for (int x = 0; x < numXCells; x++)
			{
				newNodePosition = new Vector2D(midX + (x * cellWidth), midY
						+ (y * cellHeight));
				navGraph.addNode(new MapNode(newNodePosition));

			}
		}

		// Nun die Kanten. Jeder Knoten hat 8 Nachbarn (bis auf die Kanten)
		for (int y = 0; y < numYCells; y++)
		{
			for (int x = 0; x < numXCells; x++)
			{
				addAllNeighbourEdgesToGridNode(x, y, numXCells, numYCells);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean loadMapDataFromXMLFile(String filename)
	{
		LogWriter.getInstance().logToFile(LogLevel.Info,
				"[WorldMap]: Trying to load XML-File: [" + filename + "]");

		// XML-Builder Instanz holen
		SAXBuilder xmlBuilder = new SAXBuilder();
		Document xmlMapDoc = null;

		try
		{
			// Versuchen die Konfigurationsdatei zu lesen
			xmlMapDoc = xmlBuilder.build(new File(filename));

			double xPos = 0.0, yPos = 0.0, costs = 0.0;

			int nodeID = 0, fromID = 0, toID = 0;

			// Alle Knoten hinzuf�gen
			Element nodes = xmlMapDoc.getRootElement().getChild("Nodes");
			Iterator<Element> it = nodes.getChildren().iterator();
			Element tmpEl = null;
			while (it.hasNext())
			{
				tmpEl = it.next();

				// ID �berpr�fen (muss fortlaufend sein)
				if (nodeID != Integer.parseInt(tmpEl.getAttributeValue("id")))
				{
					LogWriter.getInstance().logToFile(LogLevel.Error,
							"Wrong id found: "
									+ Integer.parseInt(tmpEl.getAttributeValue("id"))
									+ " expected: " + nodeID);
				}

				xPos = Double.parseDouble(tmpEl.getChildText("X"));
				yPos = Double.parseDouble(tmpEl.getChildText("Y"));
				navGraph.addNode(new MapNode(new Vector2D(xPos, yPos)));

				nodeID++;
			}

			// Alle Kanten durchgehen
			Element edges = xmlMapDoc.getRootElement().getChild("Edges");
			
			// Sind es gerichtete Kanten?
			String tmpString = edges.getAttributeValue("isDirected");
			if(tmpString.toLowerCase().equals("true"))
				navGraph.setDigraph(true);
			else if(tmpString.toLowerCase().equals("false"))
				navGraph.setDigraph(false);
			else
				throw new Exception("Couldnt read the 'isDirected' Attribute of the 'Edges' Node");
			
			// Sollen die Kosten automatisch berechnet werden?
			boolean autocalcCosts = false;
			tmpString = edges.getAttributeValue("autoCalculateCosts");
			if(tmpString.toLowerCase().equals("true"))
				autocalcCosts = true;
			else if(tmpString.toLowerCase().equals("false"))
				autocalcCosts = false;
			else 
				throw new Exception("Couldnt read the 'autoCalculateCosts' Attribute of the 'Edges' Node");
			
			it = edges.getChildren().iterator();
			while (it.hasNext())
			{
				tmpEl = it.next();
				
				fromID = Integer.parseInt(tmpEl.getChildText("FromNodeID"));
				toID = Integer.parseInt(tmpEl.getChildText("ToNodeID"));

				// Kosten manuell berechnet?
				if(!autocalcCosts)
					costs = Double.parseDouble(tmpEl.getChildText("CostToTraverse"));
				else
				{
					// Kosten automatisch berechnen anhand der Position der Knoten
					costs = navGraph.getNode(fromID).getPosition().sub(navGraph.getNode(toID).getPosition()).length();
				}
				
				navGraph.addEdge(new MapEdge(fromID, toID, costs));
				
				// Wenn es keine gerichteten Kanten sind, die R�ckkante einbauen
				if(!navGraph.isDigraph())
				{
					navGraph.addEdge(new MapEdge(toID, fromID, costs));
				}
			}

		}
		catch (JDOMException e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"[WorldMap]: Failed loading the XML file [Wrong Format]: "
							+ e.toString());
			return false;
		}
		catch (IOException e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"[WorldMap]: Failed loading the XML file [IO Error]: "
							+ e.toString());
			return false;
		}
		catch (Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"[WorldMap]: Failed loading the XML file: " + e.toString());
			return false;
		}

		return true;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Erstellt zu einem Knoten in einem K�stchen Raster alle m�glichen
	 * Nachbarverbindungen (Kanten)
	 * 
	 * @param x
	 *            x-position des Knoten
	 * @param y
	 *            y-position des Knoten
	 * @param numXCells
	 *            Anzahl von x Knoten
	 * @param numYCells
	 *            Anzahl von y Knoten
	 */
	private void addAllNeighbourEdgesToGridNode(int x,
												int y,
												int numXCells,
												int numYCells)
	{
		// Von links daneben bis rechts daneben (-1, 0, +1)
		for (int i = -1; i < 2; i++)
		{
			// Von oben dr�ber bis unten drunter (-1, 0, +1)
			for (int j = -1; j < 2; j++)
			{
				int nodeX = y + j;
				int nodeY = x + i;

				// Wenn es 0/0 ist �berspringen
				if ((i == 0) && (j == 0))
					continue;

				// Wenn es ein g�ltiger Nachbar ist(nicht am Rand)
				if (validNeighbour(nodeX, nodeY, numXCells, numYCells))
				{
					// Die Positionen der Knoten holen
					Vector2D posNode = navGraph.getNode(y * numXCells + x).getPosition();
					Vector2D posNeighbour = navGraph.getNode(nodeY * numXCells
							+ nodeX).getPosition();

					// �berpr�fen ob der Weg auch passierbar ist
					if (isPathObstructed(posNode, posNeighbour, 10))
					{
						// TODO: irgendwie findet er dann keinen weg mehr zu
						// manchen geb�uden
						// continue;
					}

					double dist = posNode.distance(posNeighbour);

					// Neue Kante mit dem Gewicht der L�nge zwischen den Knoten
					// erstellen
					MapEdge newEdge = new MapEdge(y * numXCells + x, nodeY
							* numXCells + nodeX, dist);

					// Dem Graphen �bergeben
					navGraph.addEdge(newEdge);

					// Wenn es kein Digraph ist, die R�ckkante ebenfalls
					// erstellen
					if (!navGraph.isDigraph())
					{
						newEdge = new MapEdge(nodeY * numXCells + nodeX, y
								* numXCells + x, dist);
						navGraph.addEdge(newEdge);
					}
				}
			}
		}
	}

	/**
	 * Schaut ob x und y innerhalb der Kartengrenzen liegen
	 * 
	 * @param x
	 *            Position auf der x-achse
	 * @param y
	 *            Position auf der y-achse
	 * @param numXCells
	 *            maximale gr��e f�r die x-achse
	 * @param numYCells
	 *            maximale gr��e f�r die y-achse
	 * @return
	 */
	private boolean validNeighbour(int x, int y, int numXCells, int numYCells)
	{
		return !((x < 0) || (x >= numXCells) || (y < 0) || (y >= numYCells));
	}

	/**
	 * �berpr�ft ob Objekte die Position position mit dem Einflussradius radius
	 * �berschneiden
	 * 
	 * @param position
	 *            Position des zu testenden Objekts
	 * @param radius
	 *            Radius des zu testenden Objekts
	 * @return true wenn Objekte im Weg liegen, ansonten false
	 */
	private boolean doObjectsIntersectCircle(Vector2D position, double radius)
	{
		ExigenceBuilding tmpBuilding = null;

		// Jedes Geb�ude besitz 4 W�nde, gegen jedes Geb�ude testen
		Iterator<ExigenceBuilding> it = buildings.iterator();
		while (it.hasNext())
		{
			tmpBuilding = it.next();

			// Die vier Eckpunkte des Geb�udes
			Vector2D topLeft = tmpBuilding.getPosition();
			Vector2D topRight = tmpBuilding.getPosition().add(new Vector2D(
					tmpBuilding.getWidth(), 0));
			Vector2D bottomLeft = tmpBuilding.getPosition().add(new Vector2D(0,
					tmpBuilding.getLength()));
			Vector2D bottomRight = tmpBuilding.getPosition().add(new Vector2D(
					tmpBuilding.getWidth(), tmpBuilding.getLength()));

			// Alle W�nde ber�cksichtigen
			if (IntersectionTests.lineSegemntCircleIntersection(topLeft,
					topRight,
					position,
					radius)
					|| IntersectionTests.lineSegemntCircleIntersection(topLeft,
							bottomLeft,
							position,
							radius)
					|| IntersectionTests.lineSegemntCircleIntersection(topRight,
							bottomRight,
							position,
							radius)
					|| IntersectionTests.lineSegemntCircleIntersection(bottomLeft,
							bottomRight,
							position,
							radius))
			{
				return true;
			}
		}
		return false;
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return Getter f�r die Adjazenzlisten-Darstellung der Karte (begebare
	 *         Knoten und Kanten)
	 */
	public AdjacenceList getNavGraph()
	{
		return navGraph;
	}

	public void setNavGraph(AdjacenceList navGraph)
	{
		this.navGraph = navGraph;
	}

	/**
	 * F�gt ein neues Geb�ude in die Simulationswelt ein
	 * 
	 * @param building
	 *            Das neue Geb�ude
	 */
	public void addBuilding(ExigenceBuilding building)
	{
		this.buildings.add(building);
	}

	public void addNonInteractiveObjekt(NonInteractiveObject objekt)
	{
		this.nonInteractiveObjects.add(objekt);
	}

	public int getWorldID()
	{
		return worldID;
	}

	public void setWorldID(int worldID)
	{
		this.worldID = worldID;
	}

	public Collection<ExigenceBuilding> getBuildings()
	{
		return buildings;
	}

	public void setBuildings(Collection<ExigenceBuilding> buildings)
	{
		this.buildings = buildings;
	}

	public Collection<NonInteractiveObject> getNonInteractiveObjects()
	{
		return nonInteractiveObjects;
	}

	public void setNonInteractiveObjects(Collection<NonInteractiveObject> nonInteractiveObjects)
	{
		this.nonInteractiveObjects = nonInteractiveObjects;
	}

}
