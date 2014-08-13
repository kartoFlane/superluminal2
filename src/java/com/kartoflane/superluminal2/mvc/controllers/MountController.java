package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.interfaces.Indexable;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
import com.kartoflane.superluminal2.events.SLDeleteEvent;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.controllers.props.PropController;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.MountView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.MountDataComposite;
import com.kartoflane.superluminal2.utils.Utils;

public class MountController extends ObjectController implements Indexable, Comparable<MountController> {
	public static final int DEFAULT_WIDTH = 16;
	public static final int DEFAULT_HEIGHT = 50;
	public static final String ARROW_PROP_ID = "DirectionArrow";

	private static final int ARROW_HEIGHT = 45;

	protected ShipContainer container = null;

	private MountController(ShipContainer container, ObjectModel model, MountView view) {
		super();
		setModel(model);
		setView(view);
		this.container = container;

		setSelectable(true);
		setLocModifiable(true);
		setBounded(false);
		setCollidable(false);
		setParent(container.getShipController());

		createProps();
	}

	public static MountController newInstance(ShipContainer container, MountObject object) {
		ObjectModel model = new ObjectModel(object);
		MountView view = new MountView();
		MountController controller = new MountController(container, model, view);

		controller.setWeapon(object.getWeapon());
		controller.setGib(object.getGib());
		controller.setDirection(object.getDirection());
		controller.setRotated(object.isRotated());
		controller.setMirrored(object.isMirrored());

		return controller;
	}

	@Override
	public boolean setLocation(int x, int y) {
		boolean result = super.setLocation(x, y);
		updateView();
		return result;
	}

	protected MountView getView() {
		return (MountView) view;
	}

	public MountObject getGameObject() {
		return (MountObject) getModel().getGameObject();
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.MOUNT);
		updateView();
	}

	public void setWeapon(WeaponObject weapon) {
		if (weapon == null)
			throw new IllegalArgumentException("Argument must not be null. For default, use DEFAULT_WEAPON_OBJ");

		setVisible(false);
		setSize(weapon.getAnimation().getFrameSize());
		getGameObject().setWeapon(weapon);
		updateView();
		setVisible(true);
	}

	public WeaponObject getWeapon() {
		return getGameObject().getWeapon();
	}

	public void setGib(GibObject gib) {
		if (gib == null)
			throw new IllegalArgumentException("Argument must not be null. For default, use DEFAULT_GIB_OBJ");

		AbstractController gibC = container.getController(getGib());
		if (gibC != null)
			gibC.removeListener(SLEvent.DELETE, this);

		getGameObject().setGib(gib);

		gibC = container.getController(getGib());
		if (gibC != null)
			gibC.addListener(SLEvent.DELETE, this);
	}

	public GibObject getGib() {
		return getGameObject().getGib();
	}

	public void setRotated(boolean rotated) {
		getGameObject().setRotated(rotated);
		setRotation(rotated ? 90 : 0);
		updateView();
	}

	public boolean isRotated() {
		return getGameObject().isRotated();
	}

	public void setMirrored(boolean mirrored) {
		getGameObject().setMirrored(mirrored);
		view.setFlippedX(mirrored);
		updateView();
	}

	public boolean isMirrored() {
		return getGameObject().isMirrored();
	}

	public void setDirection(Directions dir) {
		PropController arrowProp = getProp(ARROW_PROP_ID);
		arrowProp.setVisible(false);
		arrowProp.setRotation(dir.getAngleDeg());
		arrowProp.setFollowOffset(dir.getVectorX() * ARROW_HEIGHT / 2, dir.getVectorY() * ARROW_HEIGHT / 2);
		arrowProp.updateFollower();
		arrowProp.setVisible(dir != Directions.NONE);
		getGameObject().setDirection(dir);
	}

	public Directions getDirection() {
		return getGameObject().getDirection();
	}

	public void setId(int id) {
		getGameObject().setId(id);
	}

	public int getId() {
		return getGameObject().getId();
	}

	@Override
	public int compareTo(MountController o) {
		return getGameObject().compareTo(o.getGameObject());
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new MountDataComposite(parent, this);
	}

	@Override
	public Rectangle getBounds() {
		Rectangle b = model.getBounds();
		Point offset = getGameObject().getWeapon().getAnimation().getMountOffset();

		int s = isMirrored() ? -1 : 1;

		if (isRotated()) {
			b.x += -getH() / 2 + offset.y - 1;
			b.y += s * (b.width / 2 - offset.x);
		} else {
			b.x += s * (b.width / 2 - offset.x);
			b.y += b.height / 2 - offset.y;
		}

		return Utils.rotate(b, isRotated() ? 90 : 0);
	}

	@Override
	public boolean contains(int x, int y) {
		return getBounds().contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return getBounds().intersects(rect);
	}

	@Override
	public void addToPainter(Layers layer) {
		super.addToPainter(layer);
		PropController prop = getProp(ARROW_PROP_ID);
		prop.addToPainter(layer);
	}

	@Override
	public void addToPainterBottom(Layers layer) {
		super.addToPainterBottom(layer);
		PropController prop = getProp(ARROW_PROP_ID);
		prop.addToPainter(layer);
	}

	@Override
	public void removeFromPainter() {
		super.removeFromPainter();
		PropController prop = getProp(ARROW_PROP_ID);
		prop.removeFromPainter();
	}

	private void createProps() {
		PropController prop = new PropController(this, ARROW_PROP_ID);
		prop.setImage("cpath:/assets/arrow.png");
		prop.setAlpha(255);
		prop.addToPainter(Layers.MOUNT);
		addProp(prop);
	}

	@Override
	public void handleEvent(SLEvent e) {
		if (e instanceof SLDeleteEvent) {
			if (e.data instanceof GibController) {
				setGib(Database.DEFAULT_GIB_OBJ);
			}
		} else {
			super.handleEvent(e);
		}
	}
}
