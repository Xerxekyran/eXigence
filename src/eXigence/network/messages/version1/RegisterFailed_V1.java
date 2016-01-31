package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IRegisterFailed;

public class RegisterFailed_V1 extends XMLMessage_V1 implements IRegisterFailed
{
	private String[] reasons = null;

	@Override
	public void setReasons(String[] reasons)
	{
		this.reasons = reasons;
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.RegisterFailed;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public String toNetworkString()
	{
		StringBuilder reasonsStr = new StringBuilder("");
		
		// Alle Gr�nde zusammenf�hren
		for(int i = 0; i < this.reasons.length; i ++)
		{
			reasonsStr.append(this.reasons[i].toString());
		}
		
		// Grundger�st holen
		Document doc = getXMLStructure(getMessageType());
		
		// Mit Inhalt f�llen
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		msgContent.addContent(new Element("Reason").addContent(reasonsStr.toString()));

		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

}
