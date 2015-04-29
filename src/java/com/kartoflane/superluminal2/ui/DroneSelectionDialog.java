package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.enums.DroneStats;
import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.enums.Hotkeys;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.utils.UIUtils;

public class DroneSelectionDialog {

	private static DroneSelectionDialog instance = null;

	private static final int defaultBlueTabWidth = 200;
	private static final int defaultNameTabWidth = 150;
	private static final int minTreeWidth = defaultBlueTabWidth + defaultNameTabWidth + 5;
	private static final int defaultDataWidth = 200;
	private static final Predicate<DroneObject> defaultFilter = new Predicate<DroneObject>() {
		public boolean accept(DroneObject object) {
			return true;
		}
	};

	private static DroneObject selection = null;
	private static DroneList selectionList = null;

	private DroneObject result = null;
	private DroneList resultList = null;
	private int response = SWT.NO;

	private boolean listMode = false;
	private boolean sortByBlueprint = true;
	private HashMap<DroneTypes, TreeItem> treeItemMap = null;
	private Predicate<DroneObject> filter = defaultFilter;

	private Shell shell = null;
	private Text txtDesc;
	private StatTable statTable;
	private Button btnCancel;
	private Button btnConfirm;
	private Tree tree;
	private TreeColumn trclmnBlueprint;
	private TreeColumn trclmnName;
	private Button btnSearch;

	public DroneSelectionDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		treeItemMap = new HashMap<DroneTypes, TreeItem>();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Drone Selection");
		shell.setLayout(new GridLayout(1, false));

