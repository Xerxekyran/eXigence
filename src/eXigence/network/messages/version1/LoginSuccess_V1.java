package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.domain.PlayerProfile;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.ILoginSuccess;

public class LoginSuccess_V1 extends XMLMessage_V1 implements ILoginSuccess
{
	private PlayerProfile profile = null;

	@Override
	public EMessageType getMessageType() 
	{		
		return EMessageType.LoginSuccess;
	}

	
	@Override
	public EMessageVersion getVersion() 
	{
		return EMessageVersion.ONE;
	}


	@Override
	public String toNetworkString()
	{
		// Nur wenn es auch ein Profil gibt weiter machen
		if(this.profile == null)
			return null;
		
		Document doc = getXMLStructure(getMessageType()); 
	
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		Element profile = new Element("Profile");
		
		profile.addContent(new Element("Loginname").addContent(this.profile.getLoginName()));
		profile.addContent(new Element("Password").addContent(this.profile.getPassword()));
		profile.addContent(new Element("Email").addContent(this.profile.getEMail()));
		profile.addContent(new Element("IsGameMaster").addContent(this.profile.getIsGameMaster().toString()));
		
		// TODO: Echte Charaktere auslesen und ggf anhängen
		profile.addContent(new Element("Characters").setAttribute("count", "0"));
				
		msgContent.addContent(profile);
				
		XMLOutputter xmlOut = new XMLOutputter();			
		return xmlOut.outputString(doc);
	}


	@Override
	public void setProfil(PlayerProfile profil)
	{
		this.profile = profil;		
	}

}
