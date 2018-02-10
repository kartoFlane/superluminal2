package com.kartoflane.superluminal2.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kartoflane.ftl.floorgen.FloorImageFactory;
import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.EventHandler;
import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.Tuple;
import com.kartoflane.superluminal2.components.enums.Hotkeys;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.Modifiers;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.LayeredPainter;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.MouseInputDispatcher;
import com.kartoflane.superluminal2.db.AbstractDatabaseEntry;
import com.kartoflane.superluminal2.db.Database;
import com.kartoflane.superluminal2.db.ModDatabaseEntry;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.events.SLModAltEvent;
import com.kartoflane.superluminal2.events.SLModCommandEvent;
import com.kartoflane.superluminal2.events.SLModControlEvent;
import com.kartoflane.superluminal2.events.SLModShiftEvent;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CursorController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.tools.CreationTool;
import com.kartoflane.superluminal2.tools.DoorTool;
import com.kartoflane.superluminal2.tools.ImagesTool;
import com.kartoflane.superluminal2.tools.ManipulationTool;
import com.kartoflane.superluminal2.tools.MountTool;
import com.kartoflane.superluminal2.tools.PropertyTool;
import com.kartoflane.superluminal2.tools.RoomTool;
import com.kartoflane.superluminal2.tools.StationTool;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.GibPropContainer.PropControls;
import com.kartoflane.superluminal2.ui.SaveOptionsDialog.SaveOptions;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.undo.UndoableDeleteEdit;
import com.kartoflane.superluminal2.undo.UndoableOffsetsEdit;
import com.kartoflane.superluminal2.undo.UndoablePropertyEdit;
import com.kartoflane.superluminal2.utils.SHPUtils;
import com.kartoflane.superluminal2.utils.SWTFontUtils;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;


@SuppressWarnings("serial")
public class EditorWindow
{
	static final Logger log = LogManager.getLogger( EditorWindow.class );

	public static final int SIDEBAR_MIN_WIDTH = 290;
	public static final int CANVAS_MIN_SIZE = 400;

	private static final RGB canvasRGB = new RGB( 164, 164, 164 );

	private static EditorWindow instance;

	private final HashMap<Tools, ToolItem> toolItemMap = new HashMap<Tools, ToolItem>();

	private int sidebarWidth = SIDEBAR_MIN_WIDTH;
	private Color canvasColor = null;
	private boolean shellResizing = false;
	private static String prevShpPath = null;

	private EventHandler eventHandler = new EventHandler();

	// UI widgets' variables
	private Shell shell;
	private ScrolledComposite sideContainer;
	private SashForm editorContainer;
	private Canvas canvas;

	private DropTarget dropTarget;
	private Label lblCursorLoc;

	private ToolItem tltmPointer;
	private ToolItem tltmCreation;
	private ToolItem tltmProperties;
	private ToolItem tltmImages;
	private ToolItem tltmManager;
	private ToolItem tltmCloak;
	private ToolItem tltmAnimate;

	private MenuItem mntmNewShip;
	private MenuItem mntmLoadShip;
	private MenuItem mntmConvertShp;
	private MenuItem mntmSaveShip;
	private MenuItem mntmSaveShipAs;
	private MenuItem mntmModMan;
	private MenuItem mntmReloadDb;
	private MenuItem mntmChangeArchives;
	private MenuItem mntmCloseShip;

	private MenuItem mntmUndo;
	private MenuItem mntmRedo;
	private MenuItem mntmResetLinks;
	private MenuItem mntmOptimalOffset;
	private MenuItem mntmGenerateFloor;
	private MenuItem mntmDelete;
	private MenuItem mntmSettings;

	private MenuItem mntmZoom;
	private MenuItem mntmGrid;
	private MenuItem mntmHangar;
	private MenuItem mntmShowAnchor;
	private MenuItem mntmShowMounts;
	private MenuItem mntmShowRooms;
	private MenuItem mntmShowDoors;
	private MenuItem mntmShowStations;
	private MenuItem mntmShowHull;
	private MenuItem mntmShowFloor;
	private MenuItem mntmShowShield;
	private MenuItem mntmShowGibs;


