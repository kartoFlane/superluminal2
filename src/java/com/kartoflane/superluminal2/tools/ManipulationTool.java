package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.LayeredPainter;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;
import com.kartoflane.superluminal2.undo.UndoableDoorLinkEdit;
import com.kartoflane.superluminal2.undo.UndoableGibLinkEdit;


public class ManipulationTool extends Tool
{
	private enum States
	{
		NORMAL,
		ROOM_RESIZE,
		DOOR_LINK_LEFT,
		DOOR_LINK_RIGHT,
		MOUNT_GIB_LINK
	}


	private States state = States.NORMAL;
	private Point clickPoint = new Point( 0, 0 );


	public ManipulationTool( EditorWindow window )
	{
		super( window );
	}

	@Override
	public void select()
	{
		window.disposeSidebarContent();

		ManipulationToolComposite pointerC = getToolComposite( window.getSidebarWidget() );
		window.setSidebarContent( pointerC );

		OverviewWindow.staticUpdate();
		Manager.setSelected( null );
		setStateManipulate();

		cursor.setSize( ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE );
		cursor.setVisible( false );
	}

	@Override
	public void deselect()
	{
		Manager.setSelected( null );

		AbstractController control = LayeredPainter.getInstance().getSelectableControllerAt( cursor.getMouseLocation() );
		if ( control != null ) {
			control.setHighlighted( false );
		}

		cursor.setVisible( false );
	}

	@Override
	public ManipulationToolComposite getToolComposite( Composite parent )
	{
		return (ManipulationToolComposite)super.getToolComposite( parent );
	}

	@Override
	public ManipulationToolComposite createToolComposite( Composite parent )
	{
		if ( parent == null )
			throw new IllegalArgumentException( "Parent must not be null." );
		compositeInstance = new ManipulationToolComposite( parent, true, false );
		return (ManipulationToolComposite)compositeInstance;
	}

	public void setStateManipulate()
	{
		state = States.NORMAL;
		cursor.updateView();
	}

	public boolean isStateManipulate()
	{
		return state == States.NORMAL;
	}

	public void setStateDoorLinkLeft()
	{
		state = States.DOOR_LINK_LEFT;
		cursor.updateView();
	}

	public void setStateDoorLinkRight()
	{
		state = States.DOOR_LINK_RIGHT;
		cursor.updateView();
	}

	public boolean isStateDoorLinkLeft()
	{
		return state == States.DOOR_LINK_LEFT;
	}

	public boolean isStateDoorLinkRight()
	{
		return state == States.DOOR_LINK_RIGHT;
	}

	public void setStateMountGibLink()
	{
		state = States.MOUNT_GIB_LINK;
		cursor.updateView();
	}

	public boolean isStateMountGibLink()
	{
		return state == States.MOUNT_GIB_LINK;
	}

	public void setStateRoomResize()
	{
		state = States.ROOM_RESIZE;
		cursor.updateView();
	}

	public boolean isStateRoomResize()
	{
		return state == States.ROOM_RESIZE;
	}

	@Override
	public void mouseDoubleClick( MouseEvent e )
	{
	}

	@Override
	public void mouseDown( MouseEvent e )
	{
		clickPoint.x = e.x;
		clickPoint.y = e.y;

		AbstractController selected = Manager.getSelected();

		if ( state == States.NORMAL ) {
			// get the controller at the mouse click pos, and if found, notify it about mouseDown event
			AbstractController controller = getTopmostSelectableController( e.x, e.y );
			if ( controller != null )
				controller.mouseDown( e );
			else if ( selected != null )
				selected.mouseDown( e );
		}
		// not using else if here, since a state change can occur in the previous step
		if ( state == States.ROOM_RESIZE ) {
			RoomController room = (RoomController)selected;

			cursor.updateView();
			if ( e.button == 3 )
				cursor.setVisible( false );
			cursor.resize( room.getSize() );
			cursor.reposition( room.getLocation() );

		}
		else if ( state == States.DOOR_LINK_LEFT || state == States.DOOR_LINK_RIGHT ) {
			// get the controller at the mouse click pos
			AbstractController control = null;
			for ( int i = selectableLayerIds.length - 1; i >= 0; i-- ) {
				if ( selectableLayerIds[i] != null ) {
					control = LayeredPainter.getInstance().getSelectableControllerAt( e.x, e.y, selectableLayerIds[i] );
					if ( control != null && control instanceof RoomController )
						break;
				}
			}

			linkDoor( selected, control );

		}
		else if ( state == States.MOUNT_GIB_LINK ) {
			// get the controller at the mouse click pos, and if found, notify it about mouseDown event
			AbstractController control = null;
			for ( int i = selectableLayerIds.length - 1; i >= 0; i-- ) {
				if ( selectableLayerIds[i] != null ) {
					control = LayeredPainter.getInstance().getSelectableControllerAt( e.x, e.y, selectableLayerIds[i] );
					if ( control != null && control instanceof GibController )
						break;
				}
			}

			linkGib( selected, control );
		}

		// handle cursor
		if ( cursor.isVisible() && e.button == 1 ) {
			cursor.setVisible( state == States.ROOM_RESIZE );
		}
	}

