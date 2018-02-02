package com.kartoflane.ftl.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A container that holds all related layout objects together in
 * order to represent a ship.
 * 
 * @author kartoFlane
 *
 */
public class ShipLayout
{
	private List<LayoutObject> layoutList;
	private transient List<LayoutObject> layoutView;


	public ShipLayout()
	{
		layoutList = new ArrayList<LayoutObject>();
	}

	/**
	 * Adds the specified layout object to the layout.
	 */
	public void addLayoutObject( LayoutObject lo )
	{
		if ( lo == null ) {
			throw new IllegalArgumentException( "Argument must not be null!" );
		}
		if ( layoutList.contains( lo ) ) {
			throw new IllegalArgumentException( "Layout already contains " + lo.toString() );
		}
		layoutList.add( lo );
	}

	/**
	 * Returns an unmodifiable view of the list of layout objects
	 * currently present in the layout.
	 */
	public List<LayoutObject> listLayoutObjects()
	{
		if ( layoutView == null ) {
			layoutView = Collections.unmodifiableList( layoutList );
		}
		return layoutView;
	}

	/**
	 * Returns the first layout object of the specified type, or
	 * null if not found. Case-sensitive.
	 */
	public LayoutObject getObject( String name )
	{
		for ( LayoutObject lo : layoutList ) {
			if ( lo.getType().toString().equals( name ) ) {
				return lo;
			}
		}
		return null;
	}

	/**
	 * Returns the room object with the specified index, or
	 * null if not found.
	 */
	public RoomLayoutObject getRoom( int index )
	{
		for ( LayoutObject lo : layoutList ) {
			if ( lo.getType() == LOType.ROOM ) {
				RoomLayoutObject room = (RoomLayoutObject)lo;
				if ( room.getIndex() == index ) {
					return room;
				}
			}
		}
		return null;
	}

	/**
	 * Creates a new list containing all rooms currently present in
	 * the layout, and returns it.
	 */
	public List<RoomLayoutObject> listRooms()
	{
		List<RoomLayoutObject> result = new ArrayList<RoomLayoutObject>();
		for ( LayoutObject lo : layoutList ) {
			if ( lo.getType() == LOType.ROOM ) {
				result.add( (RoomLayoutObject)lo );
			}
		}
		return result;
	}

	/**
	 * Creates a new list containing all doors currently present in
	 * the layout, and returns it.
	 */
	public List<DoorLayoutObject> listDoors()
	{
		List<DoorLayoutObject> result = new ArrayList<DoorLayoutObject>();
		for ( LayoutObject lo : layoutList ) {
			if ( lo.getType() == LOType.DOOR ) {
				result.add( (DoorLayoutObject)lo );
			}
		}
		return result;
	}
}
