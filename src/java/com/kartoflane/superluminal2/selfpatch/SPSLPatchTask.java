package com.kartoflane.superluminal2.selfpatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kartoflane.common.selfpatch.SPPatchTask;
import com.kartoflane.superluminal2.utils.IOUtils;


public class SPSLPatchTask implements SPPatchTask
{
	public static final Logger log = LogManager.getLogger( SPSLPatchTask.class );

	private static final int bufferSize = 1024 * 16;

	private File outputDir;


	public SPSLPatchTask( File outputDir )
	{
		this.outputDir = outputDir;
	}

	@Override
	public void patch( File downloadedFile )
	{
		String name = downloadedFile.getName();

		if ( name.endsWith( "zip" ) ) {
			try {
				extractZip( downloadedFile, outputDir );
			}
			catch ( ZipException e ) {
				e.printStackTrace();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		else if ( name.endsWith( "tar.gz" ) ) {
			try {
				File tar = unGzip( downloadedFile, outputDir );
				unTar( tar, outputDir );
			}
			catch ( FileNotFoundException e ) {
				e.printStackTrace();
			}
			catch ( ArchiveException e ) {
				e.printStackTrace();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		else {
			String ext = name;
			if ( name.contains( "." ) )
				ext = name.substring( name.lastIndexOf( '.' ) );
			log.error( "Unknown archive format: " + ext );
		}

		// Patch the patcher, since it can't do it itself.
		File patcherFileNew = new File( outputDir, "patcher.jar.tmp" );
		File patcherFileOld = new File( outputDir, "patcher.jar" );

		if ( !patcherFileOld.exists() || patcherFileOld.delete() ) {
			boolean result = patcherFileNew.renameTo( patcherFileOld );
			if ( result ) {
				log.info( "Successfully updated patcher.jar" );
			}
			else {
				log.error( "Failed to update patcher.jar -- file could not be renamed!" );
			}
		}
		else {
			log.error( "Failed to update patcher.jar -- old file could not be deleted!" );
		}
	}

	private void extractZip( final File inputFile, final File outputDir ) throws ZipException, IOException
	{
		ZipFile zip = new ZipFile( inputFile );
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

		if ( zip != null )
			zip.close();
	}

	/**
	 * Untar an input file into an output file.
	 * 
	 * The output file is created in the output folder, having the same name
	 * as the input file, minus the '.tar' extension.
	 * 
	 * @param inputFile
	 *            the input .tar file
	 * @param outputDir
	 *            the output directory file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @return The {@link List} of {@link File}s with the untared content.
	 * @throws ArchiveException
	 * 
	 * @source https://stackoverflow.com/a/7556307
	 */
	private static List<File> unTar( final File inputFile, final File outputDir ) throws FileNotFoundException, IOException, ArchiveException
	{
		final List<File> untaredFiles = new LinkedList<File>();
		final InputStream is = new FileInputStream( inputFile );
		final TarArchiveInputStream debInputStream = (TarArchiveInputStream)new ArchiveStreamFactory().createArchiveInputStream( "tar", is );
		TarArchiveEntry entry = null;
		while ( ( entry = (TarArchiveEntry)debInputStream.getNextEntry() ) != null ) {
			final File outputFile = new File( outputDir, entry.getName() );
			if ( entry.isDirectory() ) {
				if ( !outputFile.exists() ) {
					if ( !outputFile.mkdirs() ) {
						throw new IllegalStateException( String.format( "Couldn't create directory %s.", outputFile.getAbsolutePath() ) );
					}
				}
			}
			else {
				final OutputStream outputFileStream = new FileOutputStream( outputFile );
				IOUtils.copy( debInputStream, outputFileStream );
				outputFileStream.close();
			}
			untaredFiles.add( outputFile );
		}
		debInputStream.close();

		return untaredFiles;
	}

	/**
	 * Ungzip an input file into an output file.
	 * <p>
	 * The output file is created in the output folder, having the same name
	 * as the input file, minus the '.gz' extension.
	 * 
	 * @param inputFile
	 *            the input .gz file
	 * @param outputDir
	 *            the output directory file.
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @source https://stackoverflow.com/a/7556307
	 * 
	 * @return The {@File} with the ungzipped content.
	 */
	private static File unGzip( final File inputFile, final File outputDir ) throws FileNotFoundException, IOException
	{
		final File outputFile = new File( outputDir, inputFile.getName().substring( 0, inputFile.getName().length() - 3 ) );

		final GZIPInputStream in = new GZIPInputStream( new FileInputStream( inputFile ) );
		final FileOutputStream out = new FileOutputStream( outputFile );

		IOUtils.copy( in, out );

		in.close();
		out.close();

		return outputFile;
	}
}
