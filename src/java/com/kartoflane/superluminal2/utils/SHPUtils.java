package com.kartoflane.superluminal2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.unsynchronized.BlockData;
import org.unsynchronized.ClassDesc;
import org.unsynchronized.Content;
import org.unsynchronized.EnumObject;
import org.unsynchronized.Field;
import org.unsynchronized.Instance;
import org.unsynchronized.JDeserialize;
import org.unsynchronized.StringObject;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.Races;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.Grid.Snapmodes;
import com.kartoflane.superluminal2.db.Database;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;


public class SHPUtils
{
	private static Map<Instance, GameObject> instanceGameMap = null;
	private static ShipObject ship = null;


	public static ShipObject loadShipSHP( File file ) throws FileNotFoundException, IOException
	{
		if ( !file.exists() )
			throw new FileNotFoundException( file.getAbsolutePath() );

		ship = null;

		JDeserialize jdes = new JDeserialize( file.getAbsolutePath() );
		FileInputStream fis = new FileInputStream( file );
		jdes.run( fis, true );
		fis.close();

		Instance shipInstance = (Instance)jdes.getContent().get( 0 );
		instanceGameMap = new HashMap<Instance, GameObject>();

		boolean playerShip = bool( getFieldValueByName( shipInstance, "isPlayer" ) );

		ship = new ShipObject( playerShip );

		String v = string( getFieldValueByName( shipInstance, "blueprintName" ) );
		if ( v != null )
			ship.setBlueprintName( v );

		v = string( getFieldValueByName( shipInstance, "shipName" ) );
		if ( v != null )
			ship.setShipName( v );

		v = string( getFieldValueByName( shipInstance, "shipClass" ) );
		if ( v != null )
			ship.setShipClass( v );

		v = string( getFieldValueByName( shipInstance, "descr" ) );
		if ( v != null )
			ship.setShipDescription( v );

		v = string( getFieldValueByName( shipInstance, "imageName" ) );
		if ( v != null )
			ship.setImageNamespace( v );

		v = string( getFieldValueByName( shipInstance, "layout" ) );
		if ( v != null )
			ship.setLayout( v );

		int i = integer( getFieldValueByName( shipInstance, "minSec" ) );
		ship.setMinSector( Math.max( 1, i ) );

		i = integer( getFieldValueByName( shipInstance, "maxSec" ) );
		ship.setMaxSector( Math.max( 1, i ) );

		i = integer( getFieldValueByName( shipInstance, "horizontal" ) );
		ship.setHorizontal( i );

		i = integer( getFieldValueByName( shipInstance, "vertical" ) );
		ship.setVertical( i );

		i = integer( getFieldValueByName( shipInstance, "weaponSlots" ) );
		ship.setWeaponSlots( i );

		i = integer( getFieldValueByName( shipInstance, "droneSlots" ) );
		ship.setDroneSlots( i );

		i = integer( getFieldValueByName( shipInstance, "hullHealth" ) );
		ship.setHealth( i );

		i = integer( getFieldValueByName( shipInstance, "reactorPower" ) );
		ship.setPower( i );

		i = integer( getFieldValueByName( shipInstance, "missiles" ) );
		ship.setMissilesAmount( i );

		i = integer( getFieldValueByName( shipInstance, "drones" ) );
		ship.setDronePartsAmount( i );

		Point p = point( getFieldValueByName( shipInstance, "offset" ) );
		ship.setXOffset( p.x );
		ship.setYOffset( p.y );

		Point anchor = point( getFieldValueByName( shipInstance, "anchor" ) );

		Rectangle r = rect( getFieldValueByName( shipInstance, "ellipse" ) );
		ship.setEllipse( r );

		r = rect( getFieldValueByName( shipInstance, "imageRect" ) );
		r.x -= anchor.x;
		r.y -= anchor.y;
		ship.setHullDimensions( r );

		v = string( getFieldValueByName( shipInstance, "imagePath" ) );
		if ( v != null ) {
			File f = new File( v );
			if ( f.exists() )
				ship.setImage( Images.HULL, "file:" + v );
		}

		v = string( getFieldValueByName( shipInstance, "floorPath" ) );
		if ( v != null ) {
			File f = new File( v );
			if ( f.exists() )
				ship.setImage( Images.FLOOR, "file:" + v );
		}

		v = string( getFieldValueByName( shipInstance, "cloakPath" ) );
		if ( v != null ) {
			File f = new File( v );
			if ( f.exists() )
				ship.setImage( Images.CLOAK, "file:" + v );
		}

		v = string( getFieldValueByName( shipInstance, "shieldPath" ) );
		if ( v != null ) {
			File f = new File( v );
			if ( f.exists() )
				ship.setImage( Images.SHIELD, "file:" + v );
		}

		v = string( getFieldValueByName( shipInstance, "miniPath" ) );
		if ( v != null ) {
			File f = new File( v );
			if ( f.exists() )
				ship.setImage( Images.THUMBNAIL, "file:" + v );
		}

		Map<Object, Object> map = null;
		Object o = null;

		o = getFieldValueByName( shipInstance, "startMap" );
		if ( o != null ) {
			map = map( o );
			// System -> boolean
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Systems sys = system( entry.getKey() );
				boolean available = bool( entry.getValue() );
				ship.getSystem( sys ).setAvailable( available );
			}
		}

