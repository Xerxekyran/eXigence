package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IDeleteProfileFailed;

public class DeleteProfileFailed_V1 extends XMLMessage_V1 implements
		IDeleteProfileFailed
{

	private String reason = "";
	
	@Override
	public String toNetworkString()
	{
		// Grundgerüst holen
		Document doc = getXMLStructure(getMessageType());
		Element msgContent = doc.getRootElement().getChild("MessageContent");

		// Success Element anhängen
		msgContent.addContent(new Element("Reason").addContent(reason));
		
		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.DeleteProfileFailed;
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
