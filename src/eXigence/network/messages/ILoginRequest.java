package eXigence.network.messages;

public interface ILoginRequest extends IIncomingMessage
{
	/**
	 * 
	 * @return Benutzernamen f�r den Loginrequest
	 */
	public String getLoginname();
	
	/**
	 * 
	 * @return Passwort f�r den LoginRequest
	 */
	public String getPassword();
}
