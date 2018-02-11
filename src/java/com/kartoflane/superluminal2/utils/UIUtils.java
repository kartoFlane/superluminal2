package com.kartoflane.superluminal2.utils;

import java.io.File;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.ui.LoadingDialog;

import net.vhati.modmanager.core.FTLUtilities;


/**
 * This class contains methods that are used to show customizable dialogs
 * and UI prompts to the user.
 * 
 * @author kartoFlane
 * 
 */
public class UIUtils
{
	/**
	 * Displays a Swing message dialog.<br>
	 * Used when the user has downloaded incorrect version of the editor for their system/architecture.
	 */
	public static void showSwingDialog( String title, String message )
	{
		JOptionPane.showMessageDialog( null, message, title, JOptionPane.INFORMATION_MESSAGE );
	}

	/**
	 * Displays a simple dialog to inform the user about an error.
	 * 
	 * @param parentShell
	 *            a shell which will be the parent of the dialog. May be null.
	 * @param title
	 *            the title of the dialog window, or null for default value:<br>
	 *            <code>APP_NAME - Error</code>
	 * @param message
	 *            the message that will be displayed to the user. Must not be null.
	 * 
	 */
	public static void showErrorDialog( Shell parentShell, String title, String message )
	{
		boolean dispose = false;

		if ( title == null )
			title = Superluminal.APP_NAME + " - Error";
		if ( message == null )
			throw new IllegalArgumentException( "Message must not be null." );

		if ( parentShell == null ) {
			parentShell = new Shell( UIUtils.getDisplay() );
			dispose = true;
		}

		MessageBox box = new MessageBox( parentShell, SWT.ICON_ERROR | SWT.OK );

		box.setText( title );
		box.setMessage( message );

		box.open();

		if ( dispose )
			parentShell.dispose();
	}

	/**
	 * Displays a simple dialog to warn the user about something.
	 * 
	 * @param parentShell
	 *            a shell which will be the parent of the dialog. May be null.
	 * @param title
	 *            the title of the dialog window, or null for default value:<br>
	 *            <code>APP_NAME - Warning</code>
	 * @param message
	 *            the message that will be displayed to the user. Must not be null.
	 * 
	 */
	public static void showWarningDialog( Shell parentShell, String title, String message )
	{
		boolean dispose = false;

		if ( title == null )
			title = Superluminal.APP_NAME + " - Warning";
		if ( message == null )
			throw new IllegalArgumentException( "Message must not be null." );

		if ( parentShell == null ) {
			parentShell = new Shell( UIUtils.getDisplay() );
			dispose = true;
		}

		MessageBox box = new MessageBox( parentShell, SWT.ICON_WARNING | SWT.OK );

		box.setText( title );
		box.setMessage( message );

		box.open();

		if ( dispose )
			parentShell.dispose();
	}

	/**
	 * Displays a simple dialog to inform the user about something.
	 * 
	 * @param parentShell
	 *            a shell which will be the parent of the dialog. May be null.
	 * @param title
	 *            the title of the dialog window, or null for default value:<br>
	 *            <code>APP_NAME - Information</code>
	 * @param message
	 *            the message that will be displayed to the user. Must not be null.
	 * 
	 */
	public static void showInfoDialog( Shell parentShell, String title, String message )
	{
		boolean dispose = false;

		if ( title == null )
			title = Superluminal.APP_NAME + " - Information";
		if ( message == null )
			throw new IllegalArgumentException( "Message must not be null." );

		if ( parentShell == null ) {
			parentShell = new Shell( UIUtils.getDisplay() );
			dispose = true;
		}

		MessageBox box = new MessageBox( parentShell, SWT.ICON_INFORMATION | SWT.OK );

		box.setText( title );
		box.setMessage( message );

		box.open();

		if ( dispose )
			parentShell.dispose();
	}

