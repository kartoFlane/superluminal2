package com.kartoflane.superluminal2.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vhati.ftldat.PkgPack;


/**
 * Represents the base game archives in post-1.6 versions of FTL.
 */
public class BasePost16DatabaseEntry extends AbstractDatabaseEntry
{
	private static final Logger log = LogManager.getLogger( BasePost16DatabaseEntry.class );

	private final PkgPack data;


	/**
	 * Creates the BaseDatabaseEntry, which serves as the core of the database.
	 * 
	 * @param data
	 *            the ftl.dat archive
	 */
	public BasePost16DatabaseEntry( PkgPack data )
	{
		this.data = data;
	}

	@Override
	public String getName()
	{
		return "Base Game";
	}

	@Override
	public boolean contains( String innerPath )
	{
		if ( innerPath == null )
			throw new IllegalArgumentException( "Inner path must not be null." );

		return data.contains( innerPath );
	}

	@Override
	public InputStream getInputStream( String innerPath ) throws FileNotFoundException, IOException
	{
		if ( innerPath == null )
			throw new IllegalArgumentException( "Inner path must not be null." );

		return data.getInputStream( innerPath );
	}

	@Override
	public Set<String> listInnerPaths()
	{
		Set<String> result = new TreeSet<String>();

		result.addAll( data.list() );

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
			data.close();
		}
		catch ( IOException e ) {
			log.error( "Error: failed to close DatabaseEntry " + this );
		}
	}

	@Override
	public int hashCode()
	{
		return data.hashCode();
	}

	@Override
	public boolean equals( Object o )
	{
		if ( o instanceof BasePost16DatabaseEntry ) {
			BasePost16DatabaseEntry other = (BasePost16DatabaseEntry)o;
			return data.equals( other.data );
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
