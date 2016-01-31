package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;

import eXigence.network.messages.EMessageType;

/**
 * Abstrakte Klasse für Nachrichten die eine XMLAusgabe benötigen (in der
 * Versoin 1)
 * 
 * @author Lars George
 * 
 */
public abstract class XMLMessage_V1
{
	/**
	 * Liefert ein Grundgerüst einer XMLMessage in der Version 1
	 * 
	 * @param type
	 *            Die Art der Nachricht dessen Grundgerüst erstellt werden soll
	 * @return Ein org.jdom.Document welches das Grundgerüst einer XMLNachricht
	 *         in der Version 1 repräsentiert mit gesetztem MessageType
	 *         (Parameter)
	 */
	public Document getXMLStructure(EMessageType type)
	{
		// Dokumenten Kopf erstellen
		Document doc = new Document();

		// Root Element erzeugen
		Element root = new Element("Message");

		// MessageType entsprechend dem Parameter erstellen
		Element msgType = new Element("MessageType");
		msgType.addContent(type.toString());
		root.addContent(msgType);

		// MessageContent Element erstellen
		Element msgContent = new Element("MessageContent");
		root.addContent(msgContent);

		doc.setRootElement(root);
		return doc;
	}
}
