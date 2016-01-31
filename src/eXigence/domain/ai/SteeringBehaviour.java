package eXigence.domain.ai;

import eXigence.core.ConfigLoader;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.map.WorldMap;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

/**
 * Kapselt das Bewegungsverhalten von sich bewegenden Objekten
 * 
 * @author Lars George
 * 
 */
public class SteeringBehaviour
{
	// Die Simulationswelt
	private WorldMap		world			= null;

	// Der Besitzer dieses Bewegungsverhaltens
	private NonPlayerCharacter	owner			= null;

	// Der Bewegungsvektor, welches als Summe aller Kräfte der aktiven
	// Bewegungsmuster berechnet wurde
	private Vector2D		steeringForce	= new Vector2D(0,0);

	// Ziel (für seek und arrive notwendig)
	private Vector2D		target			= new Vector2D(0,0);

	// Multiplikatoren zum gewichten der einzelnen Verhalten untereinander
	private double			weightSeparation;
	private double			weightSeek;
	private double			weightArrive;

	// Radius in dem andere Entities beachtet werden (für seperation benötigt)
	private double			viewDistance;

	// Länge der "Fühler" um Collisionen mit den Wänden zu berechnen
	private double			wallDetectionFeelerLength;

	// Werte zur (de)aktivierung der jeweiligen Verhalten
	private boolean			isSeeking		= false;
	private boolean			isArriving		= false;
	private boolean			isSeperating	= false;
	private boolean			isWallAvoiding	= false;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------

