package eXigence.network.messages.version1;

import java.util.Collection;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.domain.NonPlayerCharacter;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IUpdateCharacters;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public class UpdateCharacters_V1 extends XMLMessage_V1 implements
		IUpdateCharacters
{
	private Collection<NonPlayerCharacter>	playerCharacters = null;
	
	private NonPlayerCharacter characters[] = null;

	@Override
	public void setCharacters(NonPlayerCharacter characters[])
	{
		this.characters = characters;
	}

	@Override
	public String toNetworkString()
	{
		// Grundgerüst erstellen
		Document doc = getXMLStructure(getMessageType());

		// Content entsprechend füllen
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		Element chars = new Element("Characters");
		
		// Wenn das Attribut nicht gesetzt wurde
		if(characters == null || playerCharacters == null)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error, "Message UpdateCharacters_V1 found a null pointer instead of an Array!!!!");
			return "";
		}
		
		// Alle Charactere berücksichtigen
		chars.setAttribute("count", Integer.toString(characters.length));		
		
		// Temporäre Element Objekte anlegen
		Element tmpChar = null;		
		Element curPos = null;
		Element destPos = null;
		boolean isOwnCharacter = false;
		
		for(int i = 0; i < characters.length; i ++)
		{			
			isOwnCharacter = false;
			
			// Ist es der Character des Spielers selber?
			Iterator<NonPlayerCharacter> it = playerCharacters.iterator();
			while(it.hasNext())
			{
				if(it.next().getID() == characters[i].getID())
				{
					isOwnCharacter = true;
					break;
				}
			}
			
			// Einzelne Characterdaten generieren
			tmpChar = new Element("Character");
			
			tmpChar.addContent(new Element("CharacterID").addContent(Integer.toString(characters[i].getID())));		
			
			if(isOwnCharacter)
			{
				tmpChar.addContent(new Element("FirstName").addContent(characters[i].getFirstName() ));
				tmpChar.addContent(new Element("LastName").addContent(characters[i].getLastName() ));			
				tmpChar.addContent(new Element("BirthDate").addContent(characters[i].getBirthDate() ));
				tmpChar.addContent(new Element("IsMale").addContent(Boolean.toString(characters[i].isMale()) ));
			}
			
			// Bedürfnisse
			Element necessity = new Element("Necessity");			
			necessity.addContent(new Element("Hunger").addContent(Integer.toString(characters[i].getNecessitiyHunger())));
			necessity.addContent(new Element("Strangury").addContent(Integer.toString(characters[i].getNecessitiyStrangury())));
			necessity.addContent(new Element("Enjoyment").addContent(Integer.toString(characters[i].getNecessitiyEnjoyment())));
			necessity.addContent(new Element("Hygiene").addContent(Integer.toString(characters[i].getNecessitiySleep())));
			necessity.addContent(new Element("Sleep").addContent(Integer.toString(characters[i].getNecessitiySleep())));
			necessity.addContent(new Element("Education").addContent(Integer.toString(characters[i].getNecessitiyEducation())));
			necessity.addContent(new Element("Fitness").addContent(Integer.toString(characters[i].getNecessitiyFitness())));
			necessity.addContent(new Element("Money").addContent(Integer.toString(characters[i].getNecessitiyMoney())));			
			tmpChar.addContent(necessity);
			
			// Wenn es der eigene Character ist, auch die Prioritäten übertragen
			if(isOwnCharacter)
			{
				Element priorities = new Element("Priorities");			
				priorities.addContent(new Element("Hunger").addContent(Integer.toString(characters[i].getPriorHunger())));
				priorities.addContent(new Element("Strangury").addContent(Integer.toString(characters[i].getPriorStrangury())));
				priorities.addContent(new Element("Enjoyment").addContent(Integer.toString(characters[i].getPriorEnjoyment())));
				priorities.addContent(new Element("Hygiene").addContent(Integer.toString(characters[i].getPriorSleep())));
				priorities.addContent(new Element("Sleep").addContent(Integer.toString(characters[i].getPriorSleep())));
				priorities.addContent(new Element("Education").addContent(Integer.toString(characters[i].getPriorEducation())));
				priorities.addContent(new Element("Fitness").addContent(Integer.toString(characters[i].getPriorFitness())));
				priorities.addContent(new Element("Money").addContent(Integer.toString(characters[i].getPriorMoney())));			
				tmpChar.addContent(priorities);
			}			
			
			tmpChar.addContent(new Element("ModelName").addContent(characters[i].getModelName()));
			
			// Blickrichtung
			tmpChar.addContent(new Element("ViewDirection").addContent(Float.toString((float)characters[i].getHeading().getAngleInRad())));
			
			// Aktuelle Position
			curPos = new Element("CurrentCenter");
			curPos.addContent(new Element("X").addContent(Integer.toString(((int)characters[i].getPosition().getX()))));
			curPos.addContent(new Element("Y").addContent(Integer.toString(((int)characters[i].getPosition().getY()))));
			tmpChar.addContent(curPos);

			// Aktuelles Ziel
			destPos = new Element("DestinationCenter");
			destPos.addContent(new Element("X").addContent(Integer.toString(((int)((NonPlayerCharacter)characters[i]).getSteering().getTarget().getX()))));
			destPos.addContent(new Element("Y").addContent(Integer.toString(((int)((NonPlayerCharacter)characters[i]).getSteering().getTarget().getY()))));
			tmpChar.addContent(destPos);
		
			// Die maximale Kraft die der Character aufbringen kann um sich for zu bewegen
			tmpChar.addContent(new Element("MaxVelocity").addContent(Integer.toString((int)characters[i].getMaxForce()) ));
			
			// Sichtbarkeit des Characters
			tmpChar.addContent(new Element("IsVisible").addContent(Boolean.toString(characters[i].isVisible() )));
			
			// Ob er gerade gesteuert wird oder nicht
			tmpChar.addContent(new Element("IsObsessed").addContent(Boolean.toString(characters[i].isPosessed() )));
			
			// Character an die Nachricht hängen
			chars.addContent(tmpChar);			
		}		
		
		msgContent.addContent(chars);

		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.UpdateCharacters;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}
	
	@Override
	public void setOwnCharacters(Collection<NonPlayerCharacter> playerCharacters)
	{
		this.playerCharacters = playerCharacters;		
	}

}
