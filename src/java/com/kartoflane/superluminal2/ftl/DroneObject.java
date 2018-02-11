package com.kartoflane.superluminal2.ftl;

import java.util.HashMap;

import com.kartoflane.superluminal2.components.enums.DroneStats;
import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;


public class DroneObject extends GameObject implements Comparable<DroneObject>, Identifiable
{
	private static final IDeferredText NO_DRONE = new VerbatimText( "<No Drone>" );

	private final String blueprintName;
	private DroneTypes droneType;
	private IDeferredText title = IDeferredText.EMPTY;
	private IDeferredText shortName = IDeferredText.EMPTY;
	private IDeferredText description = IDeferredText.EMPTY;

	private HashMap<DroneStats, Float> statMap = null;


	public DroneObject()
	{
		droneType = null;
		blueprintName = "Default Drone";
		title = NO_DRONE;
		shortName = NO_DRONE;
	}

	public DroneObject( String blueprint )
	{
		blueprintName = blueprint;
	}

	@Override
	public String getIdentifier()
	{
		return blueprintName;
	}

	public void update()
	{
		// Nothing to do here
	}

	public void setType( DroneTypes type )
	{
		droneType = type;
	}

	public DroneTypes getType()
	{
		return droneType;
	}

	public String getBlueprintName()
	{
		return blueprintName;
	}

	public void setTitle( IDeferredText title )
	{
		if ( title == null )
			throw new IllegalArgumentException( blueprintName + ": title must not be null." );
		this.title = title;
	}

	public IDeferredText getTitle()
	{
		return title;
	}

	public void setShortName( IDeferredText name )
	{
		if ( name == null )
			throw new IllegalArgumentException( blueprintName + ": name must not be null." );
		shortName = name;
	}

	public IDeferredText getShortName()
	{
		return shortName;
	}

	public void setDescription( IDeferredText desc )
	{
		if ( desc == null )
			throw new IllegalArgumentException( blueprintName + ": description must not be null." );
		description = desc;
	}

	public IDeferredText getDescription()
	{
		return description;
	}

	public void setStat( DroneStats stat, float value )
	{
		if ( stat == null )
			throw new IllegalArgumentException( "Stat type must not be null." );
		if ( statMap == null )
			initStatMap();
		statMap.put( stat, value );
	}

	public float getStat( DroneStats stat )
	{
		if ( stat == null )
			throw new IllegalArgumentException( "Stat type must not be null." );
		if ( statMap == null )
			initStatMap();
		return statMap.get( stat );
	}

	private void initStatMap()
	{
		statMap = new HashMap<DroneStats, Float>();
		for ( DroneStats stat : DroneStats.values() )
			statMap.put( stat, 0f );
	}

	@Override
	public String toString()
	{
		return title.toString();
	}

	@Override
	public int compareTo( DroneObject o )
	{
		return blueprintName.compareTo( o.blueprintName );
	}

	@Override
	public boolean equals( Object o )
	{
		if ( o instanceof DroneObject ) {
			DroneObject other = (DroneObject)o;
			return blueprintName.equals( other.blueprintName );
		}
		else
			return false;
	}
}
