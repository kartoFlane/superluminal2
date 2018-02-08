package com.kartoflane.superluminal2.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.BlueprintList;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.ftl.ShipMetadata;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.utils.DataUtils;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.IOUtils.DecodeResult;


/**
 * A class representing a database entry that can be installed in the
 * {@link com.kartoflane.superluminal2.db.Database Database} to modify its contents.
 */
public abstract class AbstractDatabaseEntry
{
	protected ArrayList<ShipMetadata> shipMetadata = new ArrayList<ShipMetadata>();

	protected Map<String, AnimationObject> animationMap = new HashMap<String, AnimationObject>();
	protected Map<String, WeaponObject> weaponMap = new HashMap<String, WeaponObject>();
	protected Map<String, DroneObject> droneMap = new HashMap<String, DroneObject>();
	protected Map<String, AugmentObject> augmentMap = new HashMap<String, AugmentObject>();
	protected Map<String, GlowObject> glowMap = new HashMap<String, GlowObject>();
	protected Map<String, GlowSet> glowSetMap = new HashMap<String, GlowSet>();
	protected Map<String, WeaponList> weaponListMap = new HashMap<String, WeaponList>();
	protected Map<String, DroneList> droneListMap = new HashMap<String, DroneList>();
	/**
	 * Temporary map to hold anim sheets, since they
	 * need to be loaded before weaponAnims, which reference them
	 */
	protected HashMap<String, Element> animSheetMap = new HashMap<String, Element>();


	/**
	 * @return the display name of the database entry
	 */
	public abstract String getName();

	/**
	 * @return true if the entry contains the innerPath, false otherwise
	 */
	public abstract boolean contains( String innerPath );

	/**
	 * @param innerPath
	 *            the inner path of the sought file
	 * @return the stream
	 * 
	 * @throws FileNotFoundException
	 *             when the inner path was not found in the entry
	 * @throws IOException
	 *             when an IO error occurs
	 */
	public abstract InputStream getInputStream( String innerPath ) throws FileNotFoundException, IOException;

	/**
	 * @return a list of all inner paths contained in this DatabaseEntry
	 */
	public abstract Set<String> listInnerPaths();

	/**
	 * Loads the contents of this database entry.<br>
	 * This method should not be called directly. Use {@link Database#addEntry(IDatabaseEntry)} instead.
	 * 
	 * <pre>
	 * Loaded data:
	 *   - weapon anim, animSheets (only temporarily), weapon sprites
	 *   - ship blueprints
	 *   - weapon blueprints
	 *   - drone blueprints
	 *   - augment blueprints
	 *   - weapon and drone lists
	 *   - roomLayout tags (glow objects) from rooms.xml
	 *   - glow images (glow sets) in img/ship/interior, eg. pilot_glow1-3.png, etc
	 * </pre>
	 */
	public abstract void load();

	/**
	 * Closes this entry and releases any system resources associated with the stream.
	 * Clears all data that was loaded and cached in this entry.
	 */
	public abstract void dispose();

	// ===============================================================================================

	protected void store( AnimationObject anim )
	{
		animationMap.put( anim.getIdentifier(), anim );
	}

	protected void store( AugmentObject augment )
	{
		augmentMap.put( augment.getIdentifier(), augment );
	}

	protected void store( BlueprintList<?> list )
	{
		if ( list instanceof WeaponList ) {
			weaponListMap.put( list.getIdentifier(), (WeaponList)list );
		}
		else if ( list instanceof DroneList ) {
			droneListMap.put( list.getIdentifier(), (DroneList)list );
		}
		else {
			// Not interested in any other lists
		}
	}

	protected void store( DroneObject drone )
	{
		droneMap.put( drone.getIdentifier(), drone );
	}

	protected void store( GlowObject glow )
	{
		glowMap.put( glow.getIdentifier(), glow );
	}

	// TODO: Temporarily public until I figure out a better way to add user-defined glow sets.
	public void store( GlowSet set )
	{
		glowSetMap.put( set.getIdentifier(), set );
	}

	protected void store( ShipMetadata metadata )
	{
		shipMetadata.add( metadata );
	}

	protected void store( WeaponObject weapon )
	{
		weaponMap.put( weapon.getIdentifier(), weapon );
	}

	// ===============================================================================================

	/**
	 * This method should not be used. It's been made public only to be
	 * accessible by {@link DataUtils#loadAnim(BasePre16DatabaseEntry, Element)},
	 * and is used only during the preloading of anim sheets.
	 */
	public Element getAnimSheetElement( String anim )
	{
		return animSheetMap.get( anim );
	}

