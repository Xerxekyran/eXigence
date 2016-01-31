package eXigence.network.messages;

public interface ICreateCharacterSuccess extends IOutgoingMessage
{
	/**
	 * Setter für die ID des neu erstellten Characters
	 * @param id id des neuen Characters
	 */
	public void setNewCharacterID(int id);
}
