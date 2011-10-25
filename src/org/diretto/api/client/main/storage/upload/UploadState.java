package org.diretto.api.client.main.storage.upload;

/**
 * An {@code UploadState} represents the current state of the upload procedure
 * and provides a description of this {@code UploadState}.
 * 
 * @author Tobias Schlecht
 */
public enum UploadState
{
	INIT("Initializing the upload process."),

	UPLOADING("The given resource is currently uploading."),

	PUBLISHING("Publishing the corresponding attachment."),

	FINISHED("The upload process has been finished."),

	ABORTED("The upload process has been aborted.");

	private final String description;

	/**
	 * Constructs an {@link UploadState}.
	 * 
	 * @param description The description
	 */
	UploadState(String description)
	{
		this.description = description;
	}

	/**
	 * Returns the description for this {@link UploadState}.
	 * 
	 * @return The description
	 */
	public String getDescription()
	{
		return description;
	}
}
