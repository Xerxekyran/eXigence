package eXigence.network.messages;

public interface IRegisterRequest extends IIncomingMessage
{
	/**
	 * 
	 * @return Den gew�nschten Loginnamen f�r das neue Profil 
	 */
	public String getLoginname();
	
	/**
	 * 
	 * @return Das gew�nschten Passwort f�r das neue Profil 
	 */
	public String getPassword();
	
	/**
	 * 
	 * @return Die gew�nschten EMail f�r das neue Profil 
	 */
	public String getEMail();
}
