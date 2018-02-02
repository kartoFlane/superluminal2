package com.kartoflane.superluminal2.ftl;

public class DroneList extends BlueprintList<DroneObject>
{
	private static final long serialVersionUID = 4618623391139370151L;


	/**
	 * Creates the default drone list object.
	 */
	public DroneList()
	{
		super( "No Drone List" );
	}

	public DroneList( String blueprint )
	{
		super( blueprint );
	}
}
