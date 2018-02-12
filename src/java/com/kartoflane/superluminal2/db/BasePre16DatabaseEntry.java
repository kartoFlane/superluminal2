package com.kartoflane.superluminal2.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.vhati.ftldat.FTLPack;


/**
 * Represents the base game archives in pre-1.6 versions of FTL.
 */
public class BasePre16DatabaseEntry extends AbstractDatabaseEntry
{
	private static final Logger log = LogManager.getLogger();

	private final FTLPack data;
	private final FTLPack resource;


	/**
	 * Creates the BaseDatabaseEntry, which serves as the core of the database.
	 * 
	 * @param data
	 *            the data.dat archive
	 * @param resource
	 *            the resource.dat archive
	 */
	public BasePre16DatabaseEntry( FTLPack data, FTLPack resource )
	{
		this.data = data;
		this.resource = resource;
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

		boolean result = data.contains( innerPath );

		if ( !result ) {
			// Not in the data archive. Lookup resources archive.
			result = resource.contains( innerPath );
		}

		return result;
	}

	@Override
	public InputStream getInputStream( String innerPath ) throws FileNotFoundException, IOException
	{
		if ( innerPath == null )
			throw new IllegalArgumentException( "Inner path must not be null." );

		if ( innerPath.endsWith( ".txt" ) || innerPath.endsWith( ".xml" ) ||
			innerPath.endsWith( ".xml.append" ) || innerPath.endsWith( ".append.xml" ) ||
			innerPath.endsWith( ".xml.rawappend" ) || innerPath.endsWith( ".rawappend.xml" ) ) {
			return data.getInputStream( innerPath );
		}
		else {
			return resource.getInputStream( innerPath );
		}
	}

	@Override
	public Set<String> listInnerPaths()
	{
		Set<String> result = new TreeSet<String>();

		result.addAll( data.list() );
		result.addAll( resource.list() );

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
			resource.close();
		}
		catch ( IOException e ) {
			log.error( "Error: failed to close DatabaseEntry " + this );
		}
	}

	@Override
	public int hashCode()
	{
		int result = 43;
		int salt = 17;

		result = salt * result + data.hashCode();
		result = salt * result + resource.hashCode();

		return result;
	}

	@Override
	public boolean equals( Object o )
	{
		if ( o instanceof BasePre16DatabaseEntry ) {
			BasePre16DatabaseEntry other = (BasePre16DatabaseEntry)o;
			return data.equals( other.data ) && resource.equals( other.resource );
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
