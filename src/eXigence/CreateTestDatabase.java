package eXigence;

import java.io.File;
import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import eXigence.core.Simulation;
import eXigence.domain.NonInteractiveObject;
import eXigence.domain.NonPlayerCharacter;
import eXigence.domain.PlayerProfile;
import eXigence.domain.entities.EntityManager;
import eXigence.domain.entities.buildings.EBuildingTypes;
import eXigence.domain.entities.buildings.ExigenceBuilding;
import eXigence.persistence.dao.DAOFactory;
import eXigence.persistence.dao.map.IExigenceMapDAO;
import eXigence.persistence.dao.player.IPlayerProfileDAO;
import eXigence.util.LogWriter;
import eXigence.util.LogWriter.LogLevel;
import eXigence.util.math.Vector2D;

/**
 * Klasse zur Installation des Datenbankschemas
 * 
 * @author Lars George
 * 
 */
public class CreateTestDatabase
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String user = "cypork";
		String pw = "!!pork1";
		// alle Tabellen aus der Datenbank löschen
		try
		{
			// Treiber laden
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			Connection con = null;
			// Schema löschen und neu anlegen
			con = DriverManager.getConnection("jdbc:mysql://localhost/",
					user,
					pw);
			con.createStatement().execute("DROP DATABASE IF EXISTS "
					+ "exigence");
			con.createStatement().execute("CREATE DATABASE " + "exigence");
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Persistenzframework testen
		DAOFactory daoFactory = DAOFactory.getInstance();
		IPlayerProfileDAO playerProfilDao = (IPlayerProfileDAO) daoFactory.getDaoForClass(IPlayerProfileDAO.class);
		IExigenceMapDAO mapDao = (IExigenceMapDAO) daoFactory.getDaoForClass(IExigenceMapDAO.class);

		// Neues Profil mit 3 Charakteren hinzufügen
		ArrayList<NonPlayerCharacter> chars = new ArrayList<NonPlayerCharacter>();

		Simulation tmpSim = new Simulation(true);

		NonPlayerCharacter muh = null;

		try
		{
			muh = EntityManager.getInstance().createNewNPC("MuhChar",
					"Raguse",
					tmpSim);
			muh.setFitness(50);
			muh.setPriorEducation(2);
			muh.setPriorEnjoyment(3);
			muh.setPriorFitness(4);
			muh.setPriorHunger(1);
			muh.setPriorHygiene(4);
			muh.setPriorMoney(2);
			muh.setPriorSleep(6);
			muh.setPriorStrangury(2);
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}

		chars.add(muh);

		PlayerProfile p1 = new PlayerProfile("Muh", "muh", "muhu@mail.com",
				true, chars);

		playerProfilDao.saveNewPlayerProfil(p1);
		

		try
		{
					
			// Gebäude einladen
			Collection<ExigenceBuilding> buildings = loadBuildingsFromXMLFile("worldMap.xml", tmpSim);
			
			// Dem Simulationsobjekt übergeben
			Iterator<ExigenceBuilding> it = buildings.iterator();
			while(it.hasNext())
			{
				tmpSim.getMap().addBuilding(it.next());
			}
			
			// Non-interactive-objects einladen
			Collection<NonInteractiveObject> nio = loadNonInteractiveBuildingsFromXMLFile("worldMap.xml", tmpSim);
			
			// Dem Simulationsobjekt übergeben
			Iterator<NonInteractiveObject> it2 = nio.iterator();
			while(it2.hasNext())
			{
				tmpSim.getMap().addNonInteractiveObjekt(it2.next());
			}
			
			// Karte aus der XML-Datei lesen
			tmpSim.getMap().loadMapDataFromXMLFile("worldMap.xml");

			mapDao.saveNewWorldMap(tmpSim.getMap());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		tmpSim.setAbort(true);

	}
	
	@SuppressWarnings("unchecked")
	private static Collection<ExigenceBuilding> loadBuildingsFromXMLFile(String filename, Simulation sim)
	{
		Collection<ExigenceBuilding> retVal = new ArrayList<ExigenceBuilding>();
		
		// XML-Builder Instanz holen
		SAXBuilder xmlBuilder = new SAXBuilder();
		Document xmlMapDoc = null;

		try
		{
			// Versuchen die Konfigurationsdatei zu lesen
			xmlMapDoc = xmlBuilder.build(new File(filename));
			
			// Alle Knoten hinzufügen
			Element buildings = xmlMapDoc.getRootElement().getChild("Buildings");
			Iterator<Element> it = buildings.getChildren().iterator();
			Element tmpEl = null;
			while (it.hasNext())
			{
				tmpEl = it.next();
				
				System.out.println(EBuildingTypes.valueOf(tmpEl.getChildText("Type")) + ":: " + Double.parseDouble(tmpEl.getChildText("X")) + " | " + Double.parseDouble(tmpEl.getChildText("Y")) );
				
				retVal.add(EntityManager.getInstance().createNewBuilding
						(
								new Vector2D
								(
										Double.parseDouble(tmpEl.getChildText("X")), 
										Double.parseDouble(tmpEl.getChildText("Y"))
								),
								new Vector2D(0, 0),
								EBuildingTypes.valueOf(tmpEl.getChildText("Type")),
								sim.getMap())
						);
			}
			
			
		}
		catch (Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error, "Error while loading Buildings from the XML-File: "+ e.toString());
		}
			
		return retVal;
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<NonInteractiveObject> loadNonInteractiveBuildingsFromXMLFile(String filename, Simulation sim)
	{
		Collection<NonInteractiveObject> retVal = new ArrayList<NonInteractiveObject>();
		
		// XML-Builder Instanz holen
		SAXBuilder xmlBuilder = new SAXBuilder();
		Document xmlMapDoc = null;

		try
		{
			// Versuchen die Konfigurationsdatei zu lesen
			xmlMapDoc = xmlBuilder.build(new File(filename));
			
			// Alle Knoten hinzufügen
			Element buildings = xmlMapDoc.getRootElement().getChild("NonInteractiveObjects");
			Iterator<Element> it = buildings.getChildren().iterator();
			Element tmpEl = null;
			while (it.hasNext())
			{
				tmpEl = it.next();				
				
				retVal.add(EntityManager.getInstance().createNewNonInteractiveObject
						(
								new Vector2D
								(
										Double.parseDouble(tmpEl.getChildText("X")), 
										Double.parseDouble(tmpEl.getChildText("Y"))
								),
								new Vector2D								
								(
										Double.parseDouble(tmpEl.getChildText("ViewDirectX")), 
										Double.parseDouble(tmpEl.getChildText("ViewDirectY"))
								),
								tmpEl.getChildText("Name"),
								sim.getMap())
						);
			}
			
			
		}
		catch (Exception e)
		{
			LogWriter.getInstance().logToFile(LogLevel.Error, "Error while loading non-interactive-objects from the XML-File: "+ e.toString());
		}
			
		return retVal;
	}
}
