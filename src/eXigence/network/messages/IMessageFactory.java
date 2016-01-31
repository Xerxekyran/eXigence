package eXigence.network.messages;

/**
 * Interface für MessageFactory Klassen die Message Objekte bestimmter Versionen
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
	 *            Ein String der XML Informationen gemäß der
	 *            Schnittstellenbeschreibung enthält
	 * @return Erstellt eine IncomingMesssage anhand des übergebenen Strings
	 */
	public IIncomingMessage createMessageFromString(String text)
			throws IllegalArgumentException;

	/**
	 * Erzeugt ein leeres Message Objekt vom angegebenen Type
	 * 
	 * @param type
	 *            Art der zu erstellenden Nachricht
	 * @return Erstellt eine leere Nachricht vom übergebenen Typ
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
