package eXigence.core;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javassist.NotFoundException;
import eXigence.domain.ExigenceCharacter;
import eXigence.domain.NonInteractiveObject;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.domain.TelegramDispatcher;
import eXigence.domain.ai.PathManager;
import eXigence.domain.ai.PathPlanner;
import eXigence.domain.ai.SteeringBehaviour;
import eXigence.domain.entities.EntityManager;
import eXigence.domain.entities.VisibleEntity;
import eXigence.domain.entities.buildings.Bank;
import eXigence.domain.entities.buildings.Bathhouse;
import eXigence.domain.entities.buildings.Cinema;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.domain.entities.buildings.Gym;
import eXigence.domain.entities.buildings.Hospital;
import eXigence.domain.entities.buildings.Hotel;
import eXigence.domain.entities.buildings.Library;
import eXigence.domain.entities.buildings.Police;
import eXigence.domain.entities.buildings.Restaurant;
import eXigence.domain.entities.buildings.Toilet;
import eXigence.domain.map.WorldMap;
import eXigence.domain.systemMessages.ISystemMessage;
import eXigence.domain.systemMessages.PersistWorld;
import eXigence.network.EConnectionState;
import eXigence.network.IClientConnection;
import eXigence.network.MessageItem;
import eXigence.network.messages.EInteractionType;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.IChangeProfilFailed;
import eXigence.network.messages.IChangeProfilRequest;
import eXigence.network.messages.IChangeProfileSuccess;
import eXigence.network.messages.IChat;
import eXigence.network.messages.IControllingCharacterBeginFailed;
import eXigence.network.messages.IControllingCharacterBeginRequest;
import eXigence.network.messages.IControllingCharacterBeginSuccess;
import eXigence.network.messages.IControllingCharacterEndFailed;
import eXigence.network.messages.IControllingCharacterEndSuccess;
import eXigence.network.messages.ICreateCharacterFailed;
import eXigence.network.messages.ICreateCharacterRequest;
import eXigence.network.messages.ICreateCharacterSuccess;
import eXigence.network.messages.IDeleteCharacterFailed;
import eXigence.network.messages.IDeleteCharacterRequest;
import eXigence.network.messages.IDeleteCharacterSuccess;
import eXigence.network.messages.IDeleteProfileFailed;
import eXigence.network.messages.IDeleteProfileSuccess;
import eXigence.network.messages.IEditCharacterFailed;
import eXigence.network.messages.IEditCharacterRequest;
import eXigence.network.messages.IEditCharacterSuccess;
import eXigence.network.messages.IInteraction;
import eXigence.network.messages.ILoginFailed;
import eXigence.network.messages.ILoginRequest;
import eXigence.network.messages.ILoginSuccess;
import eXigence.network.messages.IMessage;
import eXigence.network.messages.IOutgoingMessage;
import eXigence.network.messages.IRegisterFailed;
import eXigence.network.messages.IRegisterRequest;
import eXigence.network.messages.IRegisterSuccess;
import eXigence.network.messages.IStatistics;
import eXigence.network.messages.IUpdateCharacters;
import eXigence.network.messages.IWorldData;
import eXigence.services.EServices;
import eXigence.services.ISimulationService;
import eXigence.services.IUserservice;
import eXigence.services.ServiceLocator;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Dise Klasse repräsentiert eine Simulation auf dem eXigence Server <br />
 * Es wird eine Liste gehalten, welche alle Verbindungen zu den Teilnehmern hält
 * und eine Liste zur Verwaltung der Nachrichten die abgearbeitet werden müssen
 * (ausgehend von den Teilnehmern und der Spiellogik)
 * 
 * @author Lars George
 * @version 0.1
 */
public class Simulation implements Runnable
{

	/**
	 * Aufzählung für die möglichen Spieleraktionen
	 */
	public enum PlayerCommand
	{
		MOVE_LEFT, MOVE_RIGHT, MOVE_UP, MOVE_DOWN
	}

	/**
	 * Referenz auf den LogWriter (für die Ausgabe von Nachrichten
	 * [Error/Debug/Info])
	 */
	private LogWriter									logWriter									= LogWriter.getInstance();

	/**
	 * Abbruchbedingung für die Simulation
	 */
	private boolean										abort										= false;

	/**
	 * Die verbindung zur Persistenzschicht über einen Userservice
	 */
	private IUserservice								userService									= null;

	/**
	 * Service welches globale Simulationsfunktionalität zur verfügung stellt,
	 * hauptsächlich für den Zugriff auf die Persistenzschicht
	 */
	private ISimulationService							simService									= null;

	/**
	 * Momentane Anzahl von angemeldeten Spielern. Unterscheidet sich von der
	 * Anzahl der Verbindungen, so lange der Client sich noch nicht per
	 * LoginRequest Nachricht erfolgreich angemeldet hat
	 */
	private int											numPlayers									= 0;

	/**
	 * Thread zur Abarbeitung der Nachrichten
	 */
	private Thread										simulationUpdate							= null;

	/**
	 * Liste der aktiven Verbindungen zu Clients
	 */
	private ConcurrentLinkedQueue<IClientConnection>	clients										= new ConcurrentLinkedQueue<IClientConnection>();

	/**
	 * Liste aller NPC die momentan auf dem Server aktiv sind
	 */
	private ConcurrentLinkedQueue<NonPlayerCharacter>	npcs										= new ConcurrentLinkedQueue<NonPlayerCharacter>();

	/**
	 * Liste von Nachrichten die abgearbeitet werden müssen. <br />
	 * Über die Methode addMessage(eXigence.Message) können neue Nachrichten
	 * hinzugefügt werden (üblicherweise hauptsächlich von der ClientConnection
	 * aus)
	 */
	private ConcurrentLinkedQueue<MessageItem>			messages									= new ConcurrentLinkedQueue<MessageItem>();

