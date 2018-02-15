package com.kartoflane.superluminal2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;


/**
 * This class contains utility methods used to interpret XML tags as game objects.
 * 
 */
public class DataUtils
{
	public static final Logger log = LogManager.getLogger( DataUtils.class );


	/**
	 * Finds all ships with the specified blueprintName within the file.
	 * 
	 * @param f
	 * @param blueprintName
	 * @return
	 * @throws JDOMParseException
	 *             when the contents of the stream could not be parsed.
	 * @throws IOException
	 */
	public static ArrayList<Element> findShipsWithName( String blueprintName, InputStream is, String fileName )
		throws IllegalArgumentException, JDOMParseException, IOException
	{
		if ( blueprintName == null )
			throw new IllegalArgumentException( "Blueprint name must not be null." );

		IOUtils.DecodeResult dr = IOUtils.decodeText( is, null );
		String contents = dr.text;
		Document doc = IOUtils.parseXML( contents, true );

		ArrayList<Element> shipList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		if ( root.getName().equals( "shipBlueprint" ) ) {
			String blueprint = root.getAttributeValue( "name" );

			if ( blueprint != null && blueprint.equals( blueprintName ) )
				shipList.add( root );
		}
		else {
			for ( Element e : root.getChildren( "shipBlueprint" ) ) {
				String blueprint = e.getAttributeValue( "name" );

				if ( blueprint != null && blueprint.equals( blueprintName ) )
					shipList.add( e );
			}
		}

		return shipList;
	}

	public static ArrayList<Element> findShipsWithName( File f, String blueprintName )
		throws IllegalArgumentException, JDOMParseException, IOException
	{
		return findShipsWithName( blueprintName, new FileInputStream( f ), f.getName() );
	}

	public static ArrayList<Element> findTagsNamed( String contents, String tagName ) throws JDOMParseException
	{
		Document doc = null;
		doc = IOUtils.parseXML( contents, true );
		ArrayList<Element> tagList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		if ( root.getName().equals( tagName ) ) {
			tagList.add( root );
		}
		else {
			for ( Element e : root.getChildren( tagName ) )
				tagList.add( e );
		}

		return tagList;
	}

	public static ArrayList<Element> findTagsNamed( InputStream is, String fileName, String tagName )
		throws IllegalArgumentException, JDOMParseException, IOException
	{
		IOUtils.DecodeResult dr = IOUtils.decodeText( is, fileName );
		return findTagsNamed( dr.text, tagName );
	}

	public static ArrayList<Element> findTagsNamed( File f, String tagName )
		throws IllegalArgumentException, JDOMParseException, IOException
	{
		return findTagsNamed( new FileInputStream( f ), f.getName(), tagName );
	}
}
