package eXigence.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import eXigence.domain.ai.PathPlanner;
import eXigence.domain.ai.SteeringBehaviour;
import eXigence.domain.ai.goals.composite.GoalThink;
import eXigence.domain.map.WorldMap;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

@NamedQuery(name = "getAllCharacters", query = "SELECT ec "
		+ "FROM NonPlayerCharacter AS ec")
@Entity
public class NonPlayerCharacter extends ExigenceCharacter
{
	@Transient
	private SteeringBehaviour	steering	= null;

	@Transient
	private GoalThink			brain		= null;

	@Transient
	private PathPlanner			pathPlanner	= null;

	@Transient
	private boolean				posessed	= false;	// Angabe ob dieser

	// Character gerade von
	// einem Spieler
	// gesteuert wird

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public NonPlayerCharacter()
	{
		brain = new GoalThink(this);
	}

	public NonPlayerCharacter(WorldMap world)
	{
		steering = new SteeringBehaviour(world, this);
		brain = new GoalThink(this);
		pathPlanner = new PathPlanner(this, world);
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	@Override
	public void update(long calcTime)
	{
		// Wenn er gerade nicht von einem Spieler kontrolliert wird die KI
		// ausführen
		if (!posessed)
		{
			// Das gerade aktive ziel everfolgen
			brain.process();

			// Die aktuelle Bewegung berechnen und ausführen
			updateMovement(calcTime);
		}
	}

	/**
	 * Lässt die Bedürfnisse entsprechend steigen für einen updateZyklus (in der
	 * Simulation bestimmt wie lang dieser dauert)
	 */
	public void updateNecessities()
	{		
		setNecessitiyEnjoyment(getNecessitiyEnjoyment() + 1);
		setNecessitiyHunger(getNecessitiyHunger() + 2);
		setNecessitiyHygiene(getNecessitiyHygiene() + 2);
		setNecessitiySleep(getNecessitiySleep() + 3);		
		setNecessitiyFitness(getNecessitiyFitness() + 1);
		
		// Ereignisse aufgrund von den Werten überprüfen
		
		// Zu starker Harndrang?
		if(getNecessitiyStrangury() > 90)
		{
			LogWriter.getInstance().logToFile(LogLevel.Info, getFirstName() + ", " + getLastName() + " hat sich in die Hosen gemacht.");
			setNecessitiyHygiene((int)getMaxNecessitiy());
		}
		
		//  zu müde?
		if(getNecessitiySleep() > 90)
		{
			LogWriter.getInstance().logToFile(LogLevel.Info, getFirstName() + ", " + getLastName() + " schläft fast im stehen ein.");
			setNecessitiyEnjoyment((int)getMaxNecessitiy());
		}
	}

	/**
	 * Schaut nach, ob man von der aktuelle Position zu der übergebenen
	 * Zielpoisition gelangt
	 * 
	 * @param pos
	 *            Zielposition
	 * @return true wenn der npc direct zum Ziel laufen kann ohne gegen Gebäude
	 *         zu stoßen
	 */
	public boolean canWalkTo(Vector2D pos)
	{
		return this.homeSimulation.getMap().isPathObstructed(getPosition(),
				pos,
				boundingRadius);
	}

	/**
	 * Schaut nach ob man von Punkt a nach Punkt b kommt
	 * 
	 * @param a
	 *            Startpunkt
	 * @param b
	 *            Zielpunkt
	 * @return true wenn der npc direct zum Ziel laufen kann ohne gegen Gebäude
	 *         zu stoßen
	 */
	public boolean canWalkBetween(Vector2D a, Vector2D b)
	{
		return this.homeSimulation.getMap().isPathObstructed(a,
				b,
				boundingRadius);
	}

	@Override
	public boolean handleTelegram(Telegram tlg)
	{
		// Erst mal gucken ob die KI Logik die Nachricht abarbeiten kann
		if (brain.handleTelegram(tlg))
			return true;

		boolean handled = false;

		// Wenn nicht selber versuchen
		switch (tlg.getType())
		{
		
		// Das Gebäude kann verlassen werden
		case ExitingBuilding:

			// Wenn es der BEschäftigteneinngang war
			if(tlg.getExtraInfo() != null && 
					((PlayerExitsBuildingTelegram)tlg.getExtraInfo()).isInEmployeeEntrance())
			{
				((PlayerExitsBuildingTelegram)tlg.getExtraInfo()).getBuilding().exitBuildingAsEmployee();
			}
			else
			{
				((PlayerExitsBuildingTelegram)tlg.getExtraInfo()).getBuilding().exitBuilding();
			}
			
			// Wieder sichtbar machen
			setVisible(true);
			
			handled = true;
			break;
			
		default:
			handled = false;
		}

		return handled;
	}

	/**
	 * Bewegt den Character nach vorne, abhängig von der dauer der Berechnung
	 * ist der Schritt entsprechend groß
	 * 
	 * @param calcTime
	 *            benötigte Zeit für die Kalkulation (dauer der Bewegung)
	 */
	public void moveForward(long calcTime)
	{
		// Richtung normalisieren
		Vector2D desiredVector = getHeading().normalize();

		// In diese Richtung bewegen lassen
		moveInDirection(desiredVector, calcTime);
	}

	/**
	 * Bewegt den Character nach hinten, abhängig von der dauer der Berechnung
	 * ist der Schritt entsprechend groß
	 * 
	 * @param calcTime
	 *            benötigte Zeit für die Kalkulation (dauer der Bewegung)
	 */
	public void moveBackward(long calcTime)
	{
		// Richtung normalisieren und umkehren
		Vector2D desiredVector = getHeading().normalize().mul(-1.0);

		// In diese Richtung bewegen lassen
		moveInDirection(desiredVector, calcTime);
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	private void moveInDirection(Vector2D direction, long calcTime)
	{
		// Abhängig von der maximalen Geschwindigkeit den Vector verlängern
		direction.mulToThis(getMaxForce());

		// Die Masse berücksichtigen
		direction = direction.divide(mass);

		// Die Zeit berücksichtigen
		double tmp = calcTime / 1000.0;
		direction.mulToThis(tmp);

		// Geschwindigkeit nach oben begrenzen
		direction.cutMax(getMaxForce() * tmp);

		// position aktualisieren
		setPosition(getPosition().add(direction));
	}

	/**
	 * Aktualisiert die Position des NPC entsprechend seiner aktuellen
	 * Einstellungen
	 */
	private void updateMovement(long calcTime)
	{
		// Richtungsänderung berechnen lassen
		Vector2D force = steering.calculate();

		// Wenn keine Richtungsänderung benötigt wird, den NPC mit einer
		// Bremskraft belegen
		if (steering.getSteeringForce().isZero())
		{
			velocity.mulToThis(0.8);
		}

		// Die Beschleunigung berechnen
		Vector2D accel = force.divide(mass);

		// Die Zeit berücksichtigen
		double tmp = calcTime / 1000.0;
		accel.mulToThis(tmp);

		// Bewegung aktualisieren
		velocity.addToThis(accel);

		// Geschwindigkeit nach oben begrenzen
		velocity.cutMax(getMaxForce() * tmp);

		// position aktualisieren
		setPosition(getPosition().add(velocity));

		// Die Blickrichtung ist der normalisierte richtungsvector
		heading = velocity.normalize();

		// Wenn die Richtung verändert wurde muss die Blickrichtung wieder
		// normalisiert werden
		if (!velocity.isZero())
		{
			heading.normalizeToThis();
		}
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public SteeringBehaviour getSteering()
	{
		return steering;
	}

	public void setSteering(SteeringBehaviour steering)
	{
		this.steering = steering;
	}

	public GoalThink getBrain()
	{
		return brain;
	}

	public void setBrain(GoalThink brain)
	{
		this.brain = brain;
	}

	public PathPlanner getPathPlanner()
	{
		return pathPlanner;
	}

	public void setPathPlanner(PathPlanner pathPlanner)
	{
		this.pathPlanner = pathPlanner;
	}

	public boolean isPosessed()
	{
		return posessed;
	}

	public void setPosessed(boolean posessed)
	{
		this.posessed = posessed;
	}

}
