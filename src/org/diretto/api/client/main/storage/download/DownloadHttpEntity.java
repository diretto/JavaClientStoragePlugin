package org.diretto.api.client.main.storage.download;

import org.apache.http.HttpEntity;
import org.diretto.api.client.base.data.PlatformMediaType;
import org.diretto.api.client.main.storage.base.AbstractProgressHttpEntity;

/**
 * A {@code DownloadHttpEntity} extends the {@link AbstractProgressHttpEntity}
 * and is therefore able to provide progress information about the corresponding
 * data transmission.
 * 
 * @author Tobias Schlecht
 */
final class DownloadHttpEntity extends AbstractProgressHttpEntity
{
	private final PlatformMediaType platformMediaType;

	/**
	 * Constructs a {@link DownloadHttpEntity}.
	 * 
	 * @param httpEntity The corresponding {@code HttpEntity}
	 * @param platformMediaType The {@code PlatformMediaType} of the resource
	 */
	DownloadHttpEntity(HttpEntity httpEntity, PlatformMediaType platformMediaType)
	{
		super(httpEntity);

		this.platformMediaType = platformMediaType;
	}

	/**
	 * Returns the {@link PlatformMediaType} of the resource.
	 * 
	 * @return The {@code PlatformMediaType} of the resource
	 */
	public PlatformMediaType getPlatformMediaType()
	{
		return platformMediaType;
	}
}
