package org.diretto.api.client.main.storage.upload;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.http.auth.AuthScope;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.diretto.api.client.base.annotations.InvocationLimited;
import org.diretto.api.client.base.data.UploadInfo;
import org.diretto.api.client.main.storage.StorageService;
import org.diretto.api.client.main.storage.StorageServiceImpl;
import org.diretto.api.client.main.storage.base.BasicAccessAuthenticationInterceptor;
import org.diretto.api.client.session.UserSession;
import org.diretto.api.client.user.UserFactory;
import org.diretto.api.client.util.InvocationUtils;
import org.restlet.Client;

/**
 * The {@code UploadManager} is responsible for the basic upload aspects.
 * 
 * @author Tobias Schlecht
 */
public final class UploadManager
{
	private final URL serviceURL;
	private final Client restletClient;

	private final ExecutorService executorService;

	/**
	 * The constructor is {@code private} to have strict control what instances
	 * exist at any time. Instead of the constructor the {@code public}
	 * <i>static factory method</i>
	 * {@link #getInstance(XMLConfiguration, URL, Client)} returns the instances
	 * of the class.
	 * 
	 * @param xmlConfiguration The {@code XMLConfiguration} object (of the
	 *        {@code StorageService})
	 * @param serviceURL The service {@code URL}
	 * @param restletClient The <i>Restlet</i> {@code Client}
	 */
	private UploadManager(XMLConfiguration xmlConfiguration, URL serviceURL, Client restletClient)
	{
		this.serviceURL = serviceURL;
		this.restletClient = restletClient;

		executorService = Executors.newFixedThreadPool(xmlConfiguration.getInt("upload/max-parallel-uploads"));
	}

	/**
	 * Returns an {@link UploadManager} instance.
	 * 
	 * @param xmlConfiguration The {@code XMLConfiguration} object (of the
	 *        {@code StorageService})
	 * @param serviceURL The service {@code URL}
	 * @param restletClient The <i>Restlet</i> {@code Client}
	 * @return A {@code UploadManager} instance
	 */
	@InvocationLimited(legitimateInvocationClasses = {StorageServiceImpl.class})
	public static synchronized UploadManager getInstance(XMLConfiguration xmlConfiguration, URL serviceURL, Client restletClient)
	{
		String warningMessage = "The method invocation \"" + UploadManager.class.getCanonicalName() + ".getInstance(XMLConfiguration, URL, Client)\" is not intended for this usage. Use the \"" + StorageService.class.getCanonicalName() + "\" for the upload functionalities.";
		InvocationUtils.checkMethodInvocation(warningMessage, "getInstance", XMLConfiguration.class, URL.class, Client.class);

		return new UploadManager(xmlConfiguration, serviceURL, restletClient);
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
	 * Returns an <i>Apache</i> {@link DefaultHttpClient} for the given
	 * {@link UserSession}.
	 * 
	 * @return An <i>Apache</i> {@code DefaultHttpClient} for the given
	 *         {@code UserSession}.
	 */
	DefaultHttpClient getHttpClient(UserSession userSession)
	{
		DefaultHttpClient httpClient = new DefaultHttpClient();

		UserFactory.setCredentials(userSession.getUser(), new AuthScope(serviceURL.getHost(), serviceURL.getPort()), httpClient.getCredentialsProvider());

		httpClient.addRequestInterceptor(new BasicAccessAuthenticationInterceptor(), 0);

		return httpClient;
	}

	/**
	 * Returns the <i>Restlet</i> {@link Client}.
	 * 
	 * @return The <i>Restlet</i> {@code Client}
	 */
	Client getRestletClient()
	{
		return restletClient;
	}

	/**
	 * Finishes the given {@code UploadProcess} ({@link UploadProcessImpl}).
	 * 
	 * @param uploadProcess The {@code UploadProcess} to be finished
	 */
	void finish(UploadProcessImpl uploadProcess)
	{
		uploadProcess.getHttpClient().getConnectionManager().shutdown();
	}

	/**
	 * @see StorageServiceImpl#createUploadProcess(UserSession, UploadInfo,
	 *      File)
	 */
	public UploadProcess createUploadProcess(UserSession userSession, UploadInfo uploadInfo, File file)
	{
		FileEntity fileEntity = new FileEntity(file, uploadInfo.getPlatformMediaType().getID());
		fileEntity.setChunked(false);

		return new UploadProcessImpl(this, userSession, uploadInfo, new UploadHttpEntity(fileEntity));
	}

	/**
	 * @see StorageServiceImpl#createUploadProcess(UserSession, UploadInfo,
	 *      InputStream)
	 */
	public UploadProcess createUploadProcess(UserSession userSession, UploadInfo uploadInfo, InputStream inputStream)
	{
		InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream, uploadInfo.getFileSize());
		inputStreamEntity.setContentType(uploadInfo.getPlatformMediaType().getID());
		inputStreamEntity.setChunked(false);

		return new UploadProcessImpl(this, userSession, uploadInfo, new UploadHttpEntity(inputStreamEntity));
	}

	/**
	 * @see StorageServiceImpl#executeUploadProcess(UploadProcess)
	 */
	public UploadReport executeUploadProcess(UploadProcess uploadProcess)
	{
		executorService.execute((UploadProcessImpl) uploadProcess);

		UploadReport uploadReport = null;

		try
		{
			uploadReport = ((UploadProcessImpl) uploadProcess).get();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();
		}

		return uploadReport;
	}
}
