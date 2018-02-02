package com.kartoflane.common.selfpatch;

import java.io.File;


public class SelfPatcher
{
	private final SPGetTask getTask;
	private final SPPatchTask patchTask;
	private final SPRunTask runTask;


	public SelfPatcher( SPGetTask get, SPPatchTask patch, SPRunTask run )
	{
		getTask = get;
		patchTask = patch;
		runTask = run;
	}

	public void patch( SPDownloadWindow window )
	{
		if ( window == null ) {
			window = new SPSwingDownloadDialog();
		}

		getTask.setObserver( window );
		SPTaskThread thread = new SPTaskThread( getTask );
		window.setTaskThread( thread );

		thread.start();
		window.spShow();

		File downloadedFile = getTask.getResult();
		if ( downloadedFile == null ) {
			System.err.println( "Get task returned null." );
		}
		else if ( getTask.isInterrupted() ) {
			System.err.println( "Aborted by user." );
		}
		else if ( getTask.isSuccess() ) {
			System.out.println( "Patching..." );
			patchTask.patch( downloadedFile );
			System.out.println( "Running..." );
			runTask.run();
			System.out.println( "Exiting..." );
			System.exit( 0 );
		}
		else {
			System.err.println( "Download task was not finished successfully." );
		}
	}
}
