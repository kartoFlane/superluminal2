package com.kartoflane.common.selfpatch;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


/**
 * Appropriated from Slipstream Mod Manager's ProgressDialog
 */
@SuppressWarnings("serial")
class SPSwingDownloadDialog extends JDialog implements SPDownloadWindow, ActionListener
{
	private final JScrollPane statusScroll;
	private final JProgressBar progressBar;
	private final JTextArea statusArea;
	private final JButton continueBtn;

	private Thread taskThread = null;

	private boolean done = false;
	private boolean succeeded = false;


	public SPSwingDownloadDialog()
	{
		super( null, "Downloading...", ModalityType.APPLICATION_MODAL );

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		progressBar = new JProgressBar();
		progressBar.setBorderPainted( true );

		JPanel progressHolder = new JPanel( new BorderLayout() );
		progressHolder.setBorder( BorderFactory.createEmptyBorder( 10, 15, 0, 15 ) );
		progressHolder.add( progressBar );
		getContentPane().add( progressHolder, BorderLayout.NORTH );

		statusArea = new JTextArea();
		statusArea.setLineWrap( true );
		statusArea.setWrapStyleWord( true );
		statusArea.setFont( statusArea.getFont().deriveFont( 13f ) );
		statusArea.setEditable( false );
		statusScroll = new JScrollPane( statusArea );

		JPanel statusHolder = new JPanel( new BorderLayout() );
		statusHolder.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) );
		statusHolder.add( statusScroll );
		getContentPane().add( statusHolder, BorderLayout.CENTER );

		continueBtn = new JButton( "Continue" );
		continueBtn.setEnabled( false );
		continueBtn.addActionListener( this );

		JPanel continueHolder = new JPanel();
		continueHolder.setLayout( new BoxLayout( continueHolder, BoxLayout.X_AXIS ) );
		continueHolder.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
		continueHolder.add( Box.createHorizontalGlue() );
		continueHolder.add( continueBtn );
		continueHolder.add( Box.createHorizontalGlue() );
		getContentPane().add( continueHolder, BorderLayout.SOUTH );

		setSize( 400, 160 );
		this.setMinimumSize( this.getPreferredSize() );
		this.setLocationRelativeTo( null );
		setResizable( false );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		Object source = e.getSource();

		if ( source == continueBtn ) {
			this.setVisible( false );
			this.dispose();
		}
	}


	/**
	 * Updates the text area's content. (Thread-safe)
	 *
	 * @param message
	 *            a string, or null
	 */
	public void setStatusTextLater( final String message )
	{
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run()
				{
					setStatusText( message != null ? message : "..." );
				}
			}
		);
	}

	protected void setStatusText( String message )
	{
		statusArea.setText( message != null ? message : "..." );
		statusArea.setCaretPosition( 0 );
	}


	/**
	 * Updates the progress bar. (Thread-safe)
	 *
	 * If the arg is -1, the bar will become indeterminate.
	 *
	 * @param value
	 *            the new value
	 */
	public void setProgressLater( final int value )
	{
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run()
				{
					if ( value >= 0 ) {
						if ( progressBar.isIndeterminate() )
							progressBar.setIndeterminate( false );

						progressBar.setValue( value );
					}
					else {
						if ( !progressBar.isIndeterminate() )
							progressBar.setIndeterminate( true );
						progressBar.setValue( 0 );
					}
				}
			}
		);
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
	public void setProgressLater( final int value, final int max )
	{
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run()
				{
					if ( value >= 0 && max >= 0 ) {
						if ( progressBar.isIndeterminate() )
							progressBar.setIndeterminate( false );

						if ( progressBar.getMaximum() != max ) {
							progressBar.setValue( 0 );
							progressBar.setMaximum( max );
						}
						progressBar.setValue( value );
					}
					else {
						if ( !progressBar.isIndeterminate() )
							progressBar.setIndeterminate( true );
						progressBar.setValue( 0 );
					}
				}
			}
		);
	}


	/**
	 * Triggers a response to the immediate task ending. (Thread-safe)
	 *
	 * If anything went wrong, e may be non-null.
	 */
	public void setTaskOutcomeLater( final boolean success, final Exception e )
	{
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run()
				{
					setTaskOutcome( success, e );
				}
			}
		);
	}

	protected void setTaskOutcome( final boolean outcome, final Exception e )
	{
		done = true;
		succeeded = outcome;

		if ( !this.isShowing() ) {
			// The window's not visible, no continueBtn to click.
			this.dispose();
		}

		continueBtn.setEnabled( true );
		continueBtn.requestFocusInWindow();
	}

	@Override
	public void taskProgress( int value, int max )
	{
		this.setProgressLater( value, max );
	}

	@Override
	public void taskStatus( String message )
	{
		setStatusTextLater( message != null ? message : "..." );
	}

	@Override
	public void taskFinished( boolean outcome, Exception e )
	{
		setTaskOutcomeLater( outcome, e );
	}

	@Override
	public void dispose()
	{
		if ( !done ) {
			if ( taskThread != null ) {
				taskThread.interrupt();
			}
			setProgressLater( 0, 100 );
			setStatusTextLater( "Aborted by user." );
			setTaskOutcomeLater( false, null );
		}
		else {
			super.dispose();
		}
	}

	@Override
	public void spShow()
	{
		setVisible( true );
	}

	@Override
	public void setTaskThread( Thread thread )
	{
		taskThread = thread;
	}
}
