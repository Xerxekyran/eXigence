package eXigence.network.messages;


/**
 * 
 * @author Lars George
 *
 */
public interface IMessage 
{	
	/**
	 * 
	 * @return Liefert den Typ der Nachricht
	 */
	public EMessageType getMessageType();
	
	/**
	 * 
	 * @return Liefert die Version der Nachricht
	 */
	public EMessageVersion getVersion();				
}
