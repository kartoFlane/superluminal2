package net.vhati.modmanager.core;

import java.io.File;


public class FTLUtilities
{
	/**
	 * Confirms the FTL resources dir exists and contains the dat files.
	 */
	public static boolean isDatsDirValid( File d )
	{
		if ( !d.exists() || !d.isDirectory() )
			return false;
		if ( !new File( d, "data.dat" ).exists() )
			return false;
		if ( !new File( d, "resource.dat" ).exists() )
			return false;
		return true;
	}

	/**
	 * Returns the FTL resources dir, or null.
	 */
	public static File findDatsDir()
	{
		String steamPath = "Steam/steamapps/common/FTL Faster Than Light/resources";
		String gogPath = "GOG.com/Faster Than Light/resources";
		String humblePath = "FTL/resources";

		String xdgDataHome = System.getenv( "XDG_DATA_HOME" );
		if ( xdgDataHome == null )
			xdgDataHome = System.getProperty( "user.home" ) + "/.local/share";

		File[] candidates = new File[] {
			// Windows - Steam
			new File( new File( "" + System.getenv( "ProgramFiles(x86)" ) ), steamPath ),
			new File( new File( "" + System.getenv( "ProgramFiles" ) ), steamPath ),
			// Windows - GOG
			new File( new File( "" + System.getenv( "ProgramFiles(x86)" ) ), gogPath ),
			new File( new File( "" + System.getenv( "ProgramFiles" ) ), gogPath ),
			// Windows - Humble Bundle
			new File( new File( "" + System.getenv( "ProgramFiles(x86)" ) ), humblePath ),
			new File( new File( "" + System.getenv( "ProgramFiles" ) ), humblePath ),
			// Linux - Steam
			new File( xdgDataHome + "/Steam/SteamApps/common/FTL Faster Than Light/data/resources" ),
			// OSX - Steam
			new File(
				System.getProperty( "user.home" )
					+ "/Library/Application Support/Steam/SteamApps/common/FTL Faster Than Light/FTL.app/Contents/Resources"
			),
			// OSX
			new File( "/Applications/FTL.app/Contents/Resources" )
		};

		File result = null;

		for ( File candidate : candidates ) {
			if ( isDatsDirValid( candidate ) ) {
				result = candidate;
				break;
			}
		}

		return result;
	}
}
