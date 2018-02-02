package com.kartoflane.common.selfpatch;

import java.io.File;


/**
 * This describes how to download the file.
 */
public abstract class SPGetTask implements SPResultTask<File>
{
	protected SPTaskObserver observer;
	protected File downloadedFile;
	protected boolean success = false;

	private boolean interrupted = false;


	/**
	 * Sets the observer of this task (optional).
	 * Observer gets notified of the task's progress, status, and when
	 * it is completed or aborted.
	 */
	public void setObserver( SPTaskObserver observer )
	{
		this.observer = observer;
	}

	/**
	 * Returns the result of the task.
	 */
	public File getResult()
	{
		return downloadedFile;
	}

	/**
	 * Interrupts the task, signalling it that it should stop.
	 * 
	 * This doesn't work wonders however, the implementation still has
	 * to manually check the isInterrupted() method at various stages
	 * during the task's execution.
	 */
	public void interrupt()
	{
		interrupted = true;
	}

	/**
	 * Returns true if the task completed successfully, false otherwise.
	 * 
	 * Implementation has to set the success field.
	 */
	public boolean isSuccess()
	{
		return success;
	}

	/**
	 * Returns true if the task has been interrupted, false otherwise.
	 */
	public boolean isInterrupted()
	{
		return interrupted;
	}
}
