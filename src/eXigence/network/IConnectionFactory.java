package eXigence.network;

public interface IConnectionFactory
{
	/**
	 * Diese Methode blockiert so lange, bis eine neue Clientverbindung
	 * aufgebaut gefunden wurde. Es wird dann ein entsprechendes Objekt erzeugt
	 * und zur�ck gegeben
	 * 
	 * @return Neue Verbidung zu einem Client
	 */
	public IClientConnection getNewConnection();

}
