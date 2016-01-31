package eXigence.network;

import javassist.NotFoundException;
import eXigence.core.Simulation;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.network.messages.IMessageFactory;
import eXigence.network.messages.IOutgoingMessage;

/**
 * Repräsentiert eine Verbindung zu einem Client. Sie enthält eine Referenz zu
 * dem dazugehörigen Spielerprofil (wenn denn schon gesetzt)
 * 
 * @author Lars George
 * 
 */
public abstract class IClientConnection extends Thread
{

	/**
	 * Methode um Nachrichten an den Client zu senden
	 * 
	 * @param msg
	 *            Nachricht die versendet werden soll
	 */
	public abstract void send(IOutgoingMessage msg);

	/**
	 * Beendet die Socketverbindung zum Server
	 */
	public abstract void closeConnection();

	/**
	 * Setter für das dazugehörige Simulationsobjekt
	 * 
	 * @param simulation
	 */
	public abstract void setSimulation(Simulation simulation);

	/**
	 * Sette für das dazugehörige Spielerprofil
	 * 
	 * @param profil
	 */
	public abstract void setPlayerProfil(PlayerProfile profil);

	/**
	 * 
	 * @return Liefert das zu der Verbindung gehörende Spielerprofil
	 */
	public abstract PlayerProfile getPlayerProfil() throws NotFoundException;

	/**
	 * Setter für die MessageFactory (versionsabhängig)
	 * 
	 * @param msgFactory
	 *            Die Referenz auf eine MessageFactory, welche zum erstellen von
	 *            Nachrichten benutzt werden soll
	 */
	public abstract void setMessageFactory(IMessageFactory msgFactory);

	/**
	 * Getter für die MessageFactory (versionsabhängig)
	 * 
	 * @return Die benutzte MessageFactory der Verbindung
	 */
	public abstract IMessageFactory getMessageFactory();

	/**
	 * Liefert den aktuellen Status der Verbindung
	 * 
	 * @return Den aktuellen Status der Verbindung
	 */
	public abstract EConnectionState getConnectionState();

	/**
	 * Setter für den Status der Verbindung
	 * 
	 * @param state
	 */
	public abstract void setConnectionState(EConnectionState state);

	/**
	 * Ermittelt ob die Verbindung beendet wurde oder nicht
	 * 
	 * @return true wenn die Verbindung noch besteht, ansonsten false
	 */
	public abstract boolean isClosed();

	/**
	 * Getter für die ID der Verbindung (um die Threads eindeutig auseinander
	 * halten zu können)
	 * 
	 * @return Die ID des Threads
	 */
	public abstract int getConectionID();

	/**
	 * Setzt die ID des Characters welcher durch den Spieler dieser Verbindung
	 * gesteuert wird
	 * 
	 * @param character
	 */
	public abstract void setControlledCharacter(NonPlayerCharacter character);

	/**
	 * Liefert die Referenz des Characters, welcher durch diese Verbindung gesteuert
	 * wird. Negative Werte bedeuten, dass kein Character gesteuert wird
	 * 
	 * @return Referenz des Characters, null wenn keiner gesteuert wird
	 */
	public abstract NonPlayerCharacter getControlledCharacter();
}