		SashForm sashForm = new SashForm(shell, SWT.SMOOTH);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tree = new Tree(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setHeaderVisible(true);
		// remove the horizontal bar so that it doesn't flicker when the tree is resized
		tree.getHorizontalBar().dispose();

		trclmnBlueprint = new TreeColumn(tree, SWT.LEFT);
		trclmnBlueprint.setWidth(defaultBlueTabWidth);
		trclmnBlueprint.setText("Blueprint");

		trclmnName = new TreeColumn(tree, SWT.RIGHT);
		trclmnName.setWidth(defaultNameTabWidth);
		trclmnName.setText("Name");

		ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite compData = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_compData = new GridLayout(1, false);
		gl_compData.marginHeight = 0;
		gl_compData.marginWidth = 0;
		compData.setLayout(gl_compData);

		Label lblDescription = new Label(compData, SWT.NONE);
		lblDescription.setText("Description:");

		txtDesc = new Text(compData, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_txtDesc = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_txtDesc.heightHint = 60;
		txtDesc.setLayoutData(gd_txtDesc);
		scrolledComposite.setContent(compData);
		scrolledComposite.setMinSize(compData.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sashForm.setWeights(new int[] { minTreeWidth, defaultDataWidth });

		statTable = new StatTable(compData);
		statTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite compButtons = new Composite(shell, SWT.NONE);
		GridLayout gl_compButtons = new GridLayout(3, false);
		gl_compButtons.marginWidth = 0;
		gl_compButtons.marginHeight = 0;
		compButtons.setLayout(gl_compButtons);
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		btnSearch = new Button(compButtons, SWT.NONE);
		GridData gd_btnSearch = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSearch.widthHint = 80;
		btnSearch.setLayoutData(gd_btnSearch);
		btnSearch.setText("Search");

		btnConfirm = new Button(compButtons, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		btnCancel = new Button(compButtons, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1 && tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					if (selectedItem.getItemCount() == 0 && btnConfirm.isEnabled())
						btnConfirm.notifyListeners(SWT.Selection, null);
					else if (selectedItem.getBounds().contains(e.x, e.y))
						selectedItem.setExpanded(!selectedItem.getExpanded());
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					Object o = selectedItem.getData();
					if (o instanceof DroneList) {
						selectionList = (DroneList) o;
						resultList = selectionList;
						result = null;
						btnConfirm.setEnabled(listMode && resultList != null);
					} else if (o instanceof DroneObject) {
						resultList = null;
						selection = (DroneObject) o;
						result = selection;
						btnConfirm.setEnabled(!listMode && result != null);
					} else {
						resultList = null;
						result = null;
						btnConfirm.setEnabled(false);
					}
				} else {
					resultList = null;
					result = null;
					btnConfirm.setEnabled(false);
				}
				updateData();
			}
		});

		trclmnBlueprint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Sort by blueprint name
				if (!listMode && !sortByBlueprint) {
					sortByBlueprint = true;
					updateTree();
				}
			}
		});

		trclmnName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Sort by title
				if (!listMode && sortByBlueprint) {
					sortByBlueprint = false;
					updateTree();
				}
			}
		});

		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DroneSearchDialog dsDialog = new DroneSearchDialog(shell);
				Predicate<DroneObject> result = dsDialog.open();

				if (result == AbstractSearchDialog.RESULT_DEFAULT) {
					filter = defaultFilter;
				} else if (result == AbstractSearchDialog.RESULT_UNCHANGED) {
					// Do nothing
				} else {
					filter = result;
				}

				updateTree();
				tree.notifyListeners(SWT.Selection, null);
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				response = SWT.NO;
				dispose();
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				response = SWT.YES;
				dispose();
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				btnCancel.notifyListeners(SWT.Selection, null);
				e.doit = false;
			}
		});

		shell.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN && tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					if (selectedItem.getItemCount() == 0 && btnConfirm.isEnabled())
						btnConfirm.notifyListeners(SWT.Selection, null);
					else
						selectedItem.setExpanded(!selectedItem.getExpanded());
				}
			}
		});

		ControlAdapter resizer = new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				final int BORDER_OFFSET = tree.getBorderWidth();
				if (trclmnBlueprint.getWidth() > tree.getClientArea().width - BORDER_OFFSET)
					trclmnBlueprint.setWidth(tree.getClientArea().width - BORDER_OFFSET);
				trclmnName.setWidth(tree.getClientArea().width - trclmnBlueprint.getWidth() - BORDER_OFFSET);
			}
		};
		tree.addControlListener(resizer);
		trclmnBlueprint.addControlListener(resizer);

		shell.setMinimumSize(minTreeWidth + defaultDataWidth, 300);
		shell.pack();
		Point size = shell.getSize();
		shell.setSize(size.x + 5, size.y);
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);

		// Register hotkeys
		Hotkey h = new Hotkey(Manager.getHotkey(Hotkeys.SEARCH));
		h.addNotifyAction(btnSearch, true);
		Manager.hookHotkey(shell, h);
	}

	private void open() {
		response = SWT.NO;

		if (listMode)
			updateTreeList();
		else
			updateTree();

		shell.open();

		updateData();

		Display display = UIUtils.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public DroneList open(DroneList current) {
		listMode = true;
		resultList = current;
		result = null;

		if (current == null || current == Database.DEFAULT_DRONE_LIST) {
			resultList = selectionList;
		} else {
			selectionList = resultList;
		}

		btnSearch.setEnabled(false);

		open();

		if (response == SWT.YES)
			return resultList;
		else
			return null;
	}

	public DroneObject open(DroneObject current) {
		listMode = false;
		resultList = null;
		result = current;

		if (current == null || current == Database.DEFAULT_DRONE_OBJ) {
			result = selection;
		} else {
			selection = result;
		}

		btnSearch.setEnabled(true);

		open();

		if (response == SWT.YES)
			return result;
		else
			return null;
	}

	private void updateTree() {
		for (TreeItem trtm : tree.getItems())
			trtm.dispose();

		TreeItem trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("No Drone");
		trtm.setData(Database.DEFAULT_DRONE_OBJ);

		TreeItem trtmDef = new TreeItem(tree, SWT.NONE);
		trtmDef.setText("Defensive");

		trtm = new TreeItem(trtmDef, SWT.NONE);
		trtm.setText("Anti-Personnel Drones");
		treeItemMap.put(DroneTypes.BATTLE, trtm);

		trtm = new TreeItem(trtmDef, SWT.NONE);
		trtm.setText("Defense Drones");
		treeItemMap.put(DroneTypes.DEFENSE, trtm);

		trtm = new TreeItem(trtmDef, SWT.NONE);
		trtm.setText("System Repair Drones");
		treeItemMap.put(DroneTypes.REPAIR, trtm);

		trtm = new TreeItem(trtmDef, SWT.NONE);
		trtm.setText("Hull Repair Drones");
		treeItemMap.put(DroneTypes.SHIP_REPAIR, trtm);

		trtm = new TreeItem(trtmDef, SWT.NONE);
		trtm.setText("Shield Drones");
		treeItemMap.put(DroneTypes.SHIELD, trtm);

		TreeItem trtmOff = new TreeItem(tree, SWT.NONE);
		trtmOff.setText("Offensive");

		trtm = new TreeItem(trtmOff, SWT.NONE);
		trtm.setText("Boarding Drones");
		treeItemMap.put(DroneTypes.BOARDER, trtm);

		trtm = new TreeItem(trtmOff, SWT.NONE);
		trtm.setText("Combat Drones");
		treeItemMap.put(DroneTypes.COMBAT, trtm);

		TreeItem selection = null;

		for (DroneTypes type : DroneTypes.getPlayableDroneTypes()) {
			TreeItem typeItem = treeItemMap.get(type);
			DroneIterator it = new DroneIterator(Database.getInstance().getDronesByType(type), sortByBlueprint);
			for (it.first(); it.hasNext(); it.next()) {
				DroneObject drone = it.current();

				trtm = new TreeItem(typeItem, SWT.NONE);
				trtm.setText(0, drone.getBlueprintName());
				trtm.setText(1, drone.getTitle());
				trtm.setData(drone);

				if (result == drone)
					selection = trtm;
			}

			if (typeItem.getItemCount() == 0)
				typeItem.dispose();
		}

		if (trtmDef.getItemCount() == 0)
			trtmDef.dispose();
		if (trtmOff.getItemCount() == 0)
			trtmOff.dispose();

		tree.layout();

		if (selection != null) {
			treeItemMap.get(result.getType()).setExpanded(true);
			tree.select(selection);
			tree.setTopItem(selection);
		} else {
			tree.select(tree.getItem(0));
		}
	}

	private void updateTreeList() {
		for (TreeItem trtm : tree.getItems())
			trtm.dispose();

		TreeItem trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("No Drone List");
		trtm.setData(Database.DEFAULT_DRONE_LIST);

		TreeItem selection = null;

		for (DroneList list : Database.getInstance().getDroneLists()) {
			trtm = new TreeItem(tree, SWT.NONE);
			trtm.setText(list.getBlueprintName());
			trtm.setData(list);

			if (resultList == list)
				selection = trtm;

			for (DroneObject drone : list) {
				TreeItem droneItem = new TreeItem(trtm, SWT.NONE);
				droneItem.setText(0, drone.getBlueprintName());
				droneItem.setText(1, drone.getTitle());
				droneItem.setData(drone);
			}
		}

		tree.layout();

		if (selection != null) {
			tree.select(selection);
			tree.setTopItem(selection);
		} else {
			tree.select(tree.getItem(0));
		}
	}

	private void updateData() {
		if (result != null) {
			statTable.setVisible(false);
			statTable.clear();
			for (DroneStats stat : DroneStats.values()) {
				if (!stat.doesApply(result.getType()))
					continue;
				float value = result.getStat(stat);
				statTable.addEntry(stat.toString(), stat.formatValue(value));
			}
			statTable.setVisible(true);
			statTable.updateColumnWidth();
			txtDesc.setText(result.getDescription());
		} else {
			statTable.clear();
			txtDesc.setText("");
		}
	}

	public static DroneSelectionDialog getInstance() {
		return instance;
	}

	public void dispose() {
		Manager.unhookHotkeys(shell);
		shell.dispose();
		instance = null;
	}

	public boolean isActive() {
		return !shell.isDisposed() && shell.isVisible();
	}

	private class DroneIterator implements Iterator<DroneObject> {
		private final ArrayList<DroneObject> list;
		private final DroneComparator comparator;

		private DroneObject current = null;

		public DroneIterator(ArrayList<DroneObject> list, boolean byBlueprint) {
			comparator = new DroneComparator(byBlueprint);

			if (filter == defaultFilter) {
				this.list = list;
			} else {
				this.list = new ArrayList<DroneObject>();
				for (DroneObject d : list) {
					if (filter.accept(d))
						this.list.add(d);
				}
			}
		}

		private DroneObject getSmallestElement() {
			DroneObject result = null;
			for (DroneObject drone : list) {
				if (result == null || comparator.compare(drone, result) < 0)
					result = drone;
			}

			return result;
		}

		public void first() {
			current = getSmallestElement();
		}

		public DroneObject current() {
			return current;
		}

		@Override
		public boolean hasNext() {
			return !list.isEmpty();
		}

		@Override
		public DroneObject next() {
			remove();
			current = getSmallestElement();
			return current;
		}

		@Override
		public void remove() {
			list.remove(current);
		}
	}

	private class DroneComparator implements Comparator<DroneObject> {
		private final boolean byBlueprint;

		public DroneComparator(boolean byBlueprint) {
			this.byBlueprint = byBlueprint;
		}

		@Override
		public int compare(DroneObject o1, DroneObject o2) {
			if (byBlueprint) {
				// Just compare the two blueprints together for alphanumerical ordering
				return o1.getBlueprintName().compareTo(o2.getBlueprintName());
			} else {
				int result = o1.getTitle().compareTo(o2.getTitle());
				if (result == 0) // If titles are the same, fall back to sorting by blueprint
					result = o1.getBlueprintName().compareTo(o2.getBlueprintName());
				return result;
			}
		}
	}
}
