package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.GibView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.GibDataComposite;

public class GibController extends ImageController {

	private ShipContainer container;

	private GibController(ShipContainer container, ObjectModel model, GibView view) {
		super(container.getShipController(), model, view);
		setModel(model);
		setView(view);

		this.container = container;

		setSelectable(true);
		setLocModifiable(true);
		setBounded(false);
		setCollidable(false);

		createProps();
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
		this.view.addToPainter(Layers.IMAGES);
		updateView();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new GibDataComposite(parent, this);
	}

	private void createProps() {
		// TODO
	}
}
