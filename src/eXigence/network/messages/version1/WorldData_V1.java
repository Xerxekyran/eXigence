package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.domain.entities.VisibleEntity;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IWorldData;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;

public class WorldData_V1 extends XMLMessage_V1 implements IWorldData
{
	private VisibleEntity[] gameObjects = null;

	@Override
	public void setGameObjects(VisibleEntity[] gameObjects)
	{
		this.gameObjects = gameObjects;
	}

	@Override
	public String toNetworkString()
	{
		// Grundgerüst erstellen
		Document doc = getXMLStructure(getMessageType());

		// Content entsprechend füllen
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		Element world = new Element("World");
		
		// Weltnamen dummyhaft eintragen
		world.addContent(new Element("WorldName").addContent("TestWorld1"));
		
		// Wenn das Attribut nicht gesetzt wurde
		if(gameObjects == null )
		{
			LogWriter.getInstance().logToFile(LogLevel.Error, "Message WorldData_V1 found a null pointer instead of an Array!!!!");
			return "";
		}

		// Temporäre Element Objekte anlegen
		Element tmpGameObj = null;		
		Element curPos = null;
		Element size = null;
		// --------------------- Entities / Game Objects ------------------------------
		
		// Alle Entities berücksichtigen
		Element entities = new Element("GameObjects");
		entities.setAttribute("count", Integer.toString(gameObjects.length));
		
		for(int i = 0; i < gameObjects.length; i++)
		{			
			// Einzelne GameObject Daten erzeugen
			tmpGameObj = new Element("GameObject");
			
			tmpGameObj.addContent(new Element("Name").addContent(Integer.toString(gameObjects[i].getID())));
			
			tmpGameObj.addContent(new Element("ModelName").addContent(gameObjects[i].getModelName() ));
			
			// Aktuelle Position
			curPos = new Element("Center");
			curPos.addContent(new Element("X").addContent(Integer.toString((int)gameObjects[i].getPosition().getX())));
			curPos.addContent(new Element("Y").addContent(Integer.toString((int)gameObjects[i].getPosition().getY())));
			tmpGameObj.addContent(curPos);
			
			// Blickrichtung		
			//System.out.println(gameObjects[i].getHeadingX() + "__" + gameObjects[i].getHeadingY() + " ::" + gameObjects[i].getHeading());
			tmpGameObj.addContent(new Element("ViewDirection").addContent(Float.toString((float)gameObjects[i].getHeading().getAngleInRad())));
			
			// Die Größe des Objekts anhängen
			size = new Element("Size");
			if(gameObjects[i] instanceof ExigenceBuilding )
			{
				size.addContent(new Element("Width").addContent(Integer.toString( (int)((ExigenceBuilding)gameObjects[i]).getWidth())) );
				size.addContent(new Element("Length").addContent(Integer.toString( (int)((ExigenceBuilding)gameObjects[i]).getLength())) );
			}
			else
			{
				size.addContent(new Element("Width").addContent("0"));
				size.addContent(new Element("Length").addContent("0"));
			}
			tmpGameObj.addContent(size);
								
			// Inhalt anhängen
			entities.addContent(tmpGameObj);
		}
		
		// Entities in den Welddatensatz integrieren
		world.addContent(entities);	
		
		// Welddaten an die XML Nachricht hängen
		msgContent.addContent(world);

		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.WorldData;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

}
