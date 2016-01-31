package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.ILoginFailed;

public class LoginFailed_V1 extends XMLMessage_V1 implements ILoginFailed
{
	private String reason = "";

	@Override
	public void setReason(String reason)
	{
		this.reason = reason;
	}

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
		return EMessageType.LoginFailed;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
