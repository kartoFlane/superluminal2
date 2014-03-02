package com.kartoflane.superluminal2.mvc.models;

import org.eclipse.swt.SWT;

import com.kartoflane.superluminal2.mvc.Model;

public class StationModel extends AbstractDependentModel {

	protected SystemModel system;

	/**
	 * Id of the tile on which the station will be located.<br>
	 * -2 = use default value<br>
	 * -1 = no station<br>
	 * A 2x2 room has slot ids like this:
	 * <p>
	 * [ 0 1 ]<br>
	 * [ 2 3 ]
	 * </p>
	 */
	protected int slotId = -2;
	/** Valid values: SWT.UP, SWT.RIGHT, SWT.DOWN, SWT.LEFT, SWT.NONE */
	protected int slotDirection = SWT.UP;

	public StationModel(SystemModel system) {
		super();
		setDependency(system);
	}

	@Override
	protected Model getDependency() {
		return system;
	}

	@Override
	protected void setDependency(Model model) {
		system = (SystemModel) model;
	}

	/**
	 * Id of the tile on which the station will be located.<br>
	 * -2 = use default value<br>
	 * -1 = no station<br>
	 * A 2x2 room has slot ids like this:
	 * <p>
	 * [ 0 1 ]<br>
	 * [ 2 3 ]
	 * </p>
	 */
	public void setSlotId(int id) {
		slotId = id;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setSlotDirection(int dir) {
		slotDirection = dir;
	}

	public int getSlotDirection() {
		return slotDirection;
	}
}
