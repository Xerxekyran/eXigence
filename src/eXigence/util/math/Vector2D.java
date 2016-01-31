package eXigence.util.math;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * Repräsentiert einen Zweidimensionalen Vektor
 * 
 * @author Lars George
 * 
 */
@Embeddable
public class Vector2D
{
	@Basic
	private double	x;
	
	@Basic
	private double	y;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2D()
	{}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Gibt einen neuen Vektor zurück, welcher mit dem des Parameters addiert
	 * wurde. Verändert das Objekt dabei nicht.
	 * 
	 * @param rhs
	 *            Der Summand
	 * @return Referenz auf das berechnete Objekt
	 */
	public Vector2D add(Vector2D rhs)
	{
		return new Vector2D(x + rhs.getX(), y + rhs.getY());
	}

	/**
	 * Addiert diesen Vektor mit dem parameter, verändert ihn entsprechend und
	 * gibt die Referenz auf sich selber wieder zurück
	 * 
	 * @param rhs
	 *            Der Summand
	 * @return Referenz auf sich selber
	 */
	public Vector2D addToThis(Vector2D rhs)
	{
		this.x += rhs.getX();
		this.y += rhs.getY();

		return this;
	}

	/**
	 * Gibt einen neuen Vektor zurück, welcher mit dem des Parameters
	 * subtrahiert wurde. Verändert das Objekt dabei nicht.
	 * 
	 * @param rhs
	 *            Der Subtrahend
	 * @return Referenz auf das berechnete Objekt
	 */
	public Vector2D sub(Vector2D rhs)
	{
		return new Vector2D(x - rhs.getX(), y - rhs.getY());
	}

	/**
	 * Subtrahiert diesen Vektor mit dem parameter, verändert ihn entsprechend
	 * und gibt die Referenz auf sich selber wieder zurück
	 * 
	 * @param rhs
	 *            Der Subtrahend
	 * @return Referenz auf sich selber
	 */
	public Vector2D subToThis(Vector2D rhs)
	{
		this.x -= rhs.getX();
		this.y -= rhs.getY();

		return this;
	}

	/**
	 * Gibt einen neuen Vektor zurück, welcher mit dem Parameter multipliziert
	 * wurde. Verändert das Objekt dabei nicht.
	 * 
	 * @param rhs
	 *            Der Multiplikator
	 * @return Referenz auf das berechnete Objekt
	 */
	public Vector2D mul(Double rhs)
	{
		return new Vector2D(this.x * rhs, this.y * rhs);
	}

	/**
	 * Multipliziert diesen Vektor mit dem parameter, verändert ihn entsprechend
	 * und gibt die Referenz auf sich selber wieder zurück
	 * 
	 * @param rhs
	 *            Der Multiplikator
	 * @return Referenz auf sich selber
	 */
	public Vector2D mulToThis(Double rhs)
	{
		this.x *= rhs;
		this.y *= rhs;

		return this;
	}

	/**
	 * Gibt einen neuen Vektor zurück, welcher mit dem Parameter dividiert
	 * wurde. Verändert das Objekt dabei nicht.
	 * 
	 * @param rhs
	 *            Der Divisor
	 * @return Referenz auf das berechnete Objekt
	 */
	public Vector2D divide(Double rhs)
	{
		return new Vector2D(this.x / rhs, this.y / rhs);
	}

