package com.kartoflane.superluminal2.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Hotkey.Hotkeys;
import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.MouseInputDispatcher;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CursorController;
import com.kartoflane.superluminal2.tools.CreationTool;
import com.kartoflane.superluminal2.tools.DoorTool;
import com.kartoflane.superluminal2.tools.GibTool;
import com.kartoflane.superluminal2.tools.ManipulationTool;
import com.kartoflane.superluminal2.tools.MountTool;
import com.kartoflane.superluminal2.tools.PropertyTool;
import com.kartoflane.superluminal2.tools.RoomTool;
import com.kartoflane.superluminal2.tools.StationTool;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.sidebar.CreationToolComposite;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;
import com.kartoflane.superluminal2.ui.sidebar.SidebarComposite;

public class EditorWindow {
	private static final Logger log = LogManager.getLogger(EditorWindow.class);

	public static final int SIDEBAR_MIN_WIDTH = 290;
	public static final int CANVAS_MIN_SIZE = 400;

	private final HashMap<Tools, ToolItem> toolItemMap = new HashMap<Tools, ToolItem>();
	private final RGB canvasRGB = new RGB(164, 164, 164);

	private static EditorWindow instance;

	private Shell shell;
	private ScrolledComposite sideContainer;
	private Canvas canvas;
	private LayeredPainter painter;

	private ToolItem tltmPointer;
	private ToolItem tltmCreation;
	private ToolItem tltmGib;
	private ToolItem tltmProperties;
	private ToolItem tltmManager;

	private MenuItem mntmUndo;
	private MenuItem mntmRedo;
	private MenuItem mntmDelete;

	private int sidebarWidth = SIDEBAR_MIN_WIDTH;
	private Color canvasColor = null;
	private boolean shellResizing = false;

	public EditorWindow(Display display) {
		instance = this;

		shell = new Shell(display);
		shell.setText(String.format("%s v%s - FTL Ship Editor", Superluminal.APP_NAME, Superluminal.APP_VERSION));
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout(gl_shell);

		Monitor m = Superluminal.display.getPrimaryMonitor();
		Rectangle displaySize = m.getClientArea();
		displaySize.width = (displaySize.width / 5) * 4;
		displaySize.height = (displaySize.height / 5) * 4;

		painter = new LayeredPainter();
		MouseInputDispatcher mouseListener = new MouseInputDispatcher();
		CursorController.newInstance();
		new OverviewWindow(shell);
		new ShipLoaderDialog(shell);

		Manager.TOOL_MAP.put(Tools.POINTER, new ManipulationTool(this));
		Manager.TOOL_MAP.put(Tools.CREATOR, new CreationTool(this));
		Manager.TOOL_MAP.put(Tools.GIB, new GibTool(this));
		Manager.TOOL_MAP.put(Tools.CONFIG, new PropertyTool(this));
		Manager.TOOL_MAP.put(Tools.ROOM, new RoomTool(this));
		Manager.TOOL_MAP.put(Tools.DOOR, new DoorTool(this));
		Manager.TOOL_MAP.put(Tools.WEAPON, new MountTool(this));
		Manager.TOOL_MAP.put(Tools.STATION, new StationTool(this));

		// Menu bar
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		// File menu
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		final MenuItem mntmNewShip = new MenuItem(menu_1, SWT.NONE);
		mntmNewShip.setText("New Ship\t" + Manager.getHotkey(Hotkeys.NEW_SHIP));

		final MenuItem mntmLoadShip = new MenuItem(menu_1, SWT.NONE);
		mntmLoadShip.setText("Load Ship\t" + Manager.getHotkey(Hotkeys.LOAD_SHIP));

		// Edit menu
		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("Edit");

		Menu menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);

		mntmUndo = new MenuItem(menu_2, SWT.NONE);
		mntmUndo.setText("Undo\t" + Manager.getHotkey(Hotkeys.UNDO));

		mntmRedo = new MenuItem(menu_2, SWT.NONE);
		mntmRedo.setText("Redo\t" + Manager.getHotkey(Hotkeys.REDO));

