package com.kartoflane.superluminal2.ui.sidebar;

import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;

public interface SidebarComposite {
	/** @return the Composite that's being held in the data container of this composite, interpreted as DataComposite. */
	public DataComposite getDataComposite();
}
