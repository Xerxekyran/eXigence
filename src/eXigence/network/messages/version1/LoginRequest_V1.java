package eXigence.network.messages.version1;

import org.jdom.Document;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.ILoginRequest;

public class LoginRequest_V1 implements ILoginRequest {
	private String loginname = "";
	private String password = "";
	
	@Override
	public EMessageType getMessageType() 
	{		
		return EMessageType.LoginRequest;
	}

	
	@Override
	public EMessageVersion getVersion() 
	{
		return EMessageVersion.ONE;
	}


	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 
		{
			this.loginname 	= xmlData.getRootElement().getChild("MessageContent").getChildText("Loginname");
			this.password 	= xmlData.getRootElement().getChild("MessageContent").getChildText("Password");
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Received wrong xmlData for LoginRequest_V1 Message: "+ e.toString());
		}						
	}


	@Override
	public String getPassword()
	{
		return this.password;
	}


	@Override
	public String getLoginname()
	{
		return this.loginname;
	}

}
