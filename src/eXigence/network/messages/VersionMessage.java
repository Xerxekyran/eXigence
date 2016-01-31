package eXigence.network.messages;

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * Dies ist die einzige nicht Versionierte Message, da sie zum festlegen
 * der Version der Nachrichten dient. Es ist stets die erste akzeptierte Nachricht
 * von den Connections.
 * In ihr steht die zu benutzende Version von Nachrichten
 * 
 * @author Lars George
 *
 */
public class VersionMessage implements IIncomingMessage
{
	private EMessageVersion	version	= null;

	/**
	 * Konstruktor
	 */
	private VersionMessage(EMessageVersion msgVersion)
	{
		this.version = msgVersion;
	}

	/**
	 * Versucht aus dem übergebenen String eine Versionsnchricht zu erstellen
	 * 
	 * @param msgText
	 */
	public static VersionMessage getVersionMessage(String msgText) throws IllegalArgumentException
	{
		VersionMessage retVal = null;
		SAXBuilder builder = new SAXBuilder();
		
		try
		{			
			// XML-Stirng Parsen
			Document doc = builder.build(new StringReader(msgText));
			
			// Type ermitteln
			String msgType = doc.getRootElement().getChildText("MessageType");

			// Wenn es die richtige Art von Nachricht ist
			if(msgType.equals("VersionHandshake"))
			{
				EMessageVersion msgVersion = null;
				
				String strVersion = doc.getRootElement().getChild("MessageContent").getChildText("Version");
				
				// Den String in eine Entsprechende Versionsnummer umwandeln
				if(strVersion.equals("1"))
				{
					msgVersion = EMessageVersion.ONE;
				}					
				else
				{
					// Eine nicht implementierte Nachrichtenversion wird angefordert
					throw new Exception("Received unsopported Messageversion ("+ strVersion +").");
				}
									
				retVal = new VersionMessage(msgVersion);
			}
			else
			{
				// Falscher Nachrichtentyp
				throw new Exception("Received wrong Messagetype ("+ msgType +"), expected: VersionHandshake. ");
			}
				
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(e.toString());
		}
		
		return retVal;
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.VersionMessage;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return this.version;
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		throw new IllegalArgumentException("setContent: Method not supported for VersionMessage Class");		
	}
}
