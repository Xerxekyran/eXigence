package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IRegisterRequest;

public class RegisterRequest_V1 implements IRegisterRequest
{
	
	private String loginname = "";
	private String password = "";
	private String email = "";
	
	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 
		{
			Element profile = xmlData.getRootElement().getChild("MessageContent").getChild("Profile");
			this.email 		= profile.getChildText("Email");
			this.loginname	= profile.getChildText("Loginname");
			this.password	= profile.getChildText("Password");
			
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Received wrong xmlData for RegisterRequest_V1 Message: "+ e.toString());			
		}
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.RegisterRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public String getEMail()
	{		
		return this.email;
	}

	@Override
	public String getLoginname()
	{
		return this.loginname;
	}

	@Override
	public String getPassword()
	{
		return this.password;
	}

}
