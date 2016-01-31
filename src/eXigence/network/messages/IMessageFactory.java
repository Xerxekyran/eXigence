package eXigence.network.messages;

/**
 * Interface f�r MessageFactory Klassen die Message Objekte bestimmter Versionen
 * erzeugen
 * 
 * @author Lars George
 * 
 */
public interface IMessageFactory
{
	/**
	 * Erstellt aus dem im Parameter angegeben String eine Nachricht
	 * 
	 * @param text
	 *            Ein String der XML Informationen gem�� der
	 *            Schnittstellenbeschreibung enth�lt
	 * @return Erstellt eine IncomingMesssage anhand des �bergebenen Strings
	 */
	public IIncomingMessage createMessageFromString(String text)
			throws IllegalArgumentException;

	/**
	 * Erzeugt ein leeres Message Objekt vom angegebenen Type
	 * 
	 * @param type
	 *            Art der zu erstellenden Nachricht
	 * @return Erstellt eine leere Nachricht vom �bergebenen Typ
	 */
	public IOutgoingMessage createEmptyMessageByType(EMessageType type);

	/**
	 * Methode zum ermitteln der Versoinen die diese MessageFactory erzeugt
	 * 
	 * @return Liefert die Version der Nachrichten, die von dieser
	 *         MessageFactory erzeugt werden
	 */
	public EMessageVersion getVersion();
}
