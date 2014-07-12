package com.kartoflane.superluminal2.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
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
			switch (this) {
				case LINEAR:
					return "Linear Velocity";
				case ANGULAR:
					return "Angular Velocity";
				default:
					return name().substring(0, 1) + name().substring(1).toLowerCase();
			}
		}
	}

	private static final String DIR_MIN_PROP_ID = "DirectionMin";
	private static final String DIR_MAX_PROP_ID = "DirectionMax";
	private static final String DIR_ARC_PROP_ID = "DirectionArc";
	private static final String ANG_PROP_ID = "AngularValue";
	private static final String ANG_ARC_PROP_ID = "AngularArc";
	private static final String LIN_MIN_PROP_ID = "LinearMin";
	private static final String LIN_MAX_PROP_ID = "LinearMax";
	private static final String LIN_MIN_RANGE_PROP_ID = "LinearRangeMin";
	private static final String LIN_MAX_RANGE_PROP_ID = "LinearRangeMax";

	private GibController currentController = null;

	private PropControls currentlyShownControls = PropControls.NONE;
	private HashSet<PropController> props = null;

	private SLListener selectionListener = null;
	private boolean dataLoad = false;

	public GibPropContainer() {
		selectionListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				if (currentController != null && !currentController.isSelected() && !currentController.isVisible())
					setCurrentController(null);

				if (currentlyShownControls == PropControls.DIRECTION) {
					PropController minProp = getProp(DIR_MIN_PROP_ID);
					PropController maxProp = getProp(DIR_MAX_PROP_ID);

					String[] propIds = { DIR_MIN_PROP_ID, DIR_MAX_PROP_ID, DIR_ARC_PROP_ID };
					for (String id : propIds) {
						PropController prop = getProp(id);
						prop.setVisible(currentController != null && currentController.isVisible() &&
								(currentController.isSelected() || minProp.isSelected() || maxProp.isSelected()));
					}
				} else if (currentlyShownControls == PropControls.LINEAR) {
					PropController minProp = getProp(LIN_MIN_PROP_ID);
					PropController maxProp = getProp(LIN_MAX_PROP_ID);

					String[] propIds = { LIN_MIN_PROP_ID, LIN_MAX_PROP_ID, LIN_MIN_RANGE_PROP_ID, LIN_MAX_RANGE_PROP_ID };
					for (String id : propIds) {
						PropController prop = getProp(id);
						prop.setVisible(currentController != null && currentController.isVisible() &&
								(currentController.isSelected() || minProp.isSelected() || maxProp.isSelected()));
					}

					if (currentController != null) {
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
					}
				} else if (currentlyShownControls == PropControls.ANGULAR) {
					PropController angProp = getProp(ANG_PROP_ID);

					String[] propIds = { ANG_PROP_ID, ANG_ARC_PROP_ID };

					for (String id : propIds) {
						PropController prop = getProp(id);
						prop.setVisible(currentController != null && currentController.isVisible() &&
								(currentController.isSelected() || angProp.isSelected()));
					}
				}
			}
		};

		createProps();
	}

	public void setCurrentController(GibController controller) {
		if (currentController == controller) {
			updateData();
			return;
		}

		dataLoad = true;

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

		if (currentController != null) {
			dataLoad = true;
			for (PropController prop : props)
				prop.updateFollower();
			dataLoad = false;
		} else {
			selectionListener.handleEvent(null);
		}
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
			prop.setVisible(control == PropControls.DIRECTION && currentController != null && currentController.isVisible());
		}

		String[] linPropIds = { LIN_MIN_PROP_ID, LIN_MAX_PROP_ID, LIN_MIN_RANGE_PROP_ID, LIN_MAX_RANGE_PROP_ID };
		for (String id : linPropIds) {
			PropController prop = getProp(id);
			prop.setVisible(control == PropControls.LINEAR && currentController != null && currentController.isVisible());
		}

		String[] angPropIds = { ANG_PROP_ID, ANG_ARC_PROP_ID };
		for (String id : angPropIds) {
			PropController prop = getProp(id);
			prop.setVisible(control == PropControls.ANGULAR && currentController != null && currentController.isVisible());
		}

		updateData();
	}

	public PropControls getShownControls() {
		return currentlyShownControls;
	}

	private void updateData() {
		if (currentController == null)
			return;

		OrbitPropController opc;
		PropController prop;
		dataLoad = true;

		if (currentlyShownControls == PropControls.DIRECTION) {
			opc = (OrbitPropController) getProp(DIR_MIN_PROP_ID);
			opc.reposition(opc.angleToOrbitLocation(currentController.getDirectionMin()));
			opc.updateFollowOffset();

			opc = (OrbitPropController) getProp(DIR_MAX_PROP_ID);
			opc.reposition(opc.angleToOrbitLocation(currentController.getDirectionMax()));
			opc.updateFollowOffset();

		} else if (currentlyShownControls == PropControls.LINEAR) {
			double velocity = 0;

			prop = getProp(LIN_MIN_PROP_ID);
			prop.setBounded(false);
			velocity = currentController.getLinearVelocityMin();
			velocity *= Database.GIB_DEATH_ANIM_TIME * Database.GIB_LINEAR_SPEED;
			prop.setFollowOffset((int) Math.round(velocity), prop.getFollowOffsetY());
			prop.updateFollower();
			prop.setBounded(!currentController.isSelected());

			prop = getProp(LIN_MAX_PROP_ID);
			prop.setBounded(false);
			velocity = currentController.getLinearVelocityMax();
			velocity *= Database.GIB_DEATH_ANIM_TIME * Database.GIB_LINEAR_SPEED;
			prop.setFollowOffset((int) Math.round(velocity), prop.getFollowOffsetY());
			prop.updateFollower();
			prop.setBounded(!currentController.isSelected());

		} else if (currentlyShownControls == PropControls.ANGULAR) {
			double angle = 0;

			opc = (OrbitPropController) getProp(ANG_PROP_ID);

			angle = currentController.getAngularVelocityMax();
			angle *= Database.GIB_DEATH_ANIM_TIME * Database.GIB_ANGULAR_SPEED;
			angle = Math.toDegrees(angle);
			opc.reposition(opc.angleToOrbitLocation(angle));
			opc.updateFollowOffset();
		}

		dataLoad = false;
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
				if (currentlyShownControls != PropControls.DIRECTION)
					return;

				ArcPropController arcProp = (ArcPropController) getProp(DIR_ARC_PROP_ID);
				PropController minProp = getProp(DIR_MIN_PROP_ID);
				PropController maxProp = getProp(DIR_MAX_PROP_ID);

				double minAngle = Utils.angle(arcProp.getLocation(), minProp.getLocation());
				double maxAngle = Utils.angle(arcProp.getLocation(), maxProp.getLocation());

				if (Math.abs(minAngle - maxAngle) <= 1) {
					// If min and max angles are within 1 degree, consider them to be equal
					arcProp.setStartAngle(0);
					arcProp.setArcSpan(360);

					if (!dataLoad) {
						currentController.setDirectionMin(0);
						currentController.setDirectionMax(360);
					}
				} else if (minAngle < maxAngle) {
					arcProp.setStartAngle((int) Math.round(minAngle) + 90);
					arcProp.setArcSpan((int) Math.round(maxAngle - minAngle));

					if (!dataLoad) {
						currentController.setDirectionMin((int) Math.round(minAngle));
						currentController.setDirectionMax((int) Math.round(maxAngle));
					}
				} else if (minAngle > maxAngle) {
					arcProp.setStartAngle((int) Math.round(maxAngle) + 90);
					arcProp.setArcSpan((int) Math.round(minAngle - maxAngle - 360));

					if (!dataLoad) {
						currentController.setDirectionMin((int) Math.round(minAngle - 360));
						currentController.setDirectionMax((int) Math.round(maxAngle));
					}
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
		ArcPropController apc = new ArcPropController(null, LIN_MAX_RANGE_PROP_ID);
		apc.setInheritVisibility(false);
		apc.setVisible(false);
		apc.setDefaultBackgroundColor(255, 32, 32);
		apc.setAlpha(128);
		apc.setStartAngle(0);
		apc.setArcSpan(360);
		apc.addToPainter(Layers.PROP);
		apc.updateView();
		addProp(apc);

		apc = new ArcPropController(null, LIN_MIN_RANGE_PROP_ID);
		apc.setInheritVisibility(false);
		apc.setVisible(false);
		apc.setDefaultBackgroundColor(128, 196, 196);
		apc.setAlpha(128);
		apc.setStartAngle(0);
		apc.setArcSpan(360);
		apc.addToPainter(Layers.PROP);
		apc.updateView();
		addProp(apc);

		OffsetPropController opc = new OffsetPropController(null, LIN_MIN_PROP_ID);
		opc.setPolygon(new Polygon(new int[] {
				0, 0,
				ShipContainer.CELL_SIZE / 2, 0,
				ShipContainer.CELL_SIZE / 4, ShipContainer.CELL_SIZE / 2
		}));
		opc.addListener(SLEvent.MOVE, new SLListener() {
			@Override
			public void handleEvent(SLEvent e) {
				if (currentlyShownControls != PropControls.LINEAR)
					return;

				ArcPropController arc = (ArcPropController) getProp(LIN_MIN_RANGE_PROP_ID);
				OffsetPropController opc = (OffsetPropController) getProp(LIN_MIN_PROP_ID);
				int dist = opc.getX() - arc.getParent().getX();
				arc.resize(Math.abs(dist * 2), Math.abs(dist * 2));

				if (!dataLoad)
					currentController.setLinearVelocityMin(((double) dist) / (Database.GIB_DEATH_ANIM_TIME * Database.GIB_LINEAR_SPEED));
			}
		});
		opc.setVisible(false);
		opc.setFollowOffset(0, -ShipContainer.CELL_SIZE / 2);
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
				if (currentlyShownControls != PropControls.LINEAR)
					return;

				ArcPropController arc = (ArcPropController) getProp(LIN_MAX_RANGE_PROP_ID);
				OffsetPropController opc = (OffsetPropController) getProp(LIN_MAX_PROP_ID);
				int dist = opc.getX() - arc.getParent().getX();
				arc.resize(Math.abs(dist * 2), Math.abs(dist * 2));

				if (!dataLoad)
					currentController.setLinearVelocityMax(((double) dist) / (Database.GIB_DEATH_ANIM_TIME * Database.GIB_LINEAR_SPEED));
			}
		});
		opc.setVisible(false);
		opc.setFollowOffset(0, -2 * ShipContainer.CELL_SIZE / 3);
		opc.setDefaultBackgroundColor(255, 0, 0);
		opc.setDefaultBorderColor(0, 0, 0);
		opc.addToPainter(Layers.PROP);
		opc.setCompositeTitle("Maximum Linear Velocity");
		opc.addListener(SLEvent.SELECT, selectionListener);
		opc.addListener(SLEvent.DESELECT, selectionListener);
		addProp(opc);
	}

	/**
	 * Creates the prop controllers responsible for angular velocity modification.
	 */
	public void createAngularVelocityProps() {
		SLListener angularListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				if (currentlyShownControls != PropControls.ANGULAR)
					return;

				PropController angProp = getProp(ANG_PROP_ID);
				ArcPropController angArc = (ArcPropController) getProp(ANG_ARC_PROP_ID);

				double angle = Utils.angle(currentController.getLocation(), angProp.getLocation());

				angArc.setArcSpan((int) Math.round(angle));
				angArc.redraw();

				if (!dataLoad) {
					// Angular velocities are symmetrical, only differ by sign
					currentController.setAngularVelocityMin(-Math.toRadians(angle) / (Database.GIB_DEATH_ANIM_TIME * Database.GIB_ANGULAR_SPEED));
					currentController.setAngularVelocityMax(Math.toRadians(angle) / (Database.GIB_DEATH_ANIM_TIME * Database.GIB_ANGULAR_SPEED));
				}
			}
		};

		ArcPropController arc = new ArcPropController(null, ANG_ARC_PROP_ID);
		arc.setInheritVisibility(false);
		arc.setVisible(false);
		arc.setDefaultBackgroundColor(64, 192, 255);
		arc.setAlpha(128);
		arc.setSize(200, 200);
		arc.setStartAngle(90);
		arc.addToPainter(Layers.PROP);
		arc.updateView();
		addProp(arc);

		OrbitPropController orbitProp = new OrbitPropController(null, ANG_PROP_ID);
		orbitProp.setShape(Shapes.OVAL);
		orbitProp.setSelectable(true);
		orbitProp.setInheritVisibility(false);
		orbitProp.setVisible(false);
		orbitProp.setDefaultBorderColor(0, 0, 0);
		orbitProp.setDefaultBackgroundColor(64, 192, 255);
		orbitProp.setOrbitOffset(100);
		orbitProp.setSize(2 * ShipContainer.CELL_SIZE / 3, 2 * ShipContainer.CELL_SIZE / 3);
		orbitProp.setBorderThickness(3);
		orbitProp.addToPainter(Layers.PROP);
		orbitProp.updateView();
		orbitProp.setCompositeTitle("Angular Velocity");
		orbitProp.addListener(SLEvent.SELECT, selectionListener);
		orbitProp.addListener(SLEvent.DESELECT, selectionListener);
		orbitProp.addListener(SLEvent.MOVE, angularListener);
		addProp(orbitProp);
	}

	public void dispose() {
		for (PropController prop : props) {
			prop.dispose();
		}
		props.clear();
	}
}
