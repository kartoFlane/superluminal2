package com.kartoflane.superluminal2.mvc.controllers;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.ShipView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.PropDataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.ShipDataComposite;

public class ShipController extends ObjectController {

	public static final String LINE_V_PROP_ID = "LineVertical";
	public static final String LINE_H_PROP_ID = "LineHorizontal";
	public static final String OFFSET_X_PROP_ID = "OffsetX";
	public static final String OFFSET_Y_PROP_ID = "OffsetY";
	public static final String OFFSET_FINE_X_PROP_ID = "OffsetHorizontal";
	public static final String OFFSET_FINE_Y_PROP_ID = "OffsetVertical";

	protected ShipContainer container = null;
	protected ArrayList<Collidable> collidables = new ArrayList<Collidable>();

	private ShipController(ShipContainer container, ObjectModel model, ShipView view) {
		super();
		setModel(model);
		setView(view);

		this.container = container;

		Point gridSize = Grid.getInstance().getSize();
		setSelectable(true);
		setLocModifiable(false);
		setBounded(true);
		gridSize = Grid.getInstance().snapToGrid(gridSize, Snapmodes.CROSS);
		setBoundingArea(0, 0, gridSize.x, gridSize.y);
		setSnapMode(Snapmodes.CROSS);
		setPresentedFactor(ShipContainer.CELL_SIZE);
		setSize(ShipContainer.CELL_SIZE / 2, ShipContainer.CELL_SIZE / 2);

		createProps();
	}

	public static ShipController newInstance(ShipContainer container, ShipObject object) {
		ObjectModel model = new ObjectModel(object);
		ShipView view = new ShipView();
		ShipController controller = new ShipController(container, model, view);
		controller.updateProps();

		return controller;
	}

	private void createProps() {
		OffsetPropController opc = new OffsetPropController(this, OFFSET_FINE_X_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				ShipContainer.CELL_SIZE / 2, 0,
				ShipContainer.CELL_SIZE / 4, 2 * ShipContainer.CELL_SIZE / 3
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {
				if (isPlayerShip()) {
					Point p = (Point) e.data;
					ShipObject ship = getGameObject();
					container.setShipFineOffset(p.x - getX(), ship.getVertical());
				}
			}
		});
		opc.setLocModifiable(true);
		opc.setDefaultBackgroundColor(192, 0, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.SHIP_ORIGIN);
		opc.setCompositeTitle("Horizontal Offset");
		opc.setInheritVisibility(isPlayerShip());
		opc.setVisible(isPlayerShip());
		addProp(opc);

