package com.kartoflane.superluminal2.mvc;

public interface View {

	public void setController(Controller controller);

	public void setModel(Model model);

	public void updateView();
}
