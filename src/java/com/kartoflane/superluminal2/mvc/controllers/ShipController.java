package com.kartoflane.superluminal2.mvc.controllers;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.components.interfaces.LocationListener;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.ShipView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.ShipDataComposite;

public class ShipController extends ObjectController {

	private static final String LINE_V_PROP_ID = "LineVertical";
	private static final String LINE_H_PROP_ID = "LineHorizontal";
	private static final String OFFSET_X_PROP_ID = "OffsetX";
	private static final String OFFSET_Y_PROP_ID = "OffsetY";

	protected ShipContainer container = null;
	protected ArrayList<Collidable> collidables = new ArrayList<Collidable>();

	private ShipController(ShipContainer container, ObjectModel model, ShipView view) {
		super();
		setModel(model);
		createProps();
		setView(view);

		this.container = container;

		Point gridSize = Grid.getInstance().getSize();
		setSelectable(true);
		setLocModifiable(false);
		setBounded(true);
		gridSize = Grid.getInstance().snapToGrid(gridSize, Snapmodes.CROSS);
		setBoundingArea(0, 0, gridSize.x, gridSize.y);
		setSnapMode(Snapmodes.CROSS);

		setSize(ShipContainer.CELL_SIZE / 2, ShipContainer.CELL_SIZE / 2);
	}

	public static ShipController newInstance(ShipContainer container, ShipObject object) {
		ObjectModel model = new ObjectModel(object);
		ShipView view = new ShipView();
		ShipController controller = new ShipController(container, model, view);
		controller.updateProps();

		return controller;
	}

