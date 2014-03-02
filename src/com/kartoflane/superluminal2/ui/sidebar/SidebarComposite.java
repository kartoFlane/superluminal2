package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.widgets.Composite;

public interface SidebarComposite {
	/** @return the Composite that's being held in the data container of this composite, interpreted as DataComposite. */
	public DataComposite getDataComposite();

	/** @return the Composite that's being held in the data container of this composite. */
	public Composite getComposite();
}
