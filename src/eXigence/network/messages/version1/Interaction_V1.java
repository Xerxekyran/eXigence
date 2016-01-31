package eXigence.network.messages.version1;

import java.util.Iterator;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import eXigence.network.messages.EInteractionType;
import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IInteraction;
import eXigence.util.math.Vector2D;

public class Interaction_V1 extends XMLMessage_V1 implements IInteraction
{
	private Vector<EInteractionType> activeInteractions = new Vector<EInteractionType>();
	private Vector2D viewDirection = new Vector2D(0,0);
	private Vector2D currentPosition = new Vector2D(0,0);

	@Override
	public Vector<EInteractionType> getActiveInteractions()
	{
		return activeInteractions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContent(Document xmlData) throws IllegalArgumentException
	{
		try 		
		{
			Element interactionMSG = xmlData.getRootElement().getChild("MessageContent");
			Element activeInteractionsElements = interactionMSG.getChild("ActiveInteractionType");
			
			Iterator<Element> it = activeInteractionsElements.getChildren().iterator();
			
			// Alle Interactions durchlaufen
			while(it.hasNext())
			{
				// Art des Elements
				String name = it.next().getName();

				// Interaction der Liste hinzufügen
				activeInteractions.add(Enum.valueOf(EInteractionType.class, name));												
			}

			// Aktuelle Position auslesen
			this.currentPosition.setX(Double.parseDouble(interactionMSG.getChild("CurrentPosition").getChildText("X")));			
			this.currentPosition.setY(Double.parseDouble(interactionMSG.getChild("CurrentPosition").getChildText("Y")));
			
			// ViewDirection auslesen
			this.viewDirection.setX(Double.parseDouble(interactionMSG.getChild("ViewDirection").getChildText("X")));			
			this.viewDirection.setY(Double.parseDouble(interactionMSG.getChild("ViewDirection").getChildText("Y")));
			

		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Received wrong xmlData for Interact Message: "+ e.toString());			
		}

	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.Interaction;
	}

	@Override
	public EMessageVersion getVersion()
	{
		return EMessageVersion.ONE;
	}

	@Override
	public Vector2D getViewDirection()
	{
		return viewDirection;
	}

	@Override
	public Vector2D getCurrentPosition()
	{
		return this.currentPosition;
	}

}
