package org.diretto.api.client.main.storage.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

/**
 * This {@code abstract} class provides a skeletal implementation for a
 * <i>Progress</i> {@link HttpEntity}, to minimize the effort required to
 * implement a specific <i>Progress</i> {@code HttpEntity}.
 * 
 * @author Tobias Schlecht
 */
public abstract class AbstractProgressHttpEntity implements HttpEntity
{
	private final HttpEntity httpEntity;

	private CountingOutputStream countingOutputStream = null;

	/**
	 * Provides base implementation to construct a <i>Progress</i>
	 * {@link HttpEntity}.
	 * 
	 * @param httpEntity The corresponding {@code HttpEntity}
	 */
	public AbstractProgressHttpEntity(HttpEntity httpEntity)
	{
		this.httpEntity = httpEntity;
	}

	/**
	 * Returns the number of {@code Bytes} that have already been transmitted.
	 * 
	 * @return The number of transmitted {@code Bytes}
	 */
	public long getByteCount()
	{
		if(countingOutputStream == null)
		{
			return 0;
		}
		else
		{
			return countingOutputStream.getByteCount();
		}
	}

	@Override
	public void writeTo(OutputStream outputStream) throws IOException
	{
		countingOutputStream = new CountingOutputStream(outputStream);

		httpEntity.writeTo(countingOutputStream);
	}

	@Override
	public boolean isRepeatable()
	{
		return httpEntity.isRepeatable();
	}

	@Override
	public boolean isChunked()
	{
		return httpEntity.isChunked();
	}

	@Override
	public long getContentLength()
	{
		return httpEntity.getContentLength();
	}

	@Override
	public Header getContentType()
	{
		return httpEntity.getContentType();
	}

	@Override
	public Header getContentEncoding()
	{
		return httpEntity.getContentEncoding();
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException
	{
		return httpEntity.getContent();
	}

	@Override
	public boolean isStreaming()
	{
		return httpEntity.isStreaming();
	}

	@Override
	public void consumeContent() throws IOException
	{
		httpEntity.consumeContent();
	}
}
