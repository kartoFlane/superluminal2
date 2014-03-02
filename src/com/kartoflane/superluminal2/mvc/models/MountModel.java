package com.kartoflane.superluminal2.mvc.models;

import org.eclipse.swt.SWT;

import com.kartoflane.superluminal2.components.interfaces.Alias;

public class MountModel extends AbstractModel implements Alias, Comparable<MountModel> {

	private static final long serialVersionUID = 8982335315779325160L;

	protected int id = -1;
	protected WeaponModel weapon = null;
	protected boolean mirrored = false;
	protected boolean rotated = false;
	protected int direction = SWT.UP;

	protected String alias = null;

	public MountModel(int id) {
		super();

		setLocModifiable(true);
		setSizeModifiable(false);

		setId(id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}

	public boolean isMirrored() {
		return mirrored;
	}

	public void setRotated(boolean rotated) {
		this.rotated = rotated;
	}

	public boolean isRotated() {
		return rotated;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setWeapon(WeaponModel weapon) {
		this.weapon = weapon;
	}

	public WeaponModel getWeapon() {
		return weapon;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public int compareTo(MountModel o) {
		return id - o.id;
	}
}
