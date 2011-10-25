package org.diretto.api.client.main.storage.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.diretto.api.client.base.data.UploadInfo;
import org.diretto.api.client.session.UserSession;
import org.diretto.api.client.user.UserFactory;
import org.restlet.Client;
import org.restlet.resource.ClientResource;

/**
 * This class is the implementation class of the {@link UploadProcess}
 * interface.
 * 
 * @author Tobias Schlecht
 */
final class UploadProcessImpl implements UploadProcess, Future<UploadReport>, Runnable
{
	private final UploadManager uploadManager;
	private final UserSession userSession;
	private final UploadInfo uploadInfo;
	private final UploadHttpEntity uploadHttpEntity;
	private final String uploadURL;
	private final DefaultHttpClient httpClient;
	private final Client restletClient;

	private final CountDownLatch countDownLatch = new CountDownLatch(1);

	private volatile boolean done = false;
	private volatile UploadState uploadState = UploadState.INIT;
	private volatile long uploadProcessStartTime = 0L;
	private volatile long uploadProcessEndTime = 0L;
	private volatile long uploadingEndTime = 0L;

	private volatile UploadReport uploadReport = null;

	/**
	 * Constructs an object of the {@link UploadProcess} interface.
	 * 
	 * @param uploadManager The corresponding {@code UploadManager}
	 * @param userSession The corresponding {@code UserSession}
	 * @param uploadInfo The {@code UploadInfo} object
	 * @param uploadHttpEntity The {@code UploadHttpEntity}
	 */
	UploadProcessImpl(UploadManager uploadManager, UserSession userSession, UploadInfo uploadInfo, UploadHttpEntity uploadHttpEntity)
	{
		this.uploadManager = uploadManager;
		this.userSession = userSession;
		this.uploadInfo = uploadInfo;
		this.uploadHttpEntity = uploadHttpEntity;

		uploadURL = uploadInfo.getTarget().toExternalForm();

		httpClient = uploadManager.getHttpClient(userSession);
		restletClient = uploadManager.getRestletClient();
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

	@Override
	public boolean cancel(boolean mayInterruptIfRunning)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public UploadReport get() throws InterruptedException, ExecutionException
	{
		countDownLatch.await();

		return uploadReport;
	}

	@Override
	public UploadReport get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
	{
		countDownLatch.await(timeout, unit);

		return uploadReport;
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

			uploadProcessStartTime = System.nanoTime();

			HttpPut httpPut = new HttpPut(uploadURL);

			httpPut.setEntity(uploadHttpEntity);

			uploadState = UploadState.UPLOADING;

			HttpResponse httpResponse = httpClient.execute(httpPut);

			uploadingEndTime = System.nanoTime();

			System.out.println("[StorageService UploadProcessImpl] " + uploadURL);

			if(httpResponse.getStatusLine().getStatusCode() != 201 && httpResponse.getStatusLine().getStatusCode() != 202)
			{
				System.err.println("[StorageService UploadProcessImpl] " + httpResponse.getStatusLine().getStatusCode());

				return;
			}

			uploadState = UploadState.PUBLISHING;

			HttpEntity httpResponseEntity = httpResponse.getEntity();

			String successToken = "";

			JsonFactory jsonFactory = new JsonFactory();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			httpResponseEntity.writeTo(byteArrayOutputStream);
			byteArrayOutputStream.flush();

			JsonParser jsonParser = jsonFactory.createJsonParser(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

			jsonParser.nextToken();

			while(jsonParser.nextToken() != JsonToken.END_OBJECT)
			{
				String fieldName = jsonParser.getCurrentName();

				jsonParser.nextToken();

				if("successToken".equals(fieldName))
				{
					successToken = jsonParser.getText();
				}
				else
				{
					return;
				}
			}

			jsonParser.close();

			if(successToken.equals(""))
			{
				return;
			}

			ClientResource clientResource = new ClientResource(uploadInfo.getAttachmentID().getUniqueResourceURL().toExternalForm() + "/lock?token=" + successToken);

			clientResource.setNext(restletClient);
			UserFactory.authenticateClientResource(userSession.getUser(), clientResource);

			clientResource.delete();

			System.out.println("[StorageService UploadProcessImpl] " + uploadInfo.getAttachmentID().getUniqueResourceURL().toExternalForm() + "/lock?token=" + successToken);

			if(clientResource.getStatus().getCode() != 204 && clientResource.getStatus().getCode() != 200 && clientResource.getStatus().getCode() != 202)
			{
				System.err.println("[StorageService UploadProcessImpl] " + clientResource.getStatus().getCode());

				return;
			}

			uploadProcessEndTime = System.nanoTime();

			uploadReport = new UploadReport(uploadInfo, uploadProcessStartTime, uploadProcessEndTime, uploadProcessStartTime, uploadingEndTime);

			uploadState = UploadState.FINISHED;

			uploadHttpEntity.consumeContent();
			httpResponseEntity.consumeContent();
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
			if(uploadingEndTime == 0L)
			{
				uploadingEndTime = System.nanoTime();
			}

			if(uploadProcessEndTime == 0L)
			{
				uploadProcessEndTime = System.nanoTime();
			}

			if(uploadState != UploadState.FINISHED)
			{
				uploadState = UploadState.ABORTED;
			}

			uploadManager.finish(this);

			done = true;

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

			case UPLOADING:
				return (int) ((double) (uploadHttpEntity.getByteCount() * 95L) / (double) uploadHttpEntity.getContentLength());

			case PUBLISHING:
				return 95;

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
		if(uploadProcessStartTime == 0L)
		{
			return 0L;
		}
		else if(uploadProcessEndTime != 0L)
		{
			return Math.round(((double) (uploadProcessEndTime - uploadProcessStartTime)) / 1000000.0d);
		}

		return Math.round(((double) (System.nanoTime() - uploadProcessStartTime)) / 1000000.0d);
	}

	@Override
	public synchronized UploadState getCurrentState()
	{
		return uploadState;
	}
}