	/**
	 * Liste von Systemnachrichten die abgearbeitet werden sollen
	 */
	private ConcurrentLinkedQueue<ISystemMessage>		systemMessages								= new ConcurrentLinkedQueue<ISystemMessage>();

	/**
	 * Ein Pfadmanager der die Suchaufträge verwaltet (und gerecht auf die
	 * Rechenzeit verteilt)
	 */
	private PathManager									pathManager									= new PathManager(
																											100);
	/**
	 * Die Karte auf der die Simulation stattfindet
	 */
	private WorldMap									map											= new WorldMap(
																											this);
	/**
	 * Zeit die mindestens für ein Updatezyklus gebraucht werden muss (wenn es
	 * schneller geht, wartet der Thread entsprechend lang)
	 */
	private int											MIN_MS_FOR_ONE_UPDATECYCLE					= 20;

	/**
	 * Zeit in Millisekunden, die vergehen muss bis die Simulation alle Objekte
	 * persistent abspeichert
	 */
	private int											MS_BETWEEN_SAVE_WORLD						= 60000;

	/**
	 * Zeitlicher Abstand zwischen den periodischen Nachrichten an die Spieler
	 * mit den aktuellen Daten
	 */
	private int											MS_FOR_ONE_BROADCASTWORLD_UPDATE			= 250;

	/**
	 * Interne Zählvariable um die Characterwerte zeitlich zu verändern
	 */
	private long										timePassedSinceLastCharacterDataUpdateMS	= 0;
	private long										timeForOneCharacterDataUpdateMS				= 30000;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Standardkonstruktor Erstellt und startet einen Thread der eigenen Klasse,
	 * zur periodischen abarbeitung der eingegangenen Nachrichten.
	 */
	public Simulation()
	{
		// Userservice holen
		userService = (IUserservice) ServiceLocator.getInstance().getService(EServices.Userservice);

		// Simulationservice holen
		simService = (ISimulationService) ServiceLocator.getInstance().getService(EServices.Simulationservice);

		// Die persistierten Objekte einladen
		simService.loadWholeWorldFromDB(this);

		// Thread mit der Hauptroutine der Simulation starten
		simulationUpdate = new Thread(this);
		simulationUpdate.start();
	}

