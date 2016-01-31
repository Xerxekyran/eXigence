package eXigence.domain;

/**
 * Repräsentiert eine Nachricht die sich GameEntities gegenseitig schicken können
 * Es ist keine Netzwerknachricht. 
 * 
 * @author Lars George
 * @version 0.1
 *
 */
public class Telegram implements Comparable<Telegram>
{
	
	// ID des Senders der Telegrams
	private int senderID;		
	
	// ID des Empfängers des Telegrams	
	private int receiverID;
	
	// Art des Telegrams
	private ETelegramType type;
	
	// Zeitpunkt (Stempel) zu dem diese Nachricht gesendet werden soll
	// Nur benötigt, wenn das Telegram verzögert gesendet werden soll
	private double delayedDispatchTime;
	
	// Platzhalter für eventuelle Extrainformationen von Telegrammen
	private Object extraInfo;
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public Telegram(double timeToDispatch, 
	                int senderID,
	                int receiverID,
	                ETelegramType type,
	                Object extraInfo)
	{
		this.delayedDispatchTime = timeToDispatch;
		this.extraInfo = extraInfo;
		this.receiverID = receiverID;
		this.senderID = senderID;
		this.type = type;
	}

	
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public int compareTo(Telegram o)
	{		 
		return (int)(this.delayedDispatchTime - o.delayedDispatchTime);
	}	
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public ETelegramType getType()
	{
		return type;
	}



	public int getSenderID()
	{
		return senderID;
	}



	public void setSenderID(int senderID)
	{
		this.senderID = senderID;
	}



	public int getReceiverID()
	{
		return receiverID;
	}



	public void setReceiverID(int receiverID)
	{
		this.receiverID = receiverID;
	}



	public double getDelayedDispatchTime()
	{
		return delayedDispatchTime;
	}



	public void setDelayedDispatchTime(double delayedDispatchTime)
	{
		this.delayedDispatchTime = delayedDispatchTime;
	}



	public Object getExtraInfo()
	{
		return extraInfo;
	}



	public void setExtraInfo(Object extraInfo)
	{
		this.extraInfo = extraInfo;
	}



	public void setType(ETelegramType type)
	{
		this.type = type;
	}	
	
}
