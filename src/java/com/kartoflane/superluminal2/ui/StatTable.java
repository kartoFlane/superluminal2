package com.kartoflane.superluminal2.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.kartoflane.superluminal2.components.Tuple;


public class StatTable extends Table
{
	private TableColumn colStat;
	private TableColumn colValue;

	private Map<String, TableItem> statMap = new HashMap<String, TableItem>();


	public StatTable( Composite parent )
	{
		super( parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
		setHeaderVisible( true );
		setLinesVisible( true );

		// remove the horizontal bar so that it doesn't flicker when the table is resized
		getHorizontalBar().dispose();

		colStat = new TableColumn( this, SWT.LEFT );
		colStat.setWidth( 100 );
		colStat.setText( "Stat" );
		colStat.setMoveable( false );
		colStat.setResizable( false );

		colValue = new TableColumn( this, SWT.RIGHT );
		colValue.setWidth( 100 );
		colValue.setText( "Value" );
		colValue.setMoveable( false );
		colValue.setResizable( false );

		addListener(
			SWT.Resize, new Listener() {
				public void handleEvent( Event e )
				{
					updateColumnWidth();
				}
			}
		);
	}

	@Override
	protected void checkSubclass()
	{
		// Need to override this in order to allow subclassing
	}

	public void addEntry( String name, String value )
	{
		if ( name == null || value == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		TableItem tbtm = null;
		if ( statMap.containsKey( name ) )
			tbtm = statMap.get( name );
		else
			tbtm = new TableItem( this, SWT.NONE );
		tbtm.setText( 0, name );
		tbtm.setText( 1, value );

		statMap.put( name, tbtm );
	}

	public void addEntries( Tuple<String, String>[] stats )
	{
		if ( stats == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		for ( Tuple<String, String> entry : stats )
			addEntry( entry.getKey(), entry.getValue() );
	}

	public void removeEntry( String name )
	{
		if ( name == null )
			throw new IllegalArgumentException( "Argument must not be null." );
		TableItem tbtm = statMap.get( name );
		if ( tbtm != null )
			tbtm.dispose();
		statMap.remove( name );
	}

	public void clear()
	{
		String[] keys = statMap.keySet().toArray( new String[0] );
		for ( String key : keys )
			removeEntry( key );
		layout();
		statMap.clear();
	}

	public void updateColumnWidth()
	{
		int hw = getSize().x;
		ScrollBar sb = getVerticalBar();
		if ( sb != null && sb.isVisible() )
			hw -= sb.getSize().x;
		hw /= 2;
		hw -= getBorderWidth();

		colStat.setWidth( hw );
		colValue.setWidth( hw );

		layout();
	}
}
