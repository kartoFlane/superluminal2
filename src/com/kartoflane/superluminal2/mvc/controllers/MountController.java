package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.MountView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.MountDataComposite;

public class MountController extends ObjectController implements Comparable<MountController> {
	public static final int DEFAULT_WIDTH = 16;
	public static final int DEFAULT_HEIGHT = 50;

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
	}

	public static MountController newInstance(ShipContainer container, MountObject object) {
		ObjectModel model = new ObjectModel(object);
		MountView view = new MountView();
		MountController controller = new MountController(container, model, view);

		controller.setWeapon(object.getWeapon());

		return controller;
	}

	@Override
	public boolean setLocation(int x, int y) {
		boolean result = super.setLocation(x, y);
		updateView();
		return result;
	}

	@Override
	public void redraw() {
		super.redraw();
		EditorWindow.getInstance().canvasRedraw(getView().getDirectionArrowBounds());
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
			throw new NullPointerException("Must not be null. For default, use DEFAULT_WEAPON_OBJ");

		setVisible(false);
		setSize(weapon.getFrameSize());
		getGameObject().setWeapon(weapon);
		updateView();
		setVisible(true);
	}

	public WeaponObject getWeapon() {
		return getGameObject().getWeapon();
	}

	public void setRotated(boolean rotated) {
		getGameObject().setRotated(rotated);
		updateView();
	}

	public boolean isRotated() {
		return getGameObject().isRotated();
	}

	public void setMirrored(boolean mirrored) {
		getGameObject().setMirrored(mirrored);
		updateView();
	}

	public boolean isMirrored() {
		return getGameObject().isMirrored();
	}

	public void setDirection(Directions direction) {
		Rectangle oldBounds = getView().getDirectionArrowBounds();
		getGameObject().setDirection(direction);
		updateView();
		redraw();
		EditorWindow.getInstance().canvasRedraw(oldBounds);
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
		Point offset = getGameObject().getWeapon().getMountOffset();

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
		return getBounds().intersects(rect) || getView().getDirectionArrowBounds().intersects(rect);
	}
}
