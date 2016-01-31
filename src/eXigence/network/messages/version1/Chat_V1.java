package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IChat;

public class Chat_V1 extends XMLMessage_V1 implements IChat
{

	private String	text	= "";

	@Override
	public void setChatText(String txt)
	{
		this.text = txt;
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try
		{
			// Content element ermitteln
			Element msgContent = xmlData.getRootElement().getChild("MessageContent");

			// Chattext extrahieren und setzen
			setChatText(msgContent.getChildText("Text"));
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(
					"Unable to create Chat_V1 Message from given xmlData: "
							+ e.toString());
		}
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.Chat;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public String toNetworkString()
	{
		// Grundgerüst erstellen
		Document doc = getXMLStructure(getMessageType());

		// Content entsprechend füllen
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Text").addContent(this.text));

		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public String getChatText()
	{
		return this.text;
	}

}
