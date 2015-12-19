package com.kartoflane.superluminal2.selfpatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.kartoflane.common.selfpatch.SPPatchTask;
import com.kartoflane.superluminal2.components.enums.OS;


public class SPSLPatchTask implements SPPatchTask {

	private static final int bufferSize = 1024 * 16;


	@Override
	public void patch( File downloadedFile ) {
		OS os = OS.identifyOS();

		if ( os.isWindows() ) {
			ZipFile zip = null;
			try {
				zip = new ZipFile( downloadedFile );
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while ( entries.hasMoreElements() ) {
					ZipEntry ze = entries.nextElement();

					String name = ze.getName();
					name = name.substring( name.indexOf( "/" ) + 1, name.length() );
					if ( name.length() == 0 )
						continue;
					if ( name.endsWith( "/" ) || name.endsWith( "\\" ) )
						name = name.substring( 0, name.length() - 1 );

					File dest = new File( name );
					if ( dest.exists() )
						name += ".tmp";

					dest = new File( name );

					FileOutputStream out = null;
					try {
						InputStream is = zip.getInputStream( ze );
						File parent = dest.getParentFile();
						if ( parent != null )
							parent.mkdirs();
						dest.createNewFile();
						out = new FileOutputStream( dest );

						int bytesRead = -1;
						byte[] buffer = new byte[bufferSize];
						while ( ( bytesRead = is.read( buffer ) ) != -1 )
							out.write( buffer, 0, bytesRead );
					}
					finally {
						if ( out != null )
							out.close();
					}
				}
			}
			catch ( ZipException e ) {
				e.printStackTrace();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
			finally {
				try {
					if ( zip != null )
						zip.close();
				}
				catch ( IOException e ) {
				}
			}
		}
		else {
			// TODO: tarball handling
		}
	}
}
