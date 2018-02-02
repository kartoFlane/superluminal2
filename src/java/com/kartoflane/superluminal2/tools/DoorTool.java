package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.LayeredPainter;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.DoorToolComposite;
import com.kartoflane.superluminal2.undo.UndoableCreateEdit;
import com.kartoflane.superluminal2.utils.Utils;


public class DoorTool extends Tool
{
	/** A flag indicating whether or not the door can be placed */
	private boolean canCreate = false;
	private boolean horizontal = false;


	public DoorTool( EditorWindow window )
	{
		super( window );
	}

	@Override
	public void select()
	{
		cursor.setSnapMode( Snapmodes.EDGES );
		cursor.setVisible( false );

		CreationTool ctool = (CreationTool)Manager.getTool( Tools.CREATOR );
		ctool.getToolComposite( null ).clearDataContainer();
		Composite dataC = ctool.getToolComposite( null ).getDataContainer();
		createToolComposite( dataC );
		dataC.layout( true );
	}

	@Override
	public void deselect()
	{
		cursor.setVisible( false );

		CreationTool ctool = (CreationTool)Manager.getTool( Tools.CREATOR );
		ctool.getToolComposite( null ).clearDataContainer();
	}

	@Override
	public DoorToolComposite getToolComposite( Composite parent )
	{
		return (DoorToolComposite)super.getToolComposite( parent );
	}

	@Override
	public DoorToolComposite createToolComposite( Composite parent )
	{
		if ( parent == null )
			throw new IllegalArgumentException( "Parent must not be null." );
		compositeInstance = new DoorToolComposite( parent );
		return (DoorToolComposite)compositeInstance;
	}

	@Override
	public void mouseDoubleClick( MouseEvent e )
	{
	}

	@Override
	public void mouseDown( MouseEvent e )
	{
		// Check the conditions again in case the variable is outdated
		canCreate = canCreate();

		if ( e.button == 1 && canCreate ) {
			ShipContainer container = Manager.getCurrentShip();
			DoorObject object = new DoorObject( horizontal );
			DoorController doorController = DoorController.newInstance( container, object );
			Rectangle oldBounds = doorController.getBounds();

			doorController.setLocation( Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() ) );
			doorController.setBounded( true );

			Point p = doorController.getLocation();
			doorController.setFollowOffset( p.x - doorController.getParent().getX(), p.y - doorController.getParent().getY() );

			container.add( doorController );
			container.store( doorController );

			window.canvasRedraw( oldBounds );
			doorController.redraw();

			container.updateBoundingArea();
			OverviewWindow.staticUpdate( doorController );

			container.postEdit( new UndoableCreateEdit( doorController ) );
		}
		// handle cursor
		if ( cursor.isVisible() && e.button == 1 ) {
			cursor.setVisible( false );
		}
	}

	@Override
	public void mouseUp( MouseEvent e )
	{
		// handle cursor
		if ( e.button == 1 ) {
			Point p = Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() );
			horizontal = p.y % ShipContainer.CELL_SIZE == 0;

			cursor.resize( horizontal ? 34 : ShipContainer.CELL_SIZE / 3, horizontal ? ShipContainer.CELL_SIZE / 3 : 34 );
			cursor.reposition( p.x, p.y );

			canCreate = canCreate();
			cursor.updateView();
			cursor.redraw();
		}
	}

	@Override
	public void mouseMove( MouseEvent e )
	{
		// move the cursor around to follow mouse
		Point p = Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() );
		horizontal = p.y % ShipContainer.CELL_SIZE == 0;
		cursor.resize( horizontal ? 34 : ShipContainer.CELL_SIZE / 3, horizontal ? ShipContainer.CELL_SIZE / 3 : 34 );
		cursor.reposition( p.x, p.y );

		canCreate = canCreate();
		cursor.updateView();
		cursor.redraw();
	}

	@Override
	public void mouseEnter( MouseEvent e )
	{
		cursor.setVisible( !Manager.leftMouseDown );
		cursor.redraw();
	}

	@Override
	public void mouseExit( MouseEvent e )
	{
		cursor.setVisible( false );
		cursor.redraw();
	}

	@Override
	public void mouseHover( MouseEvent e )
	{
	}

	private boolean canCreate()
	{
		if ( !Manager.getCurrentShip().isDoorsVisible() )
			return false;

		AbstractController control = LayeredPainter.getInstance().getSelectableControllerAt( cursor.getLocation() );
		if ( control != null && control instanceof RoomController ) {
			return control.getBounds().intersects( cursor.getBounds() ) && !Utils.contains( control.getBounds(), cursor.getBounds() );
		}
		else
			return false;
	}

	public boolean canPlace()
	{
		return canCreate;
	}

	public boolean isHorizontal()
	{
		return horizontal;
	}
}
