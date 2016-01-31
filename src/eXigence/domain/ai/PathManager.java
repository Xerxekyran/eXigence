package eXigence.domain.ai;

import java.util.LinkedList;

import eXigence.domain.map.ESearchResult;

/**
 * Klasse zum verwalten von Suchaufträgen. Die anzahl von erlaubten Aufrufen
 * (Cycles) wird gleichmäßig auf die Anzahl von Suchaufträgen verteilt
 * 
 * @author Lars George
 * 
 */
public class PathManager
{
	// Anzahl von cycleOnce-Methoden Aufrufe pro Update des Managers
	private int						numCyclesPerUpdate;

	// Liste von PathPlannern, welche die Wegberechnung für einen NPC übernehmen
	private LinkedList<PathPlanner>	searchRequests	= new LinkedList<PathPlanner>();

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public PathManager(int numCyclesPerUpdate)
	{
		this.numCyclesPerUpdate = numCyclesPerUpdate;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Bei jedem Aufruf dieser Methode wird die zur verfügung stehende Anzahl
	 * von "cycles" auf die Suchanfragen verteilt. Wenn eine Suche erfolgreich
	 * oder fehlerhaft war, wird der entsprechende NPC benachrichtigt
	 */
	public void updateSearches()
	{
		int numCyclesRemaing = numCyclesPerUpdate;
		int i = 0;

		while (numCyclesRemaing > 0 && !searchRequests.isEmpty())
		{
			// Suche einmal weiter laufen lassen
			ESearchResult result = searchRequests.get(i).cycleOnce();

			// Wenn die Suche abgeschlossen ist
			if (result.equals(ESearchResult.target_found)
					|| result.equals(ESearchResult.target_not_found))
			{
				searchRequests.remove(i);				
			}
			i++;

			// Aufpassen das wir innerhalb der Liste bleiben
			if (i >= searchRequests.size())
				i = 0;
		}
	}

	/**
	 * Meldet eine neue Suchanfrage an. Es wird darauf geachtet, das diese
	 * Suchanfrage nicht schon existiert
	 * 
	 * @param pathPlanner
	 *            Die neue Suchanfrage
	 */
	public void register(PathPlanner pathPlanner)
	{
		if (searchRequests.contains(pathPlanner))
			return;

		searchRequests.add(pathPlanner);
	}

	/**
	 * Meldet eine bestehende Suchanfrage ab
	 * 
	 * @param pathPlanner
	 *            Die ab zu meldende Suchanfrage
	 */
	public void unRegister(PathPlanner pathPlanner)
	{
		searchRequests.remove(pathPlanner);
	}

	public int getNumActiveSearches()
	{
		return searchRequests.size();
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