	/**
	 * Displays a dialog prompting the user for a yes/no response.
	 * 
	 * @param parentShell
	 *            a shell which will be the parent of the dialog. May be null.
	 * @param title
	 *            the title of the dialog window. Must not be null.
	 * @param message
	 *            the message that will be displayed to the user. Must not be null.
	 * 
	 * @return true if the user selected "Yes", false otherwise.
	 */
	public static boolean showYesNoDialog( Shell parentShell, String title, String message )
	{
		boolean dispose = false;

		if ( title == null )
			throw new IllegalArgumentException( "Title must not be null." );
		if ( message == null )
			throw new IllegalArgumentException( "Message must not be null." );

		if ( parentShell == null ) {
			parentShell = new Shell( UIUtils.getDisplay() );
			dispose = true;
		}

		MessageBox box = new MessageBox( parentShell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO );

		box.setText( title );
		box.setMessage( message );

		boolean result = box.open() == SWT.YES;

		if ( dispose )
			parentShell.dispose();

		return result;
	}

	/**
	 * Displays a dialog prompting the user for a yes/no/cancel response.
	 * 
	 * @param parentShell
	 *            a shell which will be the parent of the dialog. May be null.
	 * @param title
	 *            the title of the dialog window. Must not be null.
	 * @param message
	 *            the message that will be displayed to the user. Must not be null.
	 * 
	 * @return int value equal to {@link SWT.YES} is the user selected "Yes", {@link SWT.NO} is the user selected "No",
	 *         and {@link SWT.CANCEL} is the user selected "Cancel",
	 */
	public static int showYesNoCancelDialog( Shell parentShell, String title, String message )
	{
		boolean dispose = false;

		if ( title == null )
			throw new IllegalArgumentException( "Title must not be null." );
		if ( message == null )
			throw new IllegalArgumentException( "Message must not be null." );

		if ( parentShell == null ) {
			parentShell = new Shell( UIUtils.getDisplay() );
			dispose = true;
		}

		MessageBox box = new MessageBox( parentShell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO | SWT.CANCEL );

		box.setText( title );
		box.setMessage( message );

		int result = box.open();

		if ( dispose )
			parentShell.dispose();

		return result;
	}

	/**
	 * Prompts the user to select a directory.
	 * 
	 * @param parentShell
	 *            the parent shell. Must not be null.
	 * @param title
	 *            title of the dialog window. Must not be null.
	 * @param message
	 *            description of the purpose of the dialog. Must not be null.
	 * @param defaultPath
	 *            the path that the dialog will initially show when it is opened.
	 *            May be null for the system's default path.
	 * @return the selected directory, or null if not selected.
	 */
	public static File promptForDirectory( Shell parentShell, String title, String message, String defaultPath )
	{
		File result = null;
		DirectoryDialog dialog = new DirectoryDialog( parentShell );
		dialog.setFilterPath( defaultPath );
		dialog.setText( title );
		dialog.setMessage( message );

		String path = dialog.open();
		if ( path == null ) {
			// User aborted selection
			// Nothing to do here
		}
		else {
			result = new File( path );
		}

		return result;
	}

	/**
	 * Prompts the user to select a file for the purpose of saving.
	 * 
	 * @param parentShell
	 *            the parent shell. Must not be null.
	 * @param title
	 *            title of the dialog window. Must not be null.
	 * @param defaultPath
	 *            the path that the dialog will initially show when it is opened.
	 *            May be null for the system's default path.
	 * @param extensions
	 *            an array of file extensions the user is allowed to select. May be null.<br>
	 *            Usage: <code>new String[] { "*.txt" }</code>
	 * @return the selected file, or null if not selected.
	 */
	public static File promptForSaveFile( Shell parentShell, String title, String defaultPath, String[] extensions )
	{
		File result = null;
		FileDialog dialog = new FileDialog( parentShell, SWT.SAVE );
		dialog.setFilterExtensions( extensions );
		dialog.setFilterPath( defaultPath );
		dialog.setFileName( defaultPath );
		dialog.setText( title );
		dialog.setOverwrite( true );

		String path = dialog.open();
		if ( path == null ) {
			// User aborted selection
			// Nothing to do here
		}
		else {
			result = new File( path );
		}

		return result;
	}

