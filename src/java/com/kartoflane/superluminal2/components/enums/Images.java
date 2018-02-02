package com.kartoflane.superluminal2.components.enums;

import com.kartoflane.superluminal2.ftl.ShipObject;


public enum Images
{
	SHIELD,
	HULL,
	FLOOR,
	CLOAK,
	THUMBNAIL,
	HANGAR;

	/**
	 * @return all Images, sans {@link #HANGAR}
	 */
	public static Images[] getShipImages()
	{
		return new Images[] {
			SHIELD, HULL, FLOOR, CLOAK, THUMBNAIL
		};
	}

	/**
	 * Returns the dat-relative path for this image type.<br>
	 * The returned path has a trailing forward slash, but no
	 * leading forward slash, like so:
	 * 
	 * <pre>
	 * img/ship/
	 * </pre>
	 * 
	 * @param ship
	 *            the ship to which this image belongs (hull and gib images
	 *            go to different folders depending on the type of the ship)
	 * @return the dat-relative path for this image type
	 */
	public String getDatRelativePath( ShipObject ship )
	{
		if ( ship == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		switch ( this ) {
			case HULL:
				if ( ship.isPlayerShip() )
					return "img/ship/";
				else
					return "img/ships_glow/";
			case THUMBNAIL:
				return "img/customizeUI/";
			default:
				return "img/ship/";
		}
	}

	/**
	 * @return true if this image type should be exported for the given ship, false otherwie.
	 */
	public boolean shouldSave( ShipObject ship )
	{
		if ( ship == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		switch ( this ) {
			case SHIELD:
			case FLOOR:
			case THUMBNAIL:
				return ship.isPlayerShip();
			default:
				return true;
		}
	}

	/**
	 * @return the prefix to the ship's image namespace for this image type.
	 */
	public String getPrefix()
	{
		switch ( this ) {
			case THUMBNAIL:
				return "miniship_";
			default:
				return "";
		}
	}

	/**
	 * @return the suffix to the ship's image namespace for this image type.
	 */
	public String getSuffix()
	{
		switch ( this ) {
			case SHIELD:
				return "_shields1";
			case HULL:
				return "_base";
			case FLOOR:
				return "_floor";
			case CLOAK:
				return "_cloak";
			default:
				return "";
		}
	}

	/**
	 * @return the name of the file this image should be exported as
	 */
	public String getFilename( ShipObject ship )
	{
		if ( ship == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		return getPrefix() + ship.getImageNamespace() + getSuffix() + ".png";
	}

	@Override
	public String toString()
	{
		return name().toLowerCase();
	}
}
