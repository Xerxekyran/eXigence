package eXigence.network.messages;

public interface IChangeProfilRequest extends IIncomingMessage
{
	/**
	 * 
	 * @return Den neuen Loginnamen für das Profil
	 */
	public String getNewLoginname();
	
	/**
	 * 
	 * @return Das neue Passwort für das Profil
	 */
	public String getNewPassword();

	/**
	 * 
	 * @return Die neue EMailadresse für das Pofil
	 */
	public String getNewEMail();
}
