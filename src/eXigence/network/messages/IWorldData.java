package eXigence.network.messages;

import eXigence.domain.entities.VisibleEntity;

public interface IWorldData extends IOutgoingMessage
{
	/**
	 * Setter für die Objekte welche in der welt dargestellt werden sollen
	 * (ohne die Charaktere)
	 * @param gameObjects Array von Spielobjekten (Gebäude und nicht interaktive Elemente) 
	 */
	public void setGameObjects(VisibleEntity gameObjects[]);
	
}
