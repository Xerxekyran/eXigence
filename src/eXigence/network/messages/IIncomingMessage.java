package eXigence.network.messages;

import org.jdom.Document;

public interface IIncomingMessage extends IMessage
{
	/**
	 * Methode zum f�llen der Message mit Inhalt, anhand von XML-Daten
	 * @param xmlData
	 */
	public void setContent(Document xmlData) throws IllegalArgumentException;
}
