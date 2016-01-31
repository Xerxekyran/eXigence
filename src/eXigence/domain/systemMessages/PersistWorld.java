package eXigence.domain.systemMessages;


/**
 * Systemnachricht, welche das System dazu bewegt die Welt persistenz
 * abzuspeichern
 * 
 * @author Lars George
 * 
 */
public class PersistWorld implements ISystemMessage
{

	@Override
	public ESystemMessages getType()
	{
		return ESystemMessages.PersistWorld;
	}

}
