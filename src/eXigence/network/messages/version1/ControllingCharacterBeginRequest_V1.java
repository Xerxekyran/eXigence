package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IControllingCharacterBeginRequest;

public class ControllingCharacterBeginRequest_V1 extends XMLMessage_V1
		implements IControllingCharacterBeginRequest
{
	private int characterID;

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
			throw new IllegalArgumentException("Received wrong xmlData for ControllingCharacterBeginRequest_V1 Message: "+ e.toString());			
		}
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.ControllingCharacterBeginRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
