package org.diretto.api.client.main.storage.download;

/**
 * A {@code DownloadState} represents the current state of the download
 * procedure and provides a description of this {@code DownloadState}.
 * 
 * @author Tobias Schlecht
 */
public enum DownloadState
{
	INIT("Initializing the download process."),

	DOWNLOADING("The requested resource is currently downloading."),

	FINISHED("The download process has been finished."),

	ABORTED("The download process has been aborted.");

	private final String description;

	/**
	 * Constructs a {@link DownloadState}.
	 * 
	 * @param description The description
	 */
	DownloadState(String description)
	{
		this.description = description;
	}

	/**
	 * Returns the description for this {@link DownloadState}.
	 * 
	 * @return The description
	 */
	public String getDescription()
	{
		return description;
	}
}
