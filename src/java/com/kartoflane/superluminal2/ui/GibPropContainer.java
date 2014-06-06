package com.kartoflane.superluminal2.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.props.ArcPropController;
import com.kartoflane.superluminal2.mvc.controllers.props.OffsetPropController;
import com.kartoflane.superluminal2.mvc.controllers.props.OrbitPropController;
import com.kartoflane.superluminal2.mvc.controllers.props.PropController;
import com.kartoflane.superluminal2.utils.Utils;

public class GibPropContainer {
	public enum PropControls {
		NONE, DIRECTION, LINEAR, ANGULAR;

		@Override
		public String toString() {
			return name().substring(0, 1) + name().substring(1).toLowerCase();
		}
	}

	private static final String DIR_MIN_PROP_ID = "DirectionMin";
	private static final String DIR_MAX_PROP_ID = "DirectionMax";
	private static final String DIR_ARC_PROP_ID = "DirectionArc";
	private static final String ANG_MIN_PROP_ID = "AngularMin";
	private static final String ANG_MAX_PROP_ID = "AngularMax";
	private static final String LIN_MIN_PROP_ID = "LinearMin";
	private static final String LIN_MAX_PROP_ID = "LinearMax";
	private static final String LIN_LIN_PROP_ID = "LinearLine";

	private GibController currentController = null;

	private PropControls currentlyShownControls = PropControls.NONE;
	private HashSet<PropController> props = null;

	private SLListener selectionListener = null;
	private boolean dataLoad = false;

