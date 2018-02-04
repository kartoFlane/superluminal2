package com.kartoflane.superluminal2.selfpatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kartoflane.common.selfpatch.SPRunTask;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.ui.EditorWindow;


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

		scheduleFilesToPatch( commands );

		scheduleProgramRerun( commands );

		// Execute the patcher jar.
		pb.command( commands );
		try {
			pb.start();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}

		// Dispose the main window shell, allowing the editor to close gracefully.
		EditorWindow.getInstance().getShell().dispose();
	}

	private void scheduleFilesToPatch( List<String> commands )
	{
		// Add files for renaming by the patcher
		List<File> files = new ArrayList<File>();
		scanRec( new File( "." ), files );

		for ( File f : files ) {
			commands.add( f.getPath() );
			commands.add( f.getPath().replace( ".tmp", "" ) );
		}
	}

	private void scheduleProgramRerun( List<String> commands )
	{
		// Include a command to re-run the editor
		OS os = OS.identifyOS();
		if ( os.isWindows() )
			commands.add( "-r(cmd.exe /C start superluminal2_admin.exe)" );
		else if ( os.isLinux() )
			commands.add( "-r(superluminal-cli.sh)" );
		else if ( os.isMac() )
			commands.add( "-r(Superluminal2.command)" );
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