	/**
	 * Liefert die Länge des Vektors
	 * 
	 * @return Länge des Vektors
	 */
	public double length()
	{
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Berechnet das Punktprodukt der beiden Vektoren
	 * 
	 * @param rhs
	 *            Vektor mit dem das Punktprodukt durchgeführt werden soll
	 * @return Ergebnis des Punktprodukts
	 */
	public double dot(Vector2D rhs)
	{
		return (x * rhs.getX() + y * rhs.getY());
	}

	/**
	 * Normalisiert den Vektor (Summe aller Teile ergeben 1). Wenn die Länge 0
	 * ergibt, wird keine Normalisierung durchgeführt (Division durch 0 nicht
	 * erlaubt) Die Änderungen werden dabei in den Vektor selber geschrieben
	 * 
	 * @return Referenz auf das eigene Objekt
	 */
	public Vector2D normalizeToThis()
	{
		double vector_length = this.length();

		// Division durch 0 abfangen
		if (vector_length == 0)
			return new Vector2D(0, 0);

		// Werte normalisieren
		this.x /= vector_length;
		this.y /= vector_length;

		return this;
	}

	/**
	 * Normalisiert den Vektor (verändert jedoch die Werte nicht) und gibt eine
	 * Referenz auf das so neue entstandene Objekt zurück
	 * 
	 * @return Referenz auf ein neues Objekt, welches eine normalisierte Version
	 *         dieses Vektors darstellt
	 */
	public Vector2D normalize()
	{
		double vector_length = this.length();

		// Division durch 0 abfangen
		if (vector_length == 0)
			return new Vector2D(0, 0);

		// Werte normalisieren und Referenz auf ein neues entsprechende Objekt
		// zurückgeben
		return new Vector2D(this.x / vector_length, this.y / vector_length);
	}

	/**
	 * Setzt den Vektor auf (0/0)
	 */
	public void setZero()
	{
		this.x = 0.0;
		this.y = 0.0;
	}

	/**
	 * Überprüft ob der Vektor der Nullvektor ist
	 * 
	 * @return True wenn der Vektor (0/0) ist ansonsten false
	 */
	public boolean isZero()
	{
		if (this.x == 0 && this.y == 0)
			return true;
		else
			return false;
	}

	/**
	 * Liefert die Entfernung zwischen diesem und dem übergebenen Vektor im
	 * quadrierten Format (ohne Wurzel)
	 * 
	 * @param distanceTo
	 *            Zu welchem Punkt soll die Distanz berechnet werden
	 * @return Quadrierte Distanz zum Ziel
	 */
	public double distanceSquare(Vector2D distanceTo)
	{
		double ySeparation = distanceTo.y - this.y;
		double xSeparation = distanceTo.x - this.x;

		return ySeparation * ySeparation + xSeparation * xSeparation;
	}
	
	/**
	 * Berechnet die Distanz zwischem diesen und dem übergebenen Vektor
	 *  
	 * @param distanceTo Der Vektor zu dem die Distanz berechnet werden soll
	 * @return Die Distanz zwischen den beiden Vektoren
	 */
	public double distance(Vector2D distanceTo)
	{
		double ySeparation = distanceTo.y - y;
		double xSeparation = distanceTo.x - x;

		return Math.sqrt(ySeparation*ySeparation + xSeparation*xSeparation);
	}

	/**
	 * Kürzt den Vektor wenn nötig auf die maximale übergebene Länge und
	 * verändert ihn dabei
	 * 
	 * @param maxLength
	 *            maximale Länge des Vektors
	 * @return der ggf gekürzte Vektor
	 */
	public Vector2D cutMax(double maxLength)
	{
		// Wenn er zu lang ist
		if(length() > maxLength)
		{
			// Normalisieren um anteilig multiplizieren zu können
			normalizeToThis();
		
			// Anteilig multiplizieren
			mulToThis(maxLength);
		}
		
		return this;
	}
	
	/**
	 * Rotiert den Vektor um die angegebene Gradzahl (im Uhrzeiger) 
	 * 
	 * @param angle Grad um die der Vektor gedreht werden soll
	 * @return einen neuen Vektor
	 */
	public Vector2D rotateBy(double angle)
	{		
		double ca = Math.cos(degreeToRad(angle));
		double sa = Math.sin(degreeToRad(angle));
		
		return new Vector2D( (x * ca - y * sa),
							 (x * sa + y * ca));	
	}
	/**
	 * Rechnet Grad in Rad um
	 * @param degree Der Winkel in Grad
	 * @return Der Winkel in Rad (Bogenmaß)
	 */
	public double degreeToRad(double degree)
	{
		return (degree * (Math.PI / 180));
	}
	
	/**
	 * Wandelt den Vektor in einen entsprechenden Winkel um
	 * @return Der Radius in Rad
	 */
	public double getAngleInRad()
	{
		return Math.atan2(x, y);
	}
	
	/**
	 * Überschriebene toString Methode
	 * @return Den Vektor in der Form x|y
	 */
	public String toString()
	{
		return x + "|" + y;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

}
