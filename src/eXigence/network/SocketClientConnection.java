package eXigence.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javassist.NotFoundException;
import eXigence.core.Simulation;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.network.messages.AbstractMessageFactory;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.IDisconnect;
import eXigence.network.messages.IMessage;
import eXigence.network.messages.IMessageFactory;
import eXigence.network.messages.IOutgoingMessage;
import eXigence.network.messages.VersionMessage;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Klasse zur Verwaltung einer Socketverbindung zu einem Client. Repräsentiert
 * einen Teilnehmer am Server.
 * 
 * @version 0.2
 * @author Lars George
 */
public class SocketClientConnection extends IClientConnection
{

	/**
	 * Eine ID für die einzelnen Threads, um sie wieder zu erkennen
	 */
	private static int			IDCOUNTER				= 0;
	private int					connectionID;

	/**
	 * Simulationsobjekt für die interne Kommmunikation
	 */
	private Simulation			simulation				= null;

	/**
	 *Socketobjekt welches für die Verbindung zuständig ist
	 */
	private Socket				socket					= null;

	/**
	 * Objekt zum lesen der eingehenden Daten
	 */
	private BufferedReader		reader					= null;

	/**
	 * Objekt zum senden der Daten an den Flashclient
	 */
	private PrintWriter			writer					= null;

	/**
	 * Zugehöriges PlayerProfil-Objekt, wird erst nach erfolgreichem Login
	 * erzeugt
	 */
	private PlayerProfile		playerProfil			= null;

	/**
	 * Nachrichtenfabrik, welche versionierte Nachrichten erstellt (abhängig vom
	 * Client)
	 */
	private IMessageFactory		msgFactory				= null;

	/**
	 * Status der Verbindung
	 */
	private EConnectionState	conState				= EConnectionState.Created;

	private NonPlayerCharacter	controlledCharacter	= null;

	public LogWriter			logWriter				= LogWriter.getInstance();
	
