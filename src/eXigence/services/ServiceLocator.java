package eXigence.services;

import java.util.HashMap;

/**
 * Diese Klasse stellt die möglichen Services zur verfügung
 * 
 * @author Lars George
 * 
 */
public class ServiceLocator
{
	/**
	 * Liste der verfügbaren Services
	 */
	private HashMap<EServices, IService>	services	= new HashMap<EServices, IService>();

	/**
	 * Instanz für das Singleton Pattern
	 */
	private static ServiceLocator			instance	= null;

	/**
	 * 
	 */
	private ServiceLocator()
	{
		// Userservice erstellen und der Liste von verfügbaren Services
		// hinzufügen
		Userservice userService = new Userservice();
		SimulationService simService = new SimulationService();		
		
		setService(EServices.Userservice, userService);
		setService(EServices.Simulationservice, simService);
	}

	/**
	 * Setzt einen neuen Service in die Liste der verfügbaren
	 * 
	 * @param serviceType
	 *            Welche Art von Service ist es
	 * @param service
	 *            Referenz auf das Serviceobjekt
	 */
	private void setService(EServices serviceType, IService service)
	{
		services.put(serviceType, service);
	}

	/**
	 * Methode zum erhalten der Singleton instanz
	 * 
	 * @return Instanz des ServiceAllocators
	 */
	public static ServiceLocator getInstance()
	{
		if (instance == null)
			instance = new ServiceLocator();

		return instance;
	}

	/**
	 * Methode um den entsprechenden Service zu erhalten
	 * 
	 * @param serviceType
	 *            Art des gewünschten Service
	 * @return Ein konkretes Service-Objekt vom angegeben Typ
	 */
	public IService getService(EServices serviceType)
	{
		return services.get(serviceType);
	}
}
