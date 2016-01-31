package eXigence.domain.entities;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import eXigence.domain.Telegram;
import eXigence.util.math.Vector2D;

/**
 * Ein Objekt welches optisch dargestellt werden kann
 * 
 * @author Lars George
 * 
 */
@MappedSuperclass
public abstract class VisibleEntity extends BaseEntity
{
	// Position des Objekts
	@Basic(fetch=FetchType.EAGER, optional=false)
	private Vector2D	position = new Vector2D(1.0,1.0);

	// Blickrichtung des Objekts
	@Basic
	private double headingX = 0.0;
	
	@Basic
	private double headingY = 0.0;
		
	@Transient
	protected Vector2D	heading = new Vector2D(0.0,0.0);
	
	@Basic
	protected String modelName = "";
	
	@Transient
	protected boolean isVisible = true;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public VisibleEntity(Vector2D position,
						 Vector2D heading,
						 String modelName)
	{	
		this.heading = heading;
		this.headingX = heading.getX();
		this.headingY = heading.getY();
		this.modelName = modelName;
		setPosition(position);
	}
	
	public VisibleEntity()
	{
		
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public boolean handleTelegram(Telegram tlg)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(long calcTime)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Überprüft ob das Entity sich an dieser Position befindet
	 * 
	 * @param position Zu überprüfende Position
	 * @return true wenn das Entity an dieser Position ist, ansonsten false
	 */
	public boolean isAtPosition(Vector2D position)
	{
		double tolerance = 10.0;
		
		return getPosition().distanceSquare(position) < tolerance * tolerance;   
	}
	
	/**
	 * Überprüft ob das Entity sich an dieser Position befindet
	 * 
	 * @param position Zu überprüfende Position
	 * @param tolerance der Tolleranzbereich für die Überprüfung
	 * @return true wenn das Entity an dieser Position ist, ansonsten false
	 */
	public boolean isAtPosition(Vector2D position, double tolerance)
	{
		return getPosition().distanceSquare(position) < tolerance * tolerance;   
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------

	public Vector2D getHeading()
	{
		heading.setX(getHeadingX());		
		heading.setY(getHeadingY());
		return heading;
	}

	public Vector2D getPosition()
	{
		return position;
	}

	public String getModelName()
	{
		return modelName;
	}

	public void setModelName(String modelName)
	{
		this.modelName = modelName;
	}

	public void setPosition(Vector2D position)
	{
		//System.out.println(modelName + " SETS " + position);
		this.position = position;
	}

	public void setHeading(Vector2D heading)
	{
		this.heading = heading;
		this.headingX = heading.getX();
		this.headingY = heading.getY();
	}

	public double getHeadingX()
	{
		return heading.getX();
	}
	
	public void setHeadingX(double headingX)
	{
		this.heading.setX(headingX);
	}
	
	public double getHeadingY()
	{
		return heading.getY();
	}

	public void setHeadingY(double headingY)
	{
		this.heading.setY(headingY);
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}	
	
	
}

