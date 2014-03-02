package com.kartoflane.superluminal2.mvc;

import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.ui.sidebar.DataComposite;

public interface Controller {

	public Model getModel();

	public void setModel(Model model);

	public View getView();

	public void setView(View view);

	public DataComposite getDataComposite(Composite parent);
}
