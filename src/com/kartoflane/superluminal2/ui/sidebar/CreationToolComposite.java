package com.kartoflane.superluminal2.ui.sidebar;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;

public class CreationToolComposite extends Composite implements SidebarComposite {
	private static Tools selectedTool = Tools.ROOM;

	private Composite toolContainer;
	private Composite dataContainer;

	private ToolBar toolBar;
	private ToolItem tltmRoom;
	private ToolItem tltmDoor;
	private ToolItem tltmMount;
	private ToolItem tltmStation;

	private final HashMap<Tools, ToolItem> toolItemMap = new HashMap<Tools, ToolItem>();

	public CreationToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		label.setText("Creation Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		toolContainer = new Composite(this, SWT.BORDER);
		GridLayout gl_toolContainer = new GridLayout(1, false);
		toolContainer.setLayout(gl_toolContainer);
		toolContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		toolBar = new ToolBar(toolContainer, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tltmRoom = new ToolItem(toolBar, SWT.RADIO);
		tltmRoom.setImage(Cache.checkOutImage(CreationToolComposite.class, "/assets/room.png"));
		tltmRoom.setData(Tools.ROOM);
		tltmRoom.setToolTipText("Room Creation");
		toolItemMap.put(Tools.ROOM, tltmRoom);

		tltmDoor = new ToolItem(toolBar, SWT.RADIO);
		tltmDoor.setImage(Cache.checkOutImage(CreationToolComposite.class, "/assets/door.png"));
		tltmDoor.setData(Tools.DOOR);
		tltmDoor.setToolTipText("Door Creation");
		toolItemMap.put(Tools.DOOR, tltmDoor);

		tltmMount = new ToolItem(toolBar, SWT.RADIO);
		tltmMount.setImage(Cache.checkOutImage(CreationToolComposite.class, "/assets/mount.png"));
		tltmMount.setData(Tools.WEAPON);
		tltmMount.setToolTipText("Weapon Mount Creation");
		toolItemMap.put(Tools.WEAPON, tltmMount);

		tltmStation = new ToolItem(toolBar, SWT.RADIO);
		tltmStation.setImage(Cache.checkOutImage(CreationToolComposite.class, "/assets/station.png"));
		tltmStation.setData(Tools.STATION);
		tltmStation.setToolTipText("Station Placement");
		toolItemMap.put(Tools.STATION, tltmStation);

		dataContainer = new Composite(this, SWT.BORDER);
		GridLayout gl_dataContainer = new GridLayout(1, false);
		gl_dataContainer.marginWidth = 0;
		gl_dataContainer.marginHeight = 0;
		dataContainer.setLayout(gl_dataContainer);
		dataContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectTool((ToolItem) e.getSource());
			}
		};

		tltmRoom.addSelectionListener(listener);
		tltmDoor.addSelectionListener(listener);
		tltmMount.addSelectionListener(listener);
		tltmStation.addSelectionListener(listener);

		selectTool(selectedTool);
	}

	private void selectTool(ToolItem item) {
		selectedTool = (Tools) item.getData();
		Manager.selectTool(selectedTool);
		setToolComposite(selectedTool);
	}

	public void selectTool(Tools toolId) {
		for (Tools key : toolItemMap.keySet()) {
			ToolItem it = toolItemMap.get(key);
			it.setSelection(key == toolId);
			if (key == toolId)
				selectTool(it);
		}
	}

	public void setToolComposite(Tools t) {
		Composite c = getComposite();
		if (c != null)
			c.dispose();

		c = Manager.getTool(t).getToolComposite(dataContainer);
		if (c != null) {
			Control[] changed = { c };
			dataContainer.layout(changed);
		}
	}

	public Tools getSelectedTool() {
		return selectedTool;
	}

	@Override
	public DataComposite getDataComposite() {
		return null;
	}

	private Composite getComposite() {
		if (dataContainer.getChildren().length == 0)
			return null;
		else
			return (Composite) dataContainer.getChildren()[0];
	}
}
