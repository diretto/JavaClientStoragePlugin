package org.diretto.api.client.main.storage;

import org.apache.commons.configuration.XMLConfiguration;
import org.diretto.api.client.service.AbstractServicePluginID;
import org.diretto.api.client.service.Service;
import org.diretto.api.client.util.ConfigUtils;

/**
 * This class serves for the identification of the {@link StorageService}.
 * <br/><br/>
 * 
 * <i>Annotation:</i> <u>Singleton Pattern</u>
 * 
 * @author Tobias Schlecht
 */
public final class StorageServiceID extends AbstractServicePluginID
{
	private static final String CONFIG_FILE = "org/diretto/api/client/main/storage/config.xml";

	private static final XMLConfiguration xmlConfiguration = ConfigUtils.getXMLConfiguration(CONFIG_FILE);

	public static final StorageServiceID INSTANCE = new StorageServiceID(xmlConfiguration.getString("name"), xmlConfiguration.getString("api-version"), getInitServiceClass());

	/**
	 * Constructs the sole instance of the {@link StorageServiceID}. <br/><br/>
	 * 
	 * <i>Annotation:</i> <u>Singleton Pattern</u>
	 */
	private StorageServiceID(String name, String apiVersion, Class<Service> serviceClass)
	{
		super(name, apiVersion, serviceClass);
	}

	/**
	 * Returns the implementation class of the {@link StorageService}, which is
	 * loaded from the XML configuration file.
	 * 
	 * @return The implementation class of the {@code StorageService}
	 */
	@SuppressWarnings("unchecked")
	private static Class<Service> getInitServiceClass()
	{
		try
		{
			return (Class<Service>) Class.forName(xmlConfiguration.getString("service-class"));
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the {@link XMLConfiguration} object, which is loaded from the XML
	 * configuration file corresponding to the whole {@link StorageService}
	 * implementation.
	 * 
	 * @return The {@code XMLConfiguration} object
	 */
	XMLConfiguration getXMLConfiguration()
	{
		return xmlConfiguration;
	}
}
