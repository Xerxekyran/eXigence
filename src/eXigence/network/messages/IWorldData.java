package eXigence.network.messages;

import eXigence.domain.entities.VisibleEntity;

public interface IWorldData extends IOutgoingMessage
{
	/**
	 * Setter f�r die Objekte welche in der welt dargestellt werden sollen
	 * (ohne die Charaktere)
	 * @param gameObjects Array von Spielobjekten (Geb�ude und nicht interaktive Elemente) 
	 */
	public void setGameObjects(VisibleEntity gameObjects[]);
	
}