		opc = new OffsetPropController(this, OFFSET_FINE_Y_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				0, ShipContainer.CELL_SIZE / 2,
				2 * ShipContainer.CELL_SIZE / 3, ShipContainer.CELL_SIZE / 4
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {
				Point p = (Point) e.data;
				ShipObject ship = getGameObject();
				container.setShipFineOffset(ship.getHorizontal(), p.y - getY());
			}
		});
		opc.setLocModifiable(true);
		opc.setDefaultBackgroundColor(0, 192, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.SHIP_ORIGIN);
		opc.setCompositeTitle("Vertical Offset");
		addProp(opc);

		opc = new OffsetPropController(this, OFFSET_X_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				ShipContainer.CELL_SIZE / 2, 0,
				ShipContainer.CELL_SIZE / 4, ShipContainer.CELL_SIZE / 2
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {
				Point p = (Point) e.data;
				ShipObject ship = getGameObject();
				p.x = (p.x - getX()) / ShipContainer.CELL_SIZE;
				if (!isSelected() && ship.getXOffset() != p.x) {
					saveCollidables();
					container.setShipOffset(p.x, ship.getYOffset());
					restoreCollidables();
				}
			}
		});
		opc.setDefaultBackgroundColor(255, 0, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.SHIP_ORIGIN);
		opc.setSnapMode(Snapmodes.EDGE_V);
		opc.setPresentedFactor(ShipContainer.CELL_SIZE);
		opc.setCompositeTitle("X Offset");
		addProp(opc);

		opc = new OffsetPropController(this, OFFSET_Y_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				0, ShipContainer.CELL_SIZE / 2,
				ShipContainer.CELL_SIZE / 2, ShipContainer.CELL_SIZE / 4
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {
				Point p = (Point) e.data;
				ShipObject ship = getGameObject();
				p.y = (p.y - getY()) / ShipContainer.CELL_SIZE;
				if (!isSelected() && ship.getYOffset() != p.y) {
					saveCollidables();
					container.setShipOffset(ship.getXOffset(), p.y);
					restoreCollidables();
				}
			}
		});
		opc.setDefaultBackgroundColor(0, 255, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.SHIP_ORIGIN);
		opc.setSnapMode(Snapmodes.EDGE_H);
		opc.setPresentedFactor(ShipContainer.CELL_SIZE);
		opc.setCompositeTitle("Y Offset");
		addProp(opc);

		PropController prop = new PropController(this, LINE_H_PROP_ID);
		prop.setInheritVisibility(true);
		prop.setDefaultBackgroundColor(255, 0, 0);
		prop.setImage(null);
		prop.setBorderThickness(1);
		prop.setAlpha(255);
		prop.addToPainterBottom(Layers.SHIP_ORIGIN);
		addProp(prop);

		prop = new PropController(this, LINE_V_PROP_ID);
		prop.setInheritVisibility(true);
		prop.setDefaultBackgroundColor(0, 255, 0);
		prop.setImage(null);
		prop.setBorderThickness(1);
		prop.setAlpha(255);
		prop.addToPainterBottom(Layers.SHIP_ORIGIN);
		addProp(prop);
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
		PropController hLine = getProp(LINE_H_PROP_ID);
		PropController vLine = getProp(LINE_V_PROP_ID);
		hLine.setSize(gridSize.x, 1);
		vLine.setSize(1, gridSize.y);
		hLine.setFollowOffset(hLine.getW() / 2, 0);
		vLine.setFollowOffset(0, vLine.getH() / 2);
		hLine.updateFollower();
		vLine.updateFollower();
	}

	private void updateOffsets() {
		int cs = ShipContainer.CELL_SIZE;
		ShipObject ship = getGameObject();
		Point[] bounds = getBoundingPoints();
		Point gridSize = Grid.getInstance().getSize();

		PropController prop = getProp(OFFSET_X_PROP_ID);
		prop.setBoundingPoints(getX(), getY() - cs / 2,
				bounds[1].x + ship.getXOffset() * cs, getY() - cs / 2);
		prop.setFollowOffset(cs * ship.getXOffset(), -cs / 2);
		prop.updateFollower();

		prop = getProp(OFFSET_Y_PROP_ID);
		prop.setBoundingPoints(getX() - cs / 2, getY(),
				getX() - cs / 2, bounds[1].y + ship.getYOffset() * cs);
		prop.setFollowOffset(-cs / 2, cs * ship.getYOffset());
		prop.updateFollower();

		prop = getProp(OFFSET_FINE_X_PROP_ID);
		prop.setBoundingPoints(0, getY() - 2 * cs / 3,
				gridSize.x, getY() - 2 * cs / 3);
		prop.setFollowOffset(ship.getHorizontal(), -2 * cs / 3);
		prop.updateFollower();

		prop = getProp(OFFSET_FINE_Y_PROP_ID);
		prop.setBoundingPoints(getX() - 2 * cs / 3, 0,
				getX() - 2 * cs / 3, gridSize.y);
		prop.setFollowOffset(-2 * cs / 3, ship.getVertical());
		prop.updateFollower();
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
	public void handleEvent(SLEvent e) {
		if (e.type == SLEvent.MOD_SHIFT) {
			boolean pressed = (Boolean) e.data;
			setFollowActive(!pressed);
			if (pressed) {
				ShipObject ship = getGameObject();
				setBoundingPoints(0, 0,
						getX() + ship.getXOffset() * ShipContainer.CELL_SIZE,
						getY() + ship.getYOffset() * ShipContainer.CELL_SIZE);
			} else {
				Point offset = container.findShipOffset();
				offset.x /= ShipContainer.CELL_SIZE;
				offset.y /= ShipContainer.CELL_SIZE;
				container.setShipOffset(offset.x, offset.y);
				container.updateBoundingArea();
			}
		} else {
			super.handleEvent(e);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (isSelected() && Manager.modShift) {
			Point offset = container.findShipOffset();
			offset.x /= ShipContainer.CELL_SIZE;
			offset.y /= ShipContainer.CELL_SIZE;
			container.setShipOffset(offset.x, offset.y);
			updateProps();
		}
		super.mouseUp(e);
	}

	private class OffsetPropController extends PropController {
		public OffsetPropController(AbstractController parent, String id) {
			super(parent, id);

			setInheritVisibility(true);
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
			container.updateBoundingArea();
			container.updateChildBoundingAreas();
		}

		@Override
		public DataComposite getDataComposite(Composite parent) {
			return new PropDataComposite(parent, this);
		}
	}
}
