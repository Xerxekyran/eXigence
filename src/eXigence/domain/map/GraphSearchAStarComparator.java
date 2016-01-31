package eXigence.domain.map;

import java.util.Comparator;

/**
 * Comparator für die Priorityqueue des A* Suchalgorithmus 
 * @author Lars George
 *
 * @param <T> Zu vergleichende Elemente
 */
public class GraphSearchAStarComparator<T extends Integer> implements Comparator<T>
{
	private GraphSearchAStarTimeSliced graphSearch;
	
	public GraphSearchAStarComparator(GraphSearchAStarTimeSliced graphSearchAStar)
	{
		this.graphSearch = graphSearchAStar;
	}

	@Override
	public int compare(T arg0, T arg1)
	{		
		double w1 = graphSearch.getGCosts().get(arg0);
		double w2 = graphSearch.getGCosts().get(arg1);						
		
		if(w1 < w2) 
			return -1;
		
		else if(w1 > w2)
			return 1;
		
		// Eig überprüfung auf ein 3tes kriterium wie zB nach Name
		return -1;
	}

}
