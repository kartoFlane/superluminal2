package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.common.selfpatch.SPDownloadWindow;
import com.kartoflane.superluminal2.Superluminal;


public class DownloadDialog implements SPDownloadWindow {

	private static DownloadDialog instance = null;

	private Shell shell = null;
	private Composite barHolder = null;
	private ProgressBar barProgress = null;
	private Text txtStatus = null;
	private Button btnContinue = null;

	private Thread taskThread = null;

	private boolean progressIndeterminate = false;
	private boolean done = false;
	private boolean succeeded = false;


	public DownloadDialog( Shell parent ) {
		if ( instance != null )
			throw new IllegalStateException( "Previous instance has not been disposed!" );
		instance = this;

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );
		shell.setText( Superluminal.APP_NAME + " - Downloading" );
		GridLayout gl_shell = new GridLayout( 1, false );
		shell.setLayout( gl_shell );

		barHolder = new Composite( shell, SWT.NONE );
		barHolder.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 1, 1 ) );
		barHolder.setLayout( new FillLayout( SWT.HORIZONTAL ) );

		barProgress = new ProgressBar( barHolder, SWT.SMOOTH );

		txtStatus = new Text( shell, SWT.BORDER );
		txtStatus.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 ) );
		txtStatus.setEditable( false );

		btnContinue = new Button( shell, SWT.NONE );
		GridData gd_btnContinue = new GridData( SWT.CENTER, SWT.CENTER, true, false, 1, 1 );
		gd_btnContinue.widthHint = 80;
		btnContinue.setLayoutData( gd_btnContinue );
		btnContinue.setText( "Continue" );
		btnContinue.setEnabled( false );

		shell.setSize( 400, 140 );
		Point size = shell.getSize();
		shell.setSize( size.x + 5, size.y );
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation( parLoc.x + parSize.x / 2 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2 );

		btnContinue.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent e ) {
				dispose();
			}
		} );

		shell.addListener( SWT.Close, new Listener() {
			@Override
			public void handleEvent( Event e ) {
				e.doit = false;
				dispose();
			}
		} );
	}

	private void setIndeterminate( boolean indeterminate ) {
		progressIndeterminate = indeterminate;
		if ( barProgress != null && !barProgress.isDisposed() )
			barProgress.dispose();
		barProgress = new ProgressBar( barHolder, SWT.SMOOTH | ( indeterminate ? SWT.INDETERMINATE : SWT.NONE ) );
	}


	/**
	 * Updates the text area's content. (Thread-safe)
	 *
	 * @param message
	 *            a string, or null
	 */
	public void setStatusTextLater( final String message ) {
		shell.getDisplay().asyncExec( new Runnable() {
			public void run() {
				setStatusText( message != null ? message : "..." );
			}
		} );
	}

	protected void setStatusText( String message ) {
		txtStatus.setText( message != null ? message : "..." );
	}

	/**
	 * Updates the progress bar. (Thread-safe)
	 *
	 * If the arg is -1, the bar will become indeterminate.
	 *
	 * @param value
	 *            the new value
	 */
	public void setProgressLater( final int value ) {
		shell.getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				if ( value >= 0 ) {
					if ( progressIndeterminate )
						setIndeterminate( false );
					barProgress.setSelection( value );
				}
				else {
					if ( !progressIndeterminate )
						setIndeterminate( true );
					barProgress.setSelection( 0 );
				}
			}
		} );
	}

	/**
	 * Updates the progress bar. (Thread-safe)
	 *
	 * If either arg is -1, the bar will become indeterminate.
	 *
	 * @param value
	 *            the new value
	 * @param max
	 *            the new maximum
	 */
	public void setProgressLater( final int value, final int max ) {
		shell.getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				if ( value >= 0 && max >= 0 ) {
					if ( progressIndeterminate )
						setIndeterminate( false );

					if ( barProgress.getMaximum() != max ) {
						barProgress.setSelection( 0 );
						barProgress.setMaximum( max );
					}
					barProgress.setSelection( value );
				}
				else {
					if ( !progressIndeterminate )
						setIndeterminate( true );
					barProgress.setSelection( 0 );
				}
			}
		} );
	}

	/**
	 * Triggers a response to the immediate task ending. (Thread-safe)
	 *
	 * If anything went wrong, e may be non-null.
	 */
	public void setTaskOutcomeLater( final boolean success, final Exception e ) {
		shell.getDisplay().asyncExec( new Runnable() {
			@Override
			public void run() {
				setTaskOutcome( success, e );
			}
		} );
	}

	protected void setTaskOutcome( final boolean outcome, final Exception e ) {
		done = true;
		succeeded = outcome;

		if ( !shell.isVisible() ) {
			// The window's not visible, no continueBtn to click.
			dispose();
		}

		btnContinue.setEnabled( true );
		btnContinue.setFocus();
	}

	@Override
	public void taskProgress( int value, int max ) {
		this.setProgressLater( value, max );
	}

	@Override
	public void taskStatus( String message ) {
		setStatusTextLater( message != null ? message : "..." );
	}

	@Override
	public void taskFinished( boolean outcome, Exception e ) {
		setTaskOutcomeLater( outcome, e );
	}

	@Override
	public void spShow() {
		shell.open();

		Display display = shell.getDisplay();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() )
				display.sleep();
		}
	}

	@Override
	public void setTaskThread( Thread thread ) {
		taskThread = thread;
	}

	public void dispose() {
		if ( !done ) {
			if ( taskThread != null ) {
				taskThread.interrupt();
			}
			setProgressLater( 0, 100 );
			setStatusTextLater( "Aborted by user." );
			setTaskOutcomeLater( false, null );
		}
		else {
			shell.dispose();
			instance = null;
		}
	}
}
