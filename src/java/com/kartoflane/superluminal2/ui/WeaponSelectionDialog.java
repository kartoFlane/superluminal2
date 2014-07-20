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
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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
import com.kartoflane.superluminal2.components.enums.WeaponStats;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.views.Preview;

public class WeaponSelectionDialog {

	private static WeaponSelectionDialog instance = null;

	private static final int defaultBlueTabWidth = 200;
	private static final int defaultNameTabWidth = 150;
	private static final int minTreeWidth = defaultBlueTabWidth + defaultNameTabWidth + 5;
	private static final int defaultDataWidth = 200;
	private static final Predicate<WeaponObject> defaultFilter = new Predicate<WeaponObject>() {
		public boolean accept(WeaponObject object) {
			return true;
		}
	};

	private static WeaponObject selection = Database.DEFAULT_WEAPON_OBJ;
	private static WeaponList selectionList = Database.DEFAULT_WEAPON_LIST;

	private WeaponObject result = null;
	private WeaponList resultList = null;
	private int response = SWT.NO;

	private boolean listMode = false;
	private boolean sortByBlueprint = true;
	private HashMap<WeaponTypes, TreeItem> treeItemMap = null;
	private Preview preview = null;
	private Predicate<WeaponObject> filter = defaultFilter;

	private Shell shell = null;
	private Text txtDesc;
	private Text txtStats;
	private Button btnCancel;
	private Button btnConfirm;
	private Tree tree;
	private Canvas canvas;
	private TreeColumn trclmnBlueprint;
	private TreeColumn trclmnName;
	private Button btnSearch;

	public WeaponSelectionDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		treeItemMap = new HashMap<WeaponTypes, TreeItem>();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Weapon Selection");
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

		canvas = new Canvas(compData, SWT.DOUBLE_BUFFERED);
		GridData gd_canvas = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_canvas.heightHint = 100;
		canvas.setLayoutData(gd_canvas);

		preview = new Preview();
		RGB rgb = canvas.getBackground().getRGB();
		preview.setBackgroundColor((int) (0.9 * rgb.red), (int) (0.9 * rgb.green), (int) (0.9 * rgb.blue));
		preview.setDrawBackground(true);
		canvas.addPaintListener(preview);

		Label lblStats = new Label(compData, SWT.NONE);
		lblStats.setText("Stats:");

		txtStats = new Text(compData, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_txtStats = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_txtStats.heightHint = 80;
		txtStats.setLayoutData(gd_txtStats);

		Label lblDescription = new Label(compData, SWT.NONE);
		lblDescription.setText("Description:");

		txtDesc = new Text(compData, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_txtDesc = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_txtDesc.heightHint = 60;
		txtDesc.setLayoutData(gd_txtDesc);
		scrolledComposite.setContent(compData);
		scrolledComposite.setMinSize(compData.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sashForm.setWeights(new int[] { minTreeWidth, defaultDataWidth });

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
					if (o instanceof WeaponList) {
						selectionList = (WeaponList) o;
						resultList = selectionList;
						result = null;
						btnConfirm.setEnabled(listMode && resultList != null);
					} else if (o instanceof WeaponObject) {
						resultList = null;
						selection = (WeaponObject) o;
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
				WeaponSearchDialog wsDialog = new WeaponSearchDialog(shell);
				Predicate<WeaponObject> result = wsDialog.open();

				if (result == AbstractSearchDialog.RESULT_DEFAULT) {
					filter = defaultFilter;
				} else if (result == AbstractSearchDialog.RESULT_UNCHANGED) {
					// Do nothing
				} else {
					filter = result;
				}

				updateTree();
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

		canvas.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				updatePreview();
				canvas.redraw();
			}
		});

		ControlAdapter resizer = new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				final int BORDER_OFFSET = 5;
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
	}

