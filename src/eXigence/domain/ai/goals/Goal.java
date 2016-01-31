package eXigence.domain.ai.goals;

import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.Telegram;

/**
 * Basisklasse für Ziele jeglicher Art. Wird für die KI der NPCs verwendet
 * 
 * @author Lars George
 * @version 1.0
 * 
 */
public abstract class Goal
{
	protected NonPlayerCharacter	owner;
	protected EGoalTypes			goalType;
	protected EGoalStatus			status;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public Goal(NonPlayerCharacter owner, EGoalTypes type)
	{
		this.owner = owner;
		this.goalType = type;
		this.status = EGoalStatus.inactive;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode zum bearbeiten von Nachrichten an das Ziel
	 * 
	 * @param tlg
	 *            Die zu bearbeitende Nachricht
	 * @return true wenn die Nachricht verarbeitet werden konnte, ansonsten
	 *         false
	 */
	public boolean handleTelegram(Telegram tlg)
	{
		return false;
	}

	/**
	 * Aktiviert diese Ziel
	 */
	public abstract void activate();

	/**
	 * Beendet das Ziel und somit die Aktionen die damit verbunden sind
	 */
	public abstract void terminate();

	/**
	 * Logik welche in jedem Updatevorgang einmal aufgerufen wird
	 * 
	 * @return Den Status des Ziels
	 */
	public abstract EGoalStatus process();

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Ruft die activate() Methode für den Fall auf, dass der Status auf inactive steht 
	 */
	protected void activateIfInactive()
	{
		if (status.equals(EGoalStatus.inactive))
		{
			activate();
		}
	}

	/**
	 * Wenn das Ziel fehlgeschlagen ist, den Status auf inactive stellen, damit
	 * es beim nächsten Update neu geplant werden kann
	 */
	protected void reactivateIfFailed()
	{
		if (status.equals(EGoalStatus.failed))
		{
			status = EGoalStatus.inactive;
		}
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public EGoalStatus getStaus()
	{
		return status;
	}

	public EGoalTypes getGoalType()
	{
		return goalType;
	}
	
	

}
