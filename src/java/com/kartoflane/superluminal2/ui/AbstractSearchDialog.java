package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.components.interfaces.Predicate;

/**
 * A general search dialog framework. Allows to construct a filter for the specified type.<br>
 * <br>
 * Child classes should only call the {@link #AbstractSearchDialog(Shell)} constructor, and use {@link #createContents(Shell)} to create their
 * widgets.<br>
 * After instantiation, the dialog has to be opened using {@link #open()} - this method blocks until the user dismisses the dialog.<br>
 * <br>
 * Whenever the dialog is dismissed, {@link #dispose()} has to be called, preceded by one of the following methods to set the result:<br>
 * - {@link #setResultCurrent()} to construct a new filter using values selected by the user.<br>
 * - {@link #setResultDefault()} to return a constant indicating that the default filter should be used.<br>
 * - {@link #setResultUnchanged()} to return a constant indicating that the previous filter should be used.
 * 
 * @author kartoFlane
 *
 * @param <T>
 *            the type of objects that this dialog filters
 */
public abstract class AbstractSearchDialog<T> {

	/**
	 * Indicates that the default filter should be used.
	 */
	public static final Predicate<?> RESULT_DEFAULT = new Predicate<Object>() {
		public boolean accept(Object object) {
			return false;
		}
	};

	/**
	 * Indicates that no changes should be made to the filter as a result of this dialog.
	 */
	public static final Predicate<?> RESULT_UNCHANGED = new Predicate<Object>() {
		public boolean accept(Object object) {
			return false;
		}
	};

	private Predicate<?> result = RESULT_DEFAULT;

	protected Shell shell = null;

	/**
	 * Child classes should always call this constructor.
	 * 
	 * @param parent
	 *            parent of this dialog
	 * @throws IllegalStateException
	 *             if the shell is null (must be used for {@link #open()} to work)
	 */
	public AbstractSearchDialog(Shell parent) {
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

	/**
	 * Sets the result of the dialog to a newly constructed filter, based on parameters selected in the dialog.
	 */
	protected final void setResultCurrent() {
		result = getFilter();
	}

	/**
	 * Sets the result of the dialog to a constant indicating that the default filter is to be used.
	 */
	protected final void setResultDefault() {
		result = RESULT_DEFAULT;
	}

	/**
	 * Sets the result of the dialog to a constant indicating that the previous filter is to be used.
	 */
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
	}

	/**
	 * Opens this dialog. This method blocks until the user exits the dialog (by calling {@link #dispose()}).
	 * 
	 * @return the filter selected by the user
	 * @throws IllegalStateException
	 *             if the result is null
	 */
	@SuppressWarnings("unchecked")
	public Predicate<T> open() {
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
