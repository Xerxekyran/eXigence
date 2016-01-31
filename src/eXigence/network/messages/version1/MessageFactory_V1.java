package eXigence.network.messages.version1;

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IIncomingMessage;
import eXigence.network.messages.IMessageFactory;
import eXigence.network.messages.IOutgoingMessage;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

/**
 * Eine MessageFactory die Nachrichten in der Version 1 generiert
 * 
 * @author Lars George
 * 
 */
public class MessageFactory_V1 implements IMessageFactory
{
	/**
	 * SAXBuilder zum parsen der XML-Strings
	 */
	private SAXBuilder	xmlBuilder	= null;

	public MessageFactory_V1()
	{
		xmlBuilder = new SAXBuilder();
	}

	@Override
	public IIncomingMessage createMessageFromString(String text)
			throws IllegalArgumentException
	{
		IIncomingMessage retMsg = null;

		// XML parsen
		try
		{
			Document doc = xmlBuilder.build(new StringReader(text));
			String msgType = doc.getRootElement().getChildText("MessageType");

			// Den Typ der Message ermitteln und entsprechende neue Message
			// erstellen
			switch (EMessageType.valueOf(msgType))
			{
				case LoginRequest: 						retMsg = new LoginRequest_V1(); break;
				case Chat: 								retMsg = new Chat_V1(); break;
				case RegisterRequest: 					retMsg = new RegisterRequest_V1(); break;
				case ChangeProfileRequest: 				retMsg = new ChangeProfileRequest_V1(); break;
				case DeleteProfileRequest: 				retMsg = new DeleteProfileRequest_V1(); break;
				case StatisticRequest: 					retMsg = new StatisticRequest_V1(); break;
				case CreateCharacterRequest: 			retMsg = new CreateCharacterRequest_V1(); break;
				case EditCharacterRequest:				retMsg = new EditCharacterRequest_V1(); break;
				case WorldDataRequest:					retMsg = new WorldDataRequest_V1(); break;
				case Disconnect:						retMsg = new Disconnect_V1(); break;
				case ControllingCharacterBeginRequest: 	retMsg = new ControllingCharacterBeginRequest_V1(); break;
				case ControllingCharacterEndRequest: 	retMsg = new ControllingCharacterEndRequest_V1(); break;
				case Interaction:						retMsg = new Interaction_V1(); break;
				case DeleteCharacterRequest:			retMsg = new DeleteCharacterRequest_V1(); break;
				
				default:
					throw new IllegalArgumentException(
							"Received Message with unknown Type: " + msgType);
			}

			// Nachricht mit Inhalt füllen
			retMsg.setContent(doc);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(
					"Passed Wrong XML-String to create incoming Message: "
							+ e.toString());
		}

		return retMsg;
	}

	@Override
	public IOutgoingMessage createEmptyMessageByType(EMessageType type)
	{
		IOutgoingMessage retMsg = null;

		// Typeabhängig das jeweilige Objekt erstellen
		switch (type)
		{			
			case UpdateCharacters:					retMsg = new UpdateCharacters_V1(); break;	
			case Disconnect: 						retMsg = new Disconnect_V1(); break;
			case Statistics: 						retMsg = new Statistics_V1(); break;
			case LoginFailed: 						retMsg = new LoginFailed_V1(); break;
			case LoginSuccess: 						retMsg = new LoginSuccess_V1(); break;			
			case RegisterFailed: 					retMsg = new RegisterFailed_V1(); break;
			case RegisterSuccess: 					retMsg = new RegisterSuccess_V1(); break;		
			case ChangeProfileFailed: 				retMsg = new ChangeProfileFailed_V1(); break;
			case ChangeProfileSuccess: 				retMsg = new ChangeProfileSuccess_V1(); break;
			case DeleteProfileFailed: 				retMsg = new DeleteProfileFailed_V1(); break;
			case DeleteProfileSuccess: 				retMsg = new DeleteProfileSuccess_v1(); break;			
			case CreateCharacterFailed: 			retMsg = new CreateCharacterFailed_V1(); break;
			case CreateCharacterSuccess: 			retMsg = new CreateCharacterSuccess_V1(); break;
			case EditCharacterFailed:				retMsg = new EditCharacterFailed_V1(); break;
			case EditCharacterSuccess:				retMsg = new EditCharacterSuccess_V1(); break;			
			case WorldData:							retMsg = new WorldData_V1(); break;
			case ControllingCharacterBeginSuccess: 	retMsg = new ControllingCharacterBeginSuccess_V1(); break;
			case ControllingCharacterBeginFailed: 	retMsg = new ControllingCharacterBeginFailed_V1(); break;
			case ControllingCharacterEndSuccess: 	retMsg = new ControllingCharacterEndSuccess_V1(); break;
			case ControllingCharacterEndFailed: 	retMsg = new ControllingCharacterEndFailed_V1(); break;
			case DeleteCharacterFailed:				retMsg = new DeleteCharacterFailed_V1(); break;
			case DeleteCharacterSuccess:			retMsg = new DeleteCharacterSuccess_V1(); break;
							
			default:
				LogWriter.getInstance().logToFile(LogLevel.Error,
						"Tried to create an empty not supported Message: " + type);
				break;
		}

		return retMsg;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
