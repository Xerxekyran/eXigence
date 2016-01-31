package eXigence.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import eXigence.core.Simulation;
import eXigence.domain.entities.MovingEntity;

@MappedSuperclass
public class ExigenceCharacter extends MovingEntity
{	
	// Referenz zu der Simulation, in der der Character lebt
	@Transient
	protected Simulation homeSimulation;
	
	// Der Radius des Kreises, den das Objekt einnimmt
	@Transient
	protected double boundingRadius = 10;
	
	@Column(name = "firstName", nullable = false, length = 30)
	private String	firstName	= ""; 
	
	@Column(name = "lastName", nullable = false, length = 30)
	private String lastName = "";
	
	@Basic
	private String birthDate = "";
	
	@Basic
	private boolean	isMale		= false;
	
	@Basic
	private int Money = 100;
	
	@Basic
	private int Fitness = 50;
	
	// Prioritäten:
	@Basic
	private int priorHunger = 0;
	
	@Basic
	private int priorStrangury = 0;
	
	@Basic
	private int priorEnjoyment = 0;
	
	@Basic
	private int priorHygiene = 0;
	
	@Basic
	private int priorSleep = 0;
	
	@Basic
	private int priorEducation = 0;
	
	@Basic
	private int priorFitness = 0;
	
	@Basic
	private int priorMoney = 0;
	
	// Aktuelle Dringlichkeit der Bedürfnisse	
	@Basic
	private int necessitiyHunger = 50;
	
	@Basic
	private int necessitiyStrangury = 50;
	
	@Basic
	private int necessitiyEnjoyment = 50;
	
	@Basic
	private int necessitiyHygiene = 50;
	
	@Basic
	private int necessitiySleep = 50;
	
	@Basic
	private int necessitiyEducation = 50;
	
	@Basic
	private int necessitiyFitness = 50;
	
	@Basic
	private int necessitiyMoney = 50;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	public ExigenceCharacter()
	{
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	private int getNecessitiyValueInRange(int value)
	{
		if(value < 0)
			value = 0;
		else if(value > getMaxNecessitiy())
			value = (int)getMaxNecessitiy();
		
		return value;
	}
	
	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------	

	public void setMale(boolean isMale)
	{
		this.isMale = isMale;
	}

	public boolean isMale()
	{
		return isMale;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public int getPriorHunger()
	{
		return priorHunger;
	}

	public void setPriorHunger(int priorHunger)
	{
		this.priorHunger = priorHunger;
	}

	public int getPriorStrangury()
	{
		return priorStrangury;
	}

	public void setPriorStrangury(int priorStrangury)
	{
		this.priorStrangury = priorStrangury;
	}

	public int getPriorEnjoyment()
	{
		return priorEnjoyment;
	}

	public void setPriorEnjoyment(int priorEnjoyment)
	{
		this.priorEnjoyment = priorEnjoyment;
	}

	public int getPriorHygiene()
	{
		return priorHygiene;
	}

	public void setPriorHygiene(int priorHygiene)
	{
		this.priorHygiene = priorHygiene;
	}

	public int getPriorSleep()
	{
		return priorSleep;
	}

	public void setPriorSleep(int priorSleep)
	{
		this.priorSleep = priorSleep;
	}

	public int getPriorEducation()
	{
		return priorEducation;
	}

	public void setPriorEducation(int priorEducation)
	{
		this.priorEducation = priorEducation;
	}

	public int getPriorFitness()
	{
		return priorFitness;
	}

	public void setPriorFitness(int priorFitness)
	{
		this.priorFitness = priorFitness;
	}

	public int getPriorMoney()
	{
		return priorMoney;
	}

	public void setPriorMoney(int priorMoney)
	{
		this.priorMoney = priorMoney;
	}

	public int getNecessitiyHunger()
	{
		return necessitiyHunger;
	}

	public void setNecessitiyHunger(int necessitiyHunger)
	{		
		this.necessitiyHunger = getNecessitiyValueInRange(necessitiyHunger);
	}

	public int getNecessitiyStrangury()
	{
		return necessitiyStrangury;
	}

	public void setNecessitiyStrangury(int necessitiyStrangury)
	{
		this.necessitiyStrangury = getNecessitiyValueInRange(necessitiyStrangury);
	}

	public int getNecessitiyEnjoyment()
	{
		return necessitiyEnjoyment;
	}

	public void setNecessitiyEnjoyment(int necessitiyEnjoyment)
	{
		this.necessitiyEnjoyment = getNecessitiyValueInRange(necessitiyEnjoyment);
	}

	public int getNecessitiyHygiene()
	{
		return necessitiyHygiene;
	}

	public void setNecessitiyHygiene(int necessitiyHygiene)
	{
		this.necessitiyHygiene = getNecessitiyValueInRange(necessitiyHygiene);
	}

	public int getNecessitiySleep()
	{
		return necessitiySleep;
	}

	public void setNecessitiySleep(int necessitiySleep)
	{
		this.necessitiySleep = getNecessitiyValueInRange(necessitiySleep);
	}

	public int getNecessitiyEducation()
	{
		return necessitiyEducation;
	}

	public void setNecessitiyEducation(int necessitiyEducation)
	{
		this.necessitiyEducation = getNecessitiyValueInRange(necessitiyEducation);
	}

	public int getNecessitiyFitness()
	{
		return necessitiyFitness;
	}

	public void setNecessitiyFitness(int necessitiyFitness)
	{
		this.necessitiyFitness = getNecessitiyValueInRange(necessitiyFitness);
	}

	public int getNecessitiyMoney()
	{
		return necessitiyMoney;
	}

	public void setNecessitiyMoney(int necessitiyMoney)
	{
		this.necessitiyMoney = getNecessitiyValueInRange(necessitiyMoney);
	}

	public String getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(String birthDate)
	{
		this.birthDate = birthDate;
	}

	public Simulation getHomeSimulation()
	{
		return homeSimulation;
	}

	public void setHomeSimulation(Simulation homeSimulation)
	{
		this.homeSimulation = homeSimulation;
	}
	
	public static int getMaxMoney()
	{
		return 100;
	}
	
	public static double getMaxNecessitiy()
	{
		return 100;
	}
	
	public static double getMaxPriority()
	{
		return 10;
	}
	
	public int getMoney()
	{
		return Money;
	}

	public void setMoney(int money)
	{
		Money = money;
	}

	public int getFitness()
	{
		return Fitness;
	}

	public void setFitness(int fitness)
	{
		Fitness = fitness;
	}

	public double getMaxForce()
	{
		return 200 + getFitness();
	}
	
}
