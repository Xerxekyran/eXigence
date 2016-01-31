package eXigence.util.math;

/**
 * Klasse mit Schnittpunktberechnungen
 * 
 * @author Lars George
 * @version 1.0
 */
public class IntersectionTests
{
	/**
	 * Linie gegen Kreisschneidung testen
	 * 
	 * @param lineFrom
	 *            Startpunkt der Linie
	 * @param lineTo
	 *            Endpunkt der Linie
	 * @param circlePos
	 *            Kreisposition
	 * @param circleRadius
	 *            Kreisradius
	 * @return true wenn sich die beiden Objekte schneiden, ansonsten false
	 */
	public static boolean lineSegemntCircleIntersection(Vector2D lineFrom,
														Vector2D lineTo,
														Vector2D circlePos,
														double circleRadius)
	{
		// Distanz vom Kreis zum Liniensegment errechnen (quadriert)
		double distToLineSquare = distToLineSegmentSq(lineFrom,
				lineTo,
				circlePos);

		if (distToLineSquare < circleRadius * circleRadius)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Errechnet die Distanz des Punktes zu der Linie
	 * 
	 * param lineFrom Startpunkt der Linie
	 * 
	 * @param lineTo
	 *            Endpunkt der Linie
	 * @param point
	 *            Punktposition
	 * @return Kürzeste Distanz des Punktes zur Linie (in quadrierter Form)
	 */
	public static double distToLineSegmentSq(	Vector2D lineFrom,
												Vector2D lineTo,
												Vector2D point)
	{
		/*
		 * if the angle is obtuse between PA and AB is obtuse then the closest
		 * vertex must be A double dotA = (P.x - A.x)*(B.x - A.x) + (P.y -
		 * A.y)*(B.y - A.y);
		 * 
		 * if (dotA <= 0) return Vec2DDistanceSq(A, P);
		 * 
		 * if the angle is obtuse between PB and AB is obtuse then the closest
		 * vertex must be B double dotB = (P.x - B.x)*(A.x - B.x) + (P.y -
		 * B.y)*(A.y - B.y);
		 * 
		 * if (dotB <= 0) return Vec2DDistanceSq(B, P);
		 * 
		 * calculate the point along AB that is the closest to P Vector2D Point =
		 * A + ((B - A) * dotA)/(dotA + dotB);
		 * 
		 * calculate the distance P-Point return Vec2DDistanceSq(P,Point);
		 */

		double dotA = (point.getX() - lineFrom.getX())
				* (lineTo.getX() - lineFrom.getX())
				+ (point.getY() - lineFrom.getY())
				* (lineTo.getY() - lineFrom.getY());

		if (dotA <= 0)
			return lineFrom.distanceSquare(point);

		double dotB = (point.getX() - lineTo.getX())
				* (lineFrom.getX() - lineTo.getX())
				+ (point.getY() - lineTo.getY())
				* (lineFrom.getY() - lineTo.getY());

		if (dotB <= 0)
			return lineTo.distanceSquare(point);

		// Der Punkt auf der Linie der am nächsten zu P ist
		Vector2D pointOnLine = lineFrom.add((lineTo.sub(lineFrom)).mul(dotA).divide(dotA
				+ dotB));

		// Distanz berechnen und zurückgeben
		return point.distanceSquare(pointOnLine);
	}
}
