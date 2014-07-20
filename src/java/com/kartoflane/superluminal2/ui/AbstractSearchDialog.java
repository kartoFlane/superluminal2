package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.components.DefaultSearchResult;
import com.kartoflane.superluminal2.components.interfaces.Predicate;

/**
 * 
 * @author kartoFlane
 *
 * @param <T>
 *            the type of objects that this dialog filters
 */
public abstract class AbstractSearchDialog<T> {

	/**
	 * Indicates that no changes should be made to the filter as a result of this dialog.
	 */
	public static final Predicate<?> RESULT_UNCHANGED = new DefaultSearchResult();

	/**
	 * Indicates that the default filter should be used.
	 */
	public static final Predicate<?> RESULT_DEFAULT = new DefaultSearchResult();

	protected static AbstractSearchDialog<?> instance = null;
	private Predicate<?> result = RESULT_DEFAULT;

	protected Shell shell = null;

	/**
	 * Child classes should always call this constructor.
	 * 
	 * @param parent
	 *            parent of this dialog
	 */
	public AbstractSearchDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		createContents(parent);

		if (shell == null)
			throw new IllegalStateException("Shell must be instantiated!");
	}

	/**
	 * Create contents for this search dialog, as well as listeners governing how the UI widgets behave.<br>
	 * Widgets should modify variables, thereby changing the kind of predicate returned by {@link #getFilter()}.
	 * 
	 * @param parent
	 *            parent of this dialog
	 */
	public abstract void createContents(Shell parent);

	/**
	 * @return the predicate that is used as filter, ie. result of the dialog
	 */
	protected abstract Predicate<T> getFilter();

	protected final void setResultCurrent() {
		result = getFilter();
	}

	protected final void setResultDefault() {
		result = RESULT_DEFAULT;
	}

	protected final void setResultUnchanged() {
		result = RESULT_UNCHANGED;
	}

	/**
	 * Disposes the shell and nullifies the instance, triggering the dialog to return the filter.<br>
	 * <br>
	 * Call one of the following to set the filter before invoking this method:<br>
	 * - {@link #setResultCurrent()} to construct a new filter using values selected by the user.<br>
	 * - {@link #setResultDefault()} to return a constant indicating that the default filter should be used.<br>
	 * - {@link #setResultUnchanged()} to return a constant indicating that the previous filter should be used.
	 */
	public void dispose() {
		shell.dispose();
		instance = null;
	}

	/**
	 * Opens this dialog. This method blocks until the user exits the dialog (by calling {@link #dispose()})
	 * 
	 * @return the filter selected by the user
	 */
	@SuppressWarnings("unchecked")
	public Predicate<T> open() {
		if (instance == null)
			throw new IllegalStateException("Instance is null!");

		shell.open();

		Display display = Display.getCurrent();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		if (result == null)
			throw new IllegalStateException("Result must not be null!");

		return (Predicate<T>) result;
	}
}
