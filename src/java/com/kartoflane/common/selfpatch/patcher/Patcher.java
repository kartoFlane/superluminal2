package com.kartoflane.common.selfpatch.patcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Patcher
{
	public static void main( String[] args )
	{
		clearLog();

		List<String> pathList = new ArrayList<String>();
		for ( int i = 0; i < args.length; ++i )
			pathList.add( args[i] );

		final int retryBackoff = 10;
		int retryCount = 0;

		while ( pathList.size() > 1 ) {
			File fNew = new File( pathList.get( 0 ) );
			File fOld = new File( pathList.get( 1 ) );

			if ( !fNew.exists() ) {
				// The file we want to rename doesn't exist... Skip it.
				pathList.remove( 1 );
				pathList.remove( 0 );
				log( "File '%s' does not exist; skipping.", fNew.getPath() );
				continue;
			}
			else {
				log( "Patching '%s'", fOld.getPath() );
			}

			if ( !fOld.exists() || fOld.delete() ) {
				fNew.renameTo( fOld );
				pathList.remove( 1 );
				pathList.remove( 0 );
				retryCount = 0;
			}
			else {
				log( "Could not rename '%s'; retrying...", fNew.getPath() );
				try {
					// Sleep so that we give the other program time to do its stuff
					Thread.sleep( 500 );
					++retryCount;

					if ( retryCount >= retryBackoff ) {
						// We failed to rename the file several times in a row
						// Better back off so we don't keep running forever
						pathList.remove( 1 );
						pathList.remove( 0 );
						log( "Backed off from patching '%s'.", fNew.getPath() );
						continue;
					}
				}
				catch ( InterruptedException e ) {
				}
			}
		}

		// Use the last arg as command to re-run the program being patched.
		String run = pathList.get( 0 );
		if ( run.matches( "-r\\([^\\)]+\\)" ) ) {
			run = run.substring( 3, run.length() - 1 );
			try {
				Runtime.getRuntime().exec( run );
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		else {
			log( "Last arg is not a valid command." );
			System.exit( 1 );
		}

		log( "Patching finished successfully." );
		System.exit( 0 );
	}

	private static void clearLog()
	{
		new File( "patcher-log.txt" ).delete();
	}

	private static void log( String msg, String... args )
	{
		msg = String.format( msg, (Object[])args );
		System.out.println( msg );

		// Poor man's logging -- no need for anything fancy, just need a way to get messages into a file for debugging.
		FileWriter fw = null;

		File f = new File( "patcher-log.txt" );
		if ( !f.exists() ) {
			try {
				f.createNewFile();
			}
			catch ( IOException e ) {
			}
		}

		try {
			fw = new FileWriter( f, true );
			fw.append( msg );
			fw.append( "\n" );
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		finally {
			try {
				if ( fw != null )
					fw.close();
			}
			catch ( IOException e ) {
			}
		}
	}
}
