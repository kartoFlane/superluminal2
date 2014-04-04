package com.kartoflane.superluminal2.components.interfaces;

import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.core.Cache;

/**
 * Classes implementing this interface can be disposed, in accordance to
 * SWT's {@link org.eclipse.swt.graphics.Resource#dispose() Resource.dispose()} method.<br>
 * Disposed objects are gone forever and cannot be restored via undo.
 * 
 * @author kartoFlane
 * 
 * @see org.eclipse.swt.graphics.Resource
 */
public interface Disposable {
	/**
	 * Disposes resources used by this object via
	 * the {@link Cache#checkInColor(Object, org.eclipse.swt.graphics.RGB) Cache.checkInColor(Object, RGB)},
	 * or {@link Cache#checkInImage(Object, String)} methods.<br>
	 * 
	 * For resources that cannot be handled by Cache, call
	 * the {@link org.eclipse.swt.graphics.Resource#dispose() dispose()} method.<br>
	 * <br>
	 * Disposed objects cannot be retrieved via undo, therefore this method should be called to clean up unretrievable objects.
	 */
	public void dispose() throws NotDeletableException;
}
