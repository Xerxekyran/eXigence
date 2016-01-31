package eXigence.network.messages;

public interface IStatistics extends IOutgoingMessage
{
	/**
	 * Setter für die Werte wieviele Charactere sich momentan in den Gebäuden
	 * befinden
	 * 
	 * @param restaurant
	 * @param hotel
	 * @param bath
	 * @param cinema
	 * @param gym
	 * @param library
	 * @param police
	 * @param bank
	 * @param hospital
	 * @param church
	 * @param dixi
	 */
	public void setClientsAt(	int restaurant,
								int hotel,
								int bath,
								int cinema,
								int gym,
								int library,
								int police,
								int bank,
								int hospital,
								int church,
								int dixi);

	/**
	 * Setter für die Werte wieviele Charactere sich momentan in den Gebäuden
	 * als Arbeiter befinden
	 * 
	 * @param restaurant
	 * @param hotel
	 * @param bath
	 * @param cinema
	 * @param gym
	 * @param library
	 * @param police
	 * @param bank
	 * @param hospital
	 * @param church
	 */
	public void setWorkersAt(	int restaurant,
								int hotel,
								int bath,
								int cinema,
								int gym,
								int library,
								int police,
								int bank,
								int hospital,
								int church);
}
