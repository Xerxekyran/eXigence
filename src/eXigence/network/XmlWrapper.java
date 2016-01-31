package eXigence.network;

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import eXigence.domain.PlayerProfile;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Klasse zur verwaltung von XML Informationen.<br />
 * Es werden Methoden zur generierung der in der Schnittstellenbeschreibung definierten
 * XML Strukturen bereit gestellt. <br />
 * Desweiteren können hier Strings in org.jdom.Document und anders herum umgewandelt werden.   
 * 
 * @author Lars George
 * @version 0.1
 *
 */
public class XmlWrapper 
{
	
	public static enum OLD____MessageType 
	{
		LoginRequest,
		LoginSuccess,
		LoginFailed,
		RegisterRequest,
		RegisterSuccess,
		RegisterFailed,
		Chat,
		Disconnect		
	}
	
	//private static final String elementName	= "Message";
	//private static final String publicID	= "-//Noobisoft//DTD Message V 1.0//DE";
	//private static final String systemID	= "http://serverchris.dyndns.org:8448/eXigence/dtds/Message.dtd";
		
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Konstruktor
	 */
	private XmlWrapper() {}
	
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode zum Umwandeln von Strings zu XML Objekten 
	 * @param msg
	 */
	public static Document toXMLDoc(String msg) 
	{	
		Document doc = null;
		
		try {			
			// Builder Objekt erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(new StringReader(msg));
			
			LogWriter.getInstance().logToFile(LogLevel.Debug, "XmlWrapper: "+ toPrettyString(doc));						
		}
		catch(Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error, "XmlWrapper: Fehler beim parsen eines Strings:" + e.toString());
		}
		return doc;
	}	
	
	/**
	 * 
	 * @param message Textnachricht die versendet werden soll
	 * @return XML Dokument für eine Chatnachricht
	 */
	public static Document getChatXMLDocument(String message)
	{
		// Standard-Kopf holen
		Document doc = getXmlDocHeader(OLD____MessageType.Chat);
		
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Text").addContent(message));
				
		return doc;
	}
	
	/**
	 * 
	 * @param reason Grund für das Scheitern der Anmeldung
	 * @return XML Dokument für eine Fehlerhafte Anmeldung
	 */
	public static Document getLoginFailedXMLDocument(String reason) 
	{
		// Standard-Kopf holen
		Document doc = getXmlDocHeader(OLD____MessageType.LoginFailed);
		
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Reason").addContent(reason));
					
		return doc;
	}
	
	/**
	 * 
	 * @param pProfil Das zu den Anmeldedaten passende Profil
	 * @return XML Dokument für eine erfolgreiche Anmeldung am Server
	 */
	public static Document getLoginSuccessXMLDocument(PlayerProfile pProfil) 
	{
		// Standard-Kopf holen
		Document doc = getXmlDocHeader(OLD____MessageType.LoginSuccess);
		
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		Element profil = new Element("Profil");
		
		profil.addContent(new Element("Loginname").addContent(pProfil.getLoginName()));
		profil.addContent(new Element("Passwort").addContent(pProfil.getPassword()));
		profil.addContent(new Element("Email").addContent(pProfil.getEMail()));
		profil.addContent(new Element("IstGameMaster").addContent(pProfil.getIsGameMaster().toString()));
		
		// TODO: Echte Charaktere auslesen und ggf anhängen
		profil.addContent(new Element("Spielercharaktere").setAttribute("count", "0"));
				
		msgContent.addContent(profil);
					
		return doc;
	}
		
	/**
	 * 
	 * @param reason Grund für den Verbindungsabbruch
	 * @return XML Dokument für den Verbindungsabbruch
	 */
	public static Document getDisconnectXMLDocument(String reason) 
	{
		// Standard-Kopf holen
		Document doc = getXmlDocHeader(OLD____MessageType.Disconnect);
		
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Reason").addContent(reason));
		
		return doc;
	}
	
	/**
	 * 
	 * @param reason Der Grund für das Fehlfschlagen der Registrierung
	 * @return XML Dokument für eine fehlerhafte Registrierung am Server
	 */
	public static Document getRegistrationFailedXMLDocument(String reason) 
	{
		// Standard-Kopf holen
		Document doc = getXmlDocHeader(OLD____MessageType.RegisterFailed);
		
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Reason").addContent(reason));
		
		return doc;
	}
	
	/**
	 * 
	 * @return XML Dokument für ein erfolgeiches Anmelden eines Spielerprofils
	 */
	public static Document getRegistrationSucceededXMLDocument() 
	{
		// Standard-Kopf holen
		Document doc = getXmlDocHeader(OLD____MessageType.RegisterSuccess);
		
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Success"));
		
		return doc;
	}

	/**
	 * Parst das Dokument mit dem XMLOutputter und gibt so den String zurück
	 * @param doc XMLDokument welches in einen String gewandelt werden soll
	 * @return Liefert das Dokument als String 
	 */
	public static String toString(Document doc) 
	{
		XMLOutputter out = new XMLOutputter();
				
    	return out.outputString(doc);
	}
	
	/**
	 * Parst das Dokument mit dem XMLOutputter und dem PrettyFormat
	 * @param doc XMLDokument welches in einen String gewandelt werden soll
	 * @return Liefert einen formatierten String des Dokuments
	 */
	public static String toPrettyString(Document doc) 
	{
		XMLOutputter out = new XMLOutputter();
		
		out.setFormat(Format.getPrettyFormat());
    	return out.outputString(doc);
	}		
	
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Standard Kopf Inhalt einer XML-Nachricht bei eXigence erzeugen
	 * @param type Art der Nachricht
	 * @return ein XML-Message-Dokuement mit XML-Header und gesetztem MessageType
	 */
	private static Document getXmlDocHeader(OLD____MessageType type) 
	{				
		// Dokumenten Kopf erstellen
		Document doc = new Document();
		//doc.setDocType(new DocType(elementName, publicID, systemID));
		
		// Root Element erzeugen
		Element root = new Element("Message");
		
		// MessageType schreiben
		Element msgType = new Element("MessageType");		
		msgType.addContent(type.toString());
		root.addContent(msgType);
		
		Element msgContent = new Element("MessageContent");		
		root.addContent(msgContent);
		
		doc.setRootElement(root);
		return doc;
	}
	
	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------	
}
