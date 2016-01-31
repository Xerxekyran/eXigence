package eXigence.domain.entities;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import eXigence.util.math.Vector2D;

/**
 * Ein Objekt welches sich bewegen kann und demnach eine Poistion in der Welt hat
 * 
 * @author Lars George
 *
 */
@MappedSuperclass
public class MovingEntity extends VisibleEntity
{		
	// Aktuelle Bewegung
	@Transient
	protected Vector2D velocity = new Vector2D(0.0 ,0.0);
	
	// Die Masse des Objekts
	@Transient
	protected double mass = 1.0;
		
	// Maximale Drehgeschwindigkeit (Rad pro Sekunde)
	@Transient
	protected double maxTurnRate = 0.2;
	
	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public MovingEntity(Vector2D position,
						 Vector2D heading,
						 double mass,
						 double maxTurnRate,
						 String modelName)
	{
		// Konstruktor der Oberklasse ausführen
		super(position,heading, modelName);
		
		// Werte setzen
		this.mass = mass;
		this.maxTurnRate = maxTurnRate;
		this.velocity = new Vector2D(0.0, 0.0);
	}
	
	public MovingEntity()
	{
		
	}
	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public Vector2D getVelocity()
	{
		return velocity;
	}

	public double getMass()
	{
		return mass;
	}

	public double getMaxTurnRate()
	{
		return maxTurnRate;
	}
}
