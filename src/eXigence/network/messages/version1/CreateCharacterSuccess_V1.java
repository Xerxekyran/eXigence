package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;

public class CreateCharacterSuccess_V1 extends XMLMessage_V1 implements
		eXigence.network.messages.ICreateCharacterSuccess
{
	private int characterID = 0;

	@Override
	public String toNetworkString()
	{
		// Grundgerüst holen
		Document doc = getXMLStructure(getMessageType());
		Element msgContent = doc.getRootElement().getChild("MessageContent");

		// Success Element anhängen
		msgContent.addContent(new Element("Success").addContent(new Element("NewCharacterID").addContent(Integer.toString(characterID))));
		
		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.CreateCharacterSuccess;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public void setNewCharacterID(int id)
	{
		this.characterID = id;		
	}

}
