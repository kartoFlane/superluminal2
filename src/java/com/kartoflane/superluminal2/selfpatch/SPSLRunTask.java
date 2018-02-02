package com.kartoflane.superluminal2.selfpatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kartoflane.common.selfpatch.SPRunTask;
import com.kartoflane.superluminal2.components.enums.OS;


public class SPSLRunTask extends SPRunTask
{
	@Override
	public void run()
	{
		ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream( true );
		pb.directory( null );

		List<String> commands = new ArrayList<String>();
		commands.add( "java" );
		commands.add( "-jar" );
		commands.add( "patcher.jar" );

		List<File> files = new ArrayList<File>();
		scanRec( new File( "." ), files );

		for ( File f : files ) {
			commands.add( f.getPath() );
			commands.add( f.getPath().replace( ".tmp", "" ) );
		}

		OS os = OS.identifyOS();
		if ( os.isWindows() )
			commands.add( "-r(cmd.exe /C start superluminal2_admin.exe)" );
		else if ( os.isLinux() )
			commands.add( "-r(superluminal-cli.sh)" );
		else if ( os.isMac() )
			commands.add( "-r(Superluminal2.command)" );

		pb.command( commands );
		try {
			pb.start();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	private void scanRec( File dir, List<File> result )
	{
		if ( dir.getName().endsWith( ".tmp" ) ) {
			result.add( dir );
			return;
		}

		for ( File f : dir.listFiles() ) {
			if ( f.isDirectory() )
				scanRec( f, result );
			else if ( f.getName().endsWith( ".tmp" ) )
				result.add( f );
		}
	}
}
