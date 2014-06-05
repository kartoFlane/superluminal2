package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.controllers.props.ArcPropController;
import com.kartoflane.superluminal2.mvc.controllers.props.OrbitPropController;
import com.kartoflane.superluminal2.mvc.controllers.props.PropController;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.GibView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.GibDataComposite;
import com.kartoflane.superluminal2.utils.Utils;

public class GibController extends ImageController {

	public static final String DIR_MIN_PROP_ID = "DirectionMin";
	public static final String DIR_MAX_PROP_ID = "DirectionMax";
	public static final String DIR_ARC_PROP_ID = "DirectionArc";
	public static final String ANG_MIN_PROP_ID = "AngularMin";
	public static final String ANG_MAX_PROP_ID = "AngularMax";
	public static final String LIN_MIN_PROP_ID = "LinearMin";
	public static final String LIN_MAX_PROP_ID = "LinearMax";

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

	@Override
	public void deselect() {
		super.deselect();
		System.out.println(getDirectionMin() + " | " + getDirectionMax());
	}

	private void createProps() {
		SLListener selectionListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				PropController minProp = getProp(DIR_MIN_PROP_ID);
				PropController maxProp = getProp(DIR_MAX_PROP_ID);

				String[] propIds = { DIR_MIN_PROP_ID, DIR_MAX_PROP_ID, DIR_ARC_PROP_ID };
				for (String id : propIds) {
					PropController prop = getProp(id);
					prop.setVisible(isSelected() || minProp.isSelected() || maxProp.isSelected());
				}
			}
		};
		addListener(SLEvent.SELECT, selectionListener);
		addListener(SLEvent.DESELECT, selectionListener);

		SLListener arcListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				ArcPropController arcProp = (ArcPropController) getProp(DIR_ARC_PROP_ID);
				if (arcProp.isVisible()) {
					PropController minProp = getProp(DIR_MIN_PROP_ID);
					PropController maxProp = getProp(DIR_MAX_PROP_ID);

					float minAngle = (float) Utils.angle(arcProp.getLocation(), minProp.getLocation());
					float maxAngle = (float) Utils.angle(arcProp.getLocation(), maxProp.getLocation());

					if (minAngle < maxAngle) {
						arcProp.setStartAngle(Math.round(minAngle) + 90);
						arcProp.setArcSpan(Math.round(maxAngle - minAngle));

						setDirectionMin((int) minAngle);
						setDirectionMax((int) maxAngle);
					} else if (minAngle > maxAngle) {
						arcProp.setStartAngle(Math.round(maxAngle) + 90);
						arcProp.setArcSpan(Math.round(minAngle - maxAngle - 360));

						setDirectionMin((int) (minAngle - 360));
						setDirectionMax((int) maxAngle);
					} else {
						arcProp.setStartAngle(0);
						arcProp.setArcSpan(360);
					}
					arcProp.redraw();
				}
			}
		};

		ArcPropController arcProp = new ArcPropController(this, DIR_ARC_PROP_ID);
		arcProp.setInheritVisibility(false);
		arcProp.setVisible(false);
		arcProp.setDefaultBackgroundColor(0, 255, 0);
		arcProp.setAlpha(128);
		arcProp.setSize(200, 200);
		arcProp.addToPainter(Layers.PROP);
		arcProp.updateView();
		addProp(arcProp);

		OrbitPropController orbitProp = new OrbitPropController(this, DIR_MIN_PROP_ID);
		orbitProp.setShape(Shapes.OVAL);
		orbitProp.setSelectable(true);
		orbitProp.setInheritVisibility(false);
		orbitProp.setVisible(false);
		orbitProp.setDefaultBorderColor(0, 0, 0);
		orbitProp.setDefaultBackgroundColor(32, 164, 164);
		orbitProp.setOrbitOffset(100);
		orbitProp.setSize(2 * ShipContainer.CELL_SIZE / 3, 2 * ShipContainer.CELL_SIZE / 3);
		orbitProp.setBorderThickness(3);
		orbitProp.addToPainter(Layers.PROP);
		orbitProp.updateView();
		orbitProp.setCompositeTitle("Minimum Direction");
		orbitProp.addListener(SLEvent.SELECT, selectionListener);
		orbitProp.addListener(SLEvent.DESELECT, selectionListener);
		orbitProp.addListener(SLEvent.MOVE, arcListener);
		addProp(orbitProp);

		orbitProp = new OrbitPropController(this, DIR_MAX_PROP_ID);
		orbitProp.setShape(Shapes.OVAL);
		orbitProp.setSelectable(true);
		orbitProp.setInheritVisibility(false);
		orbitProp.setVisible(false);
		orbitProp.setDefaultBorderColor(0, 0, 0);
		orbitProp.setDefaultBackgroundColor(64, 255, 255);
		orbitProp.setOrbitOffset(120);
		orbitProp.setSize(2 * ShipContainer.CELL_SIZE / 3, 2 * ShipContainer.CELL_SIZE / 3);
		orbitProp.setBorderThickness(3);
		orbitProp.addToPainter(Layers.PROP);
		orbitProp.updateView();
		orbitProp.setCompositeTitle("Maximum Direction");
		orbitProp.addListener(SLEvent.SELECT, selectionListener);
		orbitProp.addListener(SLEvent.DESELECT, selectionListener);
		orbitProp.addListener(SLEvent.MOVE, arcListener);
		addProp(orbitProp);

		// TODO
	}
}
