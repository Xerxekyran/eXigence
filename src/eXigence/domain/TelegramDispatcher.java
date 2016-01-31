package eXigence.domain;

import java.util.PriorityQueue;

import eXigence.domain.entities.BaseEntity;
import eXigence.domain.entities.EntityManager;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Der TelegramDispatcher verteilt die Telegramme an die jeweiligen Entities zu
 * den Zeiten an denen sie geschickt werden sollen. Als Singleton realisiert
 * 
 * @author Lars George
 * 
 */
public class TelegramDispatcher
{
	// Singleton Instanz
	private static TelegramDispatcher	instance				= null;

	// Um den Code lesbarer zu machen, ein paar statische Definitionen
	public static final double			SEND_TLG_IMMEDIATELY	= 0.0;
	public static final int				SENDER_ID_IRRELEVANT	= -1;
	
	private PriorityQueue<Telegram> delayedTelegrams;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	private TelegramDispatcher()
	{
		delayedTelegrams = new PriorityQueue<Telegram>();
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Metthode zum erhalten der Singleton Instanz
	 */
	public static TelegramDispatcher getInstance()
	{
		if (instance == null)
			instance = new TelegramDispatcher();

		return instance;
	}

	/**
	 * Sendet ein neues Telegram an den entsprechenden Empfänger. Wenn der Delay
	 * nicht auf SEND_TLG_IMMEDIATELY gesetzt ist, wird das Telegram in eine
	 * Priority Queue getan und zu einem späteren Zeitpunkt verarbeitet
	 * 
	 * @param delay
	 *            Zeitliche Verzögerung beim Senden
	 * @param senderID
	 *            ID des Senders
	 * @param receiverID
	 *            ID des Empfängers
	 * @param type
	 *            Die Art des Telegrams
	 * @param ExtraInfo
	 *            Eventuelle Zusatzinformationen
	 */
	public void DispatchTelegram(	double delay,
									int senderID,
									int receiverID,
									ETelegramType type,
									Object ExtraInfo)
	{		
		BaseEntity receiver = EntityManager.getInstance().getEntityForID(receiverID);
		
		if(receiver == null)
		{
			LogWriter.getInstance().logToFile(LogLevel.Warn, "TelegramDispatcher: Telegram with receiverID: "+ receiverID + " could not be resolved an entitiy");
			return;
		}
		// Entsprechend neues Telegram erzeugen
		Telegram tlg = new Telegram(0.0, senderID, receiverID, type,
				ExtraInfo);

		// Wenn keine Zeitverzögerung gewünscht ist
		if(delay <= 0.0)
		{
			dischargeTelegram(receiver, tlg);
		}
		else
		{
			double currentTime = System.currentTimeMillis();
			
			tlg.setDelayedDispatchTime(currentTime + delay);
						
			delayedTelegrams.add(tlg);
		}

	}

	/**
	 * Geht alle verzögerten Telegramme durch und sendet diese entsprechend,
	 * wenn ihr Zeitstempel erreicht ist
	 */
	public void DispatchDelayedTelegram()
	{
		double currentTime = System.currentTimeMillis();
		
		// Telegramme verschicken die zeitlich and er Reihe sind
		while(	!delayedTelegrams.isEmpty() &&
				delayedTelegrams.peek().getDelayedDispatchTime() < currentTime &&
				delayedTelegrams.peek().getDelayedDispatchTime() > 0)
		{
			// Nächstes Telegram von der Queue holen
			Telegram tmpTelegram = delayedTelegrams.remove();
			
			// Den Empfänger suchen
			BaseEntity receiver = EntityManager.getInstance().getEntityForID(tmpTelegram.getReceiverID());
			
			// Telegram senden
			dischargeTelegram(receiver, tmpTelegram);
		}
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode zum absenden des Telegrams
	 */
	private void dischargeTelegram(BaseEntity receiver,  Telegram tlg)
	{
		if(!receiver.handleTelegram(tlg))
		{
			LogWriter.getInstance().logToFile(LogLevel.Warn, "TelegramDispatcher: Telegram could not be handled by receiver");
		}
	}
	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
}