	/**
	 * Prompts the user to select a file for the purpose of loading.
	 * 
	 * @param parentShell
	 *            the parent shell. Must not be null.
	 * @param title
	 *            title of the dialog window. Must not be null.
	 * @param defaultPath
	 *            the path that the dialog will initially show when it is opened.
	 *            May be null for the system's default path.
	 * @param extensions
	 *            an array of file extensions the user is allowed to select. May be null.<br>
	 *            Usage: <code>new String[] { "*.txt" }</code>
	 * @return the selected file, or null if not selected.
	 */
	public static File promptForLoadFile( Shell parentShell, String title, String defaultPath, String[] extensions )
	{
		File result = null;
		FileDialog dialog = new FileDialog( parentShell, SWT.OPEN );
		dialog.setFilterExtensions( extensions );
		dialog.setFilterPath( defaultPath );
		dialog.setFileName( defaultPath );
		dialog.setText( title );

		String path = dialog.open();
		if ( path == null ) {
			// User aborted selection
			// Nothing to do here
		}
		else {
			result = new File( path );
		}

		return result;
	}

	/**
	 * Modally prompts the user for the FTL resources dir.
	 * 
	 * @param parentShell
	 *            parent for the SWT dialog
	 * 
	 * @author Vhati - original method wth Swing dialogs
	 * @author kartoFlane - modified to work with SWT dialogs
	 */
	public static File promptForDatsDir( Shell parentShell )
	{
		File result = null;

		String message = ""
			+ "You will now be prompted to locate FTL manually.\n"
			+ "Look in {FTL dir} to select 'ftl.dat' or 'data.dat'.\n"
			+ "\n"
			+ "It may be buried under a subdirectory called 'resources/'.\n"
			+ "Or select 'FTL.app', if you're on OSX.";

		MessageBox box = new MessageBox( parentShell, SWT.ICON_INFORMATION | SWT.OK );
		box.setText( "Find FTL" );
		box.setMessage( message );

		FileDialog fd = new FileDialog( parentShell, SWT.OPEN );
		fd.setText( "Find ftl.dat or data.dat or FTL.app" );
		fd.setFilterExtensions( new String[] { "*.dat", "*.app" } );
		fd.setFilterNames( new String[] { "FTL Resources", "FTL App Bundle" } );

		boolean exit = false;
		while ( !exit && result == null ) {
			String filePath = fd.open();

			if ( filePath == null ) {
				// User aborted selection
				// Nothing to do here
				exit = true;
			}
			else {
				File f = new File( filePath );
				if ( f.getName().equals( "resource.dat" ) || f.getName().equals( "data.dat" )
					|| f.getName().equals( "ftl.dat" ) ) {
					result = f.getParentFile();
				}
				else if ( f.getName().endsWith( ".app" ) ) {
					// TODO test whether this works on OSX
					File contentsPath = new File( f, "Contents" );
					if ( contentsPath.exists() && contentsPath.isDirectory()
						&& new File( contentsPath, "Resources" ).exists() ) {
						result = new File( contentsPath, "Resources" );
					}
				}
				else {
					// Shouldn't ever happen unless the dat-recognizing code is wrong
					UIUtils.showInfoDialog(
						parentShell,
						"Invalid file",
						String.format(
							"The file you selected (%s) is not a valid .dat archive or FTL.app.\n"
								+ "\n"
								+ "Please select a valid file.",
							f.getName()
						)
					);
				}
			}
		}

		if ( result != null && FTLUtilities.isDatsDirValid( result ) ) {
			return result;
		}

		return null;
	}

