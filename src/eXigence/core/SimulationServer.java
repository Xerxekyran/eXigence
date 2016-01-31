package eXigence.core;

import eXigence.network.AbstractConnectionFactory;
import eXigence.network.IClientConnection;
import eXigence.network.IConnectionFactory;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * @author Lars George
 * @version 0.1
 * 
 *          Der Server dient als Listener - Thread, er horcht also auf
 *          eingehende Verbindungen und übergibt diese dann der Simulationslogik
 * 
 */
public class SimulationServer implements Runnable
{

	/**
	 * Referenz auf dn LogWriter (für die Ausgabe von Nachrichten
	 * [Error/Debug/Info])
	 */
	public LogWriter	logWriter	= LogWriter.getInstance();

	/**
	 * Instanz der Simulationslogik. Ihr werden alle eingehenden Vebrindungen
	 * übergeben
	 */
	private Simulation	simulation	= null;

	/**
	 * Thread zur Verwaltung von neu einkommenden Socketverbindungen
	 */
	private Thread		listener	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * 
	 * @param args
	 *            Konsolenparameter (nich nicht verwendet)
	 */
	public SimulationServer(String args[])
	{
		// Thread erstellen um eingehende Verbindungen entgegenzunehmen
		listener = new Thread(this);
		listener.start();

		// Instanz der Simulationslogik erstellen
		simulation = new Simulation();

		logWriter.logToFile(LogLevel.Info, "Server started.");				
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Hauptschleife für den Server Es wird auf neue Socketverbindungen gewartet
	 * und diese dann der Simulations übergeben
	 */
	public void run()
	{
		try
		{
			// ConnectionFactory holen
			IConnectionFactory conFactory = AbstractConnectionFactory.getConnectionFactory();

			while (true)
			{
				// Auf eine neue Verbindung warten
				IClientConnection newClient = conFactory.getNewConnection();

				// Zur Liste der Clients hinzufügen
				newClient.setSimulation(simulation);
				simulation.addClientConnection(newClient);

				logWriter.logToFile(LogLevel.Info, "New Client registered");
			}
		}
		catch (Exception e)
		{
			logWriter.logToFile(LogLevel.Error,
					"The server has a critical error: " + e.toString());
		}
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
