package com.kartoflane.common.selfpatch.patcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Patcher
{
	public static void main( String[] args )
	{
		if ( args.length % 2 == 0 ) {
			System.err.println( "Number of args is even. Patcher will now exit." );
			System.exit( 1 );
		}

		List<String> pathList = new ArrayList<String>();
		for ( int i = 0; i < args.length; ++i )
			pathList.add( args[i] );

		while ( pathList.size() > 1 ) {
			File f = new File( pathList.get( 0 ) );
			File s = new File( pathList.get( 1 ) );

			if ( !s.exists() || s.delete() ) {
				f.renameTo( s );
				pathList.remove( 1 );
				pathList.remove( 0 );
			}
			else {
				System.err.printf( "Could not rename '%s'; retrying...", f.getPath() );
				try {
					// Sleep so that we give the other program time to do its stuff
					Thread.sleep( 200 );
				}
				catch ( InterruptedException e ) {
				}
			}
		}

		String run = pathList.get( 0 );
		run = run.substring( 3, run.length() - 1 );
		try {
			Runtime.getRuntime().exec( run );
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

		System.exit( 0 );
	}
}
