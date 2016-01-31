package eXigence.core;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Singleton Klasse zum verwalten der Konfigurationsdatei
 * 
 * @author Lars George
 * 
 */
public class ConfigLoader
{
	// Singleton Instanz
	private static ConfigLoader	instance	= null;

	// XML-Document für die Konfigurationseinstellungen
	private Document			configDoc	= null;

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * Standardkonstruktor
	 */
	private ConfigLoader()
	{
		System.out.println("[ConfigLoader]: Loading eXigence-configuration [config.xml]");
		
		// XML-Builder Instanz holen
		SAXBuilder xmlBuilder = new SAXBuilder();

		try
		{
			// Versuchen die Konfigurationsdatei zu lesen
			configDoc = xmlBuilder.build(new File("config.xml"));
		}
		catch (JDOMException e)
		{
			System.out.println("[ConfigLoader]: Failed loading the config.xml file [Wrong Format]: "+ e.toString());
		}
		catch (IOException e)
		{
			System.out.println("[ConfigLoader]: Failed loading the config.xml file [IO Error]: " + e.toString());
		}
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------

	/**
	 * Methode zum lesen von Double-Kofigurationsvariablen
	 * 
	 * @param variableName
	 *            Name der Variable in der Konfigurationsdatei
	 * @return Den Wert der angegebenen Variable zu einem Double gecastet
	 * @throws NumberFormatException
	 *             Wird geschmissen wenn der Cast nicht erfolgreich verlaufen
	 *             ist
	 * @throws NoSuchElementException
	 *             Wenn es keinen Eintrag für diese Variable in der
	 *             Konfigurationsdatei gibt
	 */
	public double getDouble(String variableName) throws NumberFormatException,
			NoSuchElementException
	{
		// Wert entsprechend gecastet zurück geben
		return Double.parseDouble(getConfigElement(variableName).getText());
	}

	/**
	 * Methode zum lesen von Integer-Kofigurationsvariablen
	 * 
	 * @param variableName
	 *            Name der Variable in der Konfigurationsdatei
	 * @return Den Wert der angegebenen Variable zu einem Integer gecastet
	 * @throws NumberFormatException
	 *             Wird geschmissen wenn der Cast nicht erfolgreich verlaufen
	 *             ist
	 * @throws NoSuchElementException
	 *             Wenn es keinen Eintrag für diese Variable in der
	 *             Konfigurationsdatei gibt
	 */
	public int getInt(String variableName) throws NumberFormatException,
			NoSuchElementException
	{
		// Wert entsprechend gecastet zurück geben
		return Integer.parseInt(getConfigElement(variableName).getText());
	}

	/**
	 * Methode zum lesen von String-Kofigurationsvariablen
	 * 
	 * @param variableName
	 *            Name der Variable in der Konfigurationsdatei
	 * @return Den Wert der angegebenen Variable
	 * @throws NoSuchElementException
	 *             Wenn es keinen Eintrag für diese Variable in der
	 *             Konfigurationsdatei gibt
	 */
	public String getString(String variableName) throws NoSuchElementException
	{
		// Wert entsprechend zurück geben
		return getConfigElement(variableName).getText();
	}

	/**
	 * Gibt ein XML Element zurück welches sich im Root befinden muss
	 * @param name Bezeichner des XML Elements
	 * @return Referenz auf das entsprechende Element in der XML Struktur
	 * @throws NoSuchElementException Wenn zu dem angegebenen Namen kein Element gefunden werden konnte
	 */
	public Element getConfigElement(String name) throws NoSuchElementException
	{
		// Entsprechendes Element aus der XML-Struktur lesen und auf null prüfen
		Element el = configDoc.getRootElement().getChild(name);
		if (el == null)
			throw new NoSuchElementException(
					"No such element in configuration file [ " + name + " ]");

		return el;
	}
	
	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	/**
	 * Getter für die Singleton Instanz
	 */
	public static ConfigLoader getInstance()
	{
		if (instance == null)
			instance = new ConfigLoader();

		return instance;
	}
}
