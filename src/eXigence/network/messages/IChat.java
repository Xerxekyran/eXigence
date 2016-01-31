package eXigence.network.messages;

public interface IChat extends IIncomingMessage, IOutgoingMessage
{
	/**
	 * Setter für den Chatnachrichteninhalt
	 * @param txt Inhalt der Chatnachricht
	 */
	public void setChatText(String txt);
	
	/**
	 * Getter für den Inhalt der Chatnachricht
	 * @return Textliche Nachricht die gesendet werden soll
	 */
	public String getChatText();
}
