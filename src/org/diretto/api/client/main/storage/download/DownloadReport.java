package org.diretto.api.client.main.storage.download;

import java.net.URL;

import org.diretto.api.client.main.core.entities.AttachmentID;
import org.diretto.api.client.main.storage.base.AbstractProcessReport;

/**
 * This class represents a {@code DownloadReport}, which provides information
 * about the finished download process. <br/><br/>
 * 
 * <i>Annotation:</i> It is an immutable class.
 * 
 * @author Tobias Schlecht
 */
public final class DownloadReport extends AbstractProcessReport
{
	private final long downloadTime;
	private final double downloadRate;

	/**
	 * Constructs a {@link DownloadReport} using the given data.
	 * 
	 * @param downloadHttpEntity The corresponding {@code DownloadHttpEntity}
	 * @param fileURL The {@code URL} of the file
	 * @param attachmentID The corresponding {@code AttachmentID}
	 * @param downloadProcessStartTime The start time of the download procedure
	 *        in {@code nanoseconds}
	 * @param downloadProcessEndTime The end time of the download procedure in
	 *        {@code nanoseconds}
	 * @param downloadingStartTime The start time of the actual file download in
	 *        {@code nanoseconds}
	 * @param downloadingEndTime The end time of the actual file download in
	 *        {@code nanoseconds}
	 */
	DownloadReport(DownloadHttpEntity downloadHttpEntity, URL fileURL, AttachmentID attachmentID, long downloadProcessStartTime, long downloadProcessEndTime, long downloadingStartTime, long downloadingEndTime)
	{
		super(downloadHttpEntity.getContentLength(), downloadHttpEntity.getPlatformMediaType(), attachmentID, fileURL);

		long fileSize = downloadHttpEntity.getContentLength();

		downloadTime = Math.round(((double) (downloadProcessEndTime - downloadProcessStartTime)) / 1000000.0d);

		long downloadingTime = Math.round(((double) (downloadingEndTime - downloadingStartTime)) / 1000000.0d);

		downloadRate = ((double) fileSize) / (((double) downloadingTime) / 1000.0d);
	}

	/**
	 * Returns the required time for the complete download procedure in
	 * {@code milliseconds}.
	 * 
	 * @return The download time in {@code milliseconds}
	 */
	public long getDownloadTime()
	{
		return downloadTime;
	}

	/**
	 * Returns the average data rate of the actual file download process in
	 * {@code Byte/s}.
	 * 
	 * @return The average download data rate in {@code Byte/s}
	 */
	public double getDownloadRate()
	{
		return downloadRate;
	}
}
