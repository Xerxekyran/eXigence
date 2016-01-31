package eXigence.domain.entities;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import eXigence.domain.Telegram;

/**
 * Abstrakte Klasse für alle Arten von Objekten die in der Welt von eXigence
 * vorkommen (Spieler, Gebäude, Trigger etc)
 * 
 * @author Lars George
 * 
 */
@MappedSuperclass
public abstract class BaseEntity
{
	// Einzigartige ID
	@Id
	private int			id;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	protected BaseEntity()
	{
		
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	/**
	 * Methode zum behandeln von Telegrammen
	 */
	public abstract boolean handleTelegram(Telegram tlg);

	/**
	 * Methode zum updaten des Entities, wird einmal pro updatezyklus der Simulation
	 * aufgerufen
	 */
	public abstract void update(long calcTime);

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public void setID(int id)
	{
		this.id = id;
	}

	public int getID()
	{
		return this.id;
	}

}
