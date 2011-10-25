package org.diretto.api.client.main.storage.base;

/**
 * This interface represents a {@code StorageProcess}. <br/><br/>
 * 
 * With a {@code StorageProcess} object it is among other things possible to
 * trace the corresponding process.
 * 
 * @author Tobias Schlecht
 * 
 * @param <T> The type of the corresponding <i>Process State</i>
 */
public interface StorageProcess<T extends Enum<?>>
{
	/**
	 * Returns the progress of this process specified as percentage.
	 * 
	 * @return The progress specified as percentage
	 */
	int getProgress();

	/**
	 * Returns the elapsed time of this process in {@code milliseconds}.
	 * 
	 * @return The elapsed time in {@code milliseconds}
	 */
	long getElapsedTime();

	/**
	 * Returns the current <i>State</i> of this process.
	 * 
	 * @return The current <i>State</i> of this process
	 */
	T getCurrentState();
}
