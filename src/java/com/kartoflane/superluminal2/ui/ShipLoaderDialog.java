package com.kartoflane.superluminal2.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.enums.Hotkeys;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.db.Database;
import com.kartoflane.superluminal2.ftl.ShipMetadata;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.views.Preview;
import com.kartoflane.superluminal2.utils.ShipLoadUtils;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;


public class ShipLoaderDialog
{
	private static final Logger log = LogManager.getLogger( ShipLoaderDialog.class );
	private static final Predicate<ShipMetadata> defaultFilter = new Predicate<ShipMetadata>() {
		public boolean accept( ShipMetadata s )
		{
			return true;
		}
	};

	private static ShipLoaderDialog instance = null;

	private static final int defaultBlueTabWidth = 200;
	private static final int defaultClassTabWidth = 150;
	private static final int minTreeWidth = defaultBlueTabWidth + defaultClassTabWidth + 5;
	private static final int defaultMetadataWidth = 250;

	private static ShipMetadata selection = null;

	private HashMap<ShipMetadata, TreeItem> dataTreeMap = new HashMap<ShipMetadata, TreeItem>();
	private HashMap<String, TreeItem> blueprintTreeMap = new HashMap<String, TreeItem>();

	private Predicate<ShipMetadata> filter = defaultFilter;
	private boolean sortByBlueprint = true;

	private Preview preview = null;
	private Shell shell;
	private Text txtBlueprint;
	private Text txtClass;
	private Text txtName;
	private Text txtDescription;
	private TreeItem trtmPlayer;
	private TreeItem trtmEnemy;
	private Tree tree;
	private Button btnLoad;
	private Canvas canvas;
	private Button btnCancel;
	private TreeColumn trclmnBlueprint;
	private TreeColumn trclmnClass;


	public ShipLoaderDialog( Shell parent )
	{
		if ( instance != null )
			throw new IllegalStateException( "Previous instance has not been disposed!" );
		instance = this;

		preview = new Preview();

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL );
		shell.setText( Superluminal.APP_NAME + " - Ship Loader" );
		shell.setLayout( new GridLayout( 2, false ) );

