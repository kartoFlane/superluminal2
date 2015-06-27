package com.kartoflane.common.selfpatch;

import java.io.File;


/**
 * This describes how to apply the patch after it's been downloaded.
 */
public interface SPPatchTask {

	public void patch( File downloadedFile );
}
