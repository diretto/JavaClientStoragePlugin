package org.diretto.api.client.main.storage.base;

import java.net.URL;

import org.diretto.api.client.base.data.PlatformMediaType;
import org.diretto.api.client.main.core.entities.AttachmentID;

/**
 * This {@code abstract} class provides a skeletal implementation of the
 * {@link ProcessReport} interface, to minimize the effort required to implement
 * this interface.
 * 
 * @author Tobias Schlecht
 */
public abstract class AbstractProcessReport implements ProcessReport
{
	private final long fileSize;
	private final PlatformMediaType platformMediaType;
	private final AttachmentID attachmentID;
	private final URL fileURL;

	/**
	 * Provides base implementation to construct a {@link ProcessReport}.
	 * 
	 * @param fileSize The size of the file
	 * @param platformMediaType The {@code PlatformMediaType} of the resource
	 * @param attachmentID The corresponding {@code AttachmentID}
	 * @param fileURL The {@code URL} of the file
	 */
	public AbstractProcessReport(long fileSize, PlatformMediaType platformMediaType, AttachmentID attachmentID, URL fileURL)
	{
		this.fileSize = fileSize;
		this.platformMediaType = platformMediaType;
		this.attachmentID = attachmentID;
		this.fileURL = fileURL;
	}

	@Override
	public long getFileSize()
	{
		return fileSize;
	}

	@Override
	public PlatformMediaType getPlatformMediaType()
	{
		return platformMediaType;
	}

	@Override
	public AttachmentID getAttachmentID()
	{
		return attachmentID;
	}

	@Override
	public URL getFileURL()
	{
		return fileURL;
	}
}
