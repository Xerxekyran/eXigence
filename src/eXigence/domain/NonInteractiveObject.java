package eXigence.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import eXigence.domain.entities.VisibleEntity;
import eXigence.domain.map.WorldMap;

/**
 * Klasse f�r neutrale Objekte in der Simulationswelt von eXigence Beispiel:
 * Baum, Hecke, Geb�ude ohne Interaktionsm�glichkeiten
 * 
 * @author Lars George
 * 
 */
@Entity
public class NonInteractiveObject extends VisibleEntity
{
	@ManyToOne
	private WorldMap homeMap = null;

	public WorldMap getHomeMap()
	{
		return homeMap;
	}

	public void setHomeMap(WorldMap homeMap)
	{
		this.homeMap = homeMap;
	}
	
	
}
