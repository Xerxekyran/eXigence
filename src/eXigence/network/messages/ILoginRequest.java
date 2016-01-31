package eXigence.network.messages;

public interface ILoginRequest extends IIncomingMessage
{
	/**
	 * 
	 * @return Benutzernamen für den Loginrequest
	 */
	public String getLoginname();
	
	/**
	 * 
	 * @return Passwort für den LoginRequest
	 */
	public String getPassword();
}