		new MenuItem(menu_2, SWT.SEPARATOR);

		mntmDelete = new MenuItem(menu_2, SWT.NONE);
		mntmDelete.setText("Delete\t" + Manager.getHotkey(Hotkeys.DELETE));

		// View menu
		MenuItem mntmView = new MenuItem(menu, SWT.CASCADE);
		mntmView.setText("View");

		Menu menu_3 = new Menu(mntmView);
		mntmView.setMenu(menu_3);

		final MenuItem mntmSidebar = new MenuItem(menu_3, SWT.CHECK);
		mntmSidebar.setSelection(Superluminal.sidebarOnRightSide);
		mntmSidebar.setText("Sidebar on Right Side");

		new MenuItem(menu_3, SWT.SEPARATOR);

		final MenuItem mntmGrid = new MenuItem(menu_3, SWT.CHECK);
		mntmGrid.setText("Show Grid\t" + Manager.getHotkey(Hotkeys.TOGGLE_GRID));
		mntmGrid.setSelection(true);

		// Help menu
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu menu_4 = new Menu(mntmView);
		mntmHelp.setMenu(menu_4);

		MenuItem mntmAbout = new MenuItem(menu_4, SWT.NONE);
		mntmAbout.setText("About");

		// Main container - contains everything else
		Composite mainContainer = new Composite(shell, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// Tools container - tool bar, tools
		final Composite toolContainer = new Composite(mainContainer, SWT.NONE);
		toolContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gl_toolContainer = new GridLayout(2, false);
		gl_toolContainer.marginHeight = 0;
		gl_toolContainer.marginWidth = 0;
		toolContainer.setLayout(gl_toolContainer);

		// Tool bar widget
		ToolBar toolBar = new ToolBar(toolContainer, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		SelectionAdapter toolSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Manager.selectTool((Tools) ((ToolItem) e.getSource()).getData());
			}
		};

		// Pointer tool
		tltmPointer = new ToolItem(toolBar, SWT.RADIO);
		tltmPointer.setImage(Cache.checkOutImage(this, "cpath:/assets/pointer.png"));
		tltmPointer.addSelectionListener(toolSelectionAdapter);
		tltmPointer.setData(Tools.POINTER);
		tltmPointer.setToolTipText("Manipulation Tool (" + Manager.getHotkey(Hotkeys.POINTER_TOOL) + ")");
		toolItemMap.put(Tools.POINTER, tltmPointer);

		// Room tool
		tltmCreation = new ToolItem(toolBar, SWT.RADIO);
		tltmCreation.setImage(Cache.checkOutImage(this, "cpath:/assets/wrench.png"));
		tltmCreation.addSelectionListener(toolSelectionAdapter);
		tltmCreation.setData(Tools.CREATOR);
		tltmCreation.setToolTipText("Creation Tool (" + Manager.getHotkey(Hotkeys.CREATE_TOOL) + ")");
		toolItemMap.put(Tools.CREATOR, tltmCreation);

		// Gib tool
		tltmGib = new ToolItem(toolBar, SWT.RADIO);
		tltmGib.setImage(Cache.checkOutImage(this, "cpath:/assets/gib.png"));
		tltmGib.addSelectionListener(toolSelectionAdapter);
		tltmGib.setData(Tools.GIB);
		tltmGib.setToolTipText("Gib Tool (" + Manager.getHotkey(Hotkeys.GIB_TOOL) + ")");
		toolItemMap.put(Tools.GIB, tltmGib);

		// Properties button
		tltmProperties = new ToolItem(toolBar, SWT.RADIO);
		tltmProperties.setImage(Cache.checkOutImage(this, "cpath:/assets/system.png"));
		tltmProperties.addSelectionListener(toolSelectionAdapter);
		tltmProperties.setData(Tools.CONFIG);
		tltmProperties.setToolTipText("Properties (" + Manager.getHotkey(Hotkeys.PROPERTIES_TOOL) + ")");
		toolItemMap.put(Tools.CONFIG, tltmProperties);

		new ToolItem(toolBar, SWT.SEPARATOR);