	private String				msgText	= "";

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * 
	 */
	public SocketClientConnection()
	{
		this.connectionID = IDCOUNTER++;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode um Nachrichten an den Client zu senden
	 * 
	 * @param msg
	 *            Nachricht die versendet werden soll
	 */
	public synchronized void send(IOutgoingMessage msg)
	{
		try
		{
			// Die UpdateCharacters nicht anzeigen, zu viele
			if (!msg.getMessageType().equals(EMessageType.UpdateCharacters) &&
				!msg.getMessageType().equals(EMessageType.WorldData))
				logWriter.logToFile(LogLevel.Debug, "{ConnectionID: "
						+ this.connectionID + "} SENDING: "
						+ msg.toNetworkString());

			msgText = msg.toNetworkString() + '\u0000';

			//System.out.println(msgText.length() + " :: " + socket.getSendBufferSize());
			// Wenn der SendBuffer zu klein ist, diesn erweitern
			/*if(socket.getSendBufferSize() < (msgText.length() * 2))
			{								
				socket.setSendBufferSize(msgText.length() * 2);
				System.out.println("Änderung auf: " + socket.getSendBufferSize() + "___" + msgText.length());
			}*/			
			/*
			int i = 0,
				msgLength = 100;
			while(i < msgText.length())
			{
				if(i >= msgText.length() - msgLength)
				{
										
					System.out.println(msgText.charAt(i-1 + (msgText.length() - i )));
					
					writer.write(msgText, i, msgText.length() - i -1);
				}
				else
				{
					writer.write(msgText, i, msgLength );									
				}
				
				i+= msgLength;
				if(writer.checkError())
					LogWriter.getInstance().logToFile(LogLevel.Error, "SocketClientConnection->send() encountered an errir while flushing a msg to a client.");
				//writer.flush();
			}*/
			
			writer.print(msgText);
			
			
			//if(writer.checkError())
				//	LogWriter.getInstance().logToFile(LogLevel.Error, "SocketClientConnection->send() encountered an error while flushing a msg to a client.");
			writer.flush();
		}
		catch (Exception e)
		{
			logWriter.logToFile(LogLevel.Error, "{ConnectionID: "
					+ this.connectionID + "} Failed sending a Message [Type: "
					+ msg.getMessageType() + ", Version: " + msg.getVersion()
					+ "] to a client. " + e.toString());
		}
	}

	/**
	 * Hauptschleife des FlashClients Es werden Nachrichten char weise empfangen
	 * und zu einem String zusammengefasst anschließend wird dies in die
	 * Nachrichtenqueue gepackt um verarbeitet zu werden
	 */
	public void run()
	{
		char buffer[] = new char[1];
		int readResult;
		StringBuilder text;

		try
		{
			do
			{
				text = new StringBuilder("");

				// So lange etwas gelesen wurde und dann nicht das Ende des
				// Streams erreicht ist
				while (true)
				{
					readResult = reader.read(buffer, 0, 1);
					if ((buffer[0] == 0) || (readResult == -1))
						break;

					// Gelesenes Zeichen hinzufügen
					text.append(buffer[0]);
				}

				// Wenn etwas gelesen wurde
				if (text.length() > 0)
				{
					// Abhängig vom Status, die Nachricht auswerten
					switch (conState)
					{
					case Initialising:
						LogWriter.getInstance().logToFile(LogLevel.Debug,
								"{ConnectionID: "
										+ this.connectionID
										+ "} SocketClientConnection::run() -> State: Initialising, Got a Message, trying to create VersionMessage from that...");

						// Ist es eine Handshake Nachricht?
						VersionMessage versionMsg = null;
						try
						{
							// Versuchen eine entsprechende Nachricht zu
							// erstellen, welche die Version repräsentiert
							versionMsg = VersionMessage.getVersionMessage(text.toString());
						}
						catch (IllegalArgumentException e)
						{
							LogWriter.getInstance().logToFile(LogLevel.Warn,
									"{ConnectionID: "
											+ this.connectionID
											+ "} SocketClientConnection::run() -> Version could not be identified, waiting for next Message...");
							break;
						}

						// Versionierte Messagefactory erzeugen und sezten
						setMessageFactory(AbstractMessageFactory.getMessageFactory(versionMsg.getVersion()));

						// Wenn die Factory erfolgreich gesetzt werden konnte
						if (msgFactory != null)
						{
							LogWriter.getInstance().logToFile(LogLevel.Debug,
									"{ConnectionID: "
											+ this.connectionID
											+ "} SocketClientConnection::run() -> Version successfully found: "
											+ versionMsg.getVersion()
											+ ", Now changing state to: Running");

							// ConnectionState auf Running setzen (damit
							// Nachrichten verarbeitet werden können
							this.setConnectionState(EConnectionState.Running);
						}

						break;

					case Running:
						// String in eine entsprechende Nachricht umwandeln
						IMessage msg = msgFactory.createMessageFromString(text.toString());

						if (!msg.getMessageType().equals(EMessageType.Interaction))
							logWriter.logToFile(LogLevel.Debug,
									"{ConnectionID: " + this.connectionID
											+ "} RECEIVED:" + text.toString());

						// Nachricht dem System hinzufügen
						simulation.addMessage(new MessageItem(this, msg));

						// Wenn es eine Disconnect Nachricht war, nicht mehr
						// weiter auf dem Stream lesen
						if (msg.getMessageType().equals(EMessageType.Disconnect))
							readResult = -1;
						break;

					case Stoped:
						logWriter.logToFile(LogLevel.Warn, "{ConnectionID: "
								+ this.connectionID
								+ "} Received Message in State STOPED !!");
						break;
					}
				}
			} while (readResult != -1);
		}
		catch (Exception e)
		{
			logWriter.logToFile(LogLevel.Warn, "{ConnectionID: "
					+ this.connectionID
					+ "} An Exception appeared in a connection : "
					+ e.toString());
		}

		// Wenn die Socketverbindung noch besteht, diese schließen (als wenn der
		// Benutzer eine Disconnect Nachricht geschickt hätte)
		if (!socket.isClosed())
		{
			// Wenn schon eine MessageFactory existiert
			if (msgFactory != null)
			{
				// Neue Disconnect Nachricht erstellen
				IDisconnect discMsg = (IDisconnect) msgFactory.createEmptyMessageByType(EMessageType.Disconnect);
				discMsg.setReason("Timeout");

				// Der Verarbeitungsqueue hinzufügen
				simulation.addMessage(new MessageItem(this, discMsg));
			}
			else
			{
				// Es gab noch keinen Versionshandshake vor dem Abbruch
				closeConnection();
			}
		}

		// Den Connectionstate entsprechend setzen
		this.setConnectionState(EConnectionState.Stoped);
	}

	/**
	 * Beendet die Socketverbindung zum Server
	 */
	public void closeConnection()
	{
		try
		{
			if (!socket.isClosed())
			{
				logWriter.logToFile(LogLevel.Debug, "{" + this.connectionID
						+ "} Closing Connection...");

				socket.close();
				this.conState = EConnectionState.Stoped;
			}

		}
		catch (IOException e)
		{
			logWriter.logToFile(LogLevel.Error, "{ConnectionID: "
					+ this.connectionID + "} Failed closing the connection: "
					+ e.toString());
		}
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	@Override
	public void setPlayerProfil(PlayerProfile player)
	{
		playerProfil = player;
	}

	@Override
	public PlayerProfile getPlayerProfil() throws NotFoundException
	{
		if (playerProfil == null)
			throw new NotFoundException("No PlayerProfil found");
		return playerProfil;
	}

	@Override
	public void setSimulation(Simulation simulation)
	{
		this.simulation = simulation;

	}

	/**
	 * Setzt das Socket Objekt
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public void setSocket(Socket socket) throws IOException
	{
		this.socket = socket;

		socket.setTcpNoDelay(true);
		socket.setPerformancePreferences(0, 1, 2);

		// BufferedReader initialisieren (InputStream der Socketverbindung über
		// einen StreamReader gemanaged)
		this.reader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));

		// PrinWriter initialisieren (OutputStream der Socketverbindung)
		this.writer = new PrintWriter(this.socket.getOutputStream());
		
	}

	@Override
	public void setMessageFactory(IMessageFactory msgFactory)
	{
		this.msgFactory = msgFactory;
	}

	@Override
	public IMessageFactory getMessageFactory()
	{
		return msgFactory;
	}

	@Override
	public EConnectionState getConnectionState()
	{
		return this.conState;
	}

	@Override
	public void setConnectionState(EConnectionState state)
	{
		this.conState = state;
	}

	@Override
	public boolean isClosed()
	{
		return this.socket.isClosed();
	}

	@Override
	public int getConectionID()
	{
		return this.connectionID;
	}

	@Override
	public NonPlayerCharacter getControlledCharacter()
	{
		return controlledCharacter;
	}

	@Override
	public void setControlledCharacter(NonPlayerCharacter character)
	{
		this.controlledCharacter = character;
	}
}