	/**
	 * Displays a simple dialog with an indeterminate progress bar in the UI thread, while
	 * executing the given task in another thread, and waiting for it to finish.<br>
	 * <br>
	 * Note that SWT is a single-threaded library, so you <b>cannot create or modify
	 * UI widgets</b> via this method.<br>
	 * <br>
	 * Usage:
	 * 
	 * <pre>
	 * showLoadDialog(
	 * 	shell, title, message, new Action() {
	 * 		public void execute()
	 * 		{
	 * 			// your code here...
	 * 		}
	 * 	}
	 * );
	 * </pre>
	 * 
	 * @param parentShell
	 *            the shell which will be the dialog's parent. Must not be null.
	 * @param title
	 *            the title of the dialog window, or null for default value:<br>
	 *            <code>APP_NAME - Loading...</code>
	 * @param message
	 *            a brief message that will be displayed above the progress bar, or null for default value:<br>
	 *            <code>Loading, please wait...</code>
	 * @param task
	 *            the task that is to be performed in the background, or null to make the method return immediately.
	 * 
	 * @throws IllegalArgumentException
	 *             when the parent shell is null.
	 */
	public static void showLoadDialog( Shell parentShell, String title, String message, final Runnable task ) throws IllegalArgumentException
	{
		if ( task == null )
			return;

		if ( parentShell == null )
			throw new IllegalArgumentException( "Parent shell must not be null." );

		final LoadingDialog dialog = new LoadingDialog( parentShell, title, message );
		Thread loadThread = new Thread() {
			@Override
			public void run()
			{
				try {
					task.run();
				}
				finally {
					dialog.dispose();
				}
			}
		};
		loadThread.start();
		dialog.open();
	}

	/**
	 * @param c
	 *            the control to which the tooltip will be added
	 * @param message
	 *            tooltip's message
	 * 
	 * @see #addTooltip(Control, String, String)
	 */
	public static void addTooltip( Control c, String message )
	{
		addTooltip( c, "", message );
	}

	/**
	 * Adds a tooltip to the given control.<br>
	 * The tooltip will appear once the user hovers over the control, and will remain
	 * visible until the user moves the cursor away.
	 * 
	 * @param c
	 *            the control to which the tooltip will be added
	 * @param tooltipText
	 *            tooltip's title
	 * @param tooltipMessage
	 *            tooltip's message
	 */
	public static void addTooltip( final Control c, String tooltipText, String tooltipMessage )
	{
		final ToolTip tip = new ToolTip( c.getShell(), SWT.NONE );
		tip.setText( tooltipText );
		tip.setMessage( tooltipMessage );
		tip.setAutoHide( false );

		c.addListener(
			SWT.MouseHover, new Listener() {
				public void handleEvent( Event e )
				{
					Point p = c.toDisplay( e.x, e.y );
					tip.setLocation( p.x, p.y + 20 );
					tip.setVisible( true );
				}
			}
		);

		c.addListener(
			SWT.MouseExit, new Listener() {
				public void handleEvent( Event e )
				{
					tip.setVisible( false );
				}
			}
		);

		c.addListener(
			SWT.Dispose, new Listener() {
				public void handleEvent( Event e )
				{
					if ( !tip.isDisposed() )
						tip.setVisible( false );
					tip.dispose();
				}
			}
		);
	}

	/**
	 * Adds hotkey text to the given menu item.<br>
	 * This method is used to avoid a bug with GTK version of SWT; adding
	 * accelerator text to the menu item on Linux systems also changes the
	 * accelerator itself.
	 * 
	 * @param mntm
	 *            menu item to which the hotkey will be added
	 * @param hotkeyText
	 *            the text to be displayed as accelerator
	 * 
	 * @see MenuItem#setAccelerator(int)
	 * @see MenuItem#setText(String)
	 */
	public static void addHotkeyText( MenuItem mntm, String hotkeyText )
	{
		// Bug with SWT-GTK: MenuItem.setText() changes the widget's accelerator,
		// contrary to the Javadoc. The accelerator consumes the key event that triggers
		// it, therefore the application is never informed about the event.
		// (Text after \t is the accelerator text)
		// Fix is to use brackets instead of \t (ugly workaround)
		String os = System.getProperty( "os.name" ).toLowerCase();
		if ( os.contains( "linux" ) || os.contains( "nix" ) ) {
			mntm.setText( String.format( "%s [%s]", mntm.getText(), hotkeyText ) );
		}
		else {
			mntm.setText( mntm.getText() + "\t" + hotkeyText );
		}
	}

	public static Display getDisplay()
	{
		Display display = Display.getCurrent(); // Can sometimes return null
		if ( display == null )
			display = Display.getDefault();
		return display;
	}
}
