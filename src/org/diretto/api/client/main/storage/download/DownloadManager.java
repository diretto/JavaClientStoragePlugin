package org.diretto.api.client.main.storage.download;

import java.io.OutputStream;
import java.net.URL;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.http.impl.client.DefaultHttpClient;
import org.diretto.api.client.base.annotations.InvocationLimited;
import org.diretto.api.client.main.core.CoreService;
import org.diretto.api.client.main.storage.StorageService;
import org.diretto.api.client.main.storage.StorageServiceImpl;
import org.diretto.api.client.util.InvocationUtils;

/**
 * The {@code DownloadManager} is responsible for the basic download aspects.
 * 
 * @author Tobias Schlecht
 */
public final class DownloadManager
{
	private final CoreService coreService;
	private final URL apiBaseURL;
	private final URL serviceURL;
	private final DefaultHttpClient httpClient;

	private final ExecutorService executorService;

	/**
	 * The constructor is {@code private} to have strict control what instances
	 * exist at any time. Instead of the constructor the {@code public}
	 * <i>static factory method</i>
	 * {@link #getInstance(XMLConfiguration, CoreService, URL, URL, DefaultHttpClient)}
	 * returns the instances of the class.
	 * 
	 * @param xmlConfiguration The {@code XMLConfiguration} object (of the
	 *        {@code StorageService})
	 * @param coreService The corresponding {@code CoreService}
	 * @param apiBaseURL The base {@code URL} of the API
	 * @param serviceURL The service {@code URL}
	 * @param httpClient The <i>Apache</i> {@code DefaultHttpClient}
	 */
	private DownloadManager(XMLConfiguration xmlConfiguration, CoreService coreService, URL apiBaseURL, URL serviceURL, DefaultHttpClient httpClient)
	{
		this.coreService = coreService;
		this.apiBaseURL = apiBaseURL;
		this.serviceURL = serviceURL;
		this.httpClient = httpClient;

		executorService = Executors.newFixedThreadPool(xmlConfiguration.getInt("download/max-parallel-downloads"));
	}

	/**
	 * Returns a {@link DownloadManager} instance.
	 * 
	 * @param xmlConfiguration The {@code XMLConfiguration} object (of the
	 *        {@code StorageService})
	 * @param coreService The corresponding {@code CoreService}
	 * @param apiBaseURL The base {@code URL} of the API
	 * @param serviceURL The service {@code URL}
	 * @param httpClient The <i>Apache</i> {@code DefaultHttpClient}
	 * @return A {@code DownloadManager} instance
	 */
	@InvocationLimited(legitimateInvocationClasses = {StorageServiceImpl.class})
	public static synchronized DownloadManager getInstance(XMLConfiguration xmlConfiguration, CoreService coreService, URL apiBaseURL, URL serviceURL, DefaultHttpClient httpClient)
	{
		String warningMessage = "The method invocation \"" + DownloadManager.class.getCanonicalName() + ".getInstance(XMLConfiguration, CoreService, URL, URL, DefaultHttpClient)\" is not intended for this usage. Use the \"" + StorageService.class.getCanonicalName() + "\" for the download functionalities.";
		InvocationUtils.checkMethodInvocation(warningMessage, "getInstance", XMLConfiguration.class, CoreService.class, URL.class, URL.class, DefaultHttpClient.class);

		return new DownloadManager(xmlConfiguration, coreService, apiBaseURL, serviceURL, httpClient);
	}

	/**
	 * Returns the corresponding {@link CoreService}.
	 * 
	 * @return The corresponding {@code CoreService}
	 */
	CoreService getCoreService()
	{
		return coreService;
	}

	/**
	 * Returns the base {@link URL} of the API.
	 * 
	 * @return The base {@code URL} of the API
	 */
	URL getAPIBaseURL()
	{
		return apiBaseURL;
	}

	/**
	 * Returns the service {@link URL}.
	 * 
	 * @return The service {@code URL}
	 */
	URL getServiceURL()
	{
		return serviceURL;
	}

	/**
	 * Returns the <i>Apache</i> {@link DefaultHttpClient}.
	 * 
	 * @return The <i>Apache</i> {@code DefaultHttpClient}
	 */
	DefaultHttpClient getHttpClient()
	{
		return httpClient;
	}

	/**
	 * @see StorageServiceImpl#createDownloadProcess(URL, OutputStream)
	 */
	public DownloadProcess createDownloadProcess(URL fileURL, OutputStream outputStream)
	{
		return new DownloadProcessImpl(this, fileURL, outputStream);
	}

	/**
	 * @see StorageServiceImpl#executeDownloadProcess(DownloadProcess)
	 */
	public DownloadReport executeDownloadProcess(DownloadProcess downloadProcess)
	{
		executorService.execute((DownloadProcessImpl) downloadProcess);

		DownloadReport downloadReport = null;

		try
		{
			downloadReport = ((DownloadProcessImpl) downloadProcess).get();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();
		}

		return downloadReport;
	}
}