		// Manager button
		tltmManager = new ToolItem(toolBar, SWT.PUSH);
		tltmManager.setImage(Cache.checkOutImage(this, "cpath:/assets/overview.png"));
		tltmManager.setToolTipText("Overview (" + Manager.getHotkey(Hotkeys.OVERVIEW_TOOL) + ")");
		tltmManager.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				OverviewWindow.getInstance().open();
			}
		});

		// Info container - mouse position, etc
		Composite infoContainer = new Composite(toolContainer, SWT.NONE);
		infoContainer.setLayout(new GridLayout(1, false));
		infoContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		// Editor container - canvas, sidebar
		final SashForm editorContainer = new SashForm(mainContainer, SWT.SMOOTH);
		editorContainer.setLayout(new FormLayout());
		editorContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		canvasColor = Cache.checkOutColor(this, canvasRGB);
		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(canvasColor);
		canvas.addPaintListener(painter);

		sideContainer = new ScrolledComposite(shell, SWT.BORDER | SWT.V_SCROLL);
		sideContainer.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		sideContainer.setAlwaysShowScrollBars(true);
		sideContainer.setExpandHorizontal(true);
		sideContainer.setExpandVertical(true);
		sideContainer.getVerticalBar().setIncrement(15);

		if (Superluminal.sidebarOnRightSide) {
			canvas.setParent(editorContainer);
			sideContainer.setParent(editorContainer);
			editorContainer.setWeights(new int[] { displaySize.width - SIDEBAR_MIN_WIDTH, SIDEBAR_MIN_WIDTH });
		} else {
			sideContainer.setParent(editorContainer);
			canvas.setParent(editorContainer);
			editorContainer.setWeights(new int[] { SIDEBAR_MIN_WIDTH, displaySize.width - SIDEBAR_MIN_WIDTH });
		}

		shell.getDisplay().addFilter(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// display filter is notified every time a key is pressed, regardless of focus
				// only proceed if the main window has focus
				if (!isFocusControl())
					return;

				// update modifier states for use in other places in the application
				if (e.keyCode == SWT.SHIFT || e.stateMask == SWT.SHIFT)
					Manager.modShift = true;
				if (e.keyCode == SWT.ALT || e.stateMask == SWT.ALT)
					Manager.modAlt = true;
				if (e.keyCode == SWT.CTRL || e.stateMask == SWT.CTRL)
					Manager.modCtrl = true;

				// Handle hotkeys

				// ====== Menu hotkeys
				// File
				if (Manager.getHotkey(Hotkeys.NEW_SHIP).passes(e.keyCode)) {
					mntmNewShip.notifyListeners(SWT.Selection, null);
				} else if (Manager.getHotkey(Hotkeys.LOAD_SHIP).passes(e.keyCode)) {
					mntmLoadShip.notifyListeners(SWT.Selection, null);
				}

				// Edit
				else if (Manager.getHotkey(Hotkeys.UNDO).passes(e.keyCode)) {
					// undo
					if (Manager.DELETED_LIST.size() > 0)
						Manager.DELETED_LIST.removeLast().restore();
				} else if (Manager.getHotkey(Hotkeys.REDO).passes(e.keyCode)) {
					// TODO
				}

				// View
				else if (Manager.getHotkey(Hotkeys.TOGGLE_GRID).passes(e.keyCode)) {
					mntmGrid.setSelection(!mntmGrid.getSelection());
					mntmGrid.notifyListeners(SWT.Selection, null);
				}

				// ====== Tool hotkeys
				if (toolsEnabled()) {
					if (Manager.getHotkey(Hotkeys.POINTER_TOOL).passes(e.keyCode)) {
						Manager.selectTool(Tools.POINTER);
						e.doit = false;
					} else if (Manager.getHotkey(Hotkeys.CREATE_TOOL).passes(e.keyCode)) {
						Manager.selectTool(Tools.CREATOR);
						e.doit = false;
					} else if (Manager.getHotkey(Hotkeys.GIB_TOOL).passes(e.keyCode)) {
						Manager.selectTool(Tools.GIB);
						e.doit = false;
					} else if (Manager.getHotkey(Hotkeys.PROPERTIES_TOOL).passes(e.keyCode)) {
						Manager.selectTool(Tools.CONFIG);
						e.doit = false;
					} else if (Manager.getHotkey(Hotkeys.OVERVIEW_TOOL).passes(e.keyCode)) {
						OverviewWindow.getInstance().open();
					} else if (Manager.getHotkey(Hotkeys.ROOM_TOOL).passes(e.keyCode)) {
						if (Manager.getSelectedToolId() != Tools.CREATOR)
							Manager.selectTool(Tools.CREATOR);
						((CreationToolComposite) instance.getSidebarContent()).selectTool(Tools.ROOM);
					} else if (Manager.getHotkey(Hotkeys.DOOR_TOOL).passes(e.keyCode)) {
						if (Manager.getSelectedToolId() != Tools.CREATOR)
							Manager.selectTool(Tools.CREATOR);
						((CreationToolComposite) instance.getSidebarContent()).selectTool(Tools.DOOR);
					} else if (Manager.getHotkey(Hotkeys.MOUNT_TOOL).passes(e.keyCode)) {
						if (Manager.getSelectedToolId() != Tools.CREATOR)
							Manager.selectTool(Tools.CREATOR);
						((CreationToolComposite) instance.getSidebarContent()).selectTool(Tools.WEAPON);
					} else if (Manager.getHotkey(Hotkeys.STATION_TOOL).passes(e.keyCode)) {
						if (Manager.getSelectedToolId() != Tools.CREATOR)
							Manager.selectTool(Tools.CREATOR);
						((CreationToolComposite) instance.getSidebarContent()).selectTool(Tools.STATION);
					}
				}

				// ====== Tool-specific hotkeys

				if (Manager.getSelectedToolId() == Tools.POINTER) {
					// Arrow keys movement
					if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_RIGHT ||
							e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT) {
						AbstractController selected = Manager.getSelected();
						if (selected != null && !selected.isPinned() && selected.isLocModifiable()) {
							ManipulationToolComposite mtc = (ManipulationToolComposite) getSidebarContent();

							Point p = selected.getPresentedLocation();
							Rectangle oldBounds = null;

							oldBounds = selected.getBounds();

							int nudgeAmount = Manager.modShift && selected.getPresentedFactor() == 1 ? ShipContainer.CELL_SIZE : 1;

							selected.setPresentedLocation(p.x + (e.keyCode == SWT.ARROW_RIGHT ? nudgeAmount : e.keyCode == SWT.ARROW_LEFT ? -nudgeAmount : 0),
									p.y + (e.keyCode == SWT.ARROW_DOWN ? nudgeAmount : e.keyCode == SWT.ARROW_UP ? -nudgeAmount : 0));
							selected.updateFollowOffset();
							Manager.getCurrentShip().updateBoundingArea();
							selected.updateView();

							selected.redraw();
							canvasRedraw(oldBounds);

							mtc.updateData();

							e.doit = false;
						}
					} else if (Manager.getHotkey(Hotkeys.DELETE).passes(e.keyCode) || e.keyCode == SWT.DEL) {
						// Deletion
						AbstractController selected = Manager.getSelected();
						if (selected != null) {
							try {
								Manager.getCurrentShip().delete(selected);
								selected.redraw();

								Manager.setSelected(null);
							} catch (NotDeletableException ex) {
								log.trace("Selected object is not deletable: " + selected.getClass().getSimpleName());
							}
						}
					} else if (Manager.getHotkey(Hotkeys.PIN).passes(e.keyCode)) {
						// Pin
						AbstractController selected = Manager.getSelected();
						if (selected != null) {
							selected.setPinned(!selected.isPinned());
							((ManipulationToolComposite) getSidebarContent()).updateData();
							e.doit = false;
						}
					}
				}
			}
		});

		shell.getDisplay().addFilter(SWT.KeyUp, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.keyCode == SWT.SHIFT || e.stateMask == SWT.SHIFT)
					Manager.modShift = false;
				if (e.keyCode == SWT.ALT || e.stateMask == SWT.ALT)
					Manager.modAlt = false;
				if (e.keyCode == SWT.CTRL || e.stateMask == SWT.CTRL)
					Manager.modCtrl = false;

				if (e.keyCode == SWT.SPACE) {
					if (Manager.getSelected() != null)
						e.doit = false;
				}
			}
		});

		shell.getDisplay().addFilter(SWT.MouseEnter, new Listener() {
			@Override
			public void handleEvent(Event e) {
				shellResizing = false;
			}
		});

		sideContainer.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				Grid.getInstance().updateBounds(canvas.getSize().x, canvas.getSize().y);

				ShipContainer ship = Manager.getCurrentShip();
				if (ship != null) {
					ship.updateBoundingArea();
					ship.updateChildBoundingAreas();
					ship.getShipController().setVisible(true); // to update the view
					ship.getShipController().redraw();
				}

				if (!shellResizing)
					sidebarWidth = Math.max(sideContainer.getSize().x, SIDEBAR_MIN_WIDTH);
			}
		});

		shell.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
			}

			@Override
			public void controlResized(ControlEvent e) {
				shellResizing = true;

				Grid.getInstance().updateBounds(canvas.getSize().x, canvas.getSize().y);

				ShipContainer ship = Manager.getCurrentShip();
				if (ship != null) {
					ship.updateBoundingArea();
					ship.updateChildBoundingAreas();
					ship.getShipController().setVisible(true); // to update the view
					ship.getShipController().redraw();
				}

				int width = shell.getClientArea().width;
				int[] weights = editorContainer.getWeights();

				if (width >= sidebarWidth + CANVAS_MIN_SIZE) {
					weights[Superluminal.sidebarOnRightSide ? 1 : 0] = 1000000 * sidebarWidth / width;
					weights[Superluminal.sidebarOnRightSide ? 0 : 1] = 1000000 - weights[Superluminal.sidebarOnRightSide ? 1 : 0];
				} else {
					weights[Superluminal.sidebarOnRightSide ? 1 : 0] = 1000000 * sidebarWidth / (sidebarWidth + CANVAS_MIN_SIZE);
					weights[Superluminal.sidebarOnRightSide ? 0 : 1] = 1000000 * CANVAS_MIN_SIZE / (sidebarWidth + CANVAS_MIN_SIZE);
				}

				editorContainer.setWeights(weights);
			}
		});

		mntmNewShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Manager.createNewShip();
			}
		});

		mntmLoadShip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipLoaderDialog.getInstance().open();
			}
		});

		mntmSidebar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Superluminal.sidebarOnRightSide = mntmSidebar.getSelection();

				sideContainer.setParent(shell);
				canvas.setParent(shell);
				if (Superluminal.sidebarOnRightSide) {
					canvas.setParent(editorContainer);
					sideContainer.setParent(editorContainer);
				} else {
					sideContainer.setParent(editorContainer);
					canvas.setParent(editorContainer);
				}

				Control[] changed = { sideContainer, editorContainer, canvas };
				shell.layout(changed);
			}
		});

		// TODO mntmUndo
		// TODO mntmRedo
		// TODO mntmDelete
		mntmGrid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Grid.getInstance().setVisible(mntmGrid.getSelection());
			}
		});

		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuilder buf = new StringBuilder();
				buf.append(Superluminal.APP_NAME + " - a ship editor for FTL: Faster Than Light");
				buf.append("\nVersion " + Superluminal.APP_VERSION);
				buf.append("\n\nCreated by " + Superluminal.APP_AUTHOR);
				buf.append("\n ");

				AboutDialog aboutDialog = new AboutDialog(shell);
				aboutDialog.setMessage(buf.toString());
				try {
					aboutDialog.setLink(new URL(Superluminal.APP_URL), "Editor's thread at the official FTL forums");
				} catch (MalformedURLException ex) {
				}

				aboutDialog.open();
			}
		});

		sideContainer.setFocus();

		shell.setMinimumSize(SIDEBAR_MIN_WIDTH + CANVAS_MIN_SIZE, CANVAS_MIN_SIZE + toolContainer.getSize().y * 2);

		canvas.addMouseListener(mouseListener);
		canvas.addMouseMoveListener(mouseListener);
		canvas.addMouseTrackListener(mouseListener);

		Grid.getInstance().updateBounds(canvas.getSize().x, canvas.getSize().y);

		enableTools(false);
	}

	public static EditorWindow getInstance() {
		return instance;
	}

	public void open() {
		shell.open();
	}

	public LayeredPainter getPainter() {
		return painter;
	}

	/**
	 * Sets the control passed in argument as the content of the sidebar -- this is
	 * what the scrolled area will display, and will scale the scroll against.
	 */
	public void setSidebarContent(Composite c) {
		sideContainer.setContent(c);
		c.pack();
		int height = c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		sideContainer.setMinHeight(height);
	}

	/**
	 * Updates the minimum area of the sidebar, so that the scrollbar will appear when needed.
	 */
	public void updateSidebarScroll() {
		Control c = sideContainer.getContent();
		if (c != null && !c.isDisposed()) {
			int height = c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			sideContainer.setMinHeight(height);
		}
	}

	/** @return the Composite currently held by the sidebar. */
	public SidebarComposite getSidebarContent() {
		return (SidebarComposite) sideContainer.getContent();
	}

	/** @return the sidebar ScrolledComposite itself. */
	public Composite getSidebarWidget() {
		return sideContainer;
	}

	public Shell getShell() {
		return shell;
	}

	/**
	 * Checks whether the point is inside the canvas area -- eg. points non-negative coordinates
	 * with values lesser than or equal to the canvas' dimensions (width and height)
	 * 
	 * @return true if the point (relative to the canvas) is within the canvas bounds, false otherwise
	 */
	public boolean canvasContains(int x, int y) {
		Rectangle bounds = canvas.getBounds();
		bounds.x = 0;
		bounds.y = 0;
		return bounds.contains(x, y);
	}

	/** Redraws the entire canvas area. */
	public void canvasRedraw() {
		canvas.redraw();
	}

	/** Redraws the part of canvas covered by the rectangle. */
	public void canvasRedraw(Rectangle rect) {
		canvas.redraw(rect.x, rect.y, rect.width, rect.height, false);
	}

	/** Redraws the part of canvas covered by the rectangle. */
	public void canvasRedraw(int x, int y, int w, int h) {
		canvas.redraw(x, y, w, h, false);
	}

	/** Only to be used to programmatically select the tool, when the user doesn't directly click on the tool's icon. */
	public void selectTool(Tools tool) {
		if (!toolsEnabled())
			return;

		for (ToolItem it : toolItemMap.values()) {
			if (toolItemMap.containsKey(tool))
				it.setSelection(tool == (Tools) it.getData());
		}
	}

	public void enableTools(boolean enable) {
		tltmPointer.setEnabled(enable);
		tltmCreation.setEnabled(enable);
		tltmGib.setEnabled(enable);
		tltmProperties.setEnabled(enable);
		tltmManager.setEnabled(enable);

		sideContainer.getVerticalBar().setEnabled(enable);
	}

	public boolean toolsEnabled() {
		return tltmPointer.isEnabled();
	}

	/**
	 * @return true if the editor window controls the focus and should execute hotkey actions.
	 */
	public boolean isFocusControl() {
		boolean result = !OverviewWindow.getInstance().isVisible() || !OverviewWindow.getInstance().isFocusControl();
		result &= AboutDialog.getInstance() == null || !AboutDialog.getInstance().isVisible();
		result &= AliasDialog.getInstance() == null || !AliasDialog.getInstance().isVisible();
		Composite c = (Composite) getSidebarContent();
		if (c != null && !c.isDisposed())
			result &= !c.isFocusControl();

		return result;
	}

	public boolean forceFocus() {
		return canvas.forceFocus();
	}

	public void dispose() {
		Cache.checkInColor(this, canvasRGB);
		shell.dispose();
	}
}
