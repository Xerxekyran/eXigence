package eXigence.network.messages;

public interface IChangeProfilRequest extends IIncomingMessage
{
	/**
	 * 
	 * @return Den neuen Loginnamen f�r das Profil
	 */
	public String getNewLoginname();
	
	/**
	 * 
	 * @return Das neue Passwort f�r das Profil
	 */
	public String getNewPassword();

	/**
	 * 
	 * @return Die neue EMailadresse f�r das Pofil
	 */
	public String getNewEMail();
}
