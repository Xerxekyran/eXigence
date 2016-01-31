package eXigence.network.messages;

public interface IChat extends IIncomingMessage, IOutgoingMessage
{
	/**
	 * Setter f�r den Chatnachrichteninhalt
	 * @param txt Inhalt der Chatnachricht
	 */
	public void setChatText(String txt);
	
	/**
	 * Getter f�r den Inhalt der Chatnachricht
	 * @return Textliche Nachricht die gesendet werden soll
	 */
	public String getChatText();
}