	private void createProps() {
		LinePropController lpc = new LinePropController(this, LINE_H_PROP_ID);
		lpc.setBorderColor(255, 0, 0);
		lpc.setLayer(Layers.SHIP_ORIGIN);
		addProp(lpc);

		lpc = new LinePropController(this, LINE_V_PROP_ID);
		lpc.setBorderColor(0, 255, 0);
		lpc.setLayer(Layers.SHIP_ORIGIN);
		addProp(lpc);

		OffsetPropController opc = new OffsetPropController(this, OFFSET_X_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				ShipContainer.CELL_SIZE / 2, 0,
				ShipContainer.CELL_SIZE / 4, ShipContainer.CELL_SIZE / 2
		}));
		opc.addLocationListener(new LocationListener() {
			@Override
			public void notifyLocationChanged(int x, int y) {
				ShipObject ship = getGameObject();
				x = (x - getX()) / ShipContainer.CELL_SIZE;
				if (!isSelected() && ship.getXOffset() != x) {
					saveCollidables();
					setShipOffset(x, ship.getYOffset());
					restoreCollidables();
				}
			}
		});
		opc.setBackgroundColor(0, 255, 0);
		opc.setLayer(Layers.SHIP_ORIGIN);
		opc.setSnapMode(Snapmodes.EDGE_V);
		opc.setFollowOffset(0, -ShipContainer.CELL_SIZE / 2);
		opc.setCompositeTitle("X Offset");
		addProp(opc);

		opc = new OffsetPropController(this, OFFSET_Y_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				0, ShipContainer.CELL_SIZE / 2,
				ShipContainer.CELL_SIZE / 2, ShipContainer.CELL_SIZE / 4
		}));
		opc.addLocationListener(new LocationListener() {
			@Override
			public void notifyLocationChanged(int x, int y) {
				ShipObject ship = getGameObject();
				y = (y - getY()) / ShipContainer.CELL_SIZE;
				if (!isSelected() && ship.getYOffset() != y) {
					saveCollidables();
					setShipOffset(ship.getXOffset(), y);
					restoreCollidables();
				}
			}
		});
		opc.setBackgroundColor(255, 0, 0);
		opc.setLayer(Layers.SHIP_ORIGIN);
		opc.setSnapMode(Snapmodes.EDGE_H);
		opc.setFollowOffset(-ShipContainer.CELL_SIZE / 2, 0);
		opc.setCompositeTitle("Y Offset");
		addProp(opc);
	}

	@Override
	public void select() {
		super.select();

		saveCollidables();
	}

	@Override
	public void deselect() {
		super.deselect();

		restoreCollidables();
	}

	private void saveCollidables() {
		for (Follower fol : getFollowers()) {
			if (fol instanceof Collidable) {
				Collidable c = (Collidable) fol;
				if (c.isCollidable()) {
					c.setCollidable(false);
					collidables.add(c);
				}
			}
		}
	}

	private void restoreCollidables() {
		for (Collidable c : collidables)
			c.setCollidable(c instanceof RoomController == false || (c instanceof RoomController == true && !Manager.allowRoomOverlap));
		collidables.clear();
	}

	public void setShipOffset(int x, int y) {
		ShipObject ship = getGameObject();
		for (Follower fol : getFollowers()) {
			if (fol instanceof PropController == false) {
				Point old = fol.getFollowOffset();
				fol.setFollowOffset(old.x + (x - ship.getXOffset()) * ShipContainer.CELL_SIZE,
						old.y + (y - ship.getYOffset()) * ShipContainer.CELL_SIZE);
				fol.updateFollower();
			}
		}

		ship.setXOffset(x);
		ship.setYOffset(y);
	}

	@Override
	public ShipObject getGameObject() {
		return (ShipObject) getModel().getGameObject();
	}

	protected ShipView getView() {
		return (ShipView) view;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.SHIP_ORIGIN);
	}

	public boolean isPlayerShip() {
		return getGameObject().isPlayerShip();
	}

	@Override
	public boolean setLocation(int x, int y) {
		boolean result = super.setLocation(x, y);
		updateView();
		updateProps();

		return result;
	}

	public void updateProps() {
		updateLines();
		updateOffsets();
	}

	private void updateLines() {
		Point gridSize = Grid.getInstance().getSize();
		LinePropController hLine = (LinePropController) getProp(LINE_H_PROP_ID);
		LinePropController vLine = (LinePropController) getProp(LINE_V_PROP_ID);
		hLine.setSize(gridSize.x, 1);
		vLine.setSize(1, gridSize.y);
		hLine.setFollowOffset(hLine.getW() / 2, 0);
		vLine.setFollowOffset(0, vLine.getH() / 2);
		hLine.updateFollower();
		vLine.updateFollower();
	}

	private void updateOffsets() {
		int cellsize = ShipContainer.CELL_SIZE;
		ShipObject ship = getGameObject();
		Point[] bounds = getBoundingPoints();

		OffsetPropController hOff = (OffsetPropController) getProp(OFFSET_X_PROP_ID);
		OffsetPropController vOff = (OffsetPropController) getProp(OFFSET_Y_PROP_ID);
		hOff.setBoundingPoints(getX(), getY() - cellsize / 2,
				bounds[1].x + ship.getXOffset() * cellsize, getY() - cellsize / 2);
		vOff.setBoundingPoints(getX() - cellsize / 2, getY(),
				getX() - cellsize / 2, bounds[1].y + ship.getYOffset() * cellsize);
		hOff.setFollowOffset(cellsize * ship.getXOffset(), -cellsize / 2);
		vOff.setFollowOffset(-cellsize / 2, cellsize * ship.getYOffset());
		hOff.updateFollower();
		vOff.updateFollower();
	}

	public void setVisible(boolean vis) {
		super.setVisible(vis);
		getProp(LINE_H_PROP_ID).setVisible(vis);
		getProp(LINE_V_PROP_ID).setVisible(vis);
		getProp(OFFSET_X_PROP_ID).setVisible(vis);
		getProp(OFFSET_Y_PROP_ID).setVisible(vis);
	}

	@Override
	public int getPresentedFactor() {
		return ShipContainer.CELL_SIZE;
	}

	@Override
	public Point getPresentedLocation() {
		return new Point(getX() / getPresentedFactor(), getY() / getPresentedFactor());
	}

	@Override
	public Point getPresentedSize() {
		throw new UnsupportedOperationException();
	}

	/**
	 * This controller has insufficient information to update its own bounding area (needs data from ShipContainer)<br>
	 * Use {@link ShipContainer#updateBoundingArea()} instead.
	 */
	@Override
	public void updateBoundingArea() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new ShipDataComposite(parent, this);
	}

	@Override
	public void notifyModShift(boolean pressed) {
		setFollowActive(!pressed);
		if (pressed) {
			ShipObject ship = getGameObject();
			setBoundingPoints(0, 0,
					getX() + ship.getXOffset() * ShipContainer.CELL_SIZE,
					getY() + ship.getYOffset() * ShipContainer.CELL_SIZE);
		} else {
			Point offset = Manager.getCurrentShip().findShipOffset();
			offset.x /= ShipContainer.CELL_SIZE;
			offset.y /= ShipContainer.CELL_SIZE;
			setShipOffset(offset.x, offset.y);
			Manager.getCurrentShip().updateBoundingArea();
		}
	}

	private class LinePropController extends PropController {
		public LinePropController(AbstractController parent, String id) {
			super(parent, id);
			setBackgroundColor(null);
			setImage(null);
			setBorderThickness(1);
			setAlpha(255);
		}
	}

	private class OffsetPropController extends PropController {
		public OffsetPropController(AbstractController parent, String id) {
			super(parent, id);

			setDeletable(false);
			setBounded(true);
			setSelectable(true);

			setBorderColor(0, 0, 0);
			setImage(null);
			setBorderThickness(3);
			setAlpha(255);

			setShape(Shapes.POLYGON);
			setSize(3 * ShipContainer.CELL_SIZE / 2, 3 * ShipContainer.CELL_SIZE / 2);
		}

		@Override
		public boolean setLocation(int x, int y) {
			boolean result = super.setLocation(x, y);
			polygon.setLocation(getX(), getY());
			return result;
		}

		@Override
		public boolean translate(int dx, int dy) {
			boolean result = super.translate(dx, dy);
			polygon.setLocation(getX(), getY());
			return result;
		}

		/**
		 * @param rad
		 *            angle in radians, 0 = north
		 */
		@Override
		public void setRotation(float rad) {
			polygon.rotate(rad);
		}

		/**
		 * @param rad
		 *            angle in radians, 0 = north
		 */
		@Override
		public void rotate(float rad) {
			polygon.rotate(rad);
		}

		@Override
		public int getPresentedFactor() {
			return ShipContainer.CELL_SIZE;
		}

		@Override
		public Point getPresentedLocation() {
			return new Point(getX() / getPresentedFactor(), getY() / getPresentedFactor());
		}

		@Override
		public boolean contains(int x, int y) {
			return polygon.contains(x, y);
		}

		@Override
		public void select() {
			super.select();
			setBounded(true);
		}

		@Override
		public void deselect() {
			super.deselect();
			setBounded(false);
			Manager.getCurrentShip().updateBoundingArea();
			Manager.getCurrentShip().updateChildBoundingAreas();
		}
	}
}
