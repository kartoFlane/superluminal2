package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.GibView;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;
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
		setDeletable(false);

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
		this.view.addToPainter(Layers.GIBS);
		updateView();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new GibDataComposite(parent, this);
	}

	private void createProps() {
		// TODO
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.GIB) {
			boolean contains = contains(e.x, e.y);
			if (e.button == 1) {
				clickOffset.x = e.x - getX();
				clickOffset.y = e.y - getY();

				if (contains && !selected)
					Manager.setSelected(this);
				setMoving(contains);
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.GIB) {
			if (e.button == 1) {
				if (!contains(e.x, e.y) && !contains(getX() + clickOffset.x, getY() + clickOffset.y) && selected)
					Manager.setSelected(null);
				setMoving(false);
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.GIB) {
			if (selected && moving) {
				Point p = Grid.getInstance().snapToGrid(e.x - clickOffset.x, e.y - clickOffset.y, snapmode);
				if (p.x != getX() || p.y != getY()) {
					reposition(p.x, p.y);
					updateFollowOffset();

					((DataComposite) EditorWindow.getInstance().getSidebarContent()).updateData();
				}
			}
		}
	}

}
