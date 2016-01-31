package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IDeleteCharacterRequest;

public class DeleteCharacterRequest_V1 extends XMLMessage_V1 implements
		IDeleteCharacterRequest
{
	private int characterID = 0;
	
	@Override
	public int getCharacterID()
	{
		return characterID;
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 
		{
			this.characterID = Integer.parseInt(xmlData.getRootElement().getChild("MessageContent").getChildText("CharacterID"));
			
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Received wrong xmlData for DeleteCharacterRequest_V1 Message: "+ e.toString());			
		}

	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.DeleteCharacterRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
