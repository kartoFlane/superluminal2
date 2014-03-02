package com.kartoflane.superluminal2.mvc.controllers;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Triangle;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.RoomModel;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Systems;
import com.kartoflane.superluminal2.mvc.views.RoomView;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;
import com.kartoflane.superluminal2.ui.sidebar.RoomDataComposite;

public class RoomController extends AbstractController implements Alias, Comparable<RoomController> {

	public static final int COLLISION_TOLERANCE = 17;
	public static final int RESIZE_HANDLE_LENGTH = 19;

	protected RoomModel model;
	protected RoomView view;

	protected final ShipController shipController;
	protected SystemController system;
	protected boolean resizing = false;
	protected boolean canResize = false;

	private Point resizeAnchor;
	private Rectangle resizeBounds;
	private Triangle[] resizeHandles;

	private RoomController(ShipController shipController, RoomModel model, RoomView view) {
		super();

		this.shipController = shipController;

		resizeAnchor = new Point(0, 0);
		resizeBounds = new Rectangle(0, 0, 0, 0);

		resizeHandles = new Triangle[4];
		for (int i = 0; i < 4; i++)
			resizeHandles[i] = new Triangle();

		setModel(model);
		setView(view);

		setSelectable(true);
		setParent(shipController);

		setBounded(true);
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with new Model and View objects
	 * 
	 * @param shipController
	 *            the ShipController object to which this room belongs
	 */
	public static RoomController newInstance(ShipController shipController) {
		RoomModel model = new RoomModel();
		RoomView view = new RoomView();
		RoomController controller = new RoomController(shipController, model, view);

		// assign the empty system
		SystemController emptyController = shipController.getSystemController(Systems.EMPTY);
		controller.setSystem(emptyController);
		model.setSystem(emptyController.getModel());

		shipController.add(controller);

		return controller;
	}

	/**
	 * Creates a new controller and view to represent the model.
	 * 
	 * @param shipController
	 *            the ShipController object to which this room belongs
	 */
	public static RoomController newInstance(ShipController shipController, RoomModel model) {
		RoomView view = new RoomView();
		RoomController controller = new RoomController(shipController, model, view);

		shipController.getSystemController(model.getSystem().getId()).assignTo(controller);

		shipController.add(controller);

		return controller;
	}

	/**
	 * Sets the system that is assigned to this room.<br>
	 * This method <b>must not</b> be called directly. Use {@link #assignSystem(SystemController)} instead.
	 */
	protected void setSystem(SystemController system) {
		this.system = system;
	}

	public SystemController getSystemController() {
		return system;
	}

	public ShipController getShipController() {
		return shipController;
	}

	@Override
	public RoomModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (RoomModel) model;
	}

	@Override
	public RoomView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (RoomView) view;