	@Override
	public void mouseUp( MouseEvent e )
	{
		AbstractController controller = null;
		// inform the selected controller about clicks, so that it can be deselected
		// prevents a bug:
		// --controllers A and B are on the same layer, but A is "higher" than B
		// --dragging B under A, and releasing mouse button while cursor is within A's bounds, causes B to become stuck to the mouse pointer
		if ( Manager.getSelected() != null )
			Manager.getSelected().mouseUp( e );

		if ( state == States.NORMAL ) {
			// find the topmost selectable controller and notify it about mouseUp event
			for ( int i = selectableLayerIds.length - 1; i >= 0; i-- ) {
				if ( selectableLayerIds[i] != null ) {
					controller = LayeredPainter.getInstance().getSelectableControllerAt( e.x, e.y, selectableLayerIds[i] );
					if ( controller != null && controller != Manager.getSelected() ) {
						controller.mouseUp( e );
						break;
					}
				}
			}
			Point p = Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() );
			cursor.updateView();
			cursor.reposition( p.x, p.y );
		}
		else if ( state == States.ROOM_RESIZE ) {
			RoomController room = (RoomController)Manager.getSelected();
			Point p = Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() );
			cursor.setVisible( false );
			cursor.redraw();
			cursor.updateView();
			cursor.resize( ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE );
			cursor.reposition( p.x, p.y );

