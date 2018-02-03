package com.kartoflane.superluminal2.core;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.kartoflane.common.selfpatch.SelfPatcher;
import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.selfpatch.SPSLGetTask;
import com.kartoflane.superluminal2.selfpatch.SPSLPatchTask;
import com.kartoflane.superluminal2.selfpatch.SPSLRunTask;
import com.kartoflane.superluminal2.ui.DownloadDialog;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.UIUtils;

import net.vhati.modmanager.core.ComparableVersion;


public class UpdateCheckWorker extends SwingWorker<UpdateData, Void>
{
	public static final Logger log = LogManager.getLogger( UpdateCheckWorker.class );

	private final boolean manuallyTriggeredCheck;


	public UpdateCheckWorker( boolean manuallyTriggeredCheck )
	{
		this.manuallyTriggeredCheck = manuallyTriggeredCheck;
	}

	@Override
	protected UpdateData doInBackground() throws Exception
	{
		log.info( "Checking for updates..." );

		if ( manuallyTriggeredCheck ) {
			// Execute on SWT's UI thread for the popup to work correctly.
			// Yuck.
			Display.getDefault().asyncExec(
				new Runnable() {
					public void run()
					{
						UIUtils.showLoadDialog(
							EditorWindow.getInstance().getShell(),
							"Checking for updates...", "Checking for updates, please wait...",
							new Runnable() {
								public void run()
								{
									while ( !UpdateCheckWorker.this.isDone() && !UpdateCheckWorker.this.isCancelled() ) {
										try {
											Thread.sleep( 100 );
										}
										catch ( InterruptedException e ) {
										}
									}
								}
							}
						);
					}
				}
			);
		}

		UpdateData ud = null;

		InputStream is = null;
		try {
			URL url = new URL( Superluminal.APP_UPDATE_FETCH_URL );
			is = url.openStream();

			Document updateDoc = IOUtils.readStreamXML( is, "auto-update" );
			Element root = updateDoc.getRootElement();
			Element latest = root.getChild( "latest" );
			String id = latest.getAttributeValue( "id" );

			String downloadLink = latest.getAttributeValue( "url" );
			ComparableVersion remoteVersion = new ComparableVersion( id );
			List<String> changes = new ArrayList<String>();

			Element changelog = root.getChild( "changelog" );
			for ( Element version : changelog.getChildren( "version" ) ) {
				ComparableVersion vId = new ComparableVersion( version.getAttributeValue( "id" ) );
				if ( vId.compareTo( Superluminal.APP_VERSION ) > 0 ) {
					for ( Element change : version.getChildren( "change" ) ) {
						changes.add( change.getValue() );
					}
				}
			}

			ud = new UpdateData( downloadLink, remoteVersion, changes );
		}
		catch ( UnknownHostException e ) {
			log.error( "Update check failed -- connection to the repository could not be estabilished." );
			ud = new UpdateData( e );
		}
		catch ( JDOMException e ) {
			log.error( "Udpate check failed -- an error has occured while parsing update file.", e );
			ud = new UpdateData( e );
		}
		catch ( Exception e ) {
			log.error( "An unknown error occured while checking for updates.", e );
			ud = new UpdateData( e );
		}
		finally {
			try {
				if ( is != null )
					is.close();
			}
			catch ( IOException e ) {
			}
		}

		return ud;
	}

	protected void done()
	{
		// Execute on-done instructions on SWT's UI thread for popups to function correctly.
		Display.getDefault().asyncExec(
			new Runnable() {
				public void run()
				{
					// Move actual processing code to another method to preserve sane indentation level.
					postprocess();
				}
			}
		);
	}

