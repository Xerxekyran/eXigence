package eXigence.network.messages;

public interface ICreateCharacterSuccess extends IOutgoingMessage
{
	/**
	 * Setter f�r die ID des neu erstellten Characters
	 * @param id id des neuen Characters
	 */
	public void setNewCharacterID(int id);
}
