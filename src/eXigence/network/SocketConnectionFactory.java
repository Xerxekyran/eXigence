package eXigence.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Fabrikklasse zum erzeugen von MessageFactory Objekten für die jeweilige
 * Version
 * 
 * @author Lars George
 * 
 */
public class SocketConnectionFactory implements IConnectionFactory
{
	private static SocketConnectionFactory	instance;

	/**
	 * Socketobjekt des Servers für die Netzwerkverbindungen
	 */
	private ServerSocket					serverSocket	= null;

	/**
	 * Der verwendete Port des Servers für die Netzwerkkommunikation
	 */
	public static final int					SERVERPORT		= 1024;

	/**
	 * Konstruktor
	 */
	private SocketConnectionFactory()
	{
		// Socket aufbauen
		try
		{
			serverSocket = new ServerSocket(SERVERPORT);
		}
		catch (IOException e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"SocketConnectionFactory::SocketConnectionFactory() -> Error in the standard Constructor: "
							+ e.toString());
		}
	}

	/**
	 * Singleton Methode um die Referenz auf das eine Objekt dieser Klasse zu
	 * bekommen
	 * 
	 * @return Die Instanz der SocketConnectionFactory
	 */
	public static SocketConnectionFactory getInstance()
	{
		if (instance == null)
			instance = new SocketConnectionFactory();

		return instance;
	}

	@Override
	public IClientConnection getNewConnection()
	{
		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"SocketConnectionFactory::getNewConnection() -> Waiting for new Connection");

		IClientConnection con = null;
		try
		{
			// Warten bis eine neue Verbindung an kommt
			Socket clientSocket = serverSocket.accept();

			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"SocketConnectionFactory::getNewConnection() -> New Connection found!");

			// Neuen Client erstellen und starten
			con = new SocketClientConnection();

			// Dem Verdbindungsobjekt die aktive Socketverbindung übergeben
			((SocketClientConnection) con).setSocket(clientSocket);

			// Verbindungsobjekt starten (Thread)
			con.setConnectionState(EConnectionState.Initialising);
			con.start();

			LogWriter.getInstance().logToFile(LogLevel.Debug,
					"SocketConnectionFactory::getNewConnection() -> New Connection now running and set to Initialising");
		}
		catch (Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"Error while creating new SocketConnection: "
							+ e.toString());
		}

		return con;
	}
}
