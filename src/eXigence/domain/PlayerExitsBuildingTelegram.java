package eXigence.domain;

import eXigence.domain.entities.buildings.ExigenceBuilding;

public class PlayerExitsBuildingTelegram
{
	private boolean isInEmployeeEntrance = false;
	private ExigenceBuilding building = null;
	
	public PlayerExitsBuildingTelegram(ExigenceBuilding building, boolean isInEmployeeEntrance)
	{
		this.building = building;
		this.isInEmployeeEntrance = isInEmployeeEntrance;
	}

	public boolean isInEmployeeEntrance()
	{
		return isInEmployeeEntrance;
	}

	public void setInEmployeeEntrance(boolean isInEmployeeEntrance)
	{
		this.isInEmployeeEntrance = isInEmployeeEntrance;
	}

	public ExigenceBuilding getBuilding()
	{
		return building;
	}

	public void setBuilding(ExigenceBuilding building)
	{
		this.building = building;
	}
	
	
}
