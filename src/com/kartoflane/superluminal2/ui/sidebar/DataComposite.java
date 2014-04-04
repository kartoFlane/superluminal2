package com.kartoflane.superluminal2.ui.sidebar;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;

/**
 * A composite used to represent the modifiable data of an entity in the sidebar.
 * 
 * @author kartoFlane
 * 
 */
public interface DataComposite {

	public void updateData();

	/**
	 * Sets the DataComposite's controller to the specified controller, and
	 * calls the updateData() method.
	 */
	public void setController(AbstractController controller);
}
