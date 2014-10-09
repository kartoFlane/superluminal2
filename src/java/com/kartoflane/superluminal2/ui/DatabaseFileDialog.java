package com.kartoflane.superluminal2.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.interfaces.Action;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.views.Preview;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;

public class DatabaseFileDialog extends Dialog implements SelectionListener {

	private final Predicate<String> defaultFilter = new Predicate<String>() {
		public boolean accept(String s) {
			if (filterExtensions == null)
				return true;

			for (int i = 0; i < filterExtensions.length; i++) {
				if (filterExtensions[i] == null || s.matches(regexize(filterExtensions[i])))
					return true;
			}
			return false;
		}
	};

	private String result = null;
	private String[] filterExtensions = new String[0];
	private Map<String, TreeItem> itemMap = new HashMap<String, TreeItem>();

	private Predicate<String> filter = defaultFilter;

	private Shell shell;
	private Button btnConfirm;
	private Button btnCancel;
	private Tree tree;
	private Composite composite;
	private Combo cmbExtensions;
	private Text txtFile;
	private Canvas canvas;
	private Preview preview;
	private SashForm sashForm;
	private Button btnSearch;

	public DatabaseFileDialog(Shell parent, int style) {
		super(parent, style);
	}

	public DatabaseFileDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	public String open() {
		createGUI();
		loadItems(regexize(cmbExtensions.getText()));
		shell.open();

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return result;
	}

	public void setFilterExtensions(String[] ext) {
		filterExtensions = ext;
	}

