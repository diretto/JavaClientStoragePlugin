package org.diretto.api.client.main.storage.upload;

import org.diretto.api.client.base.data.UploadInfo;
import org.diretto.api.client.main.storage.base.AbstractProcessReport;

/**
 * This class represents an {@code UploadReport}, which provides information
 * about the finished upload process. <br/><br/>
 * 
 * <i>Annotation:</i> It is an immutable class.
 * 
 * @author Tobias Schlecht
 */
public final class UploadReport extends AbstractProcessReport
{
	private final long uploadTime;
	private final double uploadRate;

	/**
	 * Constructs an {@link UploadReport} using the given data.
	 * 
	 * @param uploadInfo The corresponding {@code UploadInfo} object
	 * @param uploadProcessStartTime The start time of the upload procedure in
	 *        {@code nanoseconds}
	 * @param uploadProcessEndTime The end time of the upload procedure in
	 *        {@code nanoseconds}
	 * @param uploadingStartTime The start time of the actual file upload in
	 *        {@code nanoseconds}
	 * @param uploadingEndTime The end time of the actual file upload in
	 *        {@code nanoseconds}
	 */
	UploadReport(UploadInfo uploadInfo, long uploadProcessStartTime, long uploadProcessEndTime, long uploadingStartTime, long uploadingEndTime)
	{
		super(uploadInfo.getFileSize(), uploadInfo.getPlatformMediaType(), uploadInfo.getAttachmentID(), uploadInfo.getFileURL());

		long fileSize = uploadInfo.getFileSize();

		uploadTime = Math.round(((double) (uploadProcessEndTime - uploadProcessStartTime)) / 1000000.0d);

		long uploadingTime = Math.round(((double) (uploadingEndTime - uploadingStartTime)) / 1000000.0d);

		uploadRate = ((double) fileSize) / (((double) uploadingTime) / 1000.0d);
	}

	/**
	 * Returns the required time for the complete upload procedure in
	 * {@code milliseconds}.
	 * 
	 * @return The upload time in {@code milliseconds}
	 */
	public long getUploadTime()
	{
		return uploadTime;
	}

	/**
	 * Returns the average data rate of the actual file upload process in
	 * {@code Byte/s}.
	 * 
	 * @return The average upload data rate in {@code Byte/s}
	 */
	public double getUploadRate()
	{
		return uploadRate;
	}
}
