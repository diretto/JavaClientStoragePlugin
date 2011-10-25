package org.diretto.api.client.main.storage.upload;

import org.apache.http.HttpEntity;
import org.diretto.api.client.main.storage.base.AbstractProgressHttpEntity;

/**
 * An {@code UploadHttpEntity} extends the {@link AbstractProgressHttpEntity}
 * and is therefore able to provide progress information about the corresponding
 * data transmission.
 * 
 * @author Tobias Schlecht
 */
final class UploadHttpEntity extends AbstractProgressHttpEntity
{
	/**
	 * Constructs an {@link UploadHttpEntity}.
	 * 
	 * @param httpEntity The corresponding {@code HttpEntity}
	 */
	UploadHttpEntity(HttpEntity httpEntity)
	{
		super(httpEntity);
	}
}