	private void open() {
		response = SWT.NO;

		if (listMode)
			updateTreeList();
		else
			updateTree();

		shell.open();

		updateData();

		Display display = Display.getCurrent();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public WeaponList open(WeaponList current) {
		listMode = true;
		resultList = current;
		result = null;

		if (current == null || current == Database.DEFAULT_WEAPON_LIST) {
			resultList = selectionList;
		} else {
			selectionList = resultList;
		}

		btnSearch.setEnabled(false);

		open();

		if (response == SWT.YES) {
			return resultList;
		} else {
			return null;
		}
	}

	public WeaponObject open(WeaponObject current) {
		listMode = false;
		resultList = null;
		result = current;

		if (current == null || current == Database.DEFAULT_WEAPON_OBJ) {
			result = selection;
		} else {
			selection = result;
		}

		btnSearch.setEnabled(true);

		open();

		if (response == SWT.YES) {
			return result;
		} else {
			return null;
		}
	}

	private void updateTree() {
		for (TreeItem trtm : tree.getItems())
			trtm.dispose();

		TreeItem trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("No Weapon");
		trtm.setData(Database.DEFAULT_WEAPON_OBJ);

		trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("Beams");
		treeItemMap.put(WeaponTypes.BEAM, trtm);

		trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("Bombs");
		treeItemMap.put(WeaponTypes.BOMB, trtm);

		trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("Burst");
		treeItemMap.put(WeaponTypes.BURST, trtm);

		trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("Lasers / Ions");
		treeItemMap.put(WeaponTypes.LASER, trtm);

		trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText("Missiles");
		treeItemMap.put(WeaponTypes.MISSILES, trtm);

		TreeItem selection = null;

		for (WeaponTypes type : WeaponTypes.values()) {
			WeaponIterator it = new WeaponIterator(Database.getInstance().getWeaponsByType(type), sortByBlueprint);
			for (it.first(); it.hasNext(); it.next()) {
				WeaponObject weapon = it.current();

				trtm = new TreeItem(treeItemMap.get(type), SWT.NONE);
				trtm.setText(0, weapon.getBlueprintName());
				trtm.setText(1, weapon.getTitle());
				trtm.setData(weapon);

				if (result == weapon)
					selection = trtm;
			}
		}

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
		trtm.setText("No Weapon List");
		trtm.setData(Database.DEFAULT_WEAPON_LIST);

		TreeItem selection = null;

		for (WeaponList list : Database.getInstance().getWeaponLists()) {
			trtm = new TreeItem(tree, SWT.NONE);
			trtm.setText(list.getBlueprintName());
			trtm.setData(list);

			if (resultList == list)
				selection = trtm;

			for (WeaponObject weapon : list) {
				TreeItem weaponItem = new TreeItem(trtm, SWT.NONE);
				weaponItem.setText(0, weapon.getBlueprintName());
				weaponItem.setText(1, weapon.getTitle());
				weaponItem.setData(weapon);
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
		String path = null;

		if (result != null) {
			AnimationObject anim = result.getAnimation();
			path = anim.getSheetPath();
			Point frameSize = anim.getFrameSize();
			preview.setSourceSize(frameSize.x, frameSize.y);
			preview.setOverrideSourceSize(true);
			preview.setImage(path);
			preview.setSize(frameSize.x, frameSize.y);

			StringBuilder buf = new StringBuilder();
			for (WeaponStats stat : WeaponStats.values()) {
				float value = result.getStat(stat);
				if (value != 0) {
					if (buf.length() != 0)
						buf.append("\n");
					buf.append(stat.toString() + ": " + value);
				}
			}
			txtStats.setText(buf.toString());
			txtDesc.setText(result.getDescription());
		} else {
			preview.setImage(null);
			txtStats.setText("");
			txtDesc.setText("");
		}

		preview.setDrawBackground(path != null);
		preview.setLocation(canvas.getSize().x / 2, canvas.getSize().y / 2);
		canvas.redraw();
	}

	private void updatePreview() {
		Point cSize = canvas.getSize();
		preview.setLocation(cSize.x / 2, cSize.y / 2);
	}

	public static WeaponSelectionDialog getInstance() {
		return instance;
	}

	public void dispose() {
		shell.dispose();
		instance = null;
	}

	public boolean isActive() {
		return !shell.isDisposed() && shell.isVisible();
	}

	private class WeaponIterator implements Iterator<WeaponObject> {
		private final ArrayList<WeaponObject> list;
		private final WeaponComparator comparator;

		private WeaponObject current = null;

		public WeaponIterator(ArrayList<WeaponObject> list, boolean byBlueprint) {
			comparator = new WeaponComparator(byBlueprint);

			if (filter == defaultFilter) {
				this.list = list;
			} else {
				this.list = new ArrayList<WeaponObject>();
				for (WeaponObject w : list) {
					if (filter.accept(w))
						this.list.add(w);
				}
			}
		}

		private WeaponObject getSmallestElement() {
			WeaponObject result = null;
			for (WeaponObject weapon : list) {
				if (result == null || comparator.compare(weapon, result) < 0)
					result = weapon;
			}

			return result;
		}

		public void first() {
			current = getSmallestElement();
		}

		public WeaponObject current() {
			return current;
		}

		@Override
		public boolean hasNext() {
			return !list.isEmpty();
		}

		@Override
		public WeaponObject next() {
			remove();
			current = getSmallestElement();
			return current;
		}

		@Override
		public void remove() {
			list.remove(current);
		}
	}

	private class WeaponComparator implements Comparator<WeaponObject> {
		private final boolean byBlueprint;

		public WeaponComparator(boolean byBlueprint) {
			this.byBlueprint = byBlueprint;
		}

		@Override
		public int compare(WeaponObject o1, WeaponObject o2) {
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