	public EditorWindow( Display display )
	{
		instance = this;

		shell = new Shell( display, SWT.SHELL_TRIM | SWT.SMOOTH );
		shell.setText( String.format( "%s v%s - FTL Ship Editor", Superluminal.APP_NAME, Superluminal.APP_VERSION ) );
		GridLayout gl_shell = new GridLayout( 1, false );
		gl_shell.marginHeight = 0;
		gl_shell.marginWidth = 0;
		shell.setLayout( gl_shell );

		Monitor m = display.getPrimaryMonitor();
		Rectangle displaySize = m.getClientArea();
		displaySize.width = ( displaySize.width / 5 ) * 4;
		displaySize.height = ( displaySize.height / 5 ) * 4;

		// Load icons
		Image icon16 = Cache.checkOutImage( shell, "cpath:/assets/icons/Superluminal_2_16.png" );
		Image icon24 = Cache.checkOutImage( shell, "cpath:/assets/icons/Superluminal_2_24.png" );
		Image icon32 = Cache.checkOutImage( shell, "cpath:/assets/icons/Superluminal_2_32.png" );
		Image icon48 = Cache.checkOutImage( shell, "cpath:/assets/icons/Superluminal_2_48.png" );
		Image icon64 = Cache.checkOutImage( shell, "cpath:/assets/icons/Superluminal_2_64.png" );
		Image icon128 = Cache.checkOutImage( shell, "cpath:/assets/icons/Superluminal_2_128.png" );
		Image[] icons = new Image[] { icon16, icon24, icon32, icon48, icon64, icon128 };
		shell.setImages( icons );

		// Instantiate quasi-singletons
		new MouseInputDispatcher();
		CursorController.newInstance();

		Manager.putTool( Tools.POINTER, new ManipulationTool( this ) );
		Manager.putTool( Tools.CREATOR, new CreationTool( this ) );
		Manager.putTool( Tools.IMAGES, new ImagesTool( this ) );
		Manager.putTool( Tools.CONFIG, new PropertyTool( this ) );
		Manager.putTool( Tools.ROOM, new RoomTool( this ) );
		Manager.putTool( Tools.DOOR, new DoorTool( this ) );
		Manager.putTool( Tools.WEAPON, new MountTool( this ) );
		Manager.putTool( Tools.STATION, new StationTool( this ) );

		// Menu bar
		Menu menu = new Menu( shell, SWT.BAR );
		shell.setMenuBar( menu );

		// File menu
		MenuItem mntmFile = new MenuItem( menu, SWT.CASCADE );
		mntmFile.setText( "File" );

		Menu menuFile = new Menu( mntmFile );
		mntmFile.setMenu( menuFile );

		mntmNewShip = new MenuItem( menuFile, SWT.NONE );

		mntmLoadShip = new MenuItem( menuFile, SWT.NONE );

		new MenuItem( menuFile, SWT.SEPARATOR );

		mntmConvertShp = new MenuItem( menuFile, SWT.NONE );
		mntmConvertShp.setText( "Open .shp (Legacy)" );

		new MenuItem( menuFile, SWT.SEPARATOR );

		mntmSaveShip = new MenuItem( menuFile, SWT.NONE );

		mntmSaveShipAs = new MenuItem( menuFile, SWT.NONE );
		mntmSaveShipAs.setText( "Save Ship As..." );

		new MenuItem( menuFile, SWT.SEPARATOR );

		mntmModMan = new MenuItem( menuFile, SWT.NONE );

		mntmReloadDb = new MenuItem( menuFile, SWT.NONE );
		mntmReloadDb.setText( "Reload Database" );

		mntmChangeArchives = new MenuItem( menuFile, SWT.NONE );
		mntmChangeArchives.setText( "Change .dat Files" );

		new MenuItem( menuFile, SWT.SEPARATOR );

		mntmCloseShip = new MenuItem( menuFile, SWT.NONE );

		// Edit menu
		MenuItem mntmEdit = new MenuItem( menu, SWT.CASCADE );
		mntmEdit.setText( "Edit" );

		Menu menuEdit = new Menu( mntmEdit );
		mntmEdit.setMenu( menuEdit );

		mntmUndo = new MenuItem( menuEdit, SWT.NONE );

		mntmRedo = new MenuItem( menuEdit, SWT.NONE );

		new MenuItem( menuEdit, SWT.SEPARATOR );

		mntmResetLinks = new MenuItem( menuEdit, SWT.NONE );
		mntmResetLinks.setText( "Reset All Door Links" );

		mntmOptimalOffset = new MenuItem( menuEdit, SWT.NONE );
		mntmOptimalOffset.setText( "Calculate Optimal Offset" );

		mntmGenerateFloor = new MenuItem( menuEdit, SWT.NONE );
		mntmGenerateFloor.setText( "Generate Floor Image" );

		new MenuItem( menuEdit, SWT.SEPARATOR );

		mntmDelete = new MenuItem( menuEdit, SWT.NONE );

		new MenuItem( menuEdit, SWT.SEPARATOR );

		mntmSettings = new MenuItem( menuEdit, SWT.NONE );

		// View menu
		MenuItem mntmView = new MenuItem( menu, SWT.CASCADE );
		mntmView.setText( "View" );

		Menu menuView = new Menu( mntmView );
		mntmView.setMenu( menuView );

		mntmZoom = new MenuItem( menuView, SWT.NONE );

		new MenuItem( menuView, SWT.SEPARATOR );

		mntmGrid = new MenuItem( menuView, SWT.CHECK );
		mntmGrid.setSelection( true );

		mntmHangar = new MenuItem( menuView, SWT.CHECK );

		new MenuItem( menuView, SWT.SEPARATOR );

		MenuItem mntmShipComponents = new MenuItem( menuView, SWT.CASCADE );
		mntmShipComponents.setText( "Ship Components" );

		Menu menuViewShip = new Menu( mntmShipComponents );
		mntmShipComponents.setMenu( menuViewShip );

		mntmShowAnchor = new MenuItem( menuViewShip, SWT.CHECK );
		mntmShowAnchor.setSelection( true );

		mntmShowMounts = new MenuItem( menuViewShip, SWT.CHECK );
		mntmShowMounts.setSelection( true );

		mntmShowRooms = new MenuItem( menuViewShip, SWT.CHECK );
		mntmShowRooms.setSelection( true );

		mntmShowDoors = new MenuItem( menuViewShip, SWT.CHECK );
		mntmShowDoors.setSelection( true );

		mntmShowStations = new MenuItem( menuViewShip, SWT.CHECK );
		mntmShowStations.setSelection( true );

		MenuItem mntmShipImages = new MenuItem( menuView, SWT.CASCADE );
		mntmShipImages.setText( "Ship Images" );

		Menu menuViewImages = new Menu( mntmShipImages );
		mntmShipImages.setMenu( menuViewImages );

		mntmShowHull = new MenuItem( menuViewImages, SWT.CHECK );
		mntmShowHull.setSelection( true );

		mntmShowFloor = new MenuItem( menuViewImages, SWT.CHECK );
		mntmShowFloor.setSelection( true );

		mntmShowShield = new MenuItem( menuViewImages, SWT.CHECK );
		mntmShowShield.setSelection( true );

		mntmShowGibs = new MenuItem( menuViewImages, SWT.CHECK );
		mntmShowGibs.setSelection( true );

		// Help menu
		MenuItem mntmHelp = new MenuItem( menu, SWT.CASCADE );
		mntmHelp.setText( "Help" );

		Menu menuHelp = new Menu( mntmView );
		mntmHelp.setMenu( menuHelp );

		MenuItem mntmUpdate = new MenuItem( menuHelp, SWT.NONE );
		mntmUpdate.setText( "Check for Updates" );

		MenuItem mntmAbout = new MenuItem( menuHelp, SWT.NONE );
		mntmAbout.setText( "About" );

		// Main container - contains everything else
		Composite mainContainer = new Composite( shell, SWT.NONE );
		mainContainer.setLayout( new GridLayout( 1, false ) );
		mainContainer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

		// Tools container - tool bar, tools
		final Composite toolContainer = new Composite( mainContainer, SWT.NONE );
		toolContainer.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );
		GridLayout gl_toolContainer = new GridLayout( 2, false );
		gl_toolContainer.marginHeight = 0;
		gl_toolContainer.marginWidth = 0;
		toolContainer.setLayout( gl_toolContainer );

