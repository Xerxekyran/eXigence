package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IWorldDataRequest;

public class WorldDataRequest_V1 extends XMLMessage_V1 implements
		IWorldDataRequest
{

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.WorldDataRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
