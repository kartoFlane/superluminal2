package com.kartoflane.superluminal2.mvc;

import com.kartoflane.superluminal2.components.interfaces.Redrawable;
import com.kartoflane.superluminal2.components.interfaces.Resizable;

public interface View extends Redrawable, Resizable {

	public Controller getController();

	public void setController(Controller controller);

	public void setHighlighted(boolean high);

	public boolean isHighlighted();
}