		// Tool bar widget
		ToolBar toolBar = new ToolBar( toolContainer, SWT.FLAT | SWT.RIGHT );
		toolBar.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false, 1, 1 ) );

		SelectionAdapter toolSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e )
			{
				Manager.selectTool( (Tools)( (ToolItem)e.getSource() ).getData() );
			}
		};

		// Pointer tool
		tltmPointer = new ToolItem( toolBar, SWT.RADIO );
		tltmPointer.setImage( Cache.checkOutImage( this, "cpath:/assets/pointer.png" ) );
		tltmPointer.addSelectionListener( toolSelectionAdapter );
		tltmPointer.setData( Tools.POINTER );
		toolItemMap.put( Tools.POINTER, tltmPointer );

		// Room tool
		tltmCreation = new ToolItem( toolBar, SWT.RADIO );
		tltmCreation.setImage( Cache.checkOutImage( this, "cpath:/assets/wrench.png" ) );
		tltmCreation.addSelectionListener( toolSelectionAdapter );
		tltmCreation.setData( Tools.CREATOR );
		toolItemMap.put( Tools.CREATOR, tltmCreation );

		// Images button
		tltmImages = new ToolItem( toolBar, SWT.RADIO );
		tltmImages.setImage( Cache.checkOutImage( this, "cpath:/assets/images.png" ) );
		tltmImages.addSelectionListener( toolSelectionAdapter );
		tltmImages.setData( Tools.IMAGES );
		toolItemMap.put( Tools.IMAGES, tltmImages );

		// Properties button
		tltmProperties = new ToolItem( toolBar, SWT.RADIO );
		tltmProperties.setImage( Cache.checkOutImage( this, "cpath:/assets/system.png" ) );
		tltmProperties.addSelectionListener( toolSelectionAdapter );
		tltmProperties.setData( Tools.CONFIG );
		toolItemMap.put( Tools.CONFIG, tltmProperties );

		new ToolItem( toolBar, SWT.SEPARATOR );

		// Manager button
		tltmManager = new ToolItem( toolBar, SWT.PUSH );
		tltmManager.setImage( Cache.checkOutImage( this, "cpath:/assets/overview.png" ) );
		tltmManager.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					OverviewWindow window = OverviewWindow.getInstance();
					if ( window.isDisposed() )
						window.init( shell );
					window.open();
				}
			}
		);

		tltmCloak = new ToolItem( toolBar, SWT.CHECK );
		tltmCloak.setImage( Cache.checkOutImage( this, "cpath:/assets/cloak.png" ) );
		tltmCloak.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipContainer container = Manager.getCurrentShip();
					container.setCloakedAppearance( tltmCloak.getSelection() );
					if ( !tltmCloak.getSelection() && Manager.getSelected() == container.getImageController( Images.CLOAK ) )
						Manager.setSelected( null );
				}
			}
		);

		tltmAnimate = new ToolItem( toolBar, SWT.PUSH );
		updateGibAnimationButton( true );
		tltmAnimate.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipContainer container = Manager.getCurrentShip();
					if ( container.isGibAnimationInProgress() ) {
						container.stopGibAnimation();
					}
					else {
						container.triggerGibAnimation();
					}
				}
			}
		);

		// Info container - mouse position
		Composite infoContainer = new Composite( toolContainer, SWT.NONE );
		infoContainer.setLayout( new GridLayout( 1, false ) );
		infoContainer.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, true, 1, 1 ) );

		lblCursorLoc = new Label( infoContainer, SWT.NONE );
		lblCursorLoc.setAlignment( SWT.RIGHT );
		lblCursorLoc.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		lblCursorLoc.setFont( SWTFontUtils.getMonospacedFont( display ) );

		// Editor container - canvas, sidebar
		editorContainer = new SashForm( mainContainer, SWT.SMOOTH );
		editorContainer.setSashWidth( 7 );
		editorContainer.setLayout( new FormLayout() );
		editorContainer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );

		canvasColor = Cache.checkOutColor( this, canvasRGB );
		canvas = new Canvas( shell, SWT.DOUBLE_BUFFERED );
		canvas.setBackground( canvasColor );
		canvas.addPaintListener( LayeredPainter.getInstance() );

		sideContainer = new ScrolledComposite( shell, SWT.BORDER | SWT.V_SCROLL );
		sideContainer.setLayoutData( new GridData( SWT.LEFT, SWT.TOP, false, false, 1, 1 ) );
		sideContainer.setAlwaysShowScrollBars( true );
		sideContainer.setExpandHorizontal( true );
		sideContainer.setExpandVertical( true );
		sideContainer.getVerticalBar().setIncrement( 15 );

		if ( Manager.sidebarOnRightSide ) {
			canvas.setParent( editorContainer );
			sideContainer.setParent( editorContainer );
			editorContainer.setWeights( new int[] { displaySize.width - SIDEBAR_MIN_WIDTH, SIDEBAR_MIN_WIDTH } );
		}
		else {
			sideContainer.setParent( editorContainer );
			canvas.setParent( editorContainer );
			editorContainer.setWeights( new int[] { SIDEBAR_MIN_WIDTH, displaySize.width - SIDEBAR_MIN_WIDTH } );
		}

		display.addFilter(
			SWT.KeyUp, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					if ( ( e.keyCode == SWT.SPACE || e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_RIGHT ||
						e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT ) && Manager.getSelected() != null )
						e.doit = false;
				}
			}
		);

		display.addFilter(
			SWT.MouseEnter, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					shellResizing = false;
				}
			}
		);

		display.addFilter(
			SWT.FocusIn, new Listener() {
				public void handleEvent( Event e )
				{
					Control focus = UIUtils.getDisplay().getFocusControl();
					if ( focus == shell || focus == canvas || focus == sideContainer ) {
						Database db = Database.getInstance();
						if ( db != null && db.getCore() != null && !db.verify() ) {
							log.trace( "Database failed to pass verification. Reload is required." );
							mntmReloadDb.notifyListeners( SWT.Selection, null );
						}
					}
				}
			}
		);

		sideContainer.addListener(
			SWT.Resize, new Listener() {
				public void handleEvent( Event e )
				{
					Grid.getInstance().updateBounds( canvas.getSize().x, canvas.getSize().y );

					ShipContainer ship = Manager.getCurrentShip();
					if ( ship != null ) {
						ship.updateBoundingArea();
						ship.updateChildBoundingAreas();
						ship.getShipController().updateView();
						ship.getShipController().updateProps();
						ship.getShipController().redraw();
					}

					if ( !shellResizing )
						sidebarWidth = Math.max( sideContainer.getSize().x, SIDEBAR_MIN_WIDTH );
				}
			}
		);

		shell.addListener(
			SWT.Resize, new Listener() {
				public void handleEvent( Event e )
				{
					shellResizing = true;

					int width = shell.getClientArea().width;
					int[] weights = editorContainer.getWeights();

					if ( width >= sidebarWidth + CANVAS_MIN_SIZE ) {
						weights[Manager.sidebarOnRightSide ? 1 : 0] = 1000000 * sidebarWidth / width;
						weights[Manager.sidebarOnRightSide ? 0 : 1] = 1000000 - weights[Manager.sidebarOnRightSide ? 1 : 0];
					}
					else {
						weights[Manager.sidebarOnRightSide ? 1 : 0] = 1000000 * sidebarWidth / ( sidebarWidth + CANVAS_MIN_SIZE );
						weights[Manager.sidebarOnRightSide ? 0 : 1] = 1000000 * CANVAS_MIN_SIZE / ( sidebarWidth + CANVAS_MIN_SIZE );
					}

					editorContainer.setWeights( weights );

					if ( !shell.getMaximized() ) {
						Manager.windowSize.x = shell.getSize().x;
						Manager.windowSize.y = shell.getSize().y;
					}

					shell.layout();
					Grid.getInstance().updateBounds( canvas.getSize().x, canvas.getSize().y );

					ShipContainer ship = Manager.getCurrentShip();
					if ( ship != null ) {
						ship.updateBoundingArea();
						ship.updateChildBoundingAreas();
						ship.getShipController().updateView();
						ship.getShipController().updateProps();
						ship.getShipController().redraw();
					}
				}
			}
		);

		shell.addListener(
			SWT.Close, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					e.doit = Manager.closeShip();
				}
			}
		);

		mntmNewShip.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					NewShipDialog dialog = new NewShipDialog( shell );
					int response = dialog.open();
					if ( response != -1 )
						Manager.createNewShip( response == 0 );
				}
			}
		);

		mntmLoadShip.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipLoaderDialog dialog = new ShipLoaderDialog( shell );
					dialog.open();
				}
			}
		);

		mntmConvertShp.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					File f = UIUtils.promptForLoadFile( shell, Superluminal.APP_NAME + " - Open .shp", prevShpPath, new String[] { "*.shp" } );
					if ( f != null ) {
						try {
							prevShpPath = f.getAbsolutePath();
							Manager.loadShip( SHPUtils.loadShipSHP( f ) );
						}
						catch ( Exception ex ) {
							log.error( "Error occured while reading .shp file:", ex );
							String msg = "Superluminal was unable to open the legacy project file.\n\n" +
								"This can happen when the file is structurized in a way that the editor doesn't expect.\n" +
								"Posting the .shp file in the editor's thread at the FTL forums will help the editor's author fix the issue.";
							UIUtils.showWarningDialog( shell, null, msg );
						}
					}
				}
			}
		);

		mntmSaveShip.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipContainer container = Manager.getCurrentShip();
					if ( !promptSaveShip( container, false ) )
						log.trace( "User exited save dialog, ship was not saved." );
				}
			}
		);

		mntmSaveShipAs.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipContainer container = Manager.getCurrentShip();
					if ( !promptSaveShip( container, true ) )
						log.trace( "User exited save dialog, ship was not saved." );
				}
			}
		);

		mntmModMan.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ModManagementDialog dialog = new ModManagementDialog( shell );
					dialog.open();
				}
			}
		);

		mntmReloadDb.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					reloadDatabase();
				}
			}
		);

		mntmChangeArchives.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					File datsDir = UIUtils.promptForDatsDir( getShell() );

					if ( datsDir != null ) {
						Manager.resourcePath = datsDir.getAbsolutePath();
						log.info( "FTL dats path updated to: " + Manager.resourcePath );
						reloadDatabase();
					}
				}
			}
		);

		mntmCloseShip.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.closeShip();
				}
			}
		);

		mntmUndo.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( Manager.canUndo() )
						Manager.undo();
				}
			}
		);

		mntmRedo.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( Manager.canRedo() )
						Manager.redo();
				}
			}
		);

		mntmResetLinks.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().getShipController().getGameObject().resetDoorLinks();
					updateSidebarContent();
				}
			}
		);

		mntmOptimalOffset.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipContainer container = Manager.getCurrentShip();
					ShipController shipC = container.getShipController();
					Point offset = container.findOptimalThickOffset();
					Point fineOffset = container.findOptimalFineOffset();

					UndoableOffsetsEdit edit = new UndoableOffsetsEdit( container );
					edit.setOld( new Tuple<Point, Point>( container.getShipOffset(), container.getShipFineOffset() ) );

					shipC.select();
					container.setShipFineOffset( fineOffset.x, fineOffset.y );
					container.setShipOffset( offset.x, offset.y );

					shipC.updateProps();
					// Don't deselect if it was actually selected by the user
					if ( Manager.getSelected() != shipC )
						shipC.deselect();

					edit.setCurrent( new Tuple<Point, Point>( container.getShipOffset(), container.getShipFineOffset() ) );
					container.postEdit( edit );
				}
			}
		);

		mntmGenerateFloor.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					final ShipContainer container = Manager.getCurrentShip();

					if ( container.getRoomControllers().length == 0 ) {
						UIUtils.showInfoDialog( null, null, "Unable to generate floor image, because the ship has no rooms." );
						return;
					}

					FloorgenDialog fd = new FloorgenDialog( shell );
					FloorImageFactory fif = fd.open();

					if ( fif == null )
						return;

					UndoablePropertyEdit<String> edit = new UndoablePropertyEdit<String>( container ) {
						public void callback( String arg )
						{
							container.setImage( Images.FLOOR, arg );
							updateSidebarContent();
						}

						@Override
						public String getPresentationName()
						{
							return "generate floor image";
						}
					};

					edit.setOld( container.getImage( Images.FLOOR ) );
					Manager.getCurrentShip().generateFloorImage( fif );
					edit.setCurrent( container.getImage( Images.FLOOR ) );

					if ( !edit.isValuesEqual() )
						Manager.postEdit( edit );
				}
			}
		);

		mntmDelete.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					AbstractController selected = Manager.getSelected();
					if ( selected != null ) {
						try {
							int index = -1;
							if ( selected instanceof RoomController ) {
								index = ( (RoomController)selected ).getId();
							}
							else if ( selected instanceof DoorController ) {
								index = Utils.indexOf( Manager.getCurrentShip().getDoorControllers(), selected );
							}
							else if ( selected instanceof MountController ) {
								index = ( (MountController)selected ).getId();
							}
							else if ( selected instanceof GibController ) {
								index = ( (GibController)selected ).getId();
							}

							Manager.getCurrentShip().delete( selected );
							selected.redraw();

							Manager.setSelected( null );

							Manager.getCurrentShip().postEdit( new UndoableDeleteEdit( selected, index ) );
						}
						catch ( NotDeletableException ex ) {
							log.trace( "Selected object is not deletable: " + selected.getClass().getSimpleName() );
						}
					}
				}
			}
		);

		mntmSettings.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					SettingsDialog dialog = new SettingsDialog( shell );
					dialog.open();
				}
			}
		);

		mntmZoom.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ZoomWindow zoom = ZoomWindow.getInstance();
					if ( zoom == null )
						zoom = new ZoomWindow( shell, canvas );
					zoom.open();
				}
			}
		);

		mntmGrid.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Grid.getInstance().setVisible( mntmGrid.getSelection() );
				}
			}
		);

		mntmHangar.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setHangarVisible( mntmHangar.getSelection() );
				}
			}
		);

		mntmShowAnchor.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setAnchorVisible( mntmShowAnchor.getSelection() );
				}
			}
		);

		mntmShowMounts.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setMountsVisible( mntmShowMounts.getSelection() );
				}
			}
		);

		mntmShowRooms.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setRoomsVisible( mntmShowRooms.getSelection() );
				}
			}
		);

		mntmShowDoors.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setDoorsVisible( mntmShowDoors.getSelection() );
				}
			}
		);

		mntmShowStations.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setStationsVisible( mntmShowStations.getSelection() );
				}
			}
		);

		mntmShowHull.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					AbstractController ac = Manager.getCurrentShip().getImageController( Images.HULL );
					if ( ac.isSelected() )
						Manager.setSelected( null );
					ac.setVisible( mntmShowHull.getSelection() );
				}
			}
		);

		mntmShowFloor.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					AbstractController ac = Manager.getCurrentShip().getImageController( Images.FLOOR );
					if ( ac.isSelected() )
						Manager.setSelected( null );
					ac.setVisible( mntmShowFloor.getSelection() );
				}
			}
		);

		mntmShowShield.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					AbstractController ac = Manager.getCurrentShip().getImageController( Images.SHIELD );
					if ( ac.isSelected() )
						Manager.setSelected( null );
					ac.setVisible( mntmShowShield.getSelection() );
				}
			}
		);

		mntmShowGibs.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Manager.getCurrentShip().setGibsVisible( mntmShowGibs.getSelection() );
				}
			}
		);

		mntmAbout.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					String msg = Superluminal.APP_NAME + " - a ship editor for FTL: Faster Than Light\n" +
						"Version " + Superluminal.APP_VERSION + "\n\n" +
						"Created by " + Superluminal.APP_AUTHOR + "\n";
					AboutDialog aboutDialog = new AboutDialog( shell );
					aboutDialog.setMessage( msg );
					try {
						aboutDialog.setLink( new URL( Superluminal.APP_FORUM_URL ), "Editor's thread at the official FTL forums" );
					}
					catch ( MalformedURLException ex ) {
					}

					aboutDialog.open();
				}
			}
		);

		mntmUpdate.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					Superluminal.checkForUpdates( true ); // Manually checking for updates
				}
			}
		);

		Transfer[] dropTypes = new Transfer[] { FileTransfer.getInstance() };
		dropTarget = new DropTarget( shell, DND.DROP_MOVE | DND.DROP_DEFAULT );
		dropTarget.setTransfer( dropTypes );

		dropTarget.addDropListener(
			new DropTargetAdapter() {
				@Override
				public void dragOver( DropTargetEvent e )
				{
					e.detail = DND.DROP_MOVE;
					e.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
					Object object = FileTransfer.getInstance().nativeToJava( e.currentDataType );
					if ( object instanceof String[] ) {
						String fileList[] = (String[])object;
						for ( String path : fileList ) {
							if ( !path.endsWith( ".ftl" ) && !path.endsWith( ".zip" ) ) {
								e.detail = DND.DROP_NONE;
								e.feedback = DND.FEEDBACK_NONE;
								break;
							}
						}
					}
					else {
						e.detail = DND.DROP_NONE;
						e.feedback = DND.FEEDBACK_NONE;
					}
				}

				@Override
				public void drop( DropTargetEvent e )
				{
					Object object = FileTransfer.getInstance().nativeToJava( e.currentDataType );

					if ( Manager.getCurrentShip() == null ) {
						if ( object instanceof String[] ) {
							final String fileList[] = (String[])object;

							final Database db = Database.getInstance();
							if ( db == null ) {
								String msg = "Database not found -- mods cannot be loaded.\n" +
									"In order to create database, point the editor to FTL installation.";
								UIUtils.showWarningDialog( shell, null, msg );
								return;
							}

							// Already ensured that all items are .ftl or .zip
							UIUtils.showLoadDialog(
								shell, null, "Loading mods, please wait...", new Runnable() {
									public void run()
									{
										for ( String path : fileList ) {
											try {
												db.addEntry( new ModDatabaseEntry( new File( path ) ) );
											}
											catch ( Exception ex ) {
												log.warn( String.format( "Could not create a database entry for file '%s': ", path ), ex );
											}
										}
										db.cacheAnimations();
									}
								}
							);
						}
					}
					else {
						String msg = "Mods cannot be loaded while a ship is opened.\n" +
							"Please close the ship, then try again.";
						UIUtils.showInfoDialog( shell, null, msg );
					}
				}
			}
		);

		canvas.addMouseListener( MouseInputDispatcher.getInstance() );
		canvas.addMouseMoveListener( MouseInputDispatcher.getInstance() );
		canvas.addMouseTrackListener( MouseInputDispatcher.getInstance() );

		canvas.addMouseMoveListener(
			new MouseMoveListener() {
				public void mouseMove( MouseEvent e )
				{
					int x = e.x, y = e.y;
					int gx = 0, gy = 0;

					if ( Manager.mouseShipRelative && Manager.getCurrentShip() != null ) {
						Point shipLoc = Manager.getCurrentShip().getShipController().getLocation();
						x -= shipLoc.x;
						y -= shipLoc.y;
						if ( x < 0 )
							gx -= ShipContainer.CELL_SIZE;
						if ( y < 0 )
							gy -= ShipContainer.CELL_SIZE;
					}

					gx += x;
					gy += y;
					gx /= ShipContainer.CELL_SIZE;
					gy /= ShipContainer.CELL_SIZE;

					lblCursorLoc.setText( String.format( "%-4s, %-4s | %-2s, %-2s", x, y, gx, gy ) );
				}
			}
		);

		registerHotkeys();
		updateHotkeyTooltips();

		shell.setMinimumSize( SIDEBAR_MIN_WIDTH + CANVAS_MIN_SIZE, CANVAS_MIN_SIZE + toolContainer.getSize().y * 2 );

		shell.setSize( Manager.windowSize );
		shell.setMaximized( Manager.startMaximised );
		shell.setMinimized( false );

		Grid.getInstance().updateBounds( canvas.getSize().x, canvas.getSize().y );

		sideContainer.setFocus();
		enableTools( false );
		enableOptions( false );
		setVisibilityOptions( true );
	}

	public static EditorWindow getInstance()
	{
		return instance;
	}

	public void open()
	{
		shell.open();
	}

	public void updateHotkeyTooltips()
	{
		Hotkey h = null;

		// File
		mntmNewShip.setText( "New Ship" );
		h = Manager.getHotkey( Hotkeys.NEW_SHIP );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmNewShip, h.toString() );
		mntmLoadShip.setText( "Load Ship" );
		h = Manager.getHotkey( Hotkeys.LOAD_SHIP );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmLoadShip, h.toString() );
		mntmConvertShp.setText( "Open .shp" );
		h = Manager.getHotkey( Hotkeys.LOAD_LEGACY );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmConvertShp, h.toString() );
		mntmSaveShip.setText( "Save Ship" );
		h = Manager.getHotkey( Hotkeys.SAVE_SHIP );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmSaveShip, h.toString() );
		mntmSaveShipAs.setText( "Save Ship As" );
		h = Manager.getHotkey( Hotkeys.SAVE_SHIP_AS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmSaveShipAs, h.toString() );
		mntmModMan.setText( "Mod Management" );
		h = Manager.getHotkey( Hotkeys.MANAGE_MOD );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmModMan, h.toString() );
		mntmCloseShip.setText( "Close Ship" );
		h = Manager.getHotkey( Hotkeys.CLOSE_SHIP );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmCloseShip, h.toString() );

		// Edit
		mntmUndo.setText( "Undo" );
		h = Manager.getHotkey( Hotkeys.UNDO );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmUndo, h.toString() );
		mntmRedo.setText( "Redo" );
		h = Manager.getHotkey( Hotkeys.REDO );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmRedo, h.toString() );
		mntmDelete.setText( "Delete" );
		h = Manager.getHotkey( Hotkeys.DELETE );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmDelete, h.toString() );
		mntmSettings.setText( "Settings" );
		h = Manager.getHotkey( Hotkeys.SETTINGS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmSettings, h.toString() );

		// View
		mntmZoom.setText( "Open Zoom Window" );
		h = Manager.getHotkey( Hotkeys.OPEN_ZOOM );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmZoom, h.toString() );
		mntmGrid.setText( "Show Grid" );
		h = Manager.getHotkey( Hotkeys.TOGGLE_GRID );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmGrid, h.toString() );
		mntmHangar.setText( "Show Hangar" );
		h = Manager.getHotkey( Hotkeys.TOGGLE_HANGAR );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmHangar, h.toString() );

		mntmShowAnchor.setText( "Show Ship Origin" );
		h = Manager.getHotkey( Hotkeys.SHOW_ANCHOR );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowAnchor, h.toString() );
		mntmShowMounts.setText( "Show Mounts" );
		h = Manager.getHotkey( Hotkeys.SHOW_MOUNTS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowMounts, h.toString() );
		mntmShowRooms.setText( "Show Rooms" );
		h = Manager.getHotkey( Hotkeys.SHOW_ROOMS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowRooms, h.toString() );
		mntmShowDoors.setText( "Show Doors" );
		h = Manager.getHotkey( Hotkeys.SHOW_DOORS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowDoors, h.toString() );
		mntmShowStations.setText( "Show Stations" );
		h = Manager.getHotkey( Hotkeys.SHOW_STATIONS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowStations, h.toString() );

		mntmShowHull.setText( "Show Hull" );
		h = Manager.getHotkey( Hotkeys.SHOW_HULL );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowHull, h.toString() );
		mntmShowFloor.setText( "Show Floor" );
		h = Manager.getHotkey( Hotkeys.SHOW_FLOOR );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowFloor, h.toString() );
		mntmShowShield.setText( "Show Shield" );
		h = Manager.getHotkey( Hotkeys.SHOW_SHIELD );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowShield, h.toString() );
		mntmShowGibs.setText( "Show Gibs" );
		h = Manager.getHotkey( Hotkeys.SHOW_GIBS );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmShowGibs, h.toString() );

		// Tools
		h = Manager.getHotkey( Hotkeys.POINTER_TOOL );
		tltmPointer.setToolTipText( "Manipulation Tool" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		h = Manager.getHotkey( Hotkeys.CREATE_TOOL );
		tltmCreation.setToolTipText( "Layout Creation Tool" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		h = Manager.getHotkey( Hotkeys.IMAGES_TOOL );
		tltmImages.setToolTipText( "Ship Images" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		h = Manager.getHotkey( Hotkeys.PROPERTIES_TOOL );
		tltmProperties.setToolTipText( "Ship Loadout and Properties" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		h = Manager.getHotkey( Hotkeys.OVERVIEW_TOOL );
		tltmManager.setToolTipText( "Overview" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		h = Manager.getHotkey( Hotkeys.CLOAK );
		tltmCloak.setToolTipText( "View Cloaked Appearance" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		h = Manager.getHotkey( Hotkeys.ANIMATE );
		tltmAnimate.setToolTipText( "Animate Gibs" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
	}

	public void updateUndoButtons()
	{
		mntmUndo.setEnabled( Manager.canUndo() );
		mntmUndo.setText( Manager.getUndoPresentationName() );
		Hotkey h = Manager.getHotkey( Hotkeys.UNDO );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmUndo, h.toString() );
		mntmRedo.setEnabled( Manager.canRedo() );
		mntmRedo.setText( Manager.getRedoPresentationName() );
		h = Manager.getHotkey( Hotkeys.REDO );
		if ( h.isEnabled() )
			UIUtils.addHotkeyText( mntmRedo, h.toString() );
	}

	/**
	 * Sets the control passed in argument as the content of the sidebar -- this is
	 * what the scrolled area will display, and will scale the scroll against.
	 */
	public void setSidebarContent( Composite c )
	{
		sideContainer.setContent( c );
		c.pack();
		int height = c.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
		sideContainer.setMinHeight( height );
	}

	/**
	 * Updates the minimum area of the sidebar, so that the scrollbar will appear when needed.
	 */
	public void updateSidebarScroll()
	{
		Control c = sideContainer.getContent();
		if ( c != null && !c.isDisposed() ) {
			int height = c.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
			sideContainer.setMinHeight( height );
		}
	}

	/**
	 * Do not use this method to <b>dispose</b> the content of the sidebar.
	 * To do this, use {@link #disposeSidebarContent()}.
	 * 
	 * @return the Composite currently held by the sidebar.
	 */
	public Composite getSidebarContent()
	{
		Control c = sideContainer.getContent();
		if ( c instanceof Composite )
			return (Composite)sideContainer.getContent();
		else if ( c != null )
			throw new IllegalStateException( "Content of the sidebar is not a Composite: " + c.getClass().getSimpleName() );
		else
			return null;
	}

	public void updateSidebarContent()
	{
		Control c = sideContainer.getContent();
		if ( c != null ) {
			if ( c instanceof DataComposite ) {
				( (DataComposite)c ).reloadController();
				( (DataComposite)c ).updateData();
			}
		}
	}

	public void setSidebarContentController( AbstractController controller ) throws UnsupportedOperationException
	{
		Control c = sideContainer.getContent();
		if ( c != null ) {
			if ( c instanceof DataComposite ) {
				( (DataComposite)c ).setController( controller );
			}
		}
	}

	/**
	 * Disposes the content of the sidebar, and sets the content to null.<br>
	 * Prevents the editor from crashing when trying to change the sidebar positioning after closing a ship.
	 */
	public void disposeSidebarContent()
	{
		Control c = sideContainer.getContent();
		if ( c != null )
			c.dispose();
		sideContainer.setContent( null );
	}

	/** @return the sidebar ScrolledComposite itself. */
	public Composite getSidebarWidget()
	{
		return sideContainer;
	}

	public Shell getShell()
	{
		return shell;
	}

	/**
	 * Checks whether the point is inside the canvas area -- eg. points non-negative coordinates
	 * with values lesser than or equal to the canvas' dimensions (width and height)
	 * 
	 * @return true if the point (relative to the canvas) is within the canvas bounds, false otherwise
	 */
	public boolean canvasContains( int x, int y )
	{
		Rectangle bounds = canvas.getBounds();
		bounds.x = 0;
		bounds.y = 0;
		return bounds.contains( x, y );
	}

	/** Redraws the entire canvas area. */
	public void canvasRedraw()
	{
		canvas.redraw();
	}

	/** Redraws the part of canvas covered by the rectangle. */
	public void canvasRedraw( Rectangle rect )
	{
		canvas.redraw( rect.x, rect.y, rect.width, rect.height, false );
	}

	/** Redraws the part of canvas covered by the rectangle. */
	public void canvasRedraw( int x, int y, int w, int h )
	{
		canvas.redraw( x, y, w, h, false );
	}

	/**
	 * Only to be used to programmatically select the tool, when the user doesn't directly click on the tool's icon.
	 */
	public void selectTool( Tools tool )
	{
		if ( !isToolsEnabled() )
			return;

		for ( ToolItem it : toolItemMap.values() ) {
			if ( toolItemMap.containsKey( tool ) )
				it.setSelection( tool == (Tools)it.getData() );
		}
	}

	public void enableTools( boolean enable )
	{
		tltmPointer.setEnabled( enable );
		tltmCreation.setEnabled( enable );
		tltmImages.setEnabled( enable );
		tltmProperties.setEnabled( enable );
		tltmManager.setEnabled( enable );

		ShipContainer c = Manager.getCurrentShip();
		tltmAnimate.setEnabled( enable || ( c != null && c.isGibAnimationInProgress() ) );

		tltmCloak.setEnabled( enable );
		if ( !enable && tltmCloak.getSelection() )
			tltmCloak.setSelection( false );
		else if ( enable && c.getImageController( Images.CLOAK ).isVisible() )
			tltmCloak.setSelection( true );

		sideContainer.getVerticalBar().setEnabled( enable );
	}

	public boolean isToolsEnabled()
	{
		return tltmPointer.isEnabled();
	}

	public void enableOptions( boolean enable )
	{
		// File
		mntmSaveShip.setEnabled( enable );
		mntmSaveShipAs.setEnabled( enable );
		mntmCloseShip.setEnabled( enable );

		// Edit
		updateUndoButtons();
		mntmUndo.setEnabled( enable && Manager.canUndo() );
		mntmRedo.setEnabled( enable && Manager.canRedo() );
		mntmResetLinks.setEnabled( enable );
		mntmOptimalOffset.setEnabled( enable );
		mntmGenerateFloor.setEnabled( enable );
		mntmDelete.setEnabled( enable );

		// View
		mntmGrid.setEnabled( enable );
		mntmHangar.setEnabled( enable );
		mntmShowAnchor.setEnabled( enable );
		mntmShowMounts.setEnabled( enable );
		mntmShowRooms.setEnabled( enable );
		mntmShowDoors.setEnabled( enable );
		mntmShowStations.setEnabled( enable );
		mntmShowHull.setEnabled( enable );
		mntmShowFloor.setEnabled( enable );
		mntmShowShield.setEnabled( enable );
		mntmShowGibs.setEnabled( enable );

		// Mod management only available when a ship is not loaded
		ShipContainer c = Manager.getCurrentShip();
		mntmModMan.setEnabled( !enable && c == null );
		mntmReloadDb.setEnabled( !enable && c == null );
		mntmChangeArchives.setEnabled( !enable && c == null );
	}

	public boolean isOptionsEnabled()
	{
		return mntmSaveShip.isEnabled();
	}

	/**
	 * Toggles all visibility-related options.
	 */
	public void setVisibilityOptions( boolean set )
	{
		ShipContainer container = Manager.getCurrentShip();

		mntmHangar.setSelection( false );
		mntmShowAnchor.setSelection( set );
		mntmShowMounts.setSelection( set );
		mntmShowRooms.setSelection( set );
		mntmShowDoors.setSelection( set );
		mntmShowStations.setSelection( set );
		mntmShowHull.setSelection( set );
		mntmShowFloor.setSelection( set );
		mntmShowShield.setSelection( set );
		mntmShowGibs.setSelection( set );

		if ( container != null ) {
			container.setHangarVisible( false );
			container.setAnchorVisible( set );
			container.setMountsVisible( set );
			container.setRoomsVisible( set );
			container.setDoorsVisible( set );
			container.setStationsVisible( set );
		}
	}

	/**
	 * Allows to put the editor in a completely non-interactable state, save for the menu options under "Help".
	 */
	public void setInteractable( boolean interactable )
	{
		enableTools( interactable );
		enableOptions( interactable );
		// Toggle widgets not covered by the two methods
		mntmNewShip.setEnabled( interactable );
		mntmLoadShip.setEnabled( interactable );
		mntmConvertShp.setEnabled( interactable );
		mntmSettings.setEnabled( interactable );

		// Toggle the overview window, if it is currently opened
		OverviewWindow w = OverviewWindow.getInstance();
		if ( !w.isDisposed() )
			w.setEnabled( interactable );

		// Toggle tool
		Manager.setSelected( null );
		if ( interactable ) {
			Manager.selectTool( Tools.POINTER );
		}
		else {
			Manager.selectTool( null );
		}

		// Select none gib prop controller
		ShipContainer container = Manager.getCurrentShip();
		if ( container != null && !interactable ) {
			GibPropContainer propC = container.getGibContainer();
			propC.showControls( PropControls.NONE );
		}
	}

	/**
	 * @return true if the editor window controls the focus and should execute hotkey actions.
	 */
	public boolean isFocusControl()
	{
		Control c = UIUtils.getDisplay().getFocusControl();

		boolean result = c != null && !( c.isEnabled() && ( c instanceof Spinner ||
			( c instanceof Text && ( (Text)c ).getEditable() ) ) );

		if ( result ) {
			result &= OverviewWindow.getInstance() == null || !OverviewWindow.getInstance().isActive();
			result &= ShipLoaderDialog.getInstance() == null || !ShipLoaderDialog.getInstance().isActive();
			result &= SettingsDialog.getInstance() == null || !SettingsDialog.getInstance().isActive();
			result &= NewShipDialog.getInstance() == null || !NewShipDialog.getInstance().isActive();
			result &= SaveOptionsDialog.getInstance() == null || !SaveOptionsDialog.getInstance().isActive();
			result &= ModManagementDialog.getInstance() == null || !ModManagementDialog.getInstance().isActive();
			result &= AboutDialog.getInstance() == null || !AboutDialog.getInstance().isActive();
			result &= AliasDialog.getInstance() == null || !AliasDialog.getInstance().isActive();
			result &= !LoadingDialog.isActive();
			result &= GlowSelectionDialog.getInstance() == null || !GlowSelectionDialog.getInstance().isActive();
			result &= WeaponSelectionDialog.getInstance() == null || !WeaponSelectionDialog.getInstance().isActive();
			result &= DroneSelectionDialog.getInstance() == null || !DroneSelectionDialog.getInstance().isActive();
			result &= AugmentSelectionDialog.getInstance() == null || !AugmentSelectionDialog.getInstance().isActive();
		}

		return result;
	}

	/**
	 * @return true if the control got focus, and false if it was unable to.
	 */
	public boolean forceFocus()
	{
		return canvas.forceFocus();
	}

	public boolean isImageDrawn( Images type )
	{
		switch ( type ) {
			case HULL:
				return mntmShowHull.getSelection();
			case FLOOR:
				return mntmShowFloor.getSelection();
			case SHIELD:
				return mntmShowShield.getSelection();
			case CLOAK:
				return tltmCloak.getSelection();
			default:
				return false;
		}
	}

	public void dispose()
	{
		Manager.unhookHotkeys( shell );
		Cache.checkInColor( this, canvasRGB );
		Grid.getInstance().dispose();
		Cache.dispose();
		shell.dispose();
		eventHandler.dispose();
	}

	public void addListener( int eventType, SLListener listener )
	{
		eventHandler.hook( eventType, listener );
	}

	public void removeListener( int eventType, SLListener listener )
	{
		eventHandler.unhook( eventType, listener );
	}

	/**
	 * Refreshes the sidebar's layout.
	 */
	public void layoutSidebar()
	{
		sideContainer.setParent( shell );
		canvas.setParent( shell );
		if ( Manager.sidebarOnRightSide ) {
			canvas.setParent( editorContainer );
			sideContainer.setParent( editorContainer );
		}
		else {
			sideContainer.setParent( editorContainer );
			canvas.setParent( editorContainer );
		}

		editorContainer.layout();
	}

	public boolean promptSaveShip( ShipContainer container, boolean prompt )
	{
		SaveOptions soPrev = container.getSaveOptions();

		File file = soPrev.file;
		ModDatabaseEntry mod = soPrev.mod;

		if ( file == null || prompt ) {
			SaveOptionsDialog dialog = new SaveOptionsDialog( shell );
			SaveOptions so = dialog.open();

			if ( so != null ) {
				file = so.file;
				mod = so.mod;
			}
			else {
				return false;
			}
		}

		container.save( file, mod );
		return true;
	}

	private void reloadDatabase()
	{
		final Exception[] ex = new Exception[1];
		ex[0] = null;
		UIUtils.showLoadDialog(
			shell, null, null, new Runnable() {
				public void run()
				{
					log.debug( "Reloading Database..." );

					try {
						Database db = Database.getInstance();

						ArrayList<File> entries = new ArrayList<File>();

						// Unload modded files and store them for reloading
						for ( AbstractDatabaseEntry de : db.getEntries() ) {
							if ( de instanceof ModDatabaseEntry ) {
								entries.add( ( (ModDatabaseEntry)de ).getFile() );
								db.removeEntry( de );
							}
						}

						// Reload the base game files.
						db.removeEntry( db.getCore() );

						File datsDir = new File( Manager.resourcePath );
						db.buildCore( datsDir );
						db.getCore().load();

						// Reload modded files
						for ( File modFile : entries ) {
							// Need to reopen the entries, since they were closed during removal
							db.addEntry( new ModDatabaseEntry( modFile ) );
						}

						db.cacheAnimations();
					}
					catch ( Exception e ) {
						ex[0] = e;
					}
				}
			}
		);

		if ( ex[0] != null ) {
			log.error( "An error has occured while reloading the database.", ex[0] );
			String msg = "An error has occured while reloading the Database:\n" +
				ex[0].getClass().getSimpleName() + ": " + ex[0].getMessage() + "\n\n" +
				"Check the log for details.";
			UIUtils.showErrorDialog( null, null, msg );
		}
	}

	private void registerHotkeys()
	{
		Hotkey h = null;

		// ====== Menu hotkeys

		// File
		h = Manager.getHotkey( Hotkeys.NEW_SHIP );
		h.addNotifyAction( mntmNewShip, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.LOAD_SHIP );
		h.addNotifyAction( mntmLoadShip, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SAVE_SHIP );
		h.addNotifyAction( mntmSaveShip, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SAVE_SHIP_AS );
		h.addNotifyAction( mntmSaveShipAs, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.LOAD_LEGACY );
		h.addNotifyAction( mntmConvertShp, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.MANAGE_MOD );
		h.addNotifyAction( mntmModMan, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.CLOSE_SHIP );
		h.addNotifyAction( mntmCloseShip, true );
		Manager.hookHotkey( shell, h );

		// Edit
		h = Manager.getHotkey( Hotkeys.UNDO );
		h.addNotifyAction( mntmUndo, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.REDO );
		h.addNotifyAction( mntmRedo, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SETTINGS );
		h.addNotifyAction( mntmSettings, true );
		Manager.hookHotkey( shell, h );

		// View
		h = Manager.getHotkey( Hotkeys.OPEN_ZOOM );
		h.addNotifyAndToggleAction( mntmZoom, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.TOGGLE_GRID );
		h.addNotifyAndToggleAction( mntmGrid, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.TOGGLE_HANGAR );
		h.addNotifyAndToggleAction( mntmHangar, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_ANCHOR );
		h.addNotifyAndToggleAction( mntmShowAnchor, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_MOUNTS );
		h.addNotifyAndToggleAction( mntmShowMounts, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_ROOMS );
		h.addNotifyAndToggleAction( mntmShowRooms, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_DOORS );
		h.addNotifyAndToggleAction( mntmShowDoors, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_STATIONS );
		h.addNotifyAndToggleAction( mntmShowStations, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_HULL );
		h.addNotifyAndToggleAction( mntmShowHull, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_FLOOR );
		h.addNotifyAndToggleAction( mntmShowFloor, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_SHIELD );
		h.addNotifyAndToggleAction( mntmShowShield, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.SHOW_GIBS );
		h.addNotifyAndToggleAction( mntmShowGibs, true );
		Manager.hookHotkey( shell, h );

		// ====== Tool hotkeys

		h = Manager.getHotkey( Hotkeys.POINTER_TOOL );
		h.addNotifyAction( tltmPointer, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.CREATE_TOOL );
		h.addNotifyAction( tltmCreation, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.IMAGES_TOOL );
		h.addNotifyAction( tltmImages, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.PROPERTIES_TOOL );
		h.addNotifyAction( tltmProperties, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.OVERVIEW_TOOL );
		h.addNotifyAction( tltmManager, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.CLOAK );
		h.addNotifyAndToggleAction( tltmCloak, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.ANIMATE );
		h.addNotifyAndToggleAction( tltmAnimate, true );
		Manager.hookHotkey( shell, h );

		// Creation Tool hotkeys
		h = Manager.getHotkey( Hotkeys.ROOM_TOOL );
		addCreateToolAction( h, Tools.ROOM );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.DOOR_TOOL );
		addCreateToolAction( h, Tools.DOOR );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.MOUNT_TOOL );
		addCreateToolAction( h, Tools.WEAPON );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.STATION_TOOL );
		addCreateToolAction( h, Tools.STATION );
		Manager.hookHotkey( shell, h );

		// ====== Tool-specific hotkeys

		h = Manager.getHotkey( Hotkeys.DELETE );
		h.addNotifyAction( mntmDelete, true );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.DEL );
		h.addNotifyAction( mntmDelete, true );
		Manager.hookHotkey( shell, h );

		h = Manager.getHotkey( Hotkeys.PIN );
		h.setOnPress(
			new Runnable() {
				public void run()
				{
					if ( Manager.getSelectedToolId() == Tools.POINTER ) {
						AbstractController selected = Manager.getSelected();
						if ( selected != null ) {
							selected.setPinned( !selected.isPinned() );
							updateSidebarContent();
						}
					}
				}
			}
		);
		Manager.hookHotkey( shell, h );

		// Modifier hotkeys
		h = new Hotkey();
		h.setKey( SWT.SHIFT );
		h.setShift( true );
		addModifierAction( h, Modifiers.SHIFT );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.CTRL );
		h.setCtrl( true );
		addModifierAction( h, Modifiers.CONTROL );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.ALT );
		h.setAlt( true );
		addModifierAction( h, Modifiers.ALT );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.COMMAND );
		h.setCommand( true );
		addModifierAction( h, Modifiers.COMMAND );
		Manager.hookHotkey( shell, h );

		// Arrow hotkeys
		h = new Hotkey();
		h.setKey( SWT.ARROW_UP );
		addNudgeAction( h, 0, -1 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.ARROW_RIGHT );
		addNudgeAction( h, 1, 0 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.ARROW_DOWN );
		addNudgeAction( h, 0, 1 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setKey( SWT.ARROW_LEFT );
		addNudgeAction( h, -1, 0 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setShift( true );
		h.setKey( SWT.ARROW_UP );
		addNudgeAction( h, 0, -1 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setShift( true );
		h.setKey( SWT.ARROW_RIGHT );
		addNudgeAction( h, 1, 0 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setShift( true );
		h.setKey( SWT.ARROW_DOWN );
		addNudgeAction( h, 0, 1 );
		Manager.hookHotkey( shell, h );

		h = new Hotkey();
		h.setShift( true );
		h.setKey( SWT.ARROW_LEFT );
		addNudgeAction( h, -1, 0 );
		Manager.hookHotkey( shell, h );
	}

	private void addNudgeAction( final Hotkey h, final int amountX, final int amountY )
	{
		h.setOnPress(
			new Runnable() {
				public void run()
				{
					AbstractController selected = Manager.getSelected();
					if ( selected != null && !selected.isPinned() && selected.isLocModifiable() ) {
						Point p = selected.getPresentedLocation();
						Rectangle oldBounds = null;

						oldBounds = selected.getBounds();

						int nudgeX = h.getShift() && selected.getPresentedFactor() == 1 ? Utils.sign( amountX ) * ShipContainer.CELL_SIZE : amountX;
						int nudgeY = h.getShift() && selected.getPresentedFactor() == 1 ? Utils.sign( amountY ) * ShipContainer.CELL_SIZE : amountY;

						selected.setPresentedLocation( p.x + nudgeX, p.y + nudgeY );
						selected.updateFollowOffset();
						Manager.getCurrentShip().updateBoundingArea();
						selected.updateView();

						selected.redraw();
						canvasRedraw( oldBounds );

						updateSidebarContent();
					}
				}
			}
		);
	}

	private void addCreateToolAction( Hotkey h, final Tools tool )
	{
		h.setOnPress(
			new Runnable() {
				public void run()
				{
					if ( tltmCreation.isEnabled() ) {
						if ( !tltmCreation.getSelection() )
							tltmCreation.notifyListeners( SWT.Selection, null );
						CreationTool ctool = (CreationTool)Manager.getTool( Tools.CREATOR );
						ctool.selectSubtool( tool );
					}
				}
			}
		);
	}

	private void addModifierAction( Hotkey h, Modifiers modifier )
	{
		switch ( modifier ) {
			case SHIFT:
				h.setOnPress(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModShiftEvent( EditorWindow.this, true ) );
						}
					}
				);
				h.setOnRelease(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModShiftEvent( EditorWindow.this, false ) );
						}
					}
				);
				break;
			case CONTROL:
				h.setOnPress(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModControlEvent( EditorWindow.this, true ) );
						}
					}
				);
				h.setOnRelease(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModControlEvent( EditorWindow.this, false ) );
						}
					}
				);
				break;
			case ALT:
				h.setOnPress(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModAltEvent( EditorWindow.this, true ) );
						}
					}
				);
				h.setOnRelease(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModAltEvent( EditorWindow.this, false ) );
						}
					}
				);
				break;
			case COMMAND:
				h.setOnPress(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModCommandEvent( EditorWindow.this, true ) );
						}
					}
				);
				h.setOnRelease(
					new Runnable() {
						public void run()
						{
							eventHandler.sendEvent( new SLModCommandEvent( EditorWindow.this, false ) );
						}
					}
				);
				break;
			default:
				break;
		}
	}

	/**
	 * @param animate
	 *            if true, sets the button's appearance to 'ready-to-animate' state,<br>
	 *            if false, sets the button's appearance to 'stop animating' state.
	 */
	public void updateGibAnimationButton( boolean animate )
	{
		if ( animate ) {
			Cache.checkInImage( this, "cpath:/assets/stop.png" );
			tltmAnimate.setImage( Cache.checkOutImage( this, "cpath:/assets/play.png" ) );
			Hotkey h = Manager.getHotkey( Hotkeys.ANIMATE );
			tltmAnimate.setToolTipText( "Animate Gibs" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		}
		else {
			Cache.checkInImage( this, "cpath:/assets/play.png" );
			tltmAnimate.setImage( Cache.checkOutImage( this, "cpath:/assets/stop.png" ) );
			Hotkey h = Manager.getHotkey( Hotkeys.ANIMATE );
			tltmAnimate.setToolTipText( "Stop Gib Animation" + ( h.isEnabled() ? String.format( " (%s)", h.toString() ) : "" ) );
		}
	}
}
