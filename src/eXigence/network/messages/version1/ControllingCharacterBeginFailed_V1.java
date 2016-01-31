package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IControllingCharacterBeginFailed;

public class ControllingCharacterBeginFailed_V1 extends XMLMessage_V1 implements
		IControllingCharacterBeginFailed
{
	private String reason ="";
	
	@Override
	public String toNetworkString()
	{
		Document doc = getXMLStructure(getMessageType());
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		
		Element reason = new Element("Reason");
		reason.addContent(this.reason);
		
		msgContent.addContent(reason);
		
		XMLOutputter xmlOut = new XMLOutputter();		
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.ControllingCharacterBeginFailed;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public void setReason(String reason)
	{
		this.reason = reason;
	}

}
