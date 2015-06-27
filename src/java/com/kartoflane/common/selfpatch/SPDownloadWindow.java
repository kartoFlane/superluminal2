package com.kartoflane.common.selfpatch;

/**
 * This describes how to run the program anew after it's been patched
 */
public interface SPDownloadWindow extends SPTaskObserver {

	/**
	 * Opens and shows the UI window. This method must block until the window is dismissed.
	 */
	public void spShow();

	/**
	 * Sets the thread which executes the download task, for the purpose of interrupting it
	 * on the user's request.
	 */
	public void setTaskThread( Thread thread );
}
