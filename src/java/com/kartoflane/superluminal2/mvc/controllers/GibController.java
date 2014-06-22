package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Indexable;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.GibView;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.GibControlsMenu;
import com.kartoflane.superluminal2.ui.GibPropContainer.PropControls;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.GibDataComposite;

public class GibController extends ImageController implements Indexable, Comparable<GibController> {

	private GibController(ShipContainer container, ObjectModel model, GibView view) {
		super(container.getShipController(), model, view);
		setModel(model);
		setView(view);

		setSelectable(true);
		setLocModifiable(true);
		setBounded(false);
		setCollidable(false);
		setDeletable(true);
	}

	@Override
	public GibObject getGameObject() {
		return (GibObject) getModel().getGameObject();
	}

	public static GibController newInstance(ShipContainer shipContainer, GibObject gib) {
		ObjectModel model = new ObjectModel(gib);
		GibView view = new GibView();
		GibController controller = new GibController(shipContainer, model, view);

		controller.setImage(gib.getImagePath());

		return controller;
	}

	protected GibView getView() {
		return (GibView) view;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		removeFromPainter();
		addToPainter(Layers.GIBS);
		updateView();
	}

	public int getId() {
		return getGameObject().getId();
	}

	public void setId(int index) {
		getGameObject().setId(index);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new GibDataComposite(parent, this);
	}

	public void setDirectionMin(int angle) {
		getGameObject().setDirectionMin(angle);
	}

	public int getDirectionMin() {
		return getGameObject().getDirectionMin();
	}

	public void setDirectionMax(int angle) {
		getGameObject().setDirectionMax(angle);
	}

	public int getDirectionMax() {
		return getGameObject().getDirectionMax();
	}

	public void setLinearVelocityMin(double d) {
		getGameObject().setVelocityMin(d);
	}

	public double getLinearVelocityMin() {
		return getGameObject().getVelocityMin();
	}

	public void setLinearVelocityMax(double vel) {
		getGameObject().setVelocityMax(vel);
	}

	public double getLinearVelocityMax() {
		return getGameObject().getVelocityMax();
	}

	public void setAngularVelocityMin(double vel) {
		getGameObject().setAngularMin(vel);
	}

	public double getAngularVelocityMin() {
		return getGameObject().getAngularMin();
	}

	public void setAngularVelocityMax(double vel) {
		getGameObject().setAngularMax(vel);
	}

	public double getAngularVelocityMax() {
		return getGameObject().getAngularMax();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER && e.button == 3 &&
				selected && contains(e.x, e.y)) {
			// Open prop controls selection menu
			ShipContainer container = Manager.getCurrentShip();
			GibControlsMenu propMenu = new GibControlsMenu(container.getParent().getShell());
			PropControls result = propMenu.open();
			if (result != null) {
				container.getGibContainer().showControls(result);
				container.getParent().updateSidebarContent();
			}
		} else {
			super.mouseUp(e);
		}
	}

	@Override
	public int compareTo(GibController o) {
		return getGameObject().compareTo(o.getGameObject());
	}
}
