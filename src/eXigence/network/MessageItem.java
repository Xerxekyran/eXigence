package eXigence.network;

import eXigence.network.messages.IMessage;

/**
 * 
 * Klasse für Nachrichten innerhalb des eXigence Systems.<br />
 * Eine Nachricht enthält eine Referenz zu einer eXigence.ClientConnection um
 * den Sender der Nachricht zu ermitteln und eine IMessage, welches den Inhalt
 * darstellt.<br />
 * 
 * @version 0.1
 * @author Lars George
 * 
 */
public class MessageItem
{
	private SocketClientConnection	client	= null; // Von wem kam die
													// Nachricht?
	private IMessage				message	= null; // Nachrichteninhalt

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Konstruktor mit initialisierung der Elemtattribute
	 * 
	 * @param client
	 *            Von wem kommt die Nachricht, null wenn sie vom Server erzeugt
	 *            wurde
	 * @param content
	 *            Inhalt der Nachricht
	 */
	public MessageItem(SocketClientConnection client, IMessage content)
	{
		this.client = client;
		this.message = content;
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public SocketClientConnection getClient()
	{
		return client;
	}

	public void setClient(SocketClientConnection client)
	{
		this.client = client;
	}

	public IMessage getMessage()
	{
		return message;
	}

	public void setMessage(IMessage message)
	{
		this.message = message;
	}
}