	public Simulation(boolean emptyKonstruktor)
	{
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode, welche einen neuen Befehl der Message-Queue hinzufügt
	 * 
	 * @param msg
	 *            Die neue Nachricht
	 */
	public synchronized void addMessage(MessageItem msg)
	{
		// Die Queue vor Zugriffen während dieser Aktion schützen
		synchronized (messages)
		{
			messages.add(msg);
		}
	}

	/**
	 * Methode zum pushen von neuen Systemnachrichten
	 * 
	 * @param sysMsg
	 *            Die neue Systemnachricht
	 */
	public synchronized void addSystemMessage(ISystemMessage sysMsg)
	{
		systemMessages.add(sysMsg);
	}

	/**
	 * Fügt neue Clientconnections zur Simulation hinzu
	 * 
	 * @param newCon
	 *            Die neue Clientconnection
	 */
	public synchronized void addClientConnection(IClientConnection newCon)
	{
		// Die Queue vor Zugriffen während dieser Aktion schützen
		synchronized (clients)
		{
			clients.add(newCon);
		}
	}

	/**
	 * Hauptschleife des Threads dieser Klasse<br />
	 * Es werden die Nachrichten aus der Nachrichtenqueue geholt und
	 * abgearbeitet.<br />
	 * Zusätzlich werden alle Objekte in der Simulation berechnet (z.B. KI der
	 * NSC)
	 */
	public void run()
	{
		// Zwischenspeicher für die Nachrichtenobjekte
		MessageItem currentMsg;
		ISystemMessage currentSysMsg;

		// Zum Messen der benötigten Zeit
		long startTime = 0, calcTime = 0;
		int i = 0;

		// Timer starten, welcher periodisch die Weltdaten abspeichert
		Timer saveWorldTimer = new Timer();
		saveWorldTimer.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				// Entsprechende Systemnachricht erstellen
				systemMessages.add(new PersistWorld());
			}
		}, MS_BETWEEN_SAVE_WORLD, MS_BETWEEN_SAVE_WORLD);

		// *******************************************************************
		// HAUPTSCHLEIFE DER SIMULATION
		while (!abort)
		{
			// Zeit berechnen, die nötig war um die Schleife einmal komplett
			// auszuführen
			calcTime = System.currentTimeMillis() - startTime;

			// Die Methode maximalzeitlich begrenzen, damit die CPU auch noch
			// mal atmen kann
			try
			{
				if (calcTime < MIN_MS_FOR_ONE_UPDATECYCLE)
				{
					// Kurz warten
					Thread.sleep(MIN_MS_FOR_ONE_UPDATECYCLE - calcTime);

					// Die benötigte Zeit neu berechnen
					calcTime = System.currentTimeMillis() - startTime;
				}
			}
			catch (InterruptedException e)
			{
				System.out.println("Error while setting the Simulation Thread to sleep for a short while..."
						+ e.toString());
			}

			// Startzeitpunkt der Messung
			startTime = System.currentTimeMillis();

			// Nächste Nachricht holen
			currentMsg = messages.poll();

			// Wenn es eine Nachricht zum Abarbeiten gibt, dies ausführen
			if (currentMsg != null)
				proccessMessage(currentMsg.getMessage(), currentMsg.getClient());

			// Nächste Systemnachricht holen
			currentSysMsg = systemMessages.poll();

			// Wenn es eine Systemnachricht zum Abarbeiten gibt, dies ausführen
			if (currentSysMsg != null)
				processSystemMessage(currentSysMsg);

			i += calcTime;

			if (i < 0)
				i = 0;

			// Aktualisiert die Simulationsdaten
			updateWorld(calcTime);

			// Nur jede Sekunde was an die Clients senden
			if (i > MS_FOR_ONE_BROADCASTWORLD_UPDATE)
			{
				broadcastWorldData();
				i = 0;
			}

		} // while(!abort)
		// *******************************************************************

		// Den Prozess des periodischen speicherns nicht mehr fortführen
		saveWorldTimer.cancel();
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	/**
	 * Aktualisiert alle Simulationsinhalte
	 * 
	 * @param calcTime
	 *            Die Zeit seit der letzten Aktualisierung
	 */
	private void updateWorld(long calcTime)
	{
		boolean updateCharacterNecessities = false;

		// Vergange Zeit aktualisieren
		timePassedSinceLastCharacterDataUpdateMS = timePassedSinceLastCharacterDataUpdateMS
				+ calcTime;

		// Zeitlich verzögerte Telegramme überprüfen und abschicken
		TelegramDispatcher.getInstance().DispatchDelayedTelegram();

		// Wenn die Werte wieder aktualisiert werden müssen (nach gewissen
		// zeitlichen Abständen verändern sich Werte wie Hunger, Hygiene
		// usw)
		if (timePassedSinceLastCharacterDataUpdateMS >= timeForOneCharacterDataUpdateMS)
		{
			updateCharacterNecessities = true;

			// Zeit entsprechend zurück setzen
			timePassedSinceLastCharacterDataUpdateMS = 0;
		}

		// Alle nicht Spielercharaktere durchlaufen
		Iterator<NonPlayerCharacter> it = npcs.iterator();
		NonPlayerCharacter tmpChar = null;
		while (it.hasNext())
		{
			// Routinen des NPC ausführen
			tmpChar = it.next();
			tmpChar.update(calcTime);

			// Für jeden Charakter ggf die Bedürfnisse erhöhen
			if (updateCharacterNecessities)
			{
				tmpChar.updateNecessities();
			}
		}

		// Die Wegberechnungen ausführen
		pathManager.updateSearches();
	}

	/**
	 * Sendet die Welddaten an alle Spieler
	 * 
	 * @throws NotFoundException
	 */
	private void broadcastWorldData()
	{
		synchronized (clients)
		{
			synchronized (npcs)
			{
				// Charactere in die Nachricht setzen
				NonPlayerCharacter[] chars = new NonPlayerCharacter[npcs.size()];
				chars = this.npcs.toArray(chars);

				// Alle verbundenen Clients berücksichtigen
				Iterator<IClientConnection> it = clients.iterator();
				while (it.hasNext())
				{
					IClientConnection clientCon = it.next();

					// Nur wenn die Verbindung komplett aufgebaut wurde
					try
					{
						if (clientCon.getConnectionState().equals(EConnectionState.Running)
								&& clientCon.getPlayerProfil() != null)
						{
							IUpdateCharacters updateCharactersMSG = (IUpdateCharacters) clientCon.getMessageFactory().createEmptyMessageByType(EMessageType.UpdateCharacters);

							// Nur 20 Charactere auf einmal schicken (wegen
							// Bufferbegrenzung des Outputstream)
							if (chars.length > 20)
							{
								int offset = 0;
								int charsLeft = 20;

								// Mehrere kleine Nachrichten senden
								while (offset < chars.length)
								{
									// Anzahl der noch zu übertragenden
									// Charactere ermitteln
									charsLeft = chars.length - offset;
									if (charsLeft > 20)
										charsLeft = 20;

									NonPlayerCharacter[] charsChunk = new NonPlayerCharacter[charsLeft];

									for (int i = 0; i < charsLeft; i++)
									{
										charsChunk[i] = chars[i + offset];
									}

									offset += 20;

									updateCharactersMSG.setCharacters(charsChunk);
									updateCharactersMSG.setOwnCharacters(clientCon.getPlayerProfil().getPlayerCharacters());

									clientCon.send(updateCharactersMSG);

									// Kurz warten bis der nächste Durchlauf
									// beginnen darf
									try
									{
										Thread.sleep(20);
									}
									catch (InterruptedException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

							}
							else
							{
								// Unter 20 Charactere auf einmal senden
								updateCharactersMSG.setCharacters(chars);
								updateCharactersMSG.setOwnCharacters(clientCon.getPlayerProfil().getPlayerCharacters());

								clientCon.send(updateCharactersMSG);
							}

						}
					}
					catch (NotFoundException e)
					{
						// Spieler hat noch kein Profil -> ignorieren
					}
				}
			}

		}
	}

	/**
	 * Methode zum abarbeiten einer Systemnachricht
	 * 
	 * @param sysMsg
	 *            die abzuarbeitende Systemnachricht
	 */
	private void processSystemMessage(ISystemMessage sysMsg)
	{
		try
		{
			switch (sysMsg.getType())
			{
			// ***********************************************************************************
			// PersistWorld
			case PersistWorld:
				// Die persistieren lassen vom Service
				LogWriter.getInstance().logToFile(LogLevel.Info,
						"Simulation persists alle Objects...");
				simService.saveCurrentWorldData(getSimulation());
				break;
			// ***********************************************************************************
			}
		}
		catch (Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"Error while processing a SystemMessage: " + e.toString());
		}
	}

	/**
	 * Methode zum verarbeiten einer Nachrichten
	 * 
	 * @param client
	 *            Von wem kommt diese Nachricht
	 * @param doc
	 *            Nachrichteninhalt in Form eines XML Dokuments, welches in der
	 *            Schnittstellenbeschreibung angegeben ist.
	 */
	private void proccessMessage(IMessage msg, IClientConnection client)
	{
		try
		{
			switch (msg.getMessageType())
			{
			// ***********************************************************************************
			// Interaction
			case Interaction:

				// Wenn gerade gar kein Character gesteuert wird, ist auch keine
				// Interaktion möglich
				if (client.getControlledCharacter() == null)
				{
					LogWriter.getInstance().logToFile(LogLevel.Warn,
							"Received an Interaction Message, but no character is under the control of this connection.");
					break;
				}

				// Blickrichtung aktualisieren
				client.getControlledCharacter().setHeading(((IInteraction) msg).getViewDirection());

				// Position aktualisieren
				client.getControlledCharacter().setPosition(((IInteraction) msg).getCurrentPosition());

				// Aktive Innteraktionen
				Iterator<EInteractionType> it = ((IInteraction) msg).getActiveInteractions().iterator();
				while (it.hasNext())
				{
					EInteractionType interaction = it.next();

					switch (interaction)
					{
					case Interact:
						// Alle Gebäude fragen, ob einer ihrer Eingänge in
						// Reichweite ist
						Iterator<ExigenceBuilding> buildingsIt = map.getBuildings().iterator();
						ExigenceBuilding tmpBuilding = null;
						while (buildingsIt.hasNext())
						{
							tmpBuilding = buildingsIt.next();

							// Wenn Eingang erreichbar, dann durch diesen
							// betreten
							if (tmpBuilding.tryInteractWithBuilding(client.getControlledCharacter()))
							{
								// Erfolgreich interagiert
								LogWriter.getInstance().logToFile(LogLevel.Debug,
										"player successfully interacted with building: "
												+ tmpBuilding.getModelName());

								client.getControlledCharacter().setVisible(false);

								// TODO: Spieler evt kurzzeitig sperrren, da er
								// ja im Gebäude ist

								// Weitere Gebäude ignorieren
								break;
							}
						}
						break;

					}
				}

				break;

			// ***********************************************************************************
			// LoginRequest
			case LoginRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"LoginRequest received. Checking Logindata...");

				// Profil über den Service holen
				PlayerProfile profil = userService.validateLogin(((ILoginRequest) msg).getLoginname(),
						((ILoginRequest) msg).getPassword());

				// Überprüfen ob dieses Profil nicht schon wo anders eingeladen
				// ist
				Iterator<IClientConnection> itConnections = this.clients.iterator();
				IClientConnection currentCon = null;
				while (itConnections.hasNext())
				{
					currentCon = itConnections.next();

					// Wenn es schon eine Verbindung gibt, die diesen Loginname
					// bei sich registriert hat, darf der Login nicht erfolgen
					try
					{
						if (profil != null
								&& currentCon.getPlayerProfil().getLoginName().equals(profil.getLoginName()))
						{
							LogWriter.getInstance().logToFile(LogLevel.Debug,
									"Login not possible, this Profil is already in use, sending LoginFailed.");
							ILoginFailed responseMsg = (ILoginFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.LoginFailed);
							responseMsg.setReason("Dieser Benutzer ist bereits angemeldet.");
							client.send(responseMsg);

							return;
						}
					}
					catch (NotFoundException e)
					{
						// getPlayerProfil() wirft eine NotFoundException wenn
						// noch kein Profil gesetzt wurde, also noch kein Login
						// dafür exisiert
					}

				}

				if (profil != null)
				{
					LogWriter.getInstance().logToFile(LogLevel.Debug,
							"Logindata correct, sending LoginSuccess.");

					client.setPlayerProfil(profil);

					ILoginSuccess responseMsg = (ILoginSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.LoginSuccess);
					responseMsg.setProfil(profil);
					client.send(responseMsg);
				}
				else
				{
					LogWriter.getInstance().logToFile(LogLevel.Debug,
							"Logindata incorrect, sending LoginFailed.");
					ILoginFailed responseMsg = (ILoginFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.LoginFailed);
					responseMsg.setReason("Falsche Benutzername bzw Passwort.");
					client.send(responseMsg);
				}
				break;

			// ***********************************************************************************
			// Chat
			case Chat:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"Chat received. Sending to all Clients");

				// Nachricht an alle Senden
				broadcast((IChat) msg);
				break;

			// ***********************************************************************************
			// Disconnect
			case Disconnect:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"Disconnect received.");

				// Versuchen ein Profil zu holen (also nur wenn der Spieler
				// schon eingeloggt ist
				try
				{
					// Ggf. das Profil abspeichern
					userService.savePlayerProfile(client.getPlayerProfil());
				}
				catch (NotFoundException e)
				{
					// Diese Verbindung hat sich noch nicht eingeloggt, dann
					// gibt es auch keine Daten zu speichern
				}

				// Den Character wieder "frei" lassen wenn er kontrolliert wurde
				if (client.getControlledCharacter() != null)
					client.getControlledCharacter().setPosessed(false);
				client.setControlledCharacter(null);

				// Verbindung beenden lassen
				this.closeConnection(client);
				break;

			// ***********************************************************************************
			// WorldDataRequest
			case WorldDataRequest:

				IWorldData wordlData = (IWorldData) client.getMessageFactory().createEmptyMessageByType(EMessageType.WorldData);

				// Gebäude in die Nachricht setzen
				VisibleEntity[] entities = new VisibleEntity[map.getBuildings().size()
						+ map.getNonInteractiveObjects().size()];

				int index = 0;
				// Alle Gebäude berücksichtigen
				Iterator<ExigenceBuilding> itBuildings = map.getBuildings().iterator();
				while (itBuildings.hasNext())
					entities[index++] = itBuildings.next();

				// Alle nicht interaktiven Elemente berücksichtigen
				Iterator<NonInteractiveObject> it2 = map.getNonInteractiveObjects().iterator();
				while (it2.hasNext())
					entities[index++] = it2.next();

				// Wenn es mehr als 20 Objekte sind, muss die Nachricht
				// aufgeteilt werden
				if (entities.length > 20)
				{
					int offset = 0;
					int entitiesLeft = 20;

					while (offset < entities.length)
					{
						// Auf 20 begrenzen
						entitiesLeft = entities.length - offset;
						if (entitiesLeft > 20)
							entitiesLeft = 20;

						VisibleEntity[] entitiesChunk = new VisibleEntity[entitiesLeft];

						for (int k = 0; k < entitiesLeft; k++)
						{
							entitiesChunk[k] = entities[k + offset];
						}

						// Nachricht kann mit einem mal gesendet werden
						wordlData.setGameObjects(entitiesChunk);

						// Nachricht mit allen Daten schicken
						client.send(wordlData);

						// Kurz warten bis der nächste Durchlauf beginnen darf
						Thread.sleep(50);

						offset += 20;
					}
				}
				else
				{
					// Nachricht kann mit einem mal gesendet werden
					wordlData.setGameObjects(entities);

					// Nachricht mit allen Daten schicken
					client.send(wordlData);
				}

				break;

			// ***********************************************************************************
			// RegisterRequest
			case RegisterRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"RegisterRequest received. Validating Data...");

				String[] reasons = userService.registerNewPlayerProfile(((IRegisterRequest) msg).getLoginname(),
						((IRegisterRequest) msg).getPassword(),
						((IRegisterRequest) msg).getEMail());
				// Wenn es keine Fehler gab
				if (reasons == null)
				{
					LogWriter.getInstance().logToFile(LogLevel.Debug,
							"Registration succeeded, sending RegisterSuccess.");
					// Nachricht von der Fabrik holen
					IRegisterSuccess regSuccess = (IRegisterSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.RegisterSuccess);
					// Versenden
					client.send(regSuccess);

				}
				// Wenn die Registrierung nicht funkioniert hat
				else
				{
					LogWriter.getInstance().logToFile(LogLevel.Debug,
							"Registration failed, sending RegisterFailed.");
					// Nachricht von der Fabrik holen
					IRegisterFailed regFailed = (IRegisterFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.RegisterFailed);
					// Mit Inhalt füllen
					regFailed.setReasons(reasons);
					// Versenden
					client.send(regFailed);
				}
				break;

			// ***********************************************************************************
			// CreateCharacterRequest
			case CreateCharacterRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"CreateCharacterRequest received.");

				try
				{
					ExigenceCharacter msgCharacter = ((ICreateCharacterRequest) msg).getCharacter();

					// Den Entitymanager den Character schon einmal anlegen
					// lassen
					NonPlayerCharacter newChar = new NonPlayerCharacter();

					// Den Character mit Informationen füllen
					newChar.setFirstName(msgCharacter.getFirstName());
					newChar.setLastName(msgCharacter.getLastName());
					newChar.setPriorEducation(msgCharacter.getPriorEducation());
					newChar.setPriorEnjoyment(msgCharacter.getPriorEnjoyment());
					newChar.setPriorFitness(msgCharacter.getPriorFitness());
					newChar.setPriorHunger(msgCharacter.getPriorHunger());
					newChar.setPriorHygiene(msgCharacter.getPriorHygiene());
					newChar.setPriorMoney(msgCharacter.getPriorMoney());
					newChar.setPriorSleep(msgCharacter.getPriorSleep());
					newChar.setPriorStrangury(msgCharacter.getPriorStrangury());
					newChar.setModelName(msgCharacter.getModelName());
					newChar.setMale(msgCharacter.isMale());

					// Den neuen Character der Liste hinzufügen Character
					npcs.add(newChar);

					// ID anhand des Namens generieren
					newChar.setID((newChar.getFirstName().concat(newChar.getLastName())).hashCode());

					// Steeringbehaviour erzeugen und setzen
					newChar.setSteering(new SteeringBehaviour(this.getMap(),
							newChar));

					// PathPlanner Instanz erzeugen und setzen
					newChar.setPathPlanner(new PathPlanner(newChar,
							this.getMap()));

					// Simulation zu der er gehört setzen
					newChar.setHomeSimulation(this);

					// GoalEvaluatoren initialisieren
					newChar.getBrain().initGoalEvaluators();

					// Den NPC mit dem EntityManager registrieren
					EntityManager.getInstance().registerNewEntity(newChar);

					client.getPlayerProfil().getPlayerCharacters().add(newChar);

					userService.savePlayerProfile(client.getPlayerProfil());

					// Success Nachricht senden
					ICreateCharacterSuccess createCharSuccess = (ICreateCharacterSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.CreateCharacterSuccess);
					createCharSuccess.setNewCharacterID(newChar.getID());

					client.send(createCharSuccess);

					// Worlddata erneut senden, damit alle Informationen beim
					// Client ankommen
					// IWorldData worldDataMsg =
					// generateWorldDataMessage(client);

					// Nachricht mit allen Daten schicken
					// client.send(worldDataMsg);

				}
				catch (InvalidKeyException e)
				{
					System.out.println(e.toString());
					// Wenn es schon einen Character mit diesem Namen gibt,
					// Failed Nachricht senden
					ICreateCharacterFailed createCharFailed = (ICreateCharacterFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.CreateCharacterFailed);
					createCharFailed.setReason("Ein Character mit diesem Namen existiert bereits");
					client.send(createCharFailed);
				}

				break;

			// ***********************************************************************************
			// ControllingCharacterEndRequest
			case ControllingCharacterEndRequest:

				// Wenn kein Character gesetzt wurde den es zu steuern gibt
				if (client.getControlledCharacter() == null)
				{
					// Nachricht von der Fabrik holen
					IControllingCharacterEndFailed controlEndFailed = (IControllingCharacterEndFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.ControllingCharacterEndFailed);

					// Grund setzen
					controlEndFailed.setReason("Es wir momentan kein Character von dieser Verbindung aus gesteuert.");

					// versenden
					client.send(controlEndFailed);
					break;
				}

				// Character wird nun von dem Spieler gesteuert gesteuert
				client.getControlledCharacter().setPosessed(false);

				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"ControllingCharacterEndRequest received. Now ending control of: "
								+ client.getControlledCharacter().getFirstName()
								+ " "
								+ client.getControlledCharacter().getLastName());

				// Es wird kein Character mehr gesteurt
				client.setControlledCharacter(null);

				// Nachricht von der Fabrik holen
				IControllingCharacterEndSuccess controlEndSuccess = (IControllingCharacterEndSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.ControllingCharacterEndSuccess);

				// versenden
				client.send(controlEndSuccess);

				break;

			// ***********************************************************************************
			// ControllingCharacterBeginRequest
			case ControllingCharacterBeginRequest:
				// Character anhand der ID vom EntityManager suchen lassen
				NonPlayerCharacter tmpChar = (NonPlayerCharacter) EntityManager.getInstance().getEntityForID(((IControllingCharacterBeginRequest) msg).getCharacterID());

				// Wenn kein Character mit der ID gefunden werden konnte
				if (tmpChar == null)
				{
					// Nachricht von der Fabrik holen
					IControllingCharacterBeginFailed controlFailed = (IControllingCharacterBeginFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.ControllingCharacterBeginFailed);

					// Grund setzen
					controlFailed.setReason("Es konnte kein Character mit dieser ID gefunden werden.");

					// versenden
					client.send(controlFailed);
					break;
				}
				else if (tmpChar.isPosessed()) // Wenn der Character schon von
				// jemanden gesteuert wird
				{
					// Nachricht von der Fabrik holen
					IControllingCharacterBeginFailed controlFailed = (IControllingCharacterBeginFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.ControllingCharacterBeginFailed);

					// Grund setzen
					controlFailed.setReason("Dieser Character wird bereits von jemanden gesteuert.");

					// versenden
					client.send(controlFailed);
					break;
				}

				// Character wird nun von dem Spieler gesteuert gesteuert
				tmpChar.setPosessed(true);

				// Die ID merken
				client.setControlledCharacter(tmpChar);

				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"ControllingCharacterBeginRequest received. Now controlling: "
								+ tmpChar.getFirstName() + " "
								+ tmpChar.getLastName());

				// Nachricht von der Fabrik holen
				IControllingCharacterBeginSuccess controlBeginSuccess = (IControllingCharacterBeginSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.ControllingCharacterBeginSuccess);

				// versenden
				client.send(controlBeginSuccess);

				break;

			// ***********************************************************************************
			// ChangeProfileRequest
			case ChangeProfileRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"ChangeProfilRequest received.");

				// Profil aktualisieren
				client.getPlayerProfil().setEMail(((IChangeProfilRequest) msg).getNewEMail());
				client.getPlayerProfil().setLoginName(((IChangeProfilRequest) msg).getNewLoginname());
				client.getPlayerProfil().setPassword(((IChangeProfilRequest) msg).getNewPassword());

				// Änderungen persistieren
				if (userService.savePlayerProfile(client.getPlayerProfil()))
				{
					// Nachricht von der Fabrik holen
					IChangeProfileSuccess changeSuccess = (IChangeProfileSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.ChangeProfileSuccess);

					// versenden
					client.send(changeSuccess);
				}
				else
				{
					// Nachricht von der Fabrik holen
					IChangeProfilFailed changeFailed = (IChangeProfilFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.ChangeProfileFailed);

					// Grund setzen
					changeFailed.setReason("Es trat ein Fehler beim speichern des Profils auf");

					// versenden
					client.send(changeFailed);
				}
				break;
			// ***********************************************************************************
			// EditCharacterRequest
			case EditCharacterRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"EditCharacterRequest received.");

				ExigenceCharacter newCharacterData = ((IEditCharacterRequest) msg).getNewCharacterData();
				NonPlayerCharacter characterToBeUpdated = (NonPlayerCharacter) EntityManager.getInstance().getEntityForID(newCharacterData.getID());

				// Wenn es keinen Character mit dieser ID gibt
				if (characterToBeUpdated == null)
				{
					// Nachricht von der Fabrik holen
					IEditCharacterFailed editFailed = (IEditCharacterFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.EditCharacterFailed);

					// Grund setzen
					editFailed.setReason("Ein Character mit dieser ID existiert nicht.");

					// versenden
					client.send(editFailed);
					break;
				}

				// Die Daten aus der Nachricht zum aktualisieren benutzen
				characterToBeUpdated.setFirstName(newCharacterData.getFirstName());
				characterToBeUpdated.setLastName(newCharacterData.getLastName());

				characterToBeUpdated.setPriorEducation(newCharacterData.getPriorEducation());
				characterToBeUpdated.setPriorStrangury(newCharacterData.getPriorStrangury());
				characterToBeUpdated.setPriorHunger(newCharacterData.getPriorHunger());
				characterToBeUpdated.setPriorEnjoyment(newCharacterData.getPriorEnjoyment());
				characterToBeUpdated.setPriorHygiene(newCharacterData.getPriorHygiene());
				characterToBeUpdated.setPriorSleep(newCharacterData.getPriorSleep());
				characterToBeUpdated.setPriorFitness(newCharacterData.getPriorFitness());
				characterToBeUpdated.setPriorMoney(newCharacterData.getPriorMoney());

				characterToBeUpdated.setModelName(newCharacterData.getModelName());

				userService.savePlayerProfile(client.getPlayerProfil());

				// Nachricht von der Fabrik holen
				IEditCharacterSuccess changeSuccess = (IEditCharacterSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.EditCharacterSuccess);

				// versenden
				client.send(changeSuccess);

				break;
			// ***********************************************************************************
			// DeleteProfileRequest
			case DeleteProfileRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"DeleteProfileRequest received.");

				// Änderungen persistieren
				if (userService.deletePlayerProfile(client.getPlayerProfil()))
				{
					// Nachricht von der Fabrik holen
					IDeleteProfileSuccess delSuccess = (IDeleteProfileSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.DeleteProfileSuccess);

					// versenden
					client.send(delSuccess);
				}
				else
				{
					// Nachricht von der Fabrik holen
					IDeleteProfileFailed delFailed = (IDeleteProfileFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.DeleteProfileFailed);

					// Grund setzen
					delFailed.setReason("Es trat ein Fehler beim loeschen des Profils auf");

					// versenden
					client.send(delFailed);
				}

				break;
			// ***********************************************************************************
			// DeleteCharacterRequest
			case DeleteCharacterRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"DeleteCharacterRequest received.");

				// Über die Charactere des Profil iterieren und den richtigen
				// raussuchen zum löschen
				Iterator<NonPlayerCharacter> playerIt = client.getPlayerProfil().getPlayerCharacters().iterator();
				NonPlayerCharacter tmpPlayer = null;
				while (playerIt.hasNext())
				{
					tmpPlayer = playerIt.next();

					// Wenn er gefunden wurde
					if (tmpPlayer.getID() == ((IDeleteCharacterRequest) msg).getCharacterID())
					{
						boolean deletedSuccessfully = false;
						// Diesen Character aus der Liste im Profil entfernen
						deletedSuccessfully = client.getPlayerProfil().getPlayerCharacters().remove(tmpPlayer);

						if (!deletedSuccessfully)
							break;

						// Und aus der Liste der aktiven Charaktere
						deletedSuccessfully = npcs.remove(tmpPlayer);

						if (!deletedSuccessfully)
							break;

						tmpPlayer.setVisible(false);

						// Veränderung an die Persistenzschicht geben
						userService.savePlayerProfile(client.getPlayerProfil());
						userService.deleteCharacter(tmpPlayer);

						// Erfolg melden

						// Nachricht von der Fabrik holen
						IDeleteCharacterSuccess delSuccess = (IDeleteCharacterSuccess) client.getMessageFactory().createEmptyMessageByType(EMessageType.DeleteCharacterSuccess);

						// versenden
						client.send(delSuccess);

						// Die "neuen" Informationen zu dem char an alle senden
						// (unsichtbar)
						IUpdateCharacters updateChar = (IUpdateCharacters) client.getMessageFactory().createEmptyMessageByType(EMessageType.UpdateCharacters);
						NonPlayerCharacter[] chars = { tmpPlayer };
						updateChar.setCharacters(chars);
						updateChar.setOwnCharacters(client.getPlayerProfil().getPlayerCharacters());
						// versenden
						broadcast(updateChar);

						// Worlddata erneut senden, damit alle Informationen
						// beim
						// Client ankommen
						// IWorldData worldDataMsg =
						// generateWorldDataMessage(client);

						// Nachricht mit allen Daten schicken
						// client.send(worldDataMsg);

						return;
					}
				}

				// Wenn kein Character gelöscht wurde, einen Fehler senden
				// Nachricht von der Fabrik holen
				IDeleteCharacterFailed delFailed = (IDeleteCharacterFailed) client.getMessageFactory().createEmptyMessageByType(EMessageType.DeleteCharacterFailed);

				// Grund setzen
				delFailed.setReason("Es trat ein Fehler beim löschen des Characters auf.");

				// versenden
				client.send(delFailed);

				break;

			// ***********************************************************************************
			// StatisticRequest
			case StatisticRequest:
				LogWriter.getInstance().logToFile(LogLevel.Debug,
						"StatisticRequest received.");

				// Grundgerüst holen
				IStatistics stats = (IStatistics) client.getMessageFactory().createEmptyMessageByType(EMessageType.Statistics);

				// Statistiken holen
				HashMap<String, Integer> clientsAt = getClientsAt();
				HashMap<String, Integer> workersAt = getWorkersAt();

				// Statistik Werte setzen
				stats.setClientsAt(clientsAt.get("restaurant"),
						clientsAt.get("hotel"),
						clientsAt.get("bath"),
						clientsAt.get("cinema"),
						clientsAt.get("gym"),
						clientsAt.get("library"),
						clientsAt.get("police"),
						clientsAt.get("bank"),
						clientsAt.get("hospital"),
						clientsAt.get("church"),
						clientsAt.get("dixi"));

				stats.setWorkersAt(workersAt.get("restaurant"),
						workersAt.get("hotel"),
						workersAt.get("bath"),
						workersAt.get("cinema"),
						workersAt.get("gym"),
						workersAt.get("library"),
						workersAt.get("police"),
						workersAt.get("bank"),
						workersAt.get("hospital"),
						workersAt.get("church"));

				// versenden
				client.send(stats);
				break;

			} // End switch statement
		}
		catch (Exception e)
		{
			logWriter.logToFile(LogLevel.Error,
					"Error while processing a Message: " + e.toString());
		}
	}

	/**
	 * Methode zum Senden von Nachrichten an alle Clients
	 * 
	 * @param message
	 *            Nachricht in form einer Zeichenkette
	 */
	private synchronized void broadcast(IOutgoingMessage msg)
	{
		Iterator<IClientConnection> it = clients.iterator();

		while (it.hasNext())
		{
			it.next().send(msg);
		}
	}

	/**
	 * Methoden zum beenden der Verbindung zum Server
	 * 
	 * @param client
	 *            Die zu schließende Verbindung zu einem Client
	 */
	private synchronized void closeConnection(IClientConnection client)
	{
		// Nur versuchen die Verbindung schließen wenn diese überhaupt noch
		// existiert
		if (!client.isClosed())
			client.closeConnection();

		// Client aus der Liste nehmen und IDs der anderen aktualisieren
		clients.remove(client);

		// Nur dekrementieren, wenn der Spieler auch wirklich angemeldet war
		try
		{
			// versuchen ein Profil zu hoeln
			if (client.getPlayerProfil() != null)
				numPlayers--;
		}
		catch (NotFoundException e)
		{
			// Es wurde noch kein Profil gesetzt
		}

		logWriter.logToFile(LogLevel.Debug, "Client wurde entfernt.");
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

	/**
	 * Liefert die aktuelle Anzahl von Kunden in den jeweiligen Gebäuden
	 * 
	 * @return Eine HashMap der Gebäude (Key) mit der aktuellen Anzahl an
	 *         Besuchern (Value)
	 * 
	 */
	public HashMap<String, Integer> getClientsAt()
	{
		HashMap<String, Integer> retVal = new HashMap<String, Integer>();

		// Werte initialisieren
		retVal.put("restaurant", 0);
		retVal.put("hotel", 0);
		retVal.put("bath", 0);
		retVal.put("cinema", 0);
		retVal.put("gym", 0);
		retVal.put("library", 0);
		retVal.put("police", 0);
		retVal.put("bank", 0);
		retVal.put("hospital", 0);
		retVal.put("church", 0);
		retVal.put("dixi", 0);

		// Alle Gebäude durchlaufen
		Iterator<ExigenceBuilding> it = getMap().getBuildings().iterator();
		while (it.hasNext())
		{
			ExigenceBuilding tmpBuilding = it.next();

			// Wieviele Plätze sind gerade belegt?
			int currentlyFilledSlots = tmpBuilding.getMaximumSlots()
					- tmpBuilding.getCurrentlyFreeSlots();

			// Nach der Art des Gebäudes unterscheiden und mit der entsprechende
			// Menge addieren
			if (tmpBuilding.getClass().equals(Restaurant.class))
			{
				retVal.put("restaurant", retVal.get("restaurant")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Hotel.class))
			{
				retVal.put("hotel", retVal.get("hotel") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Bathhouse.class))
			{
				retVal.put("bath", retVal.get("bath") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Cinema.class))
			{
				retVal.put("cinema", retVal.get("cinema")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Gym.class))
			{
				retVal.put("gym", retVal.get("gym") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Library.class))
			{
				retVal.put("library", retVal.get("library")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Police.class))
			{
				retVal.put("police", retVal.get("police")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Bank.class))
			{
				retVal.put("bank", retVal.get("bank") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Hospital.class))
			{
				retVal.put("hospital", retVal.get("hospital")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Cinema.class))
			{
				retVal.put("church", retVal.get("church")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Toilet.class))
			{
				retVal.put("dixi", retVal.get("dixi") + currentlyFilledSlots);
			}
		}

		return retVal;
	}

	/**
	 * Liefert die aktuelle Anzahl von Beschäftigten in den jeweiligen Gebäuden
	 * 
	 * @return Eine HashMap der Gebäude (Key) mit der aktuellen Anzahl an
	 *         Arbeitern (Value)
	 */
	public HashMap<String, Integer> getWorkersAt()
	{
		HashMap<String, Integer> retVal = new HashMap<String, Integer>();

		// Werte initialisieren
		retVal.put("restaurant", 0);
		retVal.put("hotel", 0);
		retVal.put("bath", 0);
		retVal.put("cinema", 0);
		retVal.put("gym", 0);
		retVal.put("library", 0);
		retVal.put("police", 0);
		retVal.put("bank", 0);
		retVal.put("hospital", 0);
		retVal.put("church", 0);

		// Alle Gebäude durchlaufen
		Iterator<ExigenceBuilding> it = getMap().getBuildings().iterator();
		while (it.hasNext())
		{
			ExigenceBuilding tmpBuilding = it.next();

			// Wieviele Plätze sind gerade belegt?
			int currentlyFilledSlots = tmpBuilding.getMaximumEmployeeSlots()
					- tmpBuilding.getCurrentlyFreeEmployeeSlots();

			// Nach der Art des Gebäudes unterscheiden und mit der entsprechende
			// Menge addieren
			if (tmpBuilding.getClass().equals(Restaurant.class))
			{
				retVal.put("restaurant", retVal.get("restaurant")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Hotel.class))
			{
				retVal.put("hotel", retVal.get("hotel") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Bathhouse.class))
			{
				retVal.put("bath", retVal.get("bath") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Cinema.class))
			{
				retVal.put("cinema", retVal.get("cinema")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Gym.class))
			{
				retVal.put("gym", retVal.get("gym") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Library.class))
			{
				retVal.put("library", retVal.get("library")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Police.class))
			{
				retVal.put("police", retVal.get("police")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Bank.class))
			{
				retVal.put("bank", retVal.get("bank") + currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Hospital.class))
			{
				retVal.put("hospital", retVal.get("hospital")
						+ currentlyFilledSlots);
			}
			else if (tmpBuilding.getClass().equals(Cinema.class))
			{
				retVal.put("church", retVal.get("church")
						+ currentlyFilledSlots);
			}
		}

		return retVal;
	}

	public int getNumPlayers()
	{
		return numPlayers;
	}

	public PathManager getPathManager()
	{
		return pathManager;
	}

	public WorldMap getMap()
	{
		return map;
	}

	public void setAbort(boolean abort)
	{
		this.abort = abort;
	}

	public void setNpcs(ConcurrentLinkedQueue<NonPlayerCharacter> npcs)
	{
		this.npcs = npcs;
	}

	public ConcurrentLinkedQueue<NonPlayerCharacter> getNpcs()
	{
		return npcs;
	}

	public Simulation getSimulation()
	{
		return this;
	}

	public void setMap(WorldMap map)
	{
		this.map = map;
	}

}
