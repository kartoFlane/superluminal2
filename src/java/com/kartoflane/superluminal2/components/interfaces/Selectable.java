package com.kartoflane.superluminal2.components.interfaces;

public interface Selectable
{
	public void setSelectable( boolean sel );

	public boolean isSelectable();

	public void select();

	public void deselect();

	public boolean isSelected();
}
