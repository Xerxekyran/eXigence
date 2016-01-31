package eXigence.network;

import javassist.NotFoundException;
import eXigence.core.Simulation;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.network.messages.IMessageFactory;
import eXigence.network.messages.IOutgoingMessage;

/**
 * Repr�sentiert eine Verbindung zu einem Client. Sie enth�lt eine Referenz zu
 * dem dazugeh�rigen Spielerprofil (wenn denn schon gesetzt)
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
	 * Setter f�r das dazugeh�rige Simulationsobjekt
	 * 
	 * @param simulation
	 */
	public abstract void setSimulation(Simulation simulation);

	/**
	 * Sette f�r das dazugeh�rige Spielerprofil
	 * 
	 * @param profil
	 */
	public abstract void setPlayerProfil(PlayerProfile profil);

	/**
	 * 
	 * @return Liefert das zu der Verbindung geh�rende Spielerprofil
	 */
	public abstract PlayerProfile getPlayerProfil() throws NotFoundException;

	/**
	 * Setter f�r die MessageFactory (versionsabh�ngig)
	 * 
	 * @param msgFactory
	 *            Die Referenz auf eine MessageFactory, welche zum erstellen von
	 *            Nachrichten benutzt werden soll
	 */
	public abstract void setMessageFactory(IMessageFactory msgFactory);

	/**
	 * Getter f�r die MessageFactory (versionsabh�ngig)
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
	 * Setter f�r den Status der Verbindung
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
	 * Getter f�r die ID der Verbindung (um die Threads eindeutig auseinander
	 * halten zu k�nnen)
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
