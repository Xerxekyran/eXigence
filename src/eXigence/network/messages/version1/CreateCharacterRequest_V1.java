package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;

import eXigence.domain.ExigenceCharacter;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.ICreateCharacterRequest;

public class CreateCharacterRequest_V1 implements ICreateCharacterRequest
{
	private ExigenceCharacter character = new ExigenceCharacter();
	
	@Override
	public ExigenceCharacter getCharacter()
	{
		return character;
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 
		{
			//<Priorities><Hunger>7</Hunger><Strangury>5</Strangury><Enjoyment>4</Enjoyment><Hygiene>3</Hygiene><Sleep>6</Sleep><Education>1</Education><Fitness>5</Fitness><Money>9</Money></Priorities><ModelName>spielfigur</ModelName></Character></MessageContent></Message></div>
			
			Element characterElement = xmlData.getRootElement().getChild("MessageContent").getChild("Character");
				
			character.setFirstName(characterElement.getChildText("FirstName"));
			character.setLastName(characterElement.getChildText("LastName"));
			if(characterElement.getChildText("IsMale").toLowerCase().equals("true"))
				character.setMale(true);
			else
				character.setMale(false);
			
			character.setModelName(characterElement.getChildText("ModelName"));
			
			// Priorities auslesen
			Element priorities = characterElement.getChild("Priorities"); 
			
			character.setPriorHunger(Integer.parseInt(priorities.getChildText("Hunger")));
			character.setPriorStrangury(Integer.parseInt(priorities.getChildText("Strangury")));
			character.setPriorEnjoyment(Integer.parseInt(priorities.getChildText("Enjoyment")));
			character.setPriorHygiene(Integer.parseInt(priorities.getChildText("Hygiene")));
			character.setPriorSleep(Integer.parseInt(priorities.getChildText("Sleep")));
			character.setPriorEducation(Integer.parseInt(priorities.getChildText("Education")));
			character.setPriorFitness(Integer.parseInt(priorities.getChildText("Fitness")));
			character.setPriorMoney(Integer.parseInt(priorities.getChildText("Money")));										
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Received wrong xmlData for CreateCharacterRequest_V1 Message: "+ e.toString());			
		}
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.CreateCharacterRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
