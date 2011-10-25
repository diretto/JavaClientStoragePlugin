package org.diretto.api.client.main.storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.http.impl.client.DefaultHttpClient;
import org.diretto.api.client.JavaClient;
import org.diretto.api.client.JavaClientImpl;
import org.diretto.api.client.base.annotations.InvocationLimited;
import org.diretto.api.client.base.data.UploadInfo;
import org.diretto.api.client.main.core.CoreService;
import org.diretto.api.client.main.storage.download.DownloadManager;
import org.diretto.api.client.main.storage.download.DownloadProcess;
import org.diretto.api.client.main.storage.download.DownloadReport;
import org.diretto.api.client.main.storage.upload.UploadManager;
import org.diretto.api.client.main.storage.upload.UploadProcess;
import org.diretto.api.client.main.storage.upload.UploadReport;
import org.diretto.api.client.service.AbstractService;
import org.diretto.api.client.session.UserSession;
import org.diretto.api.client.util.InvocationUtils;
import org.diretto.api.client.util.URLTransformationUtils;
import org.restlet.Client;

/**
 * This class is the implementation class of the {@link StorageService}
 * interface.
 * 
 * @author Tobias Schlecht
 */
public final class StorageServiceImpl extends AbstractService implements StorageService
{
	private final CoreService coreService;
	private final URL apiBaseURL;

	private final Client restletClient;
	private final DefaultHttpClient httpClient;

	private UploadManager uploadManager = null;
	private DownloadManager downloadManager = null;

	/**
	 * The constructor is {@code private} to have strict control what instances
	 * exist at any time. Instead of the constructor the {@code public}
	 * <i>static factory method</i> {@link #getInstance(URL, JavaClient)}
	 * returns the instances of the class.
	 * 
	 * @param serviceURL The service {@code URL}
	 * @param javaClient The corresponding {@code JavaClient}
	 */
	private StorageServiceImpl(URL serviceURL, JavaClient javaClient)
	{
		super(StorageServiceID.INSTANCE, serviceURL, javaClient);

		coreService = javaClient.getCoreService();
		apiBaseURL = javaClient.getAPIBaseURL();

		restletClient = ((JavaClientImpl) javaClient).getRestletClient();

		httpClient = new DefaultHttpClient();
	}

	/**
	 * Returns a {@link StorageService} instance for the specified service
	 * {@link URL} and the corresponding {@link JavaClient}.
	 * 
	 * @param serviceURL The service {@code URL}
	 * @param javaClient The corresponding {@code JavaClient}
	 * @return A {@code StorageService} instance
	 */
	@InvocationLimited(legitimateInvocationClasses = {JavaClientImpl.class})
	public static synchronized StorageService getInstance(URL serviceURL, JavaClient javaClient)
	{
		serviceURL = URLTransformationUtils.adjustServiceURL(serviceURL);

		String warningMessage = "The method invocation \"" + StorageServiceImpl.class.getCanonicalName() + ".getInstance(URL, JavaClient)\" is not intended for this usage. Use the method \"" + JavaClient.class.getCanonicalName() + ".getService(ServicePluginID)\" instead.";
		InvocationUtils.checkMethodInvocation(warningMessage, "getInstance", URL.class, JavaClient.class);

		return new StorageServiceImpl(serviceURL, javaClient);
	}

	/**
	 * Returns the corresponding {@link UploadManager}.
	 * 
	 * @return The corresponding {@code UploadManager}
	 */
	private UploadManager getUploadManager()
	{
		if(uploadManager == null)
		{
			uploadManager = UploadManager.getInstance(StorageServiceID.INSTANCE.getXMLConfiguration(), getServiceURL(), restletClient);
		}

		return uploadManager;
	}

	/**
	 * Returns the corresponding {@link DownloadManager}.
	 * 
	 * @return The corresponding {@code DownloadManager}
	 */
	private DownloadManager getDownloadManager()
	{
		if(downloadManager == null)
		{
			downloadManager = DownloadManager.getInstance(StorageServiceID.INSTANCE.getXMLConfiguration(), coreService, apiBaseURL, getServiceURL(), httpClient);
		}

		return downloadManager;
	}

	@Override
	public UploadProcess createUploadProcess(UserSession userSession, UploadInfo uploadInfo, File file)
	{
		if(userSession == null || uploadInfo == null || file == null)
		{
			throw new NullPointerException();
		}

		return getUploadManager().createUploadProcess(userSession, uploadInfo, file);
	}

	@Override
	public UploadProcess createUploadProcess(UserSession userSession, UploadInfo uploadInfo, InputStream inputStream)
	{
		if(userSession == null || uploadInfo == null || inputStream == null)
		{
			throw new NullPointerException();
		}

		return getUploadManager().createUploadProcess(userSession, uploadInfo, inputStream);
	}

	@Override
	public UploadReport executeUploadProcess(UploadProcess uploadProcess)
	{
		if(uploadProcess == null)
		{
			throw new NullPointerException();
		}

		return getUploadManager().executeUploadProcess(uploadProcess);
	}

	@Override
	public UploadReport executeUploadProcess(UserSession userSession, UploadInfo uploadInfo, File file)
	{
		if(userSession == null || uploadInfo == null || file == null)
		{
			throw new NullPointerException();
		}

		UploadProcess uploadProcess = createUploadProcess(userSession, uploadInfo, file);

		return executeUploadProcess(uploadProcess);
	}

	@Override
	public UploadReport executeUploadProcess(UserSession userSession, UploadInfo uploadInfo, InputStream inputStream)
	{
		if(userSession == null || uploadInfo == null || inputStream == null)
		{
			throw new NullPointerException();
		}

		UploadProcess uploadProcess = createUploadProcess(userSession, uploadInfo, inputStream);

		return executeUploadProcess(uploadProcess);
	}

	@Override
	public DownloadProcess createDownloadProcess(URL fileURL, OutputStream outputStream)
	{
		if(fileURL == null || outputStream == null)
		{
			throw new NullPointerException();
		}

		return getDownloadManager().createDownloadProcess(fileURL, outputStream);
	}

	@Override
	public DownloadReport executeDownloadProcess(DownloadProcess downloadProcess)
	{
		if(downloadProcess == null)
		{
			throw new NullPointerException();
		}

		return getDownloadManager().executeDownloadProcess(downloadProcess);
	}

	@Override
	public DownloadReport executeDownloadProcess(URL fileURL, OutputStream outputStream)
	{
		if(fileURL == null || outputStream == null)
		{
			throw new NullPointerException();
		}

		DownloadProcess downloadProcess = createDownloadProcess(fileURL, outputStream);

		return executeDownloadProcess(downloadProcess);
	}
}
