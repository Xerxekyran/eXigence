package eXigence.util;

import java.io.File;
import java.io.FileOutputStream;

import eXigence.core.ConfigLoader;

/**
 * Klasse zum schreiben eines Logfiles (). Unterstützt werden drei LogLevel
 * (Error, Info, Warn, Debug). Die Ausgaben werden je nach gesetzten LogLevel
 * gefiltert. Die Klasse ist als Singleton implementiert
 * 
 * @author Lars George
 * @version 2.1 - HTML
 * 
 */
public class LogWriter
{
	/**
	 * Einstufungen einer Lognachricht
	 * 
	 * @author Lars George
	 */
	public enum LogLevel
	{
		Error, Warn, Info, Debug
	}

	private LogLevel			logLevel	= LogLevel.Debug;

	private String				logFile		= "logfile.html";

	// Anlegen der Singleton Instanz
	private static LogWriter	instance	= new LogWriter();

	// ------------------------------------------------------------------------
	// Konstruktor
	// ------------------------------------------------------------------------
	/**
	 * privat, damit das singleton pattern umgesetzt wird und das objekt nur
	 * über die entsprechende Methode angefordert werden kann
	 * 
	 */
	private LogWriter()
	{		
		
		try
		{
			String strLogLevel = ConfigLoader.getInstance().getString("LogLevel");
			
			// Groß und Kleinschreibung ignorieren und versuchen den LogLevel auszulesen			
			if(strLogLevel.toLowerCase().equals("debug")) logLevel = LogLevel.Debug;
			else if(strLogLevel.toLowerCase().equals("info")) logLevel = LogLevel.Info;
			else if(strLogLevel.toLowerCase().equals("warn")) logLevel = LogLevel.Warn;
			else if(strLogLevel.toLowerCase().equals("error")) logLevel = LogLevel.Error;
			else
				throw new Exception("Cant read LogLevel ["+ strLogLevel +"]");
			
			// Die Datei auslesen in die geschrieben werden soll
			logFile = ConfigLoader.getInstance().getString("LogFile");

		}
		catch(Exception e)
		{
			System.out.println("[LogWriter]: Error while loading the configurations from the ConfigLoader. Using standard values. " + e.toString());
			logFile = "logfile.html";
			logLevel = LogLevel.Info;
		}
		
		
		try
		{	
			// Vorherige Datei löschen
			deleteLogFile();
			
			String text = "<html> \n"
					+ "  <head><title>eXigence Server - LogFile</title></head>\n"
					+ "    <style type='text/css'> \n"
					+ "      .error { background-color : #FF0033;}\n"
					+ "      .debug { background-color : #99CCFF;}\n"
					+ "      .info  { background-color : #FFFF99;}\n"
					+ "      .warn  { background-color : #FF8C00;}\n"
					+ "      .system  { border : solid black 2px; background-color : silver; font-weight : bold;}\n"
					+ "    </style>" + "  <body> \n"
					+ "  <h1>eXigence - LogFile</h1> \n";

			// Text in die Datei schreiben
			writeToFile(text);
			
			writeToFile(getSystemString("Using LogLevel: " + logLevel.toString()));

		}
		catch (Exception e)
		{
			System.out.println("Logger konnte nicht erfolgreich initialisiert werden:"
					+ e.toString());
		}
	}

	// ------------------------------------------------------------------------
	// Public Methoden
	// ------------------------------------------------------------------------
	/**
	 * Methode um das Singleton Objekt zu erhalten
	 * 
	 * @return Liefert die Instanz des LogWriters
	 */
	public static LogWriter getInstance()
	{
		return instance;
	}

	/**
	 * Löscht die Logdatei
	 */
	public void deleteLogFile()
	{
		try
		{
			File f = new File(logFile);
			f.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Methode um in die Nachricht in die entsprechende Datei zu schreiben
	 * 
	 * @param level
	 *            Welche Art von Nachricht ist es. Abhängig vom LogLevel des
	 *            LogWriters werd niedriegere Level ignoriert (Wenn nur Info
	 *            angezeigt werden soll, wird zB Debug ignoriert)
	 * @param text
	 */
	public synchronized void logToFile(LogLevel level, String text)
	{
		try
		{
			// Wenn der Logger diese LogLevel nicht beachten soll
			if (	(level == LogLevel.Warn && logLevel == LogLevel.Error) ||
					(level == LogLevel.Info && (logLevel == LogLevel.Error ||
												logLevel == LogLevel.Warn))||
					(level == LogLevel.Debug && (logLevel == LogLevel.Error ||
												logLevel == LogLevel.Warn ||
												logLevel == LogLevel.Info))
					)
				return;

			// Überprüfen ob das jeweilige LogLevel erfüllt ist
			switch (level)
			{
			case Debug:
				text = getDebugString(text);
				break;
			case Error:
				text = getErrorString(text);
				break;
			case Info:
				text = getInfoString(text);
				break;
			case Warn:
				text = getWarnString(text);
				break;
			}

			// Text in die Datei schreiben
			writeToFile(text);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Destruktor Schreibt die HTML Schlusstags in die Datei
	 */
	public void finalize()
	{
		try
		{
			// Datei zum schreiben öffnen
			FileOutputStream fileOutStream = new FileOutputStream(logFile, true);

			String text = "</body></html>";

			// Alle Zeichen in die Datei schreiben
			for (int i = 0; i < text.length(); i++)
				fileOutStream.write((byte) text.charAt(i));

			// Die Datei wieder schließen
			fileOutStream.close();
		}
		catch (Exception e)
		{
			System.out.println("Logger konnte nicht vollständig heruntergefahren werden: "
					+ e.toString());
		}
	}

	// ------------------------------------------------------------------------
	// Private Methoden
	// ------------------------------------------------------------------------
	private String getErrorString(String txt)
	{
		return "<div class='error'>[ERROR] " + txt + "</div>";
	}

	private String getInfoString(String txt)
	{
		return "<div class='info'>[INFO] " + txt + "</div>";
	}

	private String getDebugString(String txt)
	{
		return "<div class='debug'>[DEBUG] " + txt + "</div>";
	}

	private String getWarnString(String txt)
	{
		return "<div class='warn'>[WARNING] " + txt + "</div>";
	}

	private String getSystemString(String txt)
	{
		return "<div class='system'>" + txt + "</div>";
	}

	/**
	 * Eigentliche Methode zum schreiben in die Datei
	 * 
	 * @param text
	 */
	private void writeToFile(String text)
	{
		try
		{
			// Datei zum schreiben öffnen
			FileOutputStream fileOutStream = new FileOutputStream(logFile, true);

			// Alle Zeichen in die Datei schreiben
			for (int i = 0; i < text.length(); i++)
				fileOutStream.write((byte) text.charAt(i));

			// Zeilenumbruch hunzufügen
			fileOutStream.write((byte) '\n');

			// Die Datei wieder schließen
			fileOutStream.close();
		}
		catch (Exception e)
		{
			System.out.println("Failed writing into Logfile." + e.toString());
		}
	}

	// ------------------------------------------------------------------------
	// Get- / Set- Methoden
	// ------------------------------------------------------------------------
	public void setLogLevel(LogLevel level)
	{
		this.logLevel = level;

		writeToFile(getSystemString("Using LogLevel: " + level.toString()));
	}
}
