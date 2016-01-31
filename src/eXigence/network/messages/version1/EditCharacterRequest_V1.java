package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;

import eXigence.domain.ExigenceCharacter;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IEditCharacterRequest;

public class EditCharacterRequest_V1 extends XMLMessage_V1 implements
		IEditCharacterRequest
{
	private ExigenceCharacter character = new ExigenceCharacter();
	
	@Override
	public ExigenceCharacter getNewCharacterData()
	{
		return this.character;
	}

	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 
		{
			Element characterElement = xmlData.getRootElement().getChild("MessageContent").getChild("Character");
			
			character.setID(Integer.parseInt(characterElement.getChildText("CharacterID")));
			
			character.setFirstName(characterElement.getChildText("FirstName"));
			character.setLastName(characterElement.getChildText("LastName"));
			
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
			throw new IllegalArgumentException("Received wrong xmlData for EditCharacterRequest_V1 Message: "+ e.toString());			
		}

	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.EditCharacterRequest;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
