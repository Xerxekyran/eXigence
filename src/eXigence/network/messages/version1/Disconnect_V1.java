package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IDisconnect;

public class Disconnect_V1 extends XMLMessage_V1 implements IDisconnect
{
	private String	reason	= "";

	@Override
	public void setReason(String reason)
	{
		this.reason = reason;
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.Disconnect;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public String toNetworkString()
	{
		// Grundgerüst holen
		Document doc = getXMLStructure(getMessageType());

		// Content erstellen
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Reason").addContent(this.reason));

		// XML Ausgabe-Objekt erstellen
		XMLOutputter xmlOut = new XMLOutputter();

		return xmlOut.outputString(doc);
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try
		{
			this.reason = xmlData.getRootElement().getChild("MessageContent").getChildText("Reason");
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(
					"Received wrong xmlData for Disconnect_V1 Message: "
							+ e.toString());
		}
	}

	@Override
	public String getReason()
	{
		return this.reason;
	}

}
