package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.kartoflane.superluminal2.components.enums.Races;

public class CrewMenu {

	private static CrewMenu instance;

	private Races result = null;

	private Menu crewMenu;
	private MenuItem mntmNoCrew;

	public CrewMenu(Control parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		crewMenu = new Menu(parent);
		parent.setMenu(crewMenu);

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = (Races) ((Widget) e.getSource()).getData();
				dispose();
			}
		};

		mntmNoCrew = new MenuItem(crewMenu, SWT.NONE);
		mntmNoCrew.setText("No Crew");
		mntmNoCrew.setData(Races.NO_CREW);
		mntmNoCrew.addSelectionListener(listener);

		new MenuItem(crewMenu, SWT.SEPARATOR);

		for (Races race : Races.getRaces()) {
			MenuItem item = new MenuItem(crewMenu, SWT.NONE);
			item.setText(race.toString());
			item.setData(race);
			item.addSelectionListener(listener);
		}

		crewMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuHidden(MenuEvent e) {
				dispose();
			}

			@Override
			public void menuShown(MenuEvent e) {
			}
		});
	}

	public static CrewMenu getInstance() {
		return instance;
	}

	public Races open() {
		crewMenu.setVisible(true);

		Display display = Display.getCurrent();
		while (!crewMenu.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		System.out.println("Returning " + result);

		return result;
	}

	public void setLocation(int x, int y) {
		crewMenu.setLocation(x, y);
	}

	public void setLocation(Point p) {
		crewMenu.setLocation(p.x, p.y);
	}

	public void dispose() {
		crewMenu.dispose();
		instance = null;
	}

	public boolean isDisposed() {
		return crewMenu == null || crewMenu.isDisposed();
	}
}