	private void postprocess()
	{
		try {
			UpdateData ud = get();

			if ( ud.lastException != null ) {
				// Update fetch failed, inform the user about that
				UIUtils.showWarningDialog(
					EditorWindow.getInstance().getShell(),
					null,
					String.format(
						"An error occurred while checking for updates:%n%n%s: %s",
						ud.lastException.getClass().getSimpleName(), ud.lastException.getMessage()
					)
				);
			}
			else {
				// Update fetch successful
				if ( Superluminal.APP_VERSION.compareTo( ud.remoteVersion ) < 0 ) {
					try {
						log.info(
							"Update is available, user version: " + Superluminal.APP_VERSION +
								", remote version: " + ud.remoteVersion
						);

						MessageBox box = new MessageBox(
							EditorWindow.getInstance().getShell(),
							SWT.ICON_INFORMATION | SWT.YES | SWT.NO
						);
						box.setText( Superluminal.APP_NAME + " - Update Available" );

						StringBuilder buf = new StringBuilder();
						buf.append( "A new version of the editor is available: v." );
						buf.append( ud.remoteVersion.toString() );
						buf.append( "\n\nChanges:\n" );
						if ( ud.changes.size() > 0 ) {
							int count = 0;
							for ( String change : ud.changes ) {
								if ( count < 3 ) {
									buf.append( " - " );
									buf.append( change );
									buf.append( "\n" );
									count++;
								}
								else {
									buf.append( "...and " );
									buf.append( ud.changes.size() - count );
									buf.append( " more - check changelog file for details.\n" );
									break;
								}
							}
							buf.append( "\n" );
						}

						buf.append( "Would you like to download it now?" );
						box.setMessage( buf.toString() );

						if ( box.open() == SWT.YES ) {
							try {
								SelfPatcher sp = new SelfPatcher( new SPSLGetTask(), new SPSLPatchTask(), new SPSLRunTask() );
								DownloadDialog dd = new DownloadDialog( EditorWindow.getInstance().getShell() );
								sp.patch( dd );
							}
							catch ( Exception e ) {
								log.error( "Self-patching failed!", e );

								box = new MessageBox(
									EditorWindow.getInstance().getShell(),
									SWT.ICON_ERROR | SWT.YES | SWT.NO
								);
								box.setText( Superluminal.APP_NAME + " - Auto-Update Failed" );
								box.setMessage(
									"Whoops! Something went terribly wrong, and the editor was unable to patch itself.\n" +
										"Do you want to download and update the editor manually?"
								);

								if ( box.open() == SWT.YES ) {
									URL url = new URL( ud.downloadLink == null ? Superluminal.APP_FORUM_URL : ud.downloadLink );
									Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
									if ( desktop != null && desktop.isSupported( Desktop.Action.BROWSE ) ) {
										try {
											desktop.browse( url.toURI() );
										}
										catch ( Exception ex ) {
											log.error( "An error has occured while opening web browser.", ex );
										}
									}
								}
							}
						}
					}
					catch ( Exception e ) {
						log.error( "An error has occured while displaying update result.", e );
						UIUtils.showWarningDialog(
							EditorWindow.getInstance().getShell(),
							null,
							String.format(
								"An error has occured while displaying update result:%n%n%s: %s",
								e.getClass().getSimpleName(), e.getMessage()
							)
						);
					}
				}
				else {
					if ( Superluminal.APP_VERSION.compareTo( ud.remoteVersion ) == 0 ) {
						log.info( "Program is up to date." );
					}
					else {
						log.info( "Program is up to date. (actually ahead)" );
					}
					if ( manuallyTriggeredCheck ) {
						// The user manually initiated the version check, so probably expects some kind of
						// response in either case.
						UIUtils.showInfoDialog(
							EditorWindow.getInstance().getShell(),
							null, Superluminal.APP_NAME + " is up to date."
						);
					}
				}
			}
		}
		catch ( InterruptedException e ) {
			log.error( "Could not finalize update check - UpdateData not available yet?" );
			return;
		}
		catch ( ExecutionException e ) {
			log.error( "Could not finalize update check - UpdateData not available yet?" );
			return;
		}
	}
}
