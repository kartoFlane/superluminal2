package com.kartoflane.superluminal2.core;

import java.util.List;

import net.vhati.modmanager.core.ComparableVersion;


/**
 * Container class for information about update data.
 */
public class UpdateData
{
	/** The link from which the newest release can be downloaded */
	public final String downloadLink;
	/** The newest available version number */
	public final ComparableVersion remoteVersion;
	/** List with changes since the local version */
	public final List<String> changes;
	/**
	 * Whether to offer to self-patch the program (we might want to
	 * disable it for releases where self-patching is broken).
	 */
	public final boolean offerSelfPatch;
	/** Last caught exception, stored in case of errors */
	public final Exception lastException;


	public UpdateData( Exception ex )
	{
		if ( ex == null ) {
			throw new IllegalArgumentException( "Failed UpdateData instance must be initialized with a valid Exception instance." );
		}

		// Update fetch failed.
		downloadLink = null;
		remoteVersion = null;
		changes = null;
		offerSelfPatch = false;
		lastException = ex;
	}

	/**
	 * @param downloadLink
	 *            the link from which the newest release can be downloaded
	 * @param remoteVersion
	 *            the newest available version number
	 * @param changes
	 *            list with changes since the local version
	 * @param offerSelfPatch
	 *            whether to offer to self-patch the program.
	 */
	public UpdateData( String downloadLink, ComparableVersion remoteVersion, List<String> changes, boolean offerSelfPatch )
	{
		if ( downloadLink == null ) {
			throw new IllegalArgumentException( "Download link must not be null." );
		}
		if ( remoteVersion == null ) {
			throw new IllegalArgumentException( "Remote version must not be null." );
		}
		if ( changes == null ) {
			throw new IllegalArgumentException( "List instance must not be null." );
		}

		this.lastException = null;
		this.downloadLink = downloadLink;
		this.remoteVersion = remoteVersion;
		this.offerSelfPatch = offerSelfPatch;
		this.changes = changes;
	}
}
