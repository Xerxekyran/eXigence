package eXigence.network.messages.version1;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import eXigence.network.messages.EMessageType;
import eXigence.network.messages.EMessageVersion;
import eXigence.network.messages.IStatistics;

public class Statistics_V1 extends XMLMessage_V1 implements IStatistics
{
	private int restaurantAsClient;
	private int hotelAsClient;
	private int bathAsClient;
	private int cinemaAsClient;
	private int gymAsClient;
	private int libraryAsClient;
	private int policeAsClient;
	private int bankAsClient;
	private int hospitalAsClient;
	private int churchAsClient;
	private int dixiAsClient;
	
	private int restaurantAsWorker;
	private int hotelAsWorker;
	private int bathAsWorker;
	private int cinemaAsWorker;
	private int gymAsWorker;
	private int libraryAsWorker;
	private int policeAsWorker;
	private int bankAsWorker;
	private int hospitalAsWorker;
	private int churchAsWorker;
	
	@Override
	public String toNetworkString()
	{
		// Grundgerüst erstellen
		Document doc = getXMLStructure(getMessageType());

		// Content entsprechend füllen
		Element msgContent = doc.getRootElement().getChild("MessageContent");
		Element statistics = new Element("Statistics");
		
		Element clientsAt = new Element("ClientsAt");		
		clientsAt.addContent(new Element("Restaurant").addContent(Integer.toString(restaurantAsClient)));		
		clientsAt.addContent(new Element("Hotel").addContent(Integer.toString(hotelAsClient)));
		clientsAt.addContent(new Element("Bath").addContent(Integer.toString(bathAsClient)));
		clientsAt.addContent(new Element("Cinema").addContent(Integer.toString(cinemaAsClient)));
		clientsAt.addContent(new Element("Gym").addContent(Integer.toString(gymAsClient)));
		clientsAt.addContent(new Element("Library").addContent(Integer.toString(libraryAsClient)));
		clientsAt.addContent(new Element("Police").addContent(Integer.toString(policeAsClient)));
		clientsAt.addContent(new Element("Bank").addContent(Integer.toString(bankAsClient)));
		clientsAt.addContent(new Element("Hospital").addContent(Integer.toString(hospitalAsClient)));
		clientsAt.addContent(new Element("Church").addContent(Integer.toString(churchAsClient)));
		clientsAt.addContent(new Element("Dixi").addContent(Integer.toString(dixiAsClient)));
		statistics.addContent(clientsAt);

		Element workersAt = new Element("WorkersAt");		
		workersAt.addContent(new Element("Restaurant").addContent(Integer.toString(restaurantAsWorker)));		
		workersAt.addContent(new Element("Hotel").addContent(Integer.toString(hotelAsWorker)));
		workersAt.addContent(new Element("Bath").addContent(Integer.toString(bathAsWorker)));
		workersAt.addContent(new Element("Cinema").addContent(Integer.toString(cinemaAsWorker)));
		workersAt.addContent(new Element("Gym").addContent(Integer.toString(gymAsWorker)));
		workersAt.addContent(new Element("Library").addContent(Integer.toString(libraryAsWorker)));
		workersAt.addContent(new Element("Police").addContent(Integer.toString(policeAsWorker)));
		workersAt.addContent(new Element("Bank").addContent(Integer.toString(bankAsWorker)));
		workersAt.addContent(new Element("Hospital").addContent(Integer.toString(hospitalAsWorker)));
		workersAt.addContent(new Element("Church").addContent(Integer.toString(churchAsWorker)));
		statistics.addContent(workersAt);
		
		msgContent.addContent(statistics);

		XMLOutputter xmlOut = new XMLOutputter();
		return xmlOut.outputString(doc);
	}

	@Override
	public EMessageType getMessageType()
	{
		return EMessageType.Statistics;
	}

	@Override
	public EMessageVersion getVersion()
	{		
		return EMessageVersion.ONE;
	}

	@Override
	public void setClientsAt(	int restaurant,
								int hotel,
								int bath,
								int cinema,
								int gym,
								int library,
								int police,
								int bank,
								int hospital,
								int church,
								int dixi)
	{
		this.restaurantAsClient = restaurant;
		this.hotelAsClient = hotel;
		this.bathAsClient = bath;
		this.cinemaAsClient = cinema;
		this.gymAsClient = gym;
		this.libraryAsClient = library;
		this.policeAsClient = police;
		this.bankAsClient = bank;
		this.hospitalAsClient = hospital;
		this.churchAsClient = church;
		this.dixiAsClient = dixi;		
	}

	@Override
	public void setWorkersAt(	int restaurant,
								int hotel,
								int bath,
								int cinema,
								int gym,
								int library,
								int police,
								int bank,
								int hospital,
								int church)
	{
		this.restaurantAsWorker = restaurant;
		this.hotelAsWorker = hotel;
		this.bathAsWorker = bath;
		this.cinemaAsWorker = cinema;
		this.gymAsWorker = gym;
		this.libraryAsWorker = library;
		this.policeAsWorker = police;
		this.bankAsWorker = bank;
		this.hospitalAsWorker = hospital;
		this.churchAsWorker = church;		
	}

}