	/**
	 * @param animName
	 *            the animName of the sought animation (eg. laser_burst_1)
	 * @return the object representing the animation, or null if not found.
	 */
	public AnimationObject getAnim( String animName )
	{
		return animationMap.get( animName );
	}

	/**
	 * @param blueprint
	 *            the blueprint name of the sought augment
	 * @return the augment with the given blueprint, or null if not found
	 */
	public AugmentObject getAugment( String blueprintName )
	{
		return augmentMap.get( blueprintName );
	}

	/**
	 * @return an array of all augments in this entry
	 */
	public AugmentObject[] getAugments()
	{
		return augmentMap.values().toArray( new AugmentObject[0] );
	}

	/**
	 * @param blueprint
	 *            the blueprint name of the sought drone
	 * @return the drone with the given blueprint name, or null if not found
	 */
	public DroneObject getDrone( String blueprintName )
	{
		return droneMap.get( blueprintName );
	}

	/**
	 * @param type
	 *            the desired type of the drones
	 * @return an array containing all drones of the given type in this entry
	 */
	public DroneObject[] getDronesByType( DroneTypes type )
	{
		ArrayList<DroneObject> typeDrones = new ArrayList<DroneObject>();
		for ( DroneObject drone : droneMap.values() ) {
			if ( drone.getType() == type )
				typeDrones.add( drone );
		}

		return typeDrones.toArray( new DroneObject[0] );
	}

	/**
	 * @param the
	 *            name of the blueprint list
	 * @return the blueprint list with the given name
	 */
	public DroneList getDroneList( String listName )
	{
		return droneListMap.get( listName );
	}

	/**
	 * @return an array of all drone lists in this entry
	 */
	public DroneList[] getDroneLists()
	{
		return droneListMap.values().toArray( new DroneList[0] );
	}

	/**
	 * @param glowName
	 *            the name of the glow object (eg. pilot_1)
	 * @return the glow object with the given name, or null if not found
	 */
	public GlowObject getGlow( String glowName )
	{
		return glowMap.get( glowName );
	}

	/**
	 * @return an array of all glow objects in this entry
	 */
	public GlowObject[] getGlows()
	{
		return glowMap.values().toArray( new GlowObject[0] );
	}

	/**
	 * @param glowNamespace
	 *            the namespace of the glow image set (eg. computer1_glow)
	 * @return the glow image set with the given namespace, or null if not found
	 */
	public GlowSet getGlowSet( String glowNamespace )
	{
		return glowSetMap.get( glowNamespace );
	}

	/**
	 * @return an array of all glow sets in this entry
	 */
	public GlowSet[] getGlowSets()
	{
		return glowSetMap.values().toArray( new GlowSet[0] );
	}

	/**
	 * @return an array of all ships in this entry
	 */
	public ShipMetadata[] getShipMetadata()
	{
		return shipMetadata.toArray( new ShipMetadata[0] );
	}

	/**
	 * @param blueprintName
	 *            the blueprint name of the sought weapon
	 * @return the weapon with the given blueprint name, or null if not found
	 */
	public WeaponObject getWeapon( String blueprintName )
	{
		return weaponMap.get( blueprintName );
	}

	/**
	 * @param type
	 *            the desired type of the weapons
	 * @return a list containing all weapons of the given type in this entry
	 */
	public WeaponObject[] getWeaponsByType( WeaponTypes type )
	{
		ArrayList<WeaponObject> typeWeapons = new ArrayList<WeaponObject>();
		for ( WeaponObject weapon : weaponMap.values() ) {
			if ( weapon.getType() == type )
				typeWeapons.add( weapon );
		}

		return typeWeapons.toArray( new WeaponObject[0] );
	}

	/**
	 * @param listName
	 *            the name of the blueprint list
	 * @return the blueprint list with the given name
	 */
	public WeaponList getWeaponList( String listName )
	{
		return weaponListMap.get( listName );
	}

	/**
	 * @return an array of all weapon lists in this entry
	 */
	public WeaponList[] getWeaponLists()
	{
		return weaponListMap.values().toArray( new WeaponList[0] );
	}

	// ===============================================================================================

	/**
	 * Clears all data that was loaded and cached in this entry.
	 */
	protected void clear()
	{
		shipMetadata.clear();
		animationMap.clear();
		weaponMap.clear();
		droneMap.clear();
		augmentMap.clear();
		glowMap.clear();
		glowSetMap.clear();
		weaponListMap.clear();
		droneListMap.clear();
		animSheetMap.clear();
		System.gc();
	}

