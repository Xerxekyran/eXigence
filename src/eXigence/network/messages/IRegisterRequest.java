package eXigence.network.messages;

public interface IRegisterRequest extends IIncomingMessage
{
	/**
	 * 
	 * @return Den gewünschten Loginnamen für das neue Profil 
	 */
	public String getLoginname();
	
	/**
	 * 
	 * @return Das gewünschten Passwort für das neue Profil 
	 */
	public String getPassword();
	
	/**
	 * 
	 * @return Die gewünschten EMail für das neue Profil 
	 */
	public String getEMail();
}
