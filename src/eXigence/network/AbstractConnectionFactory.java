package eXigence.network;

import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * 
 * @author Lars George
 * 
 */
public abstract class AbstractConnectionFactory
{
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Liefert eine Factory zum erstellen von IConnection abgeleiteten Klassen
	 * 
	 * @return Konkretes ConnectionFactory-Objekt
	 */
	public static IConnectionFactory getConnectionFactory()
	{
		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"AbstractConnectionFactory::getConnectionFactory()");

		return SocketConnectionFactory.getInstance();
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
