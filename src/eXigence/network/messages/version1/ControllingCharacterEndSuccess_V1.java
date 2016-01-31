package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IControllingCharacterEndSuccess;

public class ControllingCharacterEndSuccess_V1 extends XMLMessage_V1 implements
		IControllingCharacterEndSuccess		
{

	@Override
	public String toNetworkString()
	{
		// Grundgerüst holen
		Document doc = getXMLStructure(getMessageType());
		Element msgContent = doc.getRootElement().getChild("MessageContent");

		// Success Element anhängen
		msgContent.addContent(new Element("Success"));
		
		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{	
		return EMessageType.ControllingCharacterEndSuccess;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
