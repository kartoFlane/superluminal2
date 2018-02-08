package com.kartoflane.superluminal2.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Represents an .ftl file -- a mod entry.
 */
public class ModDatabaseEntry extends AbstractDatabaseEntry
{
	private static final Logger log = LogManager.getLogger( ModDatabaseEntry.class );

	private final File file;
	private final ZipFile archive;


	/**
	 * Creates a DatabaseEntry representing an installed mod.<br>
	 * The entry then has to be loaded using {@link #load()}
	 * 
	 * @param f
	 *            the .ftl or .zip file from which the data will be read
	 * @throws ZipException
	 *             when the file is not a zip archive
	 * @throws IOException
	 *             when an IO error occurs
	 */
	public ModDatabaseEntry( File f ) throws ZipException, IOException
	{
		file = f;
		archive = new ZipFile( f );
	}

	@Override
	public String getName()
	{
		return file.getName();
	}

	/**
	 * @return the file from which this ModDatabaseEntry was created.
	 */
	public File getFile()
	{
		return file;
	}

	@Override
	public boolean contains( String innerPath )
	{
		if ( innerPath == null )
			throw new IllegalArgumentException( "Inner path must not be null." );

		return archive.getEntry( innerPath ) != null;
	}

	@Override
	public InputStream getInputStream( String innerPath ) throws FileNotFoundException, IOException
	{
		if ( innerPath == null )
			throw new IllegalArgumentException( "Inner path must not be null." );

		ZipEntry ze = archive.getEntry( innerPath );
		if ( ze == null )
			throw new FileNotFoundException( "Inner path not found: " + innerPath );
		return archive.getInputStream( ze );
	}

	@Override
	public Set<String> listInnerPaths()
	{
		Set<String> result = new TreeSet<String>();

		Enumeration<? extends ZipEntry> entries = archive.entries();
		while ( entries.hasMoreElements() ) {
			ZipEntry ze = entries.nextElement();
			if ( !ze.isDirectory() )
				result.add( ze.getName() );
		}

		return result;
	}

	@Override
	public void load()
	{
		load( log );
	}

	@Override
	public void dispose()
	{
		close();
		clear();
	}

	/**
	 * Closes this entry and releases any system resources associated with the stream.
	 */
	protected void close()
	{
		try {
			archive.close();
		}
		catch ( IOException e ) {
			log.error( "Error: failed to close DatabaseEntry " + this );
		}
	}

	@Override
	public int hashCode()
	{
		return file.hashCode();
	}

	@Override
	public boolean equals( Object o )
	{
		if ( o instanceof ModDatabaseEntry ) {
			ModDatabaseEntry other = (ModDatabaseEntry)o;
			return file.equals( other.file );
		}
		else {
			return false;
		}
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