			if ( !room.isResizing() )
				setStateManipulate();

		}
	}

	@Override
	public void mouseMove( MouseEvent e )
	{
		super.mouseMove( e );

		// faciliate mouseEnter event for controllers
		AbstractController controller = getTopmostSelectableController( e.x, e.y );
		if ( controller != null ) {
			// mouseEnter event also highlights the controller, so check if its not highlighted already
			if ( !controller.isHighlighted() && ( Manager.getSelected() == null || !Manager.getSelected().isMoving() ) )
				controller.mouseEnter( e );
		}

		if ( state == States.NORMAL ) {
			// move the cursor around to follow mouse
			Point p = Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() );
			// always redraw it - prevents an odd visual bug where the controller sometimes doesn't register that it was moved
			cursor.reposition( p.x, p.y );
			cursor.updateView();

		}
		else if ( state == States.ROOM_RESIZE ) {
			RoomController room = (RoomController)Manager.getSelected();

			Point resizeAnchor = room.getResizeAnchor();

			// choose appropriate snapmode depending on the direction in which the user is resizing
			Snapmodes resizeSnapmode = null;
			if ( e.x >= resizeAnchor.x && e.y >= resizeAnchor.y )
				resizeSnapmode = Snapmodes.CORNER_BR;
			else if ( e.x >= resizeAnchor.x && e.y < resizeAnchor.y )
				resizeSnapmode = Snapmodes.CORNER_TR;
			else if ( e.x < resizeAnchor.x && e.y >= resizeAnchor.y )
				resizeSnapmode = Snapmodes.CORNER_BL;
			else
				resizeSnapmode = Snapmodes.CORNER_TL;

			Point pointer = Grid.getInstance().snapToGrid( e.x, e.y, resizeSnapmode );
			Point anchor = new Point( 0, 0 );

			if ( pointer.x < resizeAnchor.x )
				// if mouse is to the left of the anchor...
				anchor.x = resizeAnchor.x + ( clickPoint.x > resizeAnchor.x ? ShipContainer.CELL_SIZE : 0 );
			else
				// if mouse is to the right of the anchor...
				anchor.x = resizeAnchor.x + ( clickPoint.x > resizeAnchor.x ? 0 : -ShipContainer.CELL_SIZE );
			if ( pointer.y < resizeAnchor.y )
				// if mouse is above the anchor...
				anchor.y = resizeAnchor.y + ( clickPoint.y > resizeAnchor.y ? ShipContainer.CELL_SIZE : 0 );
			else
				// if mouse is below the anchor...
				anchor.y = resizeAnchor.y + ( clickPoint.y > resizeAnchor.y ? 0 : -ShipContainer.CELL_SIZE );

			anchor = Grid.getInstance().snapToGrid( anchor.x, anchor.y, Snapmodes.CROSS );

			int x = Math.min( anchor.x, pointer.x );
			int y = Math.min( anchor.y, pointer.y );
			int w = Math.max( Math.abs( anchor.x - pointer.x ), ShipContainer.CELL_SIZE );
			int h = Math.max( Math.abs( anchor.y - pointer.y ), ShipContainer.CELL_SIZE );

			if ( cursor.getW() != w || cursor.getH() != h ) {
				cursor.updateView();
				cursor.resize( w, h );
				cursor.reposition( x + w / 2, y + h / 2 );
			}

			if ( !room.isResizing() )
				setStateManipulate();

		}
		else if ( state == States.DOOR_LINK_LEFT || state == States.DOOR_LINK_RIGHT || state == States.MOUNT_GIB_LINK ) {
			// move the cursor around to follow mouse
			Point p = Grid.getInstance().snapToGrid( e.x, e.y, cursor.getSnapMode() );
			// always redraw it - prevents an odd visual bug where the controller sometimes doesn't register that it was moved
			cursor.reposition( p.x, p.y );
			cursor.updateView();
		}

		// faciliate mouseExit event for controllers
		for ( Layers layer : LayeredPainter.getInstance().getLayerMap().descendingKeySet() ) {
			for ( AbstractController control : LayeredPainter.getInstance().getLayerMap().get( layer ) ) {
				if ( control != null && control.isVisible() ) {
					// send mouseExit event to controllers that are obscured by other controllers
					// if:
					// - control is selectable AND
					// --- control doesn't contain the mouse pointer OR
					// ----- control is not the same object as controller AND
					// ----- both control and controller contain the mouse pointer
					if ( control.isSelectable() && ( ( !control.contains( e.x, e.y ) )
						|| ( controller != null && control != controller && control.contains( e.x, e.y ) && controller.contains( e.x, e.y ) ) ) )
						control.mouseExit( e );

					// also pass on mouseMove event
					control.mouseMove( e );
				}
			}
		}
	}

	@Override
	public void mouseEnter( MouseEvent e )
	{
		cursor.setVisible( !Manager.leftMouseDown );
	}

	@Override
	public void mouseExit( MouseEvent e )
	{
		cursor.setVisible( false );
		for ( Layers layer : LayeredPainter.getInstance().getLayerMap().descendingKeySet() ) {
			for ( AbstractController control : LayeredPainter.getInstance().getLayerMap().get( layer ) ) {
				if ( control != null && control.isVisible() ) {
					control.mouseExit( e );
				}
			}
		}
	}

	@Override
	public void mouseHover( MouseEvent e )
	{
	}

	private AbstractController getTopmostSelectableController( int x, int y )
	{
		AbstractController controller = null;
		for ( int i = selectableLayerIds.length - 1; i >= 0 && controller == null; i-- ) {
			if ( selectableLayerIds[i] != null )
				controller = LayeredPainter.getInstance().getSelectableControllerAt( x, y, selectableLayerIds[i] );
		}
		return controller;
	}

	public void linkDoor( AbstractController door, AbstractController room )
	{
		if ( door instanceof DoorController == false ) {
			setStateManipulate();
			return;
		}

		DoorController doorC = (DoorController)door;
		RoomController roomC = null;
		if ( room instanceof RoomController )
			roomC = (RoomController)room;

		if ( state == States.DOOR_LINK_LEFT ) {
			UndoableDoorLinkEdit edit = new UndoableDoorLinkEdit( doorC, true );
			edit.setOld( doorC.getLeftRoom() );
			doorC.setLeftRoom( roomC == null ? null : roomC.getGameObject() );
			edit.setCurrent( doorC.getLeftRoom() );
			Manager.getCurrentShip().postEdit( edit );
		}
		else {
			UndoableDoorLinkEdit edit = new UndoableDoorLinkEdit( doorC, false );
			edit.setOld( doorC.getRightRoom() );
			doorC.setRightRoom( roomC == null ? null : roomC.getGameObject() );
			edit.setCurrent( doorC.getRightRoom() );
			Manager.getCurrentShip().postEdit( edit );
		}

		EditorWindow.getInstance().updateSidebarContent();
		setStateManipulate();
	}

	public void linkGib( AbstractController mount, AbstractController gib )
	{
		if ( mount instanceof MountController == false ) {
			setStateManipulate();
			return;
		}

		MountController mountC = (MountController)mount;
		GibController gibC = null;
		if ( gib instanceof GibController )
			gibC = (GibController)gib;

		// Can be null if the the user hides the mount/mount layer during linking
		if ( mountC != null ) {
			UndoableGibLinkEdit edit = new UndoableGibLinkEdit( mountC );
			edit.setOld( mountC.getGib() );
			mountC.setGib( gibC == null ? Database.DEFAULT_GIB_OBJ : gibC.getGameObject() );
			edit.setCurrent( mountC.getGib() );
			Manager.getCurrentShip().postEdit( edit );
		}

		EditorWindow.getInstance().updateSidebarContent();
		setStateManipulate();
	}
}
