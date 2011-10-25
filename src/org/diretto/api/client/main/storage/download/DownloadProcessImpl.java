package org.diretto.api.client.main.storage.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.diretto.api.client.main.core.CoreService;
import org.diretto.api.client.main.core.entities.AttachmentID;
import org.diretto.api.client.main.core.entities.CoreServiceEntityIDFactory;
import org.diretto.api.client.main.core.entities.DocumentID;

/**
 * This class is the implementation class of the {@link DownloadProcess}
 * interface.
 * 
 * @author Tobias Schlecht
 */
final class DownloadProcessImpl implements DownloadProcess, Future<DownloadReport>, Runnable
{
	private final URL fileURL;
	private final OutputStream outputStream;
	private final CoreService coreService;
	private final URL serviceURL;
	private final DefaultHttpClient httpClient;
	private final AttachmentID attachmentID;

	private final CountDownLatch countDownLatch = new CountDownLatch(1);

	private volatile DownloadHttpEntity downloadHttpEntity;

	private volatile boolean done = false;
	private volatile DownloadState downloadState = DownloadState.INIT;
	private volatile long downloadProcessStartTime = 0L;
	private volatile long downloadProcessEndTime = 0L;

	private volatile DownloadReport downloadReport = null;

	/**
	 * Constructs an object of the {@link DownloadProcess} interface.
	 * 
	 * @param downloadManager The corresponding {@code DownloadManager}
	 * @param fileURL The {@code URL} of the resource to be downloaded
	 * @param outputStream outputStream The {@code OutputStream} to which the
	 *        resource content should be written
	 */
	DownloadProcessImpl(DownloadManager downloadManager, URL fileURL, OutputStream outputStream)
	{
		this.fileURL = fileURL;
		this.outputStream = outputStream;

		coreService = downloadManager.getCoreService();
		serviceURL = downloadManager.getServiceURL();
		httpClient = downloadManager.getHttpClient();

		String documentIDString = downloadManager.getAPIBaseURL().toExternalForm() + "/document/";
		String fileURLString = fileURL.toExternalForm();
		fileURLString = fileURLString.substring(serviceURL.toExternalForm().length() + 1, fileURLString.length());
		documentIDString = documentIDString + fileURLString.substring(0, fileURLString.indexOf("/"));
		fileURLString = fileURLString.substring(fileURLString.indexOf("/") + 1, fileURLString.length());
		String attachmentIDString = documentIDString + "/attachment/" + fileURLString.substring(0, fileURLString.indexOf("."));

		DocumentID documentID = CoreServiceEntityIDFactory.getDocumentIDInstance(documentIDString);
		attachmentID = CoreServiceEntityIDFactory.getAttachmentIDInstance(attachmentIDString, documentID, documentID);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public DownloadReport get() throws InterruptedException, ExecutionException
	{
		countDownLatch.await();

		return downloadReport;
	}

	@Override
	public DownloadReport get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		countDownLatch.await(timeout, unit);

		return downloadReport;
	}

	@Override
	public boolean isCancelled()
	{
		return false;
	}

	@Override
	public boolean isDone()
	{
		return done;
	}

	@Override
	public void run()
	{
		try
		{
			downloadProcessStartTime = System.nanoTime();

			HttpGet httpGet = new HttpGet(fileURL.toExternalForm());

			downloadState = DownloadState.DOWNLOADING;

			HttpResponse httpResponse = httpClient.execute(httpGet);

			System.out.println("[StorageService DownloadProcessImpl] " + fileURL.toExternalForm());

			if(httpResponse.getStatusLine().getStatusCode() != 200 && httpResponse.getStatusLine().getStatusCode() != 202)
			{
				System.err.println("[StorageService DownloadProcessImpl] " + httpResponse.getStatusLine().getStatusCode());
				
				return;
			}

			downloadHttpEntity = new DownloadHttpEntity(httpResponse.getEntity(), coreService.getPlatformMediaType(httpResponse.getEntity().getContentType().getValue()));

			downloadHttpEntity.writeTo(outputStream);

			downloadProcessEndTime = System.nanoTime();

			downloadReport = new DownloadReport(downloadHttpEntity, fileURL, attachmentID, downloadProcessStartTime, downloadProcessEndTime, downloadProcessStartTime, downloadProcessEndTime);

			downloadState = DownloadState.FINISHED;

			downloadHttpEntity.consumeContent();
		}
		catch(ClientProtocolException e)
		{
			return;
		}
		catch(IOException e)
		{
			return;
		}
		finally
		{
			done = true;

			if(downloadProcessEndTime == 0L)
			{
				downloadProcessEndTime = System.nanoTime();
			}

			if(downloadState != DownloadState.FINISHED)
			{
				downloadState = DownloadState.ABORTED;
			}

			countDownLatch.countDown();
		}
	}

	@Override
	public synchronized int getProgress()
	{
		switch(getCurrentState())
		{
			case INIT:
				return 0;

			case DOWNLOADING:

				if(downloadHttpEntity == null)
				{
					return 0;
				}

				return (int) ((double) (downloadHttpEntity.getByteCount() * 100L) / (double) downloadHttpEntity.getContentLength());

			case FINISHED:
				return 100;

			case ABORTED:
				return 100;

			default:
				return 0;
		}
	}

	@Override
	public synchronized long getElapsedTime()
	{
		if(downloadProcessStartTime == 0L)
		{
			return 0L;
		}
		else if(downloadProcessEndTime != 0L)
		{
			return Math.round(((double) (downloadProcessEndTime - downloadProcessStartTime)) / 1000000.0d);
		}

		return Math.round(((double) (System.nanoTime() - downloadProcessStartTime)) / 1000000.0d);
	}

	@Override
	public synchronized DownloadState getCurrentState()
	{
		return downloadState;
	}
}
