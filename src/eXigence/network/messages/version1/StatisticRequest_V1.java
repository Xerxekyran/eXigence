package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IStatisticRequest;

public class StatisticRequest_V1 implements IStatisticRequest
{

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.StatisticRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
