package eXigence.network.messages;

import eXigence.network.messages.version1.MessageFactory_V1;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public abstract class AbstractMessageFactory
{
	/**
	 * Factory-Methode zum erstellen einer MessageFactory
	 * 
	 * @param msgVersion
	 * @return Ein konkretes MessageFactory-Objekt welches Nachrichten in der
	 *         angegebenen Version liefert
	 */
	public static IMessageFactory getMessageFactory(EMessageVersion msgVersion)
	{
		LogWriter.getInstance().logToFile(LogLevel.Debug,
				"AbstractMessageFactory::getMessageFactory(" + msgVersion + ")");

		IMessageFactory msgFactory = null;

		switch (msgVersion)
		{
		case ONE:
			msgFactory = new MessageFactory_V1();
			break;
		}

		return msgFactory;
	}
}