	/**
	 * @param log
	 *            logger instance to output messages to
	 * @see #load()
	 */
	protected void load( Logger log )
	{
		// Animations need to be loaded before weapons, since they reference them
		preloadAnims( log );
		loadGlowSets( log );

		String[] extensions = { ".xml", ".xml.append", ".append.xml", ".xml.rawappend", ".rawappend.xml" };
		InputStream is = null;

		String[] blueprintFiles = {
			"data/blueprints", "data/autoBlueprints",
			"data/dlcBlueprints", "data/dlcBlueprintsOverwrite",
			"data/dlcPirateBlueprints"
		};

		for ( String innerPath : blueprintFiles ) {
			boolean found = false;
			for ( String ext : extensions ) {
				try {
					is = getInputStream( innerPath + ext );
					DecodeResult dr = IOUtils.decodeText( is, null );

					ArrayList<Element> elements = DataUtils.findTagsNamed( dr.text, "shipBlueprint" );
					for ( Element e : elements ) {
						try {
							store( DataUtils.loadShipMetadata( e ) );
						}
						catch ( IllegalArgumentException ex ) {
							log.warn( getName() + ": could not load ship metadata: " + ex.getMessage() );
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed( dr.text, "weaponBlueprint" );
					for ( Element e : elements ) {
						try {
							store( DataUtils.loadWeapon( e ) );
						}
						catch ( IllegalArgumentException ex ) {
							log.warn( getName() + ": could not load weapon: " + ex.getMessage() );
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed( dr.text, "droneBlueprint" );
					for ( Element e : elements ) {
						try {
							store( DataUtils.loadDrone( e ) );
						}
						catch ( IllegalArgumentException ex ) {
							log.warn( getName() + ": could not load drone: " + ex.getMessage() );
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed( dr.text, "augBlueprint" );
					for ( Element e : elements ) {
						try {
							store( DataUtils.loadAugment( e ) );
						}
						catch ( IllegalArgumentException ex ) {
							log.warn( getName() + ": could not load augment: " + ex.getMessage() );
						}
					}
					found = true;
				}
				catch ( FileNotFoundException e ) {
					// Spammy and not very useful.
					// log.trace(String.format("Inner path '%s' could not be found.", innerPath + ext));
				}
				catch ( IOException e ) {
					log.error( String.format( "%s: an error occured while loading file '%s': ", getName(), innerPath ), e );
					found = true;
				}
				catch ( JDOMParseException e ) {
					log.error( String.format( "%s: an error occured while parsing file '%s': ", getName(), innerPath ), e );
					found = true;
				}
				finally {
					try {
						if ( is != null )
							is.close();
					}
					catch ( IOException e ) {
						log.error( getName() + ": an error occured while closing stream", e );
					}
				}
			}

			if ( !found )
				log.trace( String.format( "%s did not contain file %s", getName(), innerPath ) );
		}

		for ( String innerPath : blueprintFiles ) {
			for ( String ext : extensions ) {
				// Lists reference their contents directly, so they have to be loaded in a second pass
				try {
					is = getInputStream( innerPath + ext );
					DecodeResult dr = IOUtils.decodeText( is, null );

					ArrayList<Element> elements = DataUtils.findTagsNamed( dr.text, "blueprintList" );
					for ( Element e : elements ) {
						try {
							store( DataUtils.loadList( e ) );
						}
						catch ( IllegalArgumentException ex ) {
							log.warn( getName() + ": could not load blueprint list: " + ex.getMessage() );
						}
					}

					elements.clear();
					elements = null;
				}
				catch ( FileNotFoundException e ) {
					// Spammy and not very useful.
					// log.trace(String.format("Inner path '%s' could not be found.", innerPath + ext));
				}
				catch ( IOException e ) {
					log.error( String.format( "%s: an error occured while loading file '%s': ", getName(), innerPath ), e );
				}
				catch ( JDOMParseException e ) {
					log.error( String.format( "%s: an error occured while parsing file '%s': ", getName(), innerPath ), e );
				}
				finally {
					try {
						if ( is != null )
							is.close();
					}
					catch ( IOException e ) {
						log.error( getName() + ": an error occured while closing stream", e );
					}
				}
			}
		}

		// Scan rooms.xml alone
		for ( String ext : extensions ) {
			try {
				is = getInputStream( "data/rooms" + ext );
				DecodeResult dr = IOUtils.decodeText( is, null );

				ArrayList<Element> elements = DataUtils.findTagsNamed( dr.text, "roomLayout" );
				for ( Element e : elements ) {
					try {
						store( DataUtils.loadGlow( e ) );
					}
					catch ( IllegalArgumentException ex ) {
						log.warn( getName() + ": could not load glow object: " + ex.getMessage() );
					}
				}

				elements.clear();
				elements = null;
			}
			catch ( FileNotFoundException e ) {
				// log.trace(String.format("Inner path '%s' could not be found.", "data/rooms" + ext));
			}
			catch ( IOException e ) {
				log.error( String.format( "%s: an error occured while loading file '%s': ", getName(), "data/rooms" ), e );
			}
			catch ( JDOMParseException e ) {
				log.error( String.format( "%s: an error occured while parsing file '%s': ", getName(), "data/rooms" ), e );
			}
			finally {
				try {
					if ( is != null )
						is.close();
				}
				catch ( IOException e ) {
					log.error( getName() + ": an error occured while closing stream", e );
				}
			}
		}

		// Clear anim sheets, as they're no longer needed
		animSheetMap.clear();

		log.trace( getName() + " was loaded successfully." );
	}

	private void preloadAnims( Logger log )
	{
		String[] extensions = { ".xml", ".xml.append", ".append.xml", ".xml.rawappend", ".rawappend.xml" };
		String[] animPaths = new String[] { "data/animations", "data/dlcAnimations" };

		for ( String innerPath : animPaths ) {
			boolean found = false;
			for ( String ext : extensions ) {
				InputStream is = null;
				try {
					is = getInputStream( innerPath + ext );
					DecodeResult dr = IOUtils.decodeText( is, null );

					Document doc = null;
					doc = IOUtils.parseXML( dr.text );
					Element root = doc.getRootElement();

					// Preload anim sheets
					for ( Element e : root.getChildren( "animSheet" ) ) {
						String name = e.getAttributeValue( "name" );
						// If older entries are allowed to be overwritten by newer ones, bomb weapons
						// load the bomb projectile images instead of weapon images, since their sheets
						// share the same name
						if ( name != null && !animSheetMap.containsKey( name ) )
							animSheetMap.put( name, e );
					}

					// Load and store weaponAnims
					for ( Element e : root.getChildren( "weaponAnim" ) ) {
						try {
							store( DataUtils.loadAnim( this, e ) );
						}
						catch ( IllegalArgumentException ex ) {
							log.warn( getName() + ": could not load animation: " + ex.getMessage() );
						}
					}

					found = true;
				}
				catch ( FileNotFoundException e ) {
					// log.trace(String.format("Inner path '%s' could not be found.", innerPath + ext));
				}
				catch ( IOException e ) {
					log.error( String.format( "%s: an error occured while loading file '%s': ", getName(), innerPath ), e );
					found = true;
				}
				catch ( JDOMParseException e ) {
					log.error( String.format( "%s: an error occured while parsing file '%s': ", getName(), innerPath ), e );
					found = true;
				}
				finally {
					try {
						if ( is != null )
							is.close();
					}
					catch ( IOException e ) {
						log.error( getName() + ": an error occured while closing stream", e );
					}
				}
			}

			if ( !found )
				log.trace( String.format( "%s did not contain file %s", getName(), innerPath ) );
		}
	}

	private void loadGlowSets( Logger log )
	{
		final Pattern glowPtrn = Pattern.compile( "[0-9]\\.png" );

		Predicate<String> filter = new Predicate<String>() {
			@Override
			public boolean accept( String path )
			{
				return path.contains( "img/ship/interior/" ) &&
					( glowPtrn.matcher( path ).find() || path.endsWith( "_glow.png" ) );
			}
		};

		TreeSet<String> eligiblePaths = new TreeSet<String>();
		for ( String path : listInnerPaths() ) {
			if ( filter.accept( path ) )
				eligiblePaths.add( path );
		}

		for ( String s1 : eligiblePaths ) {
			if ( s1.endsWith( "_glow.png" ) ) {
				String namespace = s1.replaceAll( "_glow.png", "" );
				namespace = namespace.replace( "img/ship/interior/", "" );
				GlowSet set = new GlowSet( namespace );
				set.setImage( Glows.CLOAK, "db:" + s1 );
				glowSetMap.put( set.getIdentifier(), set );
			}
			else if ( s1.endsWith( "1.png" ) ) {
				// Might be a part of a glow image set.
				// Check if _2 and _3 exist as well. If they do, it's a glow set.
				String namespace = s1.replaceAll( "[0-9]\\.png", "" );
				String s2 = namespace + "2.png";
				String s3 = namespace + "3.png";

				if ( eligiblePaths.contains( s1 ) && eligiblePaths.contains( s2 ) && eligiblePaths.contains( s3 ) ) {
					namespace = namespace.replace( "img/ship/interior/", "" );
					GlowSet set = new GlowSet( namespace );
					set.setImage( Glows.BLUE, "db:" + s1 );
					set.setImage( Glows.GREEN, "db:" + s2 );
					set.setImage( Glows.YELLOW, "db:" + s3 );
					glowSetMap.put( set.getIdentifier(), set );
				}
			}
		}
	}
}