	private void createGUI() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setSize(500, 350);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));

		sashForm = new SashForm(shell, SWT.SMOOTH);
		sashForm.setSashWidth(5);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		tree = new Tree(sashForm, SWT.BORDER);
		tree.addSelectionListener(this);

		canvas = new Canvas(sashForm, SWT.NONE);
		preview = new Preview();
		RGB rgb = canvas.getBackground().getRGB();
		preview.setBackgroundColor((int) (0.9 * rgb.red), (int) (0.9 * rgb.green), (int) (0.9 * rgb.blue));
		preview.setDrawBackground(true);
		canvas.addPaintListener(preview);

		sashForm.setWeights(new int[] { 3, 2 });

		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		if (filterExtensions == null)
			filterExtensions = new String[0];

		cmbExtensions = new Combo(shell, SWT.READ_ONLY);
		GridData gd_cmbExtensions = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_cmbExtensions.widthHint = 80;
		cmbExtensions.setLayoutData(gd_cmbExtensions);

		for (int i = 0; i < filterExtensions.length; i++)
			cmbExtensions.add(filterExtensions[i]);
		cmbExtensions.select(0);
		cmbExtensions.addSelectionListener(this);

		composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		btnSearch = new Button(composite, SWT.NONE);
		GridData gd_btnSearch = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSearch.widthHint = 80;
		btnSearch.setLayoutData(gd_btnSearch);
		btnSearch.setText("Search");
		btnSearch.addSelectionListener(this);

		btnConfirm = new Button(composite, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");
		btnConfirm.addSelectionListener(this);

		btnCancel = new Button(composite, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(this);

		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				btnCancel.notifyListeners(SWT.Selection, null);
				e.doit = false;
			}
		});

		tree.addMouseListener(new MouseAdapter() {
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
		});

		canvas.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				updatePreview();
				canvas.redraw();
			}
		});

		txtFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text = txtFile.getText();
				boolean found = itemMap.containsKey(text) && itemMap.get(text).getItemCount() == 0;
				btnConfirm.setEnabled(found);

				if (found && text.endsWith(".png")) {
					preview.setImage("db:" + text);
					preview.setDrawBackground(true);
				} else {
					preview.setImage(null);
					preview.setDrawBackground(false);
				}
				updatePreview();
				canvas.redraw();
			}
		});

		// Register hotkeys
		Hotkey h = new Hotkey();
		h.setOnPress(new Action() {
			public void execute() {
				if (tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					if (selectedItem.getItemCount() == 0 && btnConfirm.isEnabled())
						btnConfirm.notifyListeners(SWT.Selection, null);
					else
						selectedItem.setExpanded(!selectedItem.getExpanded());
				}
			}
		});
		h.setKey(SWT.CR);
		Manager.hookHotkey(shell, h);

		h = new Hotkey();
		h.setCtrl(true);
		h.setKey('f');
		h.addNotifyAction(btnSearch, true);
		Manager.hookHotkey(shell, h);
	}

	private void loadItems(String ext) {
		for (TreeItem item : itemMap.values())
			item.dispose();
		itemMap.clear();
		tree.removeAll();

		List<String> pathlist = Database.getInstance().listFiles(filter);
		Collections.sort(pathlist, new AlphanumComparator());

		for (String ipath : pathlist) {
			if (ipath.matches(ext))
				createItems(ipath);
		}

		if (filter != defaultFilter) {
			for (TreeItem item : itemMap.values())
				item.setExpanded(true);
		}
	}

	private void createItems(String ipath) {
		String[] dirs = ipath.split("/");

		String bs = dirs[0];
		if (!itemMap.containsKey(bs)) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(dirs[0]);
			itemMap.put(bs, item);
		}

		for (int i = 1; i < dirs.length; i++) {
			String ps = bs;
			bs = bs + "/" + dirs[i];

			if (!itemMap.containsKey(bs)) {
				TreeItem item = null;
				if (i == dirs.length - 1) {
					item = new TreeItem(itemMap.get(ps), SWT.NONE);
				} else {
					item = new TreeItem(itemMap.get(ps), SWT.NONE, findPos(itemMap.get(ps), dirs[i]));
				}
				item.setText(dirs[i]);
				itemMap.put(bs, item);
			}
		}
	}

	/**
	 * Find the index at which a new item with the given name should be inserted in the parent.
	 * Allows to sort items that have children before ones that have none.
	 */
	private int findPos(TreeItem parent, String insertee) {
		int result = parent.getItemCount() - 1;

		int comp = 1;
		for (; comp == 1 && result >= 0; result--) {
			TreeItem item = parent.getItem(result);
			if (item.getItemCount() == 0)
				continue;
			comp = item.getText().compareTo(insertee);
		}

		if (result < 0)
			result = 0;

		return result;
	}

	private String regexize(String ext) {
		ext = ext.replace(".", "\\.");
		ext = ext.replace("*", ".*?");
		return ext;
	}

	private String reconstructPath(TreeItem item) {
		StringBuilder sb = new StringBuilder();
		sb.append(item.getText());

		TreeItem parent = item.getParentItem();
		while (parent != null) {
			sb.insert(0, "/");
			sb.insert(0, parent.getText());
			parent = parent.getParentItem();
		}

		return sb.toString();
	}

	private void updatePreview() {
		Point iSize = preview.getImageSize();
		Point cSize = canvas.getSize();

		double ratio = (double) iSize.y / iSize.x;
		int w = (int) (cSize.y / ratio);
		int h = (int) (cSize.x * ratio);
		preview.setSize(Utils.min(w, cSize.x, iSize.x), Utils.min(h, cSize.y, iSize.y));
		preview.setLocation(cSize.x / 2, cSize.y / 2);
	}

	protected void dispose() {
		itemMap.clear();
		preview.dispose();
		shell.dispose();
	}

	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();

		if (source == btnConfirm) {
			result = txtFile.getText();

			if (Database.getInstance().contains(result)) {
				dispose();
			} else {
				UIUtils.showWarningDialog(EditorWindow.getInstance().getShell(), null,
						"The file you have selected does not exist.");
			}
		}
		else if (source == btnCancel) {
			result = null;
			dispose();
		}
		else if (source == btnSearch) {
			DatabaseSearchDialog dbsDialog = new DatabaseSearchDialog(shell);
			Predicate<String> resultFilter = dbsDialog.open();

			if (resultFilter == AbstractSearchDialog.RESULT_DEFAULT) {
				if (filter == defaultFilter)
					return;
				filter = defaultFilter;
			} else if (resultFilter == AbstractSearchDialog.RESULT_UNCHANGED) {
				// Do nothing
				return;
			} else {
				filter = resultFilter;
			}

			loadItems(regexize(cmbExtensions.getText()));
			tree.notifyListeners(SWT.Selection, null);
		}
		else if (source == tree) {
			if (tree.getSelectionCount() > 0)
				txtFile.setText(reconstructPath(tree.getSelection()[0]));
		}
		else if (source == cmbExtensions) {
			btnConfirm.setEnabled(false);
			loadItems(regexize(cmbExtensions.getText()));
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	/**
	 * The Alphanum Algorithm is an improved sorting algorithm for strings
	 * containing numbers. Instead of sorting numbers in ASCII order like
	 * a standard sort, this algorithm sorts numbers in numeric order.
	 *
	 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
	 * 
	 * This is an updated version with enhancements made by Daniel Migowski,
	 * Andre Bogus, and David Koelle
	 */
	private class AlphanumComparator implements Comparator<String> {
		private final boolean isDigit(char ch) {
			return ch >= 48 && ch <= 57;
		}

		/** Length of string is passed in for improved efficiency (only need to calculate it once) **/
		private final String getChunk(String s, int slength, int marker) {
			StringBuilder chunk = new StringBuilder();
			char c = s.charAt(marker);
			chunk.append(c);
			marker++;
			if (isDigit(c)) {
				while (marker < slength) {
					c = s.charAt(marker);
					if (!isDigit(c))
						break;
					chunk.append(c);
					marker++;
				}
			} else {
				while (marker < slength) {
					c = s.charAt(marker);
					if (isDigit(c))
						break;
					chunk.append(c);
					marker++;
				}
			}
			return chunk.toString();
		}

		public int compare(String s1, String s2) {
			int thisMarker = 0;
			int thatMarker = 0;
			int s1Length = s1.length();
			int s2Length = s2.length();

			while (thisMarker < s1Length && thatMarker < s2Length) {
				String thisChunk = getChunk(s1, s1Length, thisMarker);
				thisMarker += thisChunk.length();

				String thatChunk = getChunk(s2, s2Length, thatMarker);
				thatMarker += thatChunk.length();

				// If both chunks contain numeric characters, sort them numerically
				int result = 0;
				if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
					// Simple chunk comparison by length.
					int thisChunkLength = thisChunk.length();
					result = thisChunkLength - thatChunk.length();
					// If equal, the first different number counts
					if (result == 0) {
						for (int i = 0; i < thisChunkLength; i++) {
							result = thisChunk.charAt(i) - thatChunk.charAt(i);
							if (result != 0) {
								return result;
							}
						}
					}
				} else {
					result = thisChunk.compareTo(thatChunk);
				}

				if (result != 0)
					return result;
			}

			return s1Length - s2Length;
		}
	}
}
