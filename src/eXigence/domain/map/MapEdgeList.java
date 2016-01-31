package eXigence.domain.map;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class MapEdgeList
{
	@Id
	@GeneratedValue
	private int MapEdgeListID;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<MapEdge> edges;

	
	public MapEdgeList()
	{
		edges = new ArrayList<MapEdge>();
	}
	
	public List<MapEdge> getEdges()
	{
		return edges;
	}

	public void setEdges(List<MapEdge> edges)
	{
		this.edges = edges;
	}

	public int getMapEdgeListID()
	{
		return MapEdgeListID;
	}

	public void setMapEdgeListID(int mapEdgeListID)
	{
		MapEdgeListID = mapEdgeListID;
	}
	
	
	
}
