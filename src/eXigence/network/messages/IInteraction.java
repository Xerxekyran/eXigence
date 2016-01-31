package eXigence.network.messages;

import java.util.Vector;

import eXigence.util.math.Vector2D;

public interface IInteraction extends IIncomingMessage
{
	/**
	 * Liefert eine Liste mit den Dingen, die der Spieler gerade bei sich am
	 * Client tätigt
	 * 
	 * @return Eine Liste von aktiven Interaktionen
	 */
	public Vector<EInteractionType> getActiveInteractions();

	/**
	 * Liefert den Wert für die aktuelle Blickrichtung
	 * 
	 * @return Blickrichtung als Vector
	 */
	public Vector2D getViewDirection();

	/**
	 * Liefert die aktuelle Position des Characters
	 * 
	 * @return Koordinaten des Characters der gearde gestuert wird
	 */
	public Vector2D getCurrentPosition();
}
