package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IControllingCharacterBeginSuccess;

public class ControllingCharacterBeginSuccess_V1 extends XMLMessage_V1
		implements IControllingCharacterBeginSuccess
{

	@Override
	public String toNetworkString()
	{
		// Grundger�st holen
		Document doc = getXMLStructure(getMessageType());
		Element msgContent = doc.getRootElement().getChild("MessageContent");

		// Success Element anh�ngen
		msgContent.addContent(new Element("Success"));
		
		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.ControllingCharacterBeginSuccess;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}