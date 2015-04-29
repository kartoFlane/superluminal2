package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ui.GibPropContainer.PropControls;
import com.kartoflane.superluminal2.utils.UIUtils;

public class GibControlsMenu {

	private static GibControlsMenu instance = null;

	private PropControls result = null;

	private Menu controlsMenu = null;

	public GibControlsMenu(Control parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;
		controlsMenu = new Menu(parent);

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = (PropControls) ((Widget) e.getSource()).getData();
			}
		};

		PropControls current = Manager.getCurrentShip().getGibContainer().getShownControls();
		for (PropControls pc : PropControls.values()) {
			MenuItem item = new MenuItem(controlsMenu, SWT.RADIO);
			item.setText(pc.toString());
			item.setData(pc);
			item.addSelectionListener(listener);
			item.setSelection(pc == current);
		}
	}

	public static GibControlsMenu getInstance() {
		return instance;
	}

	public PropControls open() {
		controlsMenu.setVisible(true);

		Display display = UIUtils.getDisplay();
		while (controlsMenu.isVisible()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		dispose();
		return result;
	}

	public void setLocation(int x, int y) {
		controlsMenu.setLocation(x, y);
	}

	public void setLocation(Point p) {
		controlsMenu.setLocation(p.x, p.y);
	}

	public void dispose() {
		controlsMenu.dispose();
		instance = null;
	}

	public boolean isDisposed() {
		return controlsMenu == null || controlsMenu.isDisposed();
	}
}
