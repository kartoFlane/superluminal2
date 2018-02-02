package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.enums.Directions;


/**
 * A Combo widget with some predefined items, plus convenience methods.<br>
 * <br>
 * Subclassing SWT widgets is generally bad practice, however this is a simple
 * wrapper class that automates instantiation to achieve consistent behaviour
 * and reduce the amount of redundant code across the application.<br>
 * <br>
 * Since it doesn't touch any of the internal methods, it should be fine.
 * 
 * @author kartoFlane
 * 
 */
public class DirectionCombo extends Combo
{
	private final boolean full;


	/**
	 * Constructs a new instance of the Combo widget and fills it with predefined items,
	 * representing values of the {@link Directions} enum.
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @param full
	 *            whether to use all values of the {@link Directions} enum; true uses all values, false
	 *            omits {@link Directions#NONE}
	 * 
	 * @see {@link Combo}
	 */
	public DirectionCombo( Composite parent, int style, boolean full )
	{
		super( parent, style );
		this.full = full;

		add( "Up" );
		add( "Left" );
		add( "Right" );
		add( "Down" );
		if ( full )
			add( "None" );
	}

	@Override
	protected void checkSubclass()
	{
		// Need to override this in order to allow subclassing
	}

	public void select( Directions dir )
	{
		if ( dir == Directions.NONE && !full )
			throw new IllegalArgumentException( "DirectionCombo wasn't instantiated as full and doesn't have an item for Directions.NONE" );
		select( toIndex( dir ) );
	}

	public Directions getDirection()
	{
		return toDirection( getSelectionIndex() );
	}

	public static int toIndex( Directions dir )
	{
		switch ( dir ) {
			case UP:
				return 0;
			case LEFT:
				return 1;
			case RIGHT:
				return 2;
			case DOWN:
				return 3;
			case NONE:
				return 4;
			default:
				throw new IllegalArgumentException( "Unknown direction: " + dir );
		}
	}

	public static Directions toDirection( int index )
	{
		switch ( index ) {
			case 0:
				return Directions.UP;
			case 1:
				return Directions.LEFT;
			case 2:
				return Directions.RIGHT;
			case 3:
				return Directions.DOWN;
			case 4:
				return Directions.NONE;
			default:
				throw new IllegalArgumentException( "Unknown index: " + index );
		}
	}
}