		view.setController(this);
		this.view.addToPainter(Layers.ROOM);
	}

	@Override
	public void setLocation(int x, int y) {
		model.setLocation(x, y);
		updateResizeHandles();
	}

	@Override
	public void translate(int dx, int dy) {
		model.translate(dx, dy);
		updateResizeHandles();
	}

	/**
	 * Change size of the object to the specified values.<br>
	 * 
	 * @param w
	 *            width of the room, in pixels
	 * @param h
	 *            height of the room, in pixels
	 */
	@Override
	public void setSize(int w, int h) {
		model.setSize(w, h);

		w = (w + 1) / CellController.SIZE;
		h = (h + 1) / CellController.SIZE;

		if (w % 2 == 1 && h % 2 == 1)
			setSnapMode(Snapmodes.CELL);
		else if (w % 2 == 1)
			setSnapMode(Snapmodes.EDGE_H);
		else if (h % 2 == 1)
			setSnapMode(Snapmodes.EDGE_V);
		else if (w % 2 == 0 && h % 2 == 0)
			setSnapMode(Snapmodes.CROSS);

		setCollidable(w != 0 && h != 0);
		updateResizeHandles();
		updateBoundingArea();
	}

	public void updateResizeHandles() {
		Rectangle bounds = getBounds();
		// redefine triangles for resize handles
		// top left corner
		int x = bounds.x + 1;
		int y = bounds.y + 1;
		resizeHandles[0].set(x, y, x + RESIZE_HANDLE_LENGTH, y, x, y + RESIZE_HANDLE_LENGTH);

		// top right corner
		x = bounds.x + getW() + view.getBorderThickness() - 1;
		resizeHandles[1].set(x, y, x - RESIZE_HANDLE_LENGTH, y, x, y + RESIZE_HANDLE_LENGTH);

		// bottom right corner
		y = bounds.y + getH() + view.getBorderThickness() - 1;
		resizeHandles[3].set(x, y, x - RESIZE_HANDLE_LENGTH, y, x, y - RESIZE_HANDLE_LENGTH);
		// yes, this is supposed to stay out of order -- this way we can access the triangle
		// on the opposite corner via resizeHandles[3 - id]

		// bottom left corner
		x = bounds.x + 1;
		resizeHandles[2].set(x, y, x + RESIZE_HANDLE_LENGTH, y, x, y - RESIZE_HANDLE_LENGTH);
	}

	@Override
	public void dispose() {
		if (system != null)
			system.assignTo(null);
		view.dispose();
	}

	/** Accessibility for RoomView. */
	public Rectangle getResizeBounds() {
		return Utils.copy(resizeBounds);
	}

	/** Accessibility for RoomView. */
	public Triangle[] getResizeHandles() {
		return resizeHandles;
	}

	public boolean isResizing() {
		return resizing;
	}

	public boolean canResize() {
		return canResize;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			super.mouseDown(e);
			if (e.button == 1 && isSelected() && !isPinned()) {
				for (int i = 0; i < 4; i++) {
					if (resizeHandles[i].contains(e.x, e.y)) {
						resizeAnchor = resizeHandles[3 - i].getFirstPoint();
						canResize = false;
						resizing = true;
						break;
					}
				}
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			super.mouseUp(e);

			if (e.button == 1) {
				if (resizing) {
					// confirm resize
					resizing = false;

					if (canResize) {
						// if the area is clear, resize and reposition the room
						Rectangle oldBounds = model.getBounds();

						setSize(resizeBounds.width, resizeBounds.height);
						setLocation(resizeBounds.x + resizeBounds.width / 2, resizeBounds.y + resizeBounds.height / 2);

						EditorWindow.getInstance().canvasRedraw(oldBounds);
						EditorWindow.getInstance().canvasRedraw(model.getBounds());

						model.setFollowOffset(getX() - model.getParent().getX(), getY() - model.getParent().getY());
						((ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent()).updateData();

						Manager.getCurrentShip().recalculateBoundedArea();
					} else {
						// if collided with a room, just redraw the area (resizeBox isn't visible anymore)
						EditorWindow.getInstance().canvasRedraw(resizeBounds);
					}
				}
			} else if (e.button == 3) {
				if (!selected)
					Manager.setSelected(this);

				if (resizing) {
					// abort resize
					resizing = false;
					setMoving(false);
					EditorWindow.getInstance().canvasRedraw(resizeBounds);
				} else if (selected && getBounds().contains(e.x, e.y)) {
					// open system popup menu
					((RoomDataComposite) ((ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent()).getDataComposite()).showSystemMenu();
				}
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		// super.mouseMove() only handles box movement -- we're implementing a different
		// handling of that functionality here, so we don't call super.mouseMove()
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			if (Manager.leftMouseDown && selected) {
				if (resizing) {
					if (!Grid.getInstance().isLocAccessible(e.x, e.y))
						return;

					// choose appropriate snapmode depending on the direction in which the user is resizing
					Snapmodes resizeSnapmode = (e.x >= resizeAnchor.x && e.y >= resizeAnchor.y ? Snapmodes.CORNER_BR :
							(e.x >= resizeAnchor.x && e.y < resizeAnchor.y ? Snapmodes.CORNER_TR :
									(e.x < resizeAnchor.x && e.y >= resizeAnchor.y ? Snapmodes.CORNER_BL :
											Snapmodes.CORNER_TL)));

					Point cursor = Grid.getInstance().snapToGrid(e.x, e.y, resizeSnapmode);
					Point anchor = Grid.getInstance().snapToGrid(resizeAnchor.x, resizeAnchor.y, Snapmodes.CROSS);
					Point offset = getLocation();
					offset = Grid.getInstance().snapToGrid(offset.x + clickOffset.x, offset.y + clickOffset.y, Snapmodes.CROSS);

					if (cursor.x < resizeAnchor.x)
						anchor.x = resizeAnchor.x + (offset.x > resizeAnchor.x ? CellController.SIZE : 0);
					else
						anchor.x = resizeAnchor.x + (offset.x > resizeAnchor.x ? 0 : -CellController.SIZE);
					if (cursor.y < resizeAnchor.y)
						anchor.y = resizeAnchor.y + (offset.y > resizeAnchor.y ? CellController.SIZE : 0);
					else
						anchor.y = resizeAnchor.y + (offset.y > resizeAnchor.y ? 0 : -CellController.SIZE);

					Rectangle oldResize = Utils.copy(resizeBounds);

					resizeBounds.x = Math.min(anchor.x, cursor.x);
					resizeBounds.y = Math.min(anchor.y, cursor.y);
					resizeBounds.width = Math.abs(anchor.x - cursor.x);
					resizeBounds.height = Math.abs(anchor.y - cursor.y);

					resizeBounds.width -= resizeBounds.width % CellController.SIZE;
					resizeBounds.height -= resizeBounds.height % CellController.SIZE;

					// TODO figure out handling of 0x0 rooms
					resizeBounds.width = Math.max(resizeBounds.width, CellController.SIZE);
					resizeBounds.height = Math.max(resizeBounds.height, CellController.SIZE);

					canResize = true;
					// only redraw when we actually have to
					// if (!oldResize.equals(resizeBounds)) {
					ShipController ship = Manager.getCurrentShip();
					if (resizeBounds.x < ship.getX() || resizeBounds.y < ship.getY())
						canResize = false;

					// calculate collision with other boxes on the same layer
					ArrayList<AbstractController> layer = EditorWindow.getInstance().getPainter().getLayerMap().get(Layers.ROOM);
					for (int i = 0; i < layer.size() && canResize; i++) {
						AbstractController c = layer.get(i);
						if (c != this && c.isVisible() && c.collides(resizeBounds.x + COLLISION_TOLERANCE, resizeBounds.y + COLLISION_TOLERANCE,
								resizeBounds.width - COLLISION_TOLERANCE * 2, resizeBounds.height - COLLISION_TOLERANCE * 2))
							canResize = false;
					}

					EditorWindow.getInstance().canvasRedraw(resizeBounds);
					EditorWindow.getInstance().canvasRedraw(oldResize);
					// }

				} else if (moving) {
					if (!Grid.getInstance().isLocAccessible(e.x, e.y))
						return;

					Point p = Grid.getInstance().snapToGrid(e.x - clickOffset.x - getW() / 2, e.y - clickOffset.y - getH() / 2, Snapmodes.CROSS);
					Rectangle checkBounds = new Rectangle(p.x, p.y, getW(), getH());

					p = Grid.getInstance().snapToGrid(checkBounds.x + getW() / 2, checkBounds.y + getH() / 2, snapmode);
					// proceed only if the position actually changed
					if (!p.equals(getLocation())) {
						reposition(p.x, p.y);
						p = getLocation();
						setFollowOffset(p.x - getParent().getX(), p.y - getParent().getY());

						// ((ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent()).updateData();
						// Manager.getCurrentShip().recalculateBoundedArea();
					}
				}
			}
		}
	}

	@Override
	public Point toPresentedLocation(int x, int y) {
		return new Point(x / CellController.SIZE, y / CellController.SIZE);
	}

	@Override
	public Point toPresentedSize(int w, int h) {
		return new Point(w / CellController.SIZE, h / CellController.SIZE);
	}

	@Override
	public Point toNormalLocation(int x, int y) {
		return new Point(x * CellController.SIZE, y * CellController.SIZE);
	}

	@Override
	public Point toNormalSize(int w, int h) {
		return new Point(w * CellController.SIZE, h * CellController.SIZE);
	}

	@Override
	public String getAlias() {
		return model.getAlias();
	}

	@Override
	public void setAlias(String alias) {
		model.setAlias(alias);
	}

	@Override
	public int compareTo(RoomController o) {
		return model.compareTo(o.model);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new RoomDataComposite(parent, this);
	}

	@Override
	public void setHighlighted(boolean high) {
		super.setHighlighted(high && !selected);

		if (view.isHighlighted()) {
			view.setBorderColor(RoomView.HIGHLIGHT_RGB);
			view.setBorderThickness(3);
		} else {
			view.setBorderColor(isSelected() ? RoomView.SELECT_RGB : RoomView.WALL_RGB);
			view.setBorderThickness(2);
		}
		redraw();
	}

	protected void updateSelectionAppearance() {
		if (selected) {
			view.setBorderColor(RoomView.SELECT_RGB);
			view.setBackgroundColor(Utils.tint(RoomView.FLOOR_RGB, RoomView.SELECT_RGB, 0.33));
			view.setBorderThickness(2);
		} else {
			view.setBorderColor(RoomView.WALL_RGB);
			view.setBackgroundColor(RoomView.FLOOR_RGB);
		}
		redraw();
	}

	@Override
	public void select() {
		super.select();
		updateSelectionAppearance();
	}

	@Override
	public void deselect() {
		super.deselect();
		setMoving(false);
		resizing = false;
		updateSelectionAppearance();
	}

	@Override
	public boolean contains(int x, int y) {
		return model.contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return model.intersects(rect);
	}

	/**
	 * Checks collision with other rooms.
	 * 
	 * @return true if collided with another room, false otherwie
	 */
	public boolean collides() {
		boolean collided = false;
		ArrayList<AbstractController> layer = EditorWindow.getInstance().getPainter().getLayerMap().get(Layers.ROOM);
		for (int i = 0; i < layer.size() && !collided; i++) {
			AbstractController c = layer.get(i);
			Rectangle b = new Rectangle(c.getX() - c.getW() / 2, c.getY() - c.getH() / 2, c.getW(), c.getH());
			if (c != this && c.isVisible() && c.collides(b.x + COLLISION_TOLERANCE, b.y + COLLISION_TOLERANCE,
					b.width - COLLISION_TOLERANCE * 2, b.height - COLLISION_TOLERANCE * 2)) {
				collided = true;
			}
		}
		return collided;
	}

	@Override
	public void updateBoundingArea() {
		Point gridSize = Grid.getInstance().getSize();
		gridSize.x -= (gridSize.x % CellController.SIZE) + CellController.SIZE;
		gridSize.y -= (gridSize.y % CellController.SIZE) + CellController.SIZE;
		setBoundingPoints(shipController.getX() + getW() / 2, shipController.getY() + getH() / 2,
				gridSize.x - getW() / 2, gridSize.y - getH() / 2);
	}
}
