package net.vhati.modmanager.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @source https://github.com/Vhati/Slipstream-Mod-Manager
 */
public class FTLUtilities
{
	/**
	 * Confirms the FTL resources dir exists and contains the dat files.
	 * 
	 * This checks for either "ftl.dat" or both "data.dat" and "resource.dat".
	 */
	public static boolean isDatsDirValid( File d )
	{
		if ( !d.exists() || !d.isDirectory() )
			return false;

		if ( new File( d, "ftl.dat" ).exists() )
			return true;
		if ( new File( d, "data.dat" ).exists() && new File( d, "resource.dat" ).exists() )
			return true;

		return false;
	}

	/**
	 * Returns the FTL resources dir, or null.
	 */
	public static File findDatsDir()
	{
		String steamPath = "Steam/steamapps/common/FTL Faster Than Light";
		String gogPath = "GOG.com/Faster Than Light";
		String humblePath = "FTL";

		String programFiles86 = System.getenv( "ProgramFiles(x86)" );
		String programFiles = System.getenv( "ProgramFiles" );

		String home = System.getProperty( "user.home" );

		String xdgDataHome = System.getenv( "XDG_DATA_HOME" );
		if ( xdgDataHome == null && home != null )
			xdgDataHome = home + "/.local/share";

		String winePrefix = System.getProperty( "WINEPREFIX" );
		if ( winePrefix == null && home != null )
			winePrefix = home + "/.wine";

		List<File> candidates = new ArrayList<File>();

		// Windows - Steam, GOG, Humble Bundle.
		if ( programFiles86 != null ) {
			candidates.add( new File( new File( programFiles86 ), steamPath ) );
			candidates.add( new File( new File( programFiles86 ), gogPath ) );
			candidates.add( new File( new File( programFiles86 ), humblePath ) );

			candidates.add( new File( new File( programFiles86 ), steamPath + "/resources" ) );
			candidates.add( new File( new File( programFiles86 ), gogPath + "/resources" ) );
			candidates.add( new File( new File( programFiles86 ), humblePath + "/resources" ) );
		}
		if ( programFiles != null ) {
			candidates.add( new File( new File( programFiles ), steamPath ) );
			candidates.add( new File( new File( programFiles ), gogPath ) );
			candidates.add( new File( new File( programFiles ), humblePath ) );

			candidates.add( new File( new File( programFiles ), steamPath + "/resources" ) );
			candidates.add( new File( new File( programFiles ), gogPath + "/resources" ) );
			candidates.add( new File( new File( programFiles ), humblePath + "/resources" ) );
		}
		// Linux - Steam.
		if ( xdgDataHome != null ) {
			candidates.add( new File( xdgDataHome + "/Steam/steamapps/common/FTL Faster Than Light/data" ) );
			candidates.add( new File( xdgDataHome + "/Steam/SteamApps/common/FTL Faster Than Light/data" ) );

			candidates.add( new File( xdgDataHome + "/Steam/steamapps/common/FTL Faster Than Light/data/resources" ) );
			candidates.add( new File( xdgDataHome + "/Steam/SteamApps/common/FTL Faster Than Light/data/resources" ) );
		}
		if ( home != null ) {  // I think .steam/ contains symlinks to the paths above.
			candidates.add( new File( home + "/.steam/steam/steamapps/common/FTL Faster Than Light/data" ) );
			candidates.add( new File( home + "/.steam/steam/SteamApps/common/FTL Faster Than Light/data" ) );

			candidates.add( new File( home + "/.steam/steam/steamapps/common/FTL Faster Than Light/data/resources" ) );
			candidates.add( new File( home + "/.steam/steam/SteamApps/common/FTL Faster Than Light/data/resources" ) );
		}
		// Linux - Wine.
		if ( winePrefix != null ) {
			candidates.add( new File( winePrefix + "/drive_c/Program Files (x86)/" + gogPath ) );
			candidates.add( new File( winePrefix + "/drive_c/Program Files (x86)/" + humblePath ) );
			candidates.add( new File( winePrefix + "/drive_c/Program Files/" + gogPath ) );
			candidates.add( new File( winePrefix + "/drive_c/Program Files/" + humblePath ) );

			candidates.add( new File( winePrefix + "/drive_c/Program Files (x86)/" + gogPath + "/resources" ) );
			candidates.add( new File( winePrefix + "/drive_c/Program Files (x86)/" + humblePath + "/resources" ) );
			candidates.add( new File( winePrefix + "/drive_c/Program Files/" + gogPath + "/resources" ) );
			candidates.add( new File( winePrefix + "/drive_c/Program Files/" + humblePath + "/resources" ) );
		}
		// OSX - Steam.
		if ( home != null ) {
			candidates
				.add( new File( home + "/Library/Application Support/Steam/steamapps/common/FTL Faster Than Light/FTL.app/Contents/Resources" ) );
			candidates
				.add( new File( home + "/Library/Application Support/Steam/SteamApps/common/FTL Faster Than Light/FTL.app/Contents/Resources" ) );
		}
		// OSX - Standalone.
		candidates.add( new File( "/Applications/FTL.app/Contents/Resources" ) );

		File result = null;

		for ( File candidate : candidates ) {
			// Resolve symlinks.
			try {
				candidate = candidate.getCanonicalFile();
			}
			catch ( IOException e ) {
				continue;
			}

			if ( isDatsDirValid( candidate ) ) {
				result = candidate;
				break;
			}
		}

		return result;
	}
}