		o = getFieldValueByName( shipInstance, "powerMap" );
		if ( o != null ) {
			map = map( o );
			// System -> integer
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Systems sys = system( entry.getKey() );
				int level = integer( entry.getValue() );
				ship.getSystem( sys ).setLevelStart( level );
			}
		}

		o = getFieldValueByName( shipInstance, "levelMap" );
		if ( o != null ) {
			map = map( o );
			// System -> integer
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Systems sys = system( entry.getKey() );
				int level = integer( entry.getValue() );
				ship.getSystem( sys ).setLevelMax( level );
			}
		}

		o = getFieldValueByName( shipInstance, "slotMap" );
		if ( o != null ) {
			map = map( o );
			// System -> integer
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Systems sys = system( entry.getKey() );
				if ( !sys.canContainStation() )
					continue;

				int slot = integer( entry.getValue() );
				SystemObject system = ship.getSystem( sys );
				StationObject station = system.getStation();
				station.setSlotId( slot );
			}
		}

		o = getFieldValueByName( shipInstance, "slotDirMap" );
		if ( o != null ) {
			map = map( o );
			// System -> Direction
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Systems sys = system( entry.getKey() );
				if ( !sys.canContainStation() )
					continue;

				Directions dir = direction( entry.getValue() );
				SystemObject system = ship.getSystem( sys );
				StationObject station = system.getStation();
				station.setSlotDirection( dir );
			}
		}

		o = getFieldValueByName( shipInstance, "crewMap" );
		if ( o != null ) {
			map = map( o );
			// Crew -> integer
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Races race = race( entry.getKey() );
				int count = integer( entry.getValue() );
				ship.setCrewMin( race, count );
				for ( int in = 0; in < count; in++ )
					ship.changeCrew( Races.NO_CREW, race );
			}
		}

		o = getFieldValueByName( shipInstance, "crewMaxMap" );
		if ( o != null ) {
			map = map( o );
			// Crew -> integer
			for ( Map.Entry<Object, Object> entry : map.entrySet() ) {
				Races race = race( entry.getKey() );
				int count = integer( entry.getValue() );
				ship.setCrewMax( race, count );
			}
		}

		Collection<Object> col = null;
		o = null;

		o = getFieldValueByName( shipInstance, "rooms" );
		if ( o != null ) {
			col = collection( o );
			// RoomObject
			for ( Object ob : col ) {
				GameObject go = room( ob, anchor );
				ship.add( go );
			}
		}

		o = getFieldValueByName( shipInstance, "doors" );
		if ( o != null ) {
			col = collection( o );
			// DoorObject
			for ( Object ob : col ) {
				GameObject go = door( ob, anchor );
				ship.add( go );
			}
		}

		o = getFieldValueByName( shipInstance, "gibs" );
		if ( o != null ) {
			col = collection( o );
			// GibObject
			for ( Object ob : col ) {
				GameObject go = gib( ob );
				ship.add( go );
			}
		}

		o = getFieldValueByName( shipInstance, "mounts" );
		if ( o != null ) {
			col = collection( o );
			// MountObject
			for ( Object ob : col ) {
				GameObject go = mount( ob, anchor );
				ship.add( go );
			}
		}

		Database db = Database.getInstance();

		boolean bySet = bool( getFieldValueByName( shipInstance, "dronesBySet" ) );
		col = collection( getFieldValueByName( shipInstance, "droneSet" ) );
		if ( bySet ) {
			for ( Object ob : col ) {
				DroneList list = db.getDroneList( string( ob ) );
				if ( list != null )
					ship.setDroneList( list );
				break;
			}
		}
		else {
			for ( Object ob : col ) {
				DroneObject drone = db.getDrone( string( ob ) );
				if ( drone != null )
					ship.changeDrone( Database.DEFAULT_DRONE_OBJ, drone );
			}
		}

		bySet = bool( getFieldValueByName( shipInstance, "weaponsBySet" ) );
		col = collection( getFieldValueByName( shipInstance, "weaponSet" ) );
		if ( bySet ) {
			for ( Object ob : col ) {
				WeaponList list = db.getWeaponList( string( ob ) );
				if ( list != null )
					ship.setWeaponList( list );
				break;
			}
		}
		else {
			for ( Object ob : col ) {
				WeaponObject weapon = db.getWeapon( string( ob ) );
				if ( weapon != null )
					ship.changeWeapon( Database.DEFAULT_WEAPON_OBJ, weapon );
			}
		}

		col = collection( getFieldValueByName( shipInstance, "augmentSet" ) );
		for ( Object ob : col ) {
			AugmentObject aug = db.getAugment( string( ob ) );
			if ( aug != null )
				ship.changeAugment( Database.DEFAULT_AUGMENT_OBJ, aug );
		}

		instanceGameMap.clear();
		instanceGameMap = null;

		System.gc();

		return ship;
	}

	private static Map.Entry<Field, Object> getFieldByName( Instance i, ClassDesc cd, String name )
	{
		if ( i == null || cd == null || name == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		for ( Map.Entry<Field, Object> entry : i.fielddata.get( cd ).entrySet() ) {
			if ( entry.getKey().name.equals( name ) )
				return entry;
		}

		return null;
	}

	private static Map.Entry<Field, Object> getFieldByName( Instance i, String name )
	{
		if ( i == null || name == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		for ( ClassDesc cd : i.fielddata.keySet() ) {
			Map.Entry<Field, Object> result = getFieldByName( i, cd, name );
			if ( result != null )
				return result;
		}

		return null;
	}

	private static Object getFieldValueByName( Instance i, ClassDesc cd, String name )
	{
		if ( i == null || cd == null || name == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		Map.Entry<Field, Object> result = getFieldByName( i, cd, name );
		if ( result != null )
			return result.getValue();
		return null;
	}

	private static Object getFieldValueByName( Instance i, String name )
	{
		if ( i == null || name == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		Map.Entry<Field, Object> result = getFieldByName( i, name );
		if ( result != null )
			return result.getValue();
		return null;
	}

	private static String string( Object o )
	{
		if ( o == null )
			return null;

		if ( o instanceof String )
			return (String)o;
		else if ( o instanceof StringObject )
			return ( (StringObject)o ).value;
		else
			throw new IllegalArgumentException( "Not a String: " + o.getClass() );
	}

	private static Point point( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		Point result = new Point( 0, 0 );

		try {
			result.x = integer( getFieldValueByName( i, "x" ) );
			result.y = integer( getFieldValueByName( i, "y" ) );
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "Not a Point: " + o.getClass() );
		}

		return result;
	}

	private static Rectangle rect( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		Rectangle result = new Rectangle( 0, 0, 0, 0 );

		try {
			result.x = integer( getFieldValueByName( i, "x" ) );
			result.y = integer( getFieldValueByName( i, "y" ) );
			result.width = integer( getFieldValueByName( i, "width" ) );
			result.height = integer( getFieldValueByName( i, "height" ) );
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "Not a Rectangle: " + o.getClass() );
		}

		return result;
	}

	private static Map<Object, Object> map( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		Map<Object, Object> result = new HashMap<Object, Object>();

		try {
			for ( Map.Entry<ClassDesc, List<Content>> entry : i.annotations.entrySet() ) {
				Object k = null, v = null;
				for ( Content c : entry.getValue() ) {
					if ( c instanceof BlockData == false ) {
						if ( k == null )
							k = c;
						else
							v = c;

						if ( k != null && v != null ) {
							result.put( k, v );
							k = null;
							v = null;
						}
					}
				}
			}
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "Not a Map: " + o.getClass() );
		}

		return result;
	}

	private static Collection<Object> collection( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		Collection<Object> result = new ArrayList<Object>();

		try {
			for ( Map.Entry<ClassDesc, List<Content>> entry : i.annotations.entrySet() ) {
				for ( Content c : entry.getValue() ) {
					if ( c instanceof BlockData == false )
						result.add( c );
				}
			}
		}
		catch ( Exception e ) {
			throw new IllegalArgumentException( "Not a Collection: " + o.getClass() );
		}

		return result;
	}

	private static boolean bool( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Boolean ) {
			return (Boolean)o;
		}
		else if ( o instanceof String ) {
			return Boolean.valueOf( (String)o );
		}
		else if ( o instanceof StringObject ) {
			return Boolean.valueOf( ( (StringObject)o ).value );
		}
		else if ( o instanceof Instance ) {
			Instance i = (Instance)o;
			return bool( getFieldValueByName( i, "value" ) );
		}
		else
			throw new IllegalArgumentException( "Not a Boolean: " + o.getClass() );
	}

	private static int integer( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Integer ) {
			return (Integer)o;
		}
		else if ( o instanceof String ) {
			return Integer.valueOf( (String)o );
		}
		else if ( o instanceof StringObject ) {
			return Integer.valueOf( ( (StringObject)o ).value );
		}
		else if ( o instanceof Instance ) {
			Instance i = (Instance)o;
			return integer( getFieldValueByName( i, "value" ) );
		}
		else
			throw new IllegalArgumentException( "Not an Integer: " + o.getClass() );
	}

	private static double doubel( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Double ) {
			return (Double)o;
		}
		else if ( o instanceof Float ) {
			return (Float)o;
		}
		else if ( o instanceof String ) {
			return Double.valueOf( (String)o );
		}
		else if ( o instanceof StringObject ) {
			return Double.valueOf( ( (StringObject)o ).value );
		}
		else if ( o instanceof Instance ) {
			Instance i = (Instance)o;
			return doubel( getFieldValueByName( i, "value" ) );
		}
		else
			throw new IllegalArgumentException( "Not a Double or Float: " + o.getClass() );
	}

	private static Systems system( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Systems )
			return (Systems)o;
		else if ( o instanceof String )
			return Systems.valueOf( ( (String)o ).toUpperCase() );
		else if ( o instanceof StringObject )
			return system( ( (StringObject)o ).value );
		else if ( o instanceof EnumObject )
			return system( ( (EnumObject)o ).value );
		else
			throw new IllegalArgumentException( "Not a System: " + o.getClass() );
	}

	private static Directions direction( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Directions )
			return (Directions)o;
		else if ( o instanceof String )
			return Directions.parseDir( (String)o );
		else if ( o instanceof StringObject )
			return direction( ( (StringObject)o ).value );
		else if ( o instanceof EnumObject )
			return direction( ( (EnumObject)o ).value );
		else
			throw new IllegalArgumentException( "Not a Direction: " + o.getClass() );
	}

	private static Races race( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Races )
			return (Races)o;
		else if ( o instanceof String )
			return Races.valueOf( ( (String)o ).toUpperCase() );
		else if ( o instanceof StringObject )
			return race( ( (StringObject)o ).value );
		else
			throw new IllegalArgumentException( "Not a Race: " + o.getClass() );
	}

	private static RoomObject room( Object o, Point anchor )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		RoomObject result = new RoomObject();

		result.setId( integer( getFieldValueByName( i, "id" ) ) );

		SystemObject system = ship.getSystem( system( getFieldValueByName( i, "sys" ) ) );
		system.setRoom( result );

		Rectangle r = rect( getFieldValueByName( i, "bounds" ) );
		result.setSize( Math.max( 1, r.width / 35 ), Math.max( 1, r.height / 35 ) );
		result.setLocation( ( r.x - anchor.x ) / 35, ( r.y - anchor.y ) / 35 );

		instanceGameMap.put( i, result );
		return result;
	}

	private static DoorObject door( Object o, Point anchor )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		DoorObject result = new DoorObject();

		result.setHorizontal( bool( getFieldValueByName( i, "horizontal" ) ) );

		o = getFieldValueByName( i, "leftRoom" );
		if ( o != null )
			result.setLeftRoom( (RoomObject)instanceGameMap.get( (Instance)o ) );

		o = getFieldValueByName( i, "rightRoom" );
		if ( o != null )
			result.setRightRoom( (RoomObject)instanceGameMap.get( (Instance)o ) );

		Rectangle r = rect( getFieldValueByName( i, "bounds" ) );
		r.x += 35 / 2 - anchor.x;
		r.y += 35 / 2 - anchor.y;
		Point p = Grid.getInstance().snapToGrid( r.x, r.y, Snapmodes.CELL );
		result.setLocation( p.x / 35, p.y / 35 );

		instanceGameMap.put( i, result );
		return result;
	}

	private static GibObject gib( Object o )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		GibObject result = new GibObject();

		String path = string( getFieldValueByName( i, "path" ) );
		if ( path != null ) {
			File f = new File( path );
			if ( f.exists() )
				result.setImagePath( "file:" + path );
		}

		result.setId( integer( getFieldValueByName( i, "number" ) ) );
		result.setAlias( string( getFieldValueByName( i, "ID" ) ) );
		result.setDirectionMin( integer( getFieldValueByName( i, "minDir" ) ) );
		result.setDirectionMax( integer( getFieldValueByName( i, "maxDir" ) ) );
		result.setVelocityMin( doubel( getFieldValueByName( i, "minVel" ) ) );
		result.setVelocityMax( doubel( getFieldValueByName( i, "maxVel" ) ) );
		result.setAngularMin( doubel( getFieldValueByName( i, "minAng" ) ) );
		result.setAngularMax( doubel( getFieldValueByName( i, "maxAng" ) ) );

		Point offset = point( getFieldValueByName( i, "position" ) );
		result.setOffset( offset.x, offset.y );

		instanceGameMap.put( i, result );
		return result;
	}

	private static MountObject mount( Object o, Point anchor )
	{
		if ( o == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		if ( o instanceof Instance == false )
			throw new IllegalArgumentException( "Argument must be an Instance: " + o );

		Instance i = (Instance)o;
		MountObject result = new MountObject();

		int id = integer( getFieldValueByName( i, "gib" ) );
		for ( GameObject go : instanceGameMap.values() ) {
			if ( go instanceof GibObject ) {
				GibObject g = (GibObject)go;
				if ( g.getId() == id )
					result.setGib( g );
			}
		}

		result.setRotated( bool( getFieldValueByName( i, "rotate" ) ) );
		result.setMirrored( bool( getFieldValueByName( i, "mirror" ) ) );
		result.setDirection( direction( getFieldValueByName( i, "slide" ) ) );

		Point offset = point( getFieldValueByName( i, "orig" ) );
		result.setLocation( offset.x - anchor.x, offset.y - anchor.x );

		instanceGameMap.put( i, result );
		return result;
	}
}
