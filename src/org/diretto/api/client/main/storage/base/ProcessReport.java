package org.diretto.api.client.main.storage.base;

import java.net.URL;

import org.diretto.api.client.base.data.PlatformMediaType;
import org.diretto.api.client.main.core.entities.AttachmentID;

/**
 * This interface represents a {@code ProcessReport}. All classes, which are
 * implementing this interface, are able to provide information about a finished
 * process.
 * 
 * @author Tobias Schlecht
 */
public interface ProcessReport
{
	/**
	 * Returns the size of the file.
	 * 
	 * @return The size of the file
	 */
	long getFileSize();

	/**
	 * Returns the {@link PlatformMediaType} of the resource.
	 * 
	 * @return The {@code PlatformMediaType} of the resource
	 */
	PlatformMediaType getPlatformMediaType();

	/**
	 * Returns the corresponding {@link AttachmentID}.
	 * 
	 * @return The corresponding {@code AttachmentID}
	 */
	AttachmentID getAttachmentID();

	/**
	 * Returns the {@link URL} of the file.
	 * 
	 * @return The file {@code URL}
	 */
	URL getFileURL();
}