	/**
	 * Konstruktor mit den minimal nötigen Angaben
	 * 
	 * @param owner
	 *            Besitzer dieses Bewegungsverhaltens
	 */
	public SteeringBehaviour(WorldMap world, NonPlayerCharacter owner)
	{
		this.owner = owner;
		this.world = world;

		// Temporär den Loader zwischenspeichern
		ConfigLoader cfgLoad = ConfigLoader.getInstance();

		try
		{
			// Werte entsprechend der Konfigurationsdatei setzen
			this.weightArrive = cfgLoad.getDouble("SeperationWeight");
			this.weightSeek = cfgLoad.getDouble("SeekWeight");
			this.weightSeparation = cfgLoad.getDouble("ArriveWeight");
			this.viewDistance = cfgLoad.getDouble("ViewDistance");
			this.wallDetectionFeelerLength = cfgLoad.getDouble("WallDetectionFeelerLength");
		}
		catch (Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error,
					"Could not read all Values from config.xml needed for the SteeringBehaviour, using default Values. "
							+ e.toString());

			// Wenn es einen Fehler beim lesen gab, Standardwerte setzen
			this.weightArrive = 1.0;
			this.weightSeek = 0.5;
			this.weightSeparation = 10.0;
			this.viewDistance = 15.0;
			this.wallDetectionFeelerLength = 25.0;
		}

	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * 
	 * @return Der Kraftvektor welcher sich aus den SteeringBehaviour ergibt
	 */
	public Vector2D calculate()
	{
		// Vector erst einmal zurück auf 0 setzen
		steeringForce.setZero();

		// Prioritätenabhängig die Kräfte berechnen und zusammenrechnen
		steeringForce = calculatePrioritized();		
		
		return steeringForce;
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	/**
	 * Ruft jedes aktive SteeringBehaviour auf und berechnet daraus anteilig die
	 * Gesamtkraft (nach oben hin begrenzt)
	 * 
	 * @return Gesamtkraftvektor der SteeringBehaviour
	 */
	private Vector2D calculatePrioritized()
	{
		Vector2D force = new Vector2D(0, 0);

		// Wenn Seeking aktiviert ist
		if (isSeeking())
		{
			// Gewichtete Addierung der Kraft um im Seeking-Verhalten zum Ziel zu kommen
			force = seek(target).mul(weightSeek);

			// Wenn kein Platz mehr für weitere Krafteinwirkungen ist, abbrechen
			if (!accumulateForce(steeringForce, force))
				return steeringForce;
		}

		// Wenn Arriving aktiviert ist
		if (isArriving())
		{
			// Gewichtete Addierung der Kraft um im Arriving-Verhalten zum Ziel zu kommen
			force = arrive(target).mul(weightArrive);

			// Wenn kein Platz mehr für weitere Krafteinwirkungen ist, abbrechen
			if (!accumulateForce(steeringForce, force))
				return steeringForce;
		}
		
		return steeringForce;
	}

	/**
	 * Berechnet wieviel "Kraft" noch übrig ist und und addiert ggf forceToAdd
	 * zu currentForce.
	 * 
	 * @param currentForce
	 *            Momentane Kraft auf die drauf addiert werden soll
	 * @param forceToAdd
	 *            Die zu addierende Kraft
	 * @return ob noch Kraft übrig ist für weitere Kräfte
	 */
	private boolean accumulateForce(Vector2D currentForce, Vector2D forceToAdd)
	{
		// Wieviel wurde shcon verbraucht
		double magnitudeSoFar = currentForce.length();

		// Wieviel noch übrig ist (von der maximal möglichen)
		double magnitudeRemaining = owner.getMaxForce() - magnitudeSoFar;

		// Wenn kein Platz mehr ist false zurück geben
		if (magnitudeRemaining <= 0.0)
			return false;

		// Die "Menge" der zu addierenden Kraft berechnen
		double magnitudeToAdd = forceToAdd.length();

		// So viel Kraft hinzu addieren wie möglich
		if (magnitudeToAdd < magnitudeRemaining)
		{			
			currentForce.addToThis(forceToAdd);
		}
		else
		{			
			// Anteilhaft addieren
			currentForce.addToThis(forceToAdd.normalize().mul(magnitudeToAdd));
		}

		return true;
	}

	/**
	 * Berechnet die benötigte Richtungsänderung (von der Momentan) um zu dem
	 * Ziel zu navigieren
	 * 
	 * @param target
	 *            Ziel Koordinate
	 * @return Vektor mit der maximal möglichen Länge um zum angegebenen Ziel zu
	 *         gelangen
	 */
	private Vector2D seek(Vector2D target)
	{
		// Differenz zum Ziel berechnen und anschließend normalisieren
		Vector2D desiredVector = target.sub(owner.getPosition()).normalize();

		// Abhängig von der maximalen Geschwindigkeit den Vector verlängern
		desiredVector.mulToThis(owner.getMaxForce());

		// Momentane Bewegungsrichtung davon abziehen um die Richtungsändeurng
		// zu erhalten
		return desiredVector.subToThis(owner.getVelocity());
	}

	/**
	 * Berechnet die benötigte Richtungsveränderung (von der momentanen) um zum
	 * Ziel zu navigieren im Sinne des Arrive Verhaltens
	 * 
	 * @param target
	 *            Ziel Koordinaten
	 * @return Vektor mit der maximal möglichen Länge um zum angegebenen Ziel zu
	 *         "arriven"
	 */
	private Vector2D arrive(Vector2D target)
	{
		// Den Vector von der Position aus zum Ziel berechnen
		Vector2D toTarget = target.sub(owner.getPosition());
		
		// Länge bis zum Ziel berechnen
		double dist = toTarget.length();
		
		if(dist > 0)
		{
			// Geschwindigkeit berechnen (Testwert: 0.6)
			double speed = dist / 0.6;
			
			// Nicht über das Maximum kommen
			speed = Math.min(speed, owner.getMaxForce());
			
			// benötigten Richtungsänderung berechnen
			Vector2D desiredVelocity = toTarget.mul(speed / dist);
			
			// Momentane Bewegungsrichtung davon abziehen um die Richtungsändeurng
			// zu erhalten
			return desiredVelocity.subToThis(owner.getVelocity());
		}

		// Wenn keine Bewegung notwenidg ist
		return new Vector2D(0,0);
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public void seekOn()
	{
		this.isSeeking = true;
	}

	public void seekOff()
	{
		this.isSeeking = false;
	}

	public void arriveOn()
	{
		this.isArriving = true;
	}

	public void arriveOff()
	{
		this.isArriving = false;
	}

	public void seperateOn()
	{
		this.isSeperating = true;
	}

	public void seperateOff()
	{
		this.isSeperating = false;
	}

	public void wallAvoidenceOn()
	{
		this.isWallAvoiding = true;
	}

	public void wallAvoidenceOff()
	{
		this.isWallAvoiding = false;
	}

	public boolean isSeeking()
	{
		return isSeeking;
	}

	public boolean isArriving()
	{
		return isArriving;
	}

	public boolean isSeperating()
	{
		return isSeperating;
	}

	public boolean isWallAvoiding()
	{
		return isWallAvoiding;
	}
	
	public Vector2D getSteeringForce()
	{
		return steeringForce;
	}

	public Vector2D getTarget()
	{
		if(owner.isPosessed())
			return owner.getPosition();
		
		return target;
	}

	public void setTarget(Vector2D target)
	{
		this.target = target;
	}
	
	

}
