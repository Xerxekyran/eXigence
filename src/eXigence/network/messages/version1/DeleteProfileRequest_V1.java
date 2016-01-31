package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;

public class DeleteProfileRequest_V1 implements
		eXigence.network.messages.IDeleteProfileRequest
{

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		// No content yet

	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.DeleteProfileRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
