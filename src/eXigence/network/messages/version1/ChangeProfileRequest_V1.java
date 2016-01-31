package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IChangeProfilRequest;

public class ChangeProfileRequest_V1 implements IChangeProfilRequest
{

	private String mail = "";
	private String loginname = "";
	private String password = "";
	
	@Override
	public String getNewEMail()
	{
		return mail;
	}

	@Override
	public String getNewLoginname()
	{
		return loginname;
	}

	@Override
	public String getNewPassword()
	{
		return password;
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 
		{
			this.loginname 	= xmlData.getRootElement().getChild("MessageContent").getChild("Profil").getChildText("NewLoginname");
			this.password 	= xmlData.getRootElement().getChild("MessageContent").getChild("Profil").getChildText("NewPassword");
			this.mail 		= xmlData.getRootElement().getChild("MessageContent").getChild("Profil").getChildText("NewEmail");
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Received wrong xmlData for LoginRequest_V1 Message: "+ e.toString());
		}
		
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.ChangeProfileRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
