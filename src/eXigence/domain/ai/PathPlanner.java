package eXigence.domain.ai;

import java.util.Iterator;
import java.util.LinkedList;

import eXigence.domain.ENecessities;
import eXigence.domain.ETelegramType;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.TelegramDispatcher;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.domain.map.ESearchResult;
import eXigence.domain.map.GraphSearchAStarTimeSliced;
import eXigence.domain.map.MapNode;
import eXigence.domain.map.WorldMap;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

/**
 * Klasse zum planen von Wegen (benutzt den A* Algorithmus für die Suchanfragen)
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public class PathPlanner
{
	// Der Besitzer des Planers
	private NonPlayerCharacter			owner	= null;

	// Die Karte auf der die Suche stattfinden soll
	private WorldMap					worldMap;

	// Die Zielposition der Suche
	private Vector2D					destinationPos;

	// Aktuelles Suchobjekt (A* Suchanfrage welche zeitlich aufgeteilt werden
	// kann)
	private GraphSearchAStarTimeSliced	currentSearch;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public PathPlanner(NonPlayerCharacter owner, WorldMap map)
	{
		this.owner = owner;
		worldMap = map;
		currentSearch = null;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Zu erst wird geprüft ob das Ziel überhaupt erreichbar ist von der
	 * Position des NPC aus. Wenn er erreichbar ist, wird ein Weg anhand des A*
	 * Algorithmus gesucht: Es wird eine Instanz des zeitlich aufgetrennten
	 * Suchalgorithmus erstellt.
	 * 
	 * @param targetPos
	 *            Das zu erreichende Ziel
	 * @return true wenn der Punkt erreichbar ist ansonsten false
	 */
	public boolean requestPathToPosition(Vector2D targetPos)
	{
		// Alte informationen aufräumen
		getReadyForNewSearch();

		destinationPos = targetPos;

		// Wenn ein direkter Weg existiert
		if (owner.canWalkTo(targetPos))
			return true;

		// Den nächsten sichtbaren Knoten am NPC finden
		int closestNodeIndexToNPC = getClosestNodeIndexToPosition(owner.getPosition());

		// Wenn kein Knoten gefunden wurde
		if (closestNodeIndexToNPC == -1)
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"Pathfinding: Kein Knoten beim NPC gefunden");
			return false;
		}

		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Pathfinding: Knoten beim NPC gefunden ["
						+ closestNodeIndexToNPC + "]");

		// Den nächsten sichtbaren Knoten am Ziel finden
		int closestNodeIndexToTarget = getClosestNodeIndexToPosition(targetPos);

		// Wenn kein Knoten gefunden wurde
		if (closestNodeIndexToTarget == -1)
		{
			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"Pathfinding: Kein Knoten am Ziel gefunden");
			return false;
		}

		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Pathfinding: Knoten am Ziel gefunden ["
						+ closestNodeIndexToTarget + "]");

		// Verteiltes A* Algorithmus Objekt instanziieren
		currentSearch = new GraphSearchAStarTimeSliced(worldMap.getNavGraph(),
				closestNodeIndexToNPC, closestNodeIndexToTarget);

		// Und mit dem Manager registrieren
		owner.getHomeSimulation().getPathManager().register(this);

		return true;
	}

	/**
	 * Versucht eine Suchanfrage zu dem nächstgelegenen Gebäude zu erstellen,
	 * welches das übergebene Bedürfnis befriedigt
	 * 
	 * @param necessitie
	 *            das benötigte bedürfnis
	 * @return Wenn ein Weg gefunden wurde, wird die Referenz auf das Gebäude zurückgegeben ansonsten null
	 */
	public ExigenceBuilding requestPathToBuilding(ENecessities necessitie)
	{
		/*LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Pathfinding [" + owner.getFirstName() + ", "
						+ owner.getLastName() + "]: requestPathToBuilding("
						+ necessitie.toString() + ")");
		 */
		// aufräumen
		getReadyForNewSearch();
		
		// Position des anzusteuernden Eingangs
		Vector2D entrancePosition = new Vector2D(0,0);

		ExigenceBuilding building = null;
		double dist = Double.MAX_VALUE;

		// Alle gebäude durchgehen und das raus suchen, welches das Bedürfnis
		// befriedigt und am nächsten dran ist
		Iterator<ExigenceBuilding> it = worldMap.getBuildings().iterator();
		while (it.hasNext())
		{
			ExigenceBuilding tmpBuilding = it.next();			
			
			// Wenn es das richtige Bedürfnis behandelt auf die Entfernung achten
			if (tmpBuilding.getNecessityType().equals(necessitie) || necessitie.equals(ENecessities.Money))
			{		
				// Gebäude ohne Employee Eingang beim Bedürfnis Money nicht berücksichtigen
				if(necessitie.equals(ENecessities.Money))
				{
					if(		tmpBuilding.getNecessityType().equals(ENecessities.Strangury) ||
							tmpBuilding.getNecessityType().equals(ENecessities.Education) ||
							tmpBuilding.getNecessityType().equals(ENecessities.Fitness) ||
							tmpBuilding.getNecessityType().equals(ENecessities.Death) ||
							tmpBuilding.getNecessityType().equals(ENecessities.Hygiene))
						continue;
				}
				
				// Absolute Position des Eingangs errechnen
				entrancePosition = (tmpBuilding.getPosition().add(tmpBuilding.getEntrancePos(necessitie))).rotateBy(tmpBuilding.getHeading().getAngleInRad());
				double distToBuilding = owner.getPosition().distance(entrancePosition);

				// Wenn es ein kürzerer Weg ist als der bisher gefundene
				if (distToBuilding < dist)
				{
					/*LogWriter.getInstance().logToFile(LogLevel.Debug,
							"Pathfinding [" + owner.getFirstName() + ", "
									+ owner.getLastName()
									+ "]: requestPathToBuilding("
									+ necessitie.toString()
									+ ") -> found closer Building ["
									+ tmpBuilding.getModelName() + "]");
					 */
					// Dieses Gebäude merken
					building = tmpBuilding;
					dist = distToBuilding;
				}
			}
		}

		// Wenn kein Gebäude gefunden werden konnte, kann auch kein Weg
		// berechnet werden
		if (building == null)
		{
			LogWriter.getInstance().logToFile(LogLevel.Warn,
					"Pathfinding: no building found");
			return null;
		}

		// Absolute Position nochmal berechnen, weil sie wahrscheinlich mit einem anderen Wert überschrieben wurde
		entrancePosition = (building.getPosition().add(building.getEntrancePos(necessitie))).rotateBy(building.getHeading().getAngleInRad());
		
		// Knoten des Graphen holen, welche an den Positionen der Punkte liegen
		int closestNodeIndexToNPC = getClosestNodeIndexToPosition(owner.getPosition());
		int closestNodeIndexToBuilding = getClosestNodeIndexToPosition(entrancePosition);

		if (closestNodeIndexToNPC < 0 || closestNodeIndexToBuilding < 0)
		{
			LogWriter.getInstance().logToFile(LogLevel.Warn,
					"Pathfinding ["
							+ owner.getFirstName()
							+ ", "
							+ owner.getLastName()
							+ "]: Cannot reach the tow nodes to get to the building");
			return null;
		}

		/*LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Pathfinding [" + owner.getFirstName() + ", "
						+ owner.getLastName() + "]: requestPathToBuilding("
						+ necessitie.toString() + ") -> Nodeindex of NPC:["
						+ closestNodeIndexToNPC + "] Nodeindex of Building:["
						+ closestNodeIndexToBuilding + "]");
*/
		// Verteiltes A* Algorithmus Objekt instanziieren
		currentSearch = new GraphSearchAStarTimeSliced(worldMap.getNavGraph(),
				closestNodeIndexToNPC, closestNodeIndexToBuilding);

		destinationPos = entrancePosition;
		
		// Und mit dem Manager registrieren
		owner.getHomeSimulation().getPathManager().register(this);

		return building;
	}

	/**
	 * Wird von einem NPC aufgerufen, nachdem er benachrichtit wurde, dass für
	 * ihn ein Weg berechnet wurde Es werden noch weitere Kanten hinzugefügt,
	 * damit der NPC den Weg korrekt begehen kann
	 * 
	 * @return Der zu gehende Weg als Liste von Kanten
	 */
	public LinkedList<PathEdge> getPath()
	{
		// Wenn keine Suche aktiv ist, kann es auch keinen Weg geben
		if (currentSearch == null)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"PathPlanner ["
							+ owner.getFirstName()
							+ ", "
							+ owner.getLastName()
							+ "]: Tried to call getPath() without an active Search");
			return null;
		}

		// Den berechneten Weg holen
		LinkedList<PathEdge> path = currentSearch.getShortestPathAsPathEdges();

		int closest = getClosestNodeIndexToPosition(owner.getPosition());

		if (closest < 0)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"PathPlanner ["
							+ owner.getFirstName()
							+ ", "
							+ owner.getLastName()
							+ "]: no closest node for the npc available. position ["
							+ owner.getPosition() + "]");
			return null;
		}

		// Neue "Kante" von der aktuellen Position zum ersten Knoten erstellen
		path.addFirst(new PathEdge(owner.getPosition(),
				worldMap.getNavGraph().getNodes().get(closest).getPosition()));
		
		// Letzte "Kante" direkt zum genauen Ziel anfügen
		path.addLast(new PathEdge(path.getLast().getDestination(),
				destinationPos));

		return path;
	}

	/**
	 * Wird vom PathManager aufgerufen um die Suchanfrage einen cyclus weiter
	 * durchzuführen
	 * 
	 * @return Status der Suchanfrage
	 */
	public ESearchResult cycleOnce()
	{
		if (currentSearch == null)
			return ESearchResult.target_not_found;

		// Die Suche einen Cyclus weiter laufen lassen
		ESearchResult result = currentSearch.cycleOnce();

		// Wenn kein Weg gefunden werden kann den NPC entsprechend
		// benachrichtigen
		if (result.equals(ESearchResult.target_not_found))
		{
			TelegramDispatcher.getInstance().DispatchTelegram(TelegramDispatcher.SEND_TLG_IMMEDIATELY,
					TelegramDispatcher.SENDER_ID_IRRELEVANT,
					owner.getID(),
					ETelegramType.NoPathAvailable,
					null);
		}
		// Wenn ein Weg gefunden den NPC dies mitteilen
		else if (result.equals(ESearchResult.target_found))
		{
			TelegramDispatcher.getInstance().DispatchTelegram(TelegramDispatcher.SEND_TLG_IMMEDIATELY,
					TelegramDispatcher.SENDER_ID_IRRELEVANT,
					owner.getID(),
					ETelegramType.PathReady,
					null);
		}

		return result;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Räumt alles so weit auf, das eine neue Suchanfrage gestartet werden kann
	 */
	private void getReadyForNewSearch()
	{
		owner.getHomeSimulation().getPathManager().unRegister(this);

		currentSearch = null;
	}

	/**
	 * 
	 * @param position
	 * @return Index des am nächsten liegenden Knoten. -1 wenn keiner gefunden
	 *         wurde
	 */
	private int getClosestNodeIndexToPosition(Vector2D position)
	{
		/*LogWriter.getInstance().logToFile(LogLevel.Debug,
				"Pathfinding [" + owner.getFirstName() + ", "
						+ owner.getLastName()
						+ "]: getClosestNodeIndexToPosition("
						+ position.toString() + ")");
		 */
		double closestSoFar = Double.MAX_VALUE;
		double dist = 0.0;

		int closestNodeIndex = -1;

		// Über alle Nodes iterieren und schauen welcher davon am nähesten am
		// NPC liegt
		Iterator<MapNode> it = worldMap.getNavGraph().getNodes().iterator();
		while (it.hasNext())
		{
			MapNode tmpNode = it.next();
			if (owner.canWalkBetween(position, tmpNode.getPosition()))
			{
				// Wuadratische Entfernung zum Knoten
				dist = position.distanceSquare(tmpNode.getPosition());

				// Wenn dieser näher dran ist
				if (dist < closestSoFar)
				{
					// Werte aktualisieren
					closestSoFar = dist;
					closestNodeIndex = worldMap.getNavGraph().getNodes().indexOf(tmpNode);
				}
			}
		}

		// Den Knoten zurückgeben der am nächsten liegt
		return closestNodeIndex;
	}
	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
