package eXigence;

import eXigence.core.ConfigLoader;
import eXigence.core.SimulationServer;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Programmeinstiegspunkt
 * 
 * @author Lars George
 * @version 0.1
 */
public class Main
{

	/**
	 * Haupteinstiegspunkt des eXigence Servers
	 * 
	 * @param args
	 *            Parameter bei Programmstart (noch keine Unterstützung)
	 */
	public static void main(String[] args)
	{
		// Logger konfigurieren
		//LogWriter.getInstance();
		
		// Konfigurationsloader-Objekt initialisieren
		ConfigLoader.getInstance();

		// Einen Server erstellen
		new SimulationServer(args);
	}
}