	public GibPropContainer() {
		selectionListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				if (currentlyShownControls == PropControls.DIRECTION) {
					PropController minProp = getProp(DIR_MIN_PROP_ID);
					PropController maxProp = getProp(DIR_MAX_PROP_ID);

					String[] propIds = { DIR_MIN_PROP_ID, DIR_MAX_PROP_ID, DIR_ARC_PROP_ID };
					for (String id : propIds) {
						PropController prop = getProp(id);
						prop.setVisible(currentController.isSelected() || minProp.isSelected() || maxProp.isSelected());
					}
				} else if (currentlyShownControls == PropControls.LINEAR) {
					PropController minProp = getProp(LIN_MIN_PROP_ID);
					PropController maxProp = getProp(LIN_MAX_PROP_ID);

					String[] propIds = { LIN_MIN_PROP_ID, LIN_MAX_PROP_ID, LIN_LIN_PROP_ID };
					for (String id : propIds) {
						PropController prop = getProp(id);
						prop.setVisible(currentController.isSelected() || minProp.isSelected() || maxProp.isSelected());
					}

					minProp.setBounded(!currentController.isSelected());
					maxProp.setBounded(!currentController.isSelected());

					dataLoad = true;

					int cs = ShipContainer.CELL_SIZE;
					int gridW = Grid.getInstance().getSize().x;

					minProp.setBoundingPoints(currentController.getX(), currentController.getY() - cs / 2,
							maxProp.getX(), currentController.getY() - cs / 2);
					minProp.setFollowOffset(minProp.getFollowOffsetX(), -cs / 2);
					minProp.updateFollower();

					maxProp.setBoundingPoints(minProp.getX(), currentController.getY() - 2 * cs / 3,
							gridW, currentController.getY() - 2 * cs / 3);
					maxProp.setFollowOffset(maxProp.getFollowOffsetX(), -2 * cs / 3);
					maxProp.updateFollower();

					dataLoad = false;
				} else if (currentlyShownControls == PropControls.ANGULAR) {
				}
			}
		};

		createProps();
	}

	public void setCurrentController(GibController controller) {
		if (currentController != null) {
			currentController.removeListener(SLEvent.SELECT, selectionListener);
			currentController.removeListener(SLEvent.DESELECT, selectionListener);

			for (PropController prop : props)
				prop.setParent(null);
		}

		currentController = controller;

		if (currentController != null) {
			currentController.addListener(SLEvent.SELECT, selectionListener);
			currentController.addListener(SLEvent.DESELECT, selectionListener);
		}

		for (PropController prop : props)
			prop.setParent(currentController);
		updateData();
		for (PropController prop : props)
			prop.updateFollower();
	}

	public GibController getCurrentController() {
		return currentController;
	}

	/**
	 * Shows prop controllers responsible for modification of the selected property.
	 */
	public void showControls(PropControls control) {
		if (control == null)
			throw new IllegalArgumentException("Argument must not be null.");

		currentlyShownControls = control;

		String[] dirPropIds = { DIR_MIN_PROP_ID, DIR_MAX_PROP_ID, DIR_ARC_PROP_ID };
		for (String id : dirPropIds) {
			PropController prop = getProp(id);
			prop.setVisible(control == PropControls.DIRECTION);
		}

		String[] linPropIds = { LIN_MIN_PROP_ID, LIN_MAX_PROP_ID, LIN_LIN_PROP_ID };
		for (String id : linPropIds) {
			PropController prop = getProp(id);
			prop.setVisible(control == PropControls.LINEAR);
		}

		// TODO other controls

		updateData();
	}

	public PropControls getShownControls() {
		return currentlyShownControls;
	}

	private void updateData() {
		if (currentController == null)
			return;

		dataLoad = true;

		OrbitPropController opc;
		opc = (OrbitPropController) getProp(DIR_MIN_PROP_ID);
		opc.setLocation(opc.angleToOrbitLocation(currentController.getDirectionMin()));
		opc.updateFollowOffset();

		opc = (OrbitPropController) getProp(DIR_MAX_PROP_ID);
		opc.setLocation(opc.angleToOrbitLocation(currentController.getDirectionMax()));
		opc.updateFollowOffset();

		PropController prop;
		prop = getProp(LIN_MIN_PROP_ID);
		prop.setBounded(false);
		float velocity = currentController.getLinearVelocityMin();
		velocity *= Database.GIB_EXPLO_TIME * 10;
		prop.setFollowOffset(Math.round(velocity), prop.getFollowOffsetY());
		prop.updateFollower();
		prop.setBounded(!currentController.isSelected());

		prop = getProp(LIN_MAX_PROP_ID);
		prop.setBounded(false);
		velocity = currentController.getLinearVelocityMax();
		velocity *= Database.GIB_EXPLO_TIME * 10;
		prop.setFollowOffset(Math.round(velocity), prop.getFollowOffsetY());
		prop.updateFollower();
		prop.setBounded(!currentController.isSelected());

		dataLoad = false;

		opc.setLocation(opc.getX(), opc.getY()); // Trigger SLEvent.MOVE
	}

	private void addProp(PropController prop) {
		if (prop == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (props == null)
			props = new HashSet<PropController>();
		if (getProp(prop.getIdentifier()) != null)
			throw new IllegalArgumentException(String.format("This object already owns a prop named '%s'", prop.getIdentifier()));
		props.add(prop);
	}

	/**
	 * @param id
	 *            the identifier of the sought prop
	 * @return prop with the given identifier, or null if not found
	 */
	private PropController getProp(String id) {
		if (id == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (props == null) {
			return null;
		} else {
			PropController result = null;
			Iterator<PropController> it = props.iterator();
			while (it.hasNext() && result == null) {
				PropController p = it.next();
				if (p.getIdentifier().equals(id))
					result = p;
			}

			return result;
		}
	}

	private void createProps() {
		createDirectionProps();
		createLinearVelocityProps();
		createAngularVelocityProps();
	}

	/**
	 * Creates the prop controllers responsible for direction modification.
	 */
	private void createDirectionProps() {
		SLListener arcListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				if (dataLoad || currentlyShownControls != PropControls.DIRECTION)
					return;

				ArcPropController arcProp = (ArcPropController) getProp(DIR_ARC_PROP_ID);
				PropController minProp = getProp(DIR_MIN_PROP_ID);
				PropController maxProp = getProp(DIR_MAX_PROP_ID);

				float minAngle = (float) Utils.angle(arcProp.getLocation(), minProp.getLocation());
				float maxAngle = (float) Utils.angle(arcProp.getLocation(), maxProp.getLocation());

				if (minAngle < maxAngle) {
					arcProp.setStartAngle(Math.round(minAngle) + 90);
					arcProp.setArcSpan(Math.round(maxAngle - minAngle));

					currentController.setDirectionMin((int) minAngle);
					currentController.setDirectionMax((int) maxAngle);
				} else if (minAngle > maxAngle) {
					arcProp.setStartAngle(Math.round(maxAngle) + 90);
					arcProp.setArcSpan(Math.round(minAngle - maxAngle - 360));

					currentController.setDirectionMin((int) (minAngle - 360));
					currentController.setDirectionMax((int) maxAngle);
				} else {
					arcProp.setStartAngle(0);
					arcProp.setArcSpan(360);
				}
				arcProp.redraw();
			}
		};

		ArcPropController arcProp = new ArcPropController(null, DIR_ARC_PROP_ID);
		arcProp.setInheritVisibility(false);
		arcProp.setVisible(false);
		arcProp.setDefaultBackgroundColor(0, 255, 0);
		arcProp.setAlpha(128);
		arcProp.setSize(200, 200);
		arcProp.addToPainter(Layers.PROP);
		arcProp.updateView();
		addProp(arcProp);

		OrbitPropController orbitProp = new OrbitPropController(null, DIR_MIN_PROP_ID);
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

		orbitProp = new OrbitPropController(null, DIR_MAX_PROP_ID);
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
	}

	/**
	 * Creates the prop controllers responsible for linear velocity modification.
	 */
	public void createLinearVelocityProps() {
		SLListener lineListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				if (dataLoad || currentlyShownControls != PropControls.LINEAR)
					return;

				PropController line = getProp(LIN_LIN_PROP_ID);
				OffsetPropController max = (OffsetPropController) getProp(LIN_MAX_PROP_ID);
				int diff = max.getX() - line.getParent().getX();
				line.resize(Math.abs(diff), 1);
				line.setFollowOffset(diff / 2, 0);
				line.updateFollower();
			}
		};

		PropController prop = new PropController(null, LIN_LIN_PROP_ID);
		prop.setInheritVisibility(true);
		prop.setDefaultBackgroundColor(255, 0, 0);
		prop.setImage(null);
		prop.setBorderThickness(1);
		prop.setAlpha(255);
		prop.addToPainterBottom(Layers.PROP);
		addProp(prop);

		OffsetPropController opc = new OffsetPropController(null, LIN_MIN_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				ShipContainer.CELL_SIZE / 2, 0,
				ShipContainer.CELL_SIZE / 4, ShipContainer.CELL_SIZE / 2
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {
				// TODO set min
			}
		});
		opc.setDefaultBackgroundColor(255, 0, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.PROP);
		opc.setCompositeTitle("Minimum Linear Velocity");
		opc.addListener(SLEvent.SELECT, selectionListener);
		opc.addListener(SLEvent.DESELECT, selectionListener);
		addProp(opc);

		opc = new OffsetPropController(null, LIN_MAX_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				ShipContainer.CELL_SIZE / 2, 0,
				ShipContainer.CELL_SIZE / 4, 2 * ShipContainer.CELL_SIZE / 3
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {

				// TODO set max
			}
		});
		opc.setDefaultBackgroundColor(255, 0, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.PROP);
		opc.setCompositeTitle("Maximum Linear Velocity");
		opc.addListener(SLEvent.SELECT, selectionListener);
		opc.addListener(SLEvent.DESELECT, selectionListener);
		opc.addListener(SLEvent.MOVE, lineListener);
		addProp(opc);
	}

	/**
	 * Creates the prop controllers responsible for angular velocity modification.
	 */
	public void createAngularVelocityProps() {
	}

	public void dispose() {
		for (PropController prop : props) {
			prop.dispose();
		}
		props.clear();
	}
}