		SashForm sashForm = new SashForm( shell, SWT.SMOOTH );
		sashForm.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );

		tree = new Tree( sashForm, SWT.BORDER | SWT.FULL_SELECTION );
		tree.setHeaderVisible( true );
		// remove the horizontal bar so that it doesn't flicker when the tree is resized
		tree.getHorizontalBar().dispose();

		trclmnBlueprint = new TreeColumn( tree, SWT.LEFT );
		trclmnBlueprint.setWidth( defaultBlueTabWidth );
		trclmnBlueprint.setText( "Blueprint Name" );
		trclmnBlueprint.setToolTipText( "Click to sort by blueprint name." );

		trclmnClass = new TreeColumn( tree, SWT.RIGHT );
		trclmnClass.setWidth( defaultClassTabWidth );
		trclmnClass.setText( "Ship Class" );
		trclmnClass.setToolTipText( "Click to sort by class name." );

		trtmPlayer = new TreeItem( tree, SWT.NONE );
		trtmPlayer.setText( 0, "Player Ships" );
		trtmPlayer.setText( 1, "" );

		trtmEnemy = new TreeItem( tree, 0 );
		trtmEnemy.setText( 0, "Enemy Ships" );
		trtmEnemy.setText( 1, "" );

		ScrolledComposite scrolledComposite = new ScrolledComposite( sashForm, SWT.BORDER | SWT.V_SCROLL );
		scrolledComposite.setExpandHorizontal( true );
		scrolledComposite.setExpandVertical( true );

		Composite metadataComposite = new Composite( scrolledComposite, SWT.NONE );
		metadataComposite.setLayout( new GridLayout( 2, false ) );

		canvas = new Canvas( metadataComposite, SWT.DOUBLE_BUFFERED );
		GridData gd_canvas = new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 );
		gd_canvas.heightHint = 100;
		canvas.setLayoutData( gd_canvas );
		canvas.addPaintListener( preview );

		Label lblBlueprint = new Label( metadataComposite, SWT.NONE );
		lblBlueprint.setText( "Blueprint:" );

		txtBlueprint = new Text( metadataComposite, SWT.BORDER | SWT.READ_ONLY );
		txtBlueprint.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false, 1, 1 ) );

		Label lblClass = new Label( metadataComposite, SWT.NONE );
		lblClass.setText( "Class:" );

		txtClass = new Text( metadataComposite, SWT.BORDER | SWT.READ_ONLY );
		txtClass.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Label lblName = new Label( metadataComposite, SWT.NONE );
		lblName.setText( "Name:" );

		txtName = new Text( metadataComposite, SWT.BORDER | SWT.READ_ONLY );
		txtName.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );

		Label lblDescription = new Label( metadataComposite, SWT.NONE );
		lblDescription.setLayoutData( new GridData( SWT.LEFT, SWT.BOTTOM, false, false, 2, 1 ) );
		lblDescription.setText( "Description:" );

		txtDescription = new Text( metadataComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI );
		GridData gd_txtDescription = new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1 );
		gd_txtDescription.heightHint = 100;
		txtDescription.setLayoutData( gd_txtDescription );
		scrolledComposite.setContent( metadataComposite );
		scrolledComposite.setMinSize( metadataComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

		sashForm.setWeights( new int[] { minTreeWidth, defaultMetadataWidth } );

		Composite buttonComposite = new Composite( shell, SWT.NONE );
		GridLayout gl_buttonComposite = new GridLayout( 3, false );
		gl_buttonComposite.marginWidth = 0;
		gl_buttonComposite.marginHeight = 0;
		buttonComposite.setLayout( gl_buttonComposite );
		buttonComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1 ) );

		Button btnSearch = new Button( buttonComposite, SWT.NONE );
		btnSearch.setText( "Search" );
		GridData gd_btnSearch = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnSearch.widthHint = 80;
		btnSearch.setLayoutData( gd_btnSearch );

		btnLoad = new Button( buttonComposite, SWT.NONE );
		GridData gd_btnLoad = new GridData( SWT.RIGHT, SWT.CENTER, true, false, 1, 1 );
		gd_btnLoad.widthHint = 80;
		btnLoad.setLayoutData( gd_btnLoad );
		btnLoad.setText( "Load" );
		btnLoad.setEnabled( false );

		btnCancel = new Button( buttonComposite, SWT.NONE );
		GridData gd_btnCancel = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData( gd_btnCancel );
		btnCancel.setText( "Close" );

		tree.addMouseListener(
			new MouseAdapter() {
				@Override
				public void mouseDoubleClick( MouseEvent e )
				{
					if ( e.button == 1 && tree.getSelectionCount() != 0 ) {
						TreeItem selectedItem = tree.getSelection()[0];
						if ( selectedItem.getItemCount() == 0 && btnLoad.isEnabled() )
							btnLoad.notifyListeners( SWT.Selection, null );
						else if ( selectedItem.getBounds().contains( e.x, e.y ) )
							selectedItem.setExpanded( !selectedItem.getExpanded() );
					}
				}
			}
		);

		tree.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( tree.getSelectionCount() != 0 ) {
						TreeItem selectedItem = tree.getSelection()[0];
						selection = (ShipMetadata)selectedItem.getData();

						if ( selection == null ) {
							btnLoad.setEnabled( false );
							txtBlueprint.setText( "" );
							txtClass.setText( "" );
							txtName.setText( "" );
							txtDescription.setText( "" );

							preview.setImage( null );
							canvas.redraw();
						}
						else {
							btnLoad.setEnabled( true );
							txtBlueprint.setText( selection.getBlueprintName() );
							txtClass.setText( selection.getShipClass().toString() );
							if ( selection.isPlayerShip() ) {
								txtName.setText( selection.getShipName().toString() );
								txtDescription.setText( selection.getShipDescription().toString() );
							}
							else {
								txtName.setText( "N/A" );
								txtDescription.setText( "N/A" );
							}

							String path = selection.getHullImagePath();
							preview.setImage( path == null ? "db:img/nullResource.png" : path );
							updatePreview();
							canvas.redraw();
						}
					}
					else {
						btnLoad.setEnabled( false );
						preview.setImage( null );
						canvas.redraw();
					}
				}
			}
		);

		trclmnBlueprint.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					// Sort by blueprint name
					if ( !sortByBlueprint ) {
						sortByBlueprint = true;
						loadShipList( Database.getInstance().getShipMetadata() );
					}
				}
			}
		);

		trclmnClass.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					// Sort by class name
					if ( sortByBlueprint ) {
						sortByBlueprint = false;
						loadShipList( Database.getInstance().getShipMetadata() );
					}
				}
			}
		);

		btnSearch.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					ShipSearchDialog ssDialog = new ShipSearchDialog( shell );
					Predicate<ShipMetadata> resultFilter = ssDialog.open();

					if ( resultFilter == AbstractSearchDialog.RESULT_DEFAULT ) {
						if ( filter == defaultFilter )
							return;
						filter = defaultFilter;
					}
					else if ( resultFilter == AbstractSearchDialog.RESULT_UNCHANGED ) {
						// Do nothing
						return;
					}
					else {
						filter = resultFilter;
					}

					loadShipList();
					tree.notifyListeners( SWT.Selection, null );
				}
			}
		);

		btnLoad.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					if ( tree.getSelectionCount() != 0 ) {
						TreeItem selectedItem = tree.getSelection()[0];
						ShipMetadata metadata = (ShipMetadata)selectedItem.getData();
						try {
							ShipObject object = ShipLoadUtils.loadShipXML( metadata.getElement() );

							if ( !Manager.allowRoomOverlap && object.hasOverlappingRooms() ) {
								log.info( "Ship contains overlapping rooms, but overlap is disabled - forcing room overlap enable." );
								Manager.allowRoomOverlap = true;
							}

							Manager.loadShip( object );

							if ( Manager.closeLoader ) {
								dispose();
							}
						}
						catch ( IllegalArgumentException ex ) {
							handleException( metadata, ex );
						}
						catch ( JDOMParseException ex ) {
							handleException( metadata, ex );
						}
						catch ( IOException ex ) {
							handleException( metadata, ex );
						}
					}
				}
			}
		);

		btnCancel.addSelectionListener(
			new SelectionAdapter() {
				@Override
				public void widgetSelected( SelectionEvent e )
				{
					dispose();
				}
			}
		);

		shell.addListener(
			SWT.Close, new Listener() {
				@Override
				public void handleEvent( Event e )
				{
					btnCancel.notifyListeners( SWT.Selection, null );
					e.doit = false;
				}
			}
		);

		canvas.addControlListener(
			new ControlAdapter() {
				@Override
				public void controlResized( ControlEvent e )
				{
					updatePreview();
					canvas.redraw();
				}
			}
		);

		ControlAdapter resizer = new ControlAdapter() {
			@Override
			public void controlResized( ControlEvent e )
			{
				final int BORDER_OFFSET = tree.getBorderWidth();
				if ( trclmnBlueprint.getWidth() > tree.getClientArea().width - BORDER_OFFSET )
					trclmnBlueprint.setWidth( tree.getClientArea().width - BORDER_OFFSET );
				trclmnClass.setWidth( tree.getClientArea().width - trclmnBlueprint.getWidth() - BORDER_OFFSET );
			}
		};
		tree.addControlListener( resizer );
		trclmnBlueprint.addControlListener( resizer );

		shell.setMinimumSize( minTreeWidth + defaultMetadataWidth, 300 );
		shell.pack();
		Point size = shell.getSize();
		shell.setSize( size.x + 5, size.y );
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation( parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2 );

		// Register hotkeys
		Hotkey h = new Hotkey();
		h.setOnPress(
			new Runnable() {
				public void run()
				{
					if ( tree.getSelectionCount() != 0 ) {
						TreeItem selectedItem = tree.getSelection()[0];
						if ( selectedItem.getItemCount() == 0 && btnLoad.isEnabled() )
							btnLoad.notifyListeners( SWT.Selection, null );
						else
							selectedItem.setExpanded( !selectedItem.getExpanded() );
					}
				}
			}
		);
		h.setKey( SWT.CR );
		Manager.hookHotkey( shell, h );

		h = new Hotkey( Manager.getHotkey( Hotkeys.SEARCH ) );
		h.addNotifyAction( btnSearch, true );
		Manager.hookHotkey( shell, h );

		loadShipList();
	}

	public void loadShipList( HashMap<String, ArrayList<ShipMetadata>> metadataMap )
	{
		for ( TreeItem it : dataTreeMap.values() )
			it.dispose();
		for ( TreeItem it : blueprintTreeMap.values() )
			it.dispose();
		dataTreeMap.clear();
		blueprintTreeMap.clear();

		Database db = Database.getInstance();
		MetadataIterator it = new MetadataIterator( metadataMap, sortByBlueprint );

		for ( it.first(); it.hasNext(); it.next() ) {
			String blueprint = it.current();
			boolean isPlayer = db.isPlayerShip( blueprint );

			TreeItem blueprintItem = new TreeItem( isPlayer ? trtmPlayer : trtmEnemy, SWT.NONE );
			blueprintItem.setText( 0, blueprint );
			blueprintItem.setText( 1, "" );

			ArrayList<ShipMetadata> dataList = metadataMap.get( blueprint );

			if ( dataList.size() == 1 ) {
				ShipMetadata metadata = dataList.get( 0 );

				blueprintItem.setData( metadata );
				blueprintItem.setText( 1, metadata.getShipClass().toString() );
				dataTreeMap.put( metadata, blueprintItem );
			}
			else {
				for ( ShipMetadata metadata : dataList ) {
					TreeItem metadataItem = new TreeItem( blueprintItem, SWT.NONE );

					metadataItem.setText( 0, blueprint );
					metadataItem.setText( 1, metadata.getShipClass().toString() );
					metadataItem.setData( metadata );
					dataTreeMap.put( metadata, metadataItem );
				}
			}

			blueprintTreeMap.put( blueprint, blueprintItem );
		}
	}

	public void loadShipList()
	{
		Database db = Database.getInstance();
		HashMap<String, ArrayList<ShipMetadata>> ships = db.getShipMetadata();

		if ( filter != null && filter != defaultFilter ) {
			String[] set = ships.keySet().toArray( new String[0] );
			for ( String s : set ) {
				ShipMetadata[] mdata = ships.get( s ).toArray( new ShipMetadata[0] );
				for ( ShipMetadata md : mdata ) {
					if ( !filter.accept( md ) )
						ships.get( s ).remove( md );
				}

				if ( ships.get( s ).isEmpty() )
					ships.remove( s );
			}
		}

		loadShipList( ships );
	}

	private void handleException( ShipMetadata metadata, Exception ex )
	{
		log.warn( "An error has occured while loading " + metadata.getBlueprintName() + ": ", ex );
		StringBuilder buf = new StringBuilder();
		buf.append( metadata.getBlueprintName() );
		buf.append( " could not be loaded:\n\n" );
		buf.append( ex.getClass().getSimpleName() );
		buf.append( ": " );
		buf.append( ex.getMessage() );
		buf.append( "\n\nCheck the log or console for details." );
		UIUtils.showWarningDialog( shell, null, buf.toString() );
	}

	private void updatePreview()
	{
		Point iSize = preview.getImageSize();
		Point cSize = canvas.getSize();

		double ratio = (double)iSize.y / iSize.x;
		int w = (int)( cSize.y / ratio );
		int h = (int)( cSize.x * ratio );
		preview.setSize( Utils.min( w, cSize.x, iSize.x ), Utils.min( h, cSize.y, iSize.y ) );
		preview.setLocation( cSize.x / 2, cSize.y / 2 );
	}

	public void open()
	{
		TreeItem item = dataTreeMap.get( selection );
		if ( item != null ) {
			tree.select( item );
			tree.setTopItem( item );
			tree.notifyListeners( SWT.Selection, null );
		}

		shell.open();
	}

	public boolean isActive()
	{
		return !shell.isDisposed() && shell.isVisible();
	}

	public static ShipLoaderDialog getInstance()
	{
		return instance;
	}

	public void dispose()
	{
		Manager.unhookHotkeys( shell );
		dataTreeMap.clear();
		blueprintTreeMap.clear();
		preview.dispose();
		shell.dispose();
		instance = null;
	}


	private class MetadataIterator implements Iterator<String>
	{
		private final HashMap<String, ArrayList<ShipMetadata>> map;
		private final MetadataComparator comparator;

		private String current = null;


		public MetadataIterator( HashMap<String, ArrayList<ShipMetadata>> map, boolean byBlueprint )
		{
			comparator = new MetadataComparator( map, byBlueprint );
			this.map = map;
		}

		private String getSmallestElement()
		{
			String result = null;
			for ( String blueprint : map.keySet() ) {
				if ( result == null || comparator.compare( blueprint, result ) < 0 )
					result = blueprint;
			}

			return result;
		}

		public void first()
		{
			current = getSmallestElement();
		}

		public String current()
		{
			return current;
		}

		@Override
		public boolean hasNext()
		{
			return !map.isEmpty();
		}

		@Override
		public String next()
		{
			remove();
			current = getSmallestElement();
			return current;
		}

		@Override
		public void remove()
		{
			map.remove( current );
		}
	}

	private class MetadataComparator implements Comparator<String>
	{
		private final boolean byBlueprint;
		private final HashMap<String, ArrayList<ShipMetadata>> map;


		public MetadataComparator( HashMap<String, ArrayList<ShipMetadata>> map, boolean byBlueprint )
		{
			this.byBlueprint = byBlueprint;
			this.map = map;
		}

		@Override
		public int compare( String o1, String o2 )
		{
			if ( byBlueprint ) {
				// Just compare the two blueprints together for alphanumerical ordering
				return o1.compareTo( o2 );
			}
			else {
				// If there are multiple ships overriding the same blueprint, sorting by class name
				// becomes tricky. Take class name of the default ship and sort by that.
				ShipMetadata m1, m2;
				m1 = map.get( o1 ).get( 0 );
				m2 = map.get( o2 ).get( 0 );
				int result = m1.getShipClass().compareTo( m2.getShipClass() );
				if ( result == 0 ) // If class names are the same, fall back to sorting by blueprint
					result = o1.compareTo( o2 );
				return result;
			}
		}
	}
}
