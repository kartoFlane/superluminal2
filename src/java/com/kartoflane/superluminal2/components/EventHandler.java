package com.kartoflane.superluminal2.components;

import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;


/**
 * A copy of SWT's own {@link org.eclipse.swt.widgets.EventTable EventTable}, with the addition of comments.
 */
public class EventHandler
{
	private static final int growSize = 4;

	// Lazily instantiated arrays
	private int[] types;
	private SLListener[] listeners;

	/**
	 * Helper variable to detect when a listener that has been informed of an event has unregistered
	 * itself from the handler as a result of the event.
	 * This allows the handler to clean up the removed listeners without messing up the sendEvent loop.
	 */
	private int level;


	public EventHandler()
	{
	}

	public SLListener[] getListeners( int eventType )
	{
		if ( types == null )
			return new SLListener[0];

		int count = 0;
		for ( int i = 0; i < types.length; i++ ) {
			if ( types[i] == eventType )
				count++;
		}

		if ( count == 0 )
			return new SLListener[0];

		SLListener[] result = new SLListener[count];
		count = 0;
		for ( int i = 0; i < types.length; i++ ) {
			if ( types[i] == eventType ) {
				result[count++] = listeners[i];
			}
		}
		return result;
	}

	/**
	 * Registers the listener with the handler, listening for the specified event.
	 * 
	 * @param eventType
	 *            id of the event type the listener is interested in being notified about
	 * @param listener
	 *            the listener that will be notified when an event of the specified type is sent
	 */
	public void hook( int eventType, SLListener listener )
	{
		if ( types == null )
			types = new int[growSize];
		if ( listeners == null )
			listeners = new SLListener[growSize];

		int length = types.length;
		int index = length - 1;

		while ( index >= 0 ) {
			if ( types[index] != 0 )
				break;
			--index;
		}
		index++;

		if ( index == length ) {
			int[] newTypes = new int[length + growSize];
			System.arraycopy( types, 0, newTypes, 0, length );
			types = newTypes;

			SLListener[] newListeners = new SLListener[length + growSize];
			System.arraycopy( listeners, 0, newListeners, 0, length );
			listeners = newListeners;
		}
		types[index] = eventType;
		listeners[index] = listener;
	}

	/**
	 * @return true if the handler has at least one listener listening for this event, false otherwise.
	 */
	public boolean hooks( int eventType )
	{
		if ( types == null )
			return false;
		for ( int i = 0; i < types.length; i++ ) {
			if ( types[i] == eventType )
				return true;
		}
		return false;
	}

	/**
	 * Sends the event, notifying all interested listeners.
	 * 
	 * @param event
	 *            the event to be sent
	 */
	public void sendEvent( SLEvent event )
	{
		if ( types == null )
			return;
		level += level >= 0 ? 1 : -1;

		try {
			for ( int i = 0; i < types.length; i++ ) {
				if ( event.type == SLEvent.NONE )
					return;
				if ( types[i] == event.type ) {
					SLListener listener = listeners[i];
					if ( listener != null )
						listener.handleEvent( event );
				}
			}
		}
		finally {
			// If a listener unregisters itself from the handler as a result of the event,
			// then the level variable will become negative, triggering the cleanup
			boolean compact = level < 0;
			level -= level >= 0 ? 1 : -1;
			if ( compact && level == 0 ) {
				int index = 0;
				// Remove null type events
				for ( int i = 0; i < types.length; i++ ) {
					if ( types[i] != 0 ) {
						types[index] = types[i];
						listeners[index] = listeners[i];
						index++;
					}
				}
				// Nullify the tail of the array, since it was shrunk
				for ( int i = index; i < types.length; i++ ) {
					types[i] = 0;
					listeners[i] = null;
				}
			}
		}
	}

	/**
	 * @return the number of listeners currently registered with the handler
	 */
	public int size()
	{
		if ( types == null )
			return 0;
		int count = 0;
		for ( int i = 0; i < types.length; i++ ) {
			if ( types[i] != 0 )
				count++;
		}
		return count;
	}

	/**
	 * Unregisters the listener under the specified index from the handler.
	 * 
	 * @param index
	 *            index to be removed
	 */
	private void remove( int index )
	{
		// If a listener unregisters itself from the handler as a result of an event, then shifting
		// the array will mess up the sending loop. If that is the case, only nullify the index to
		// be removed, and negate the level variable -- sendEvent() will shift the array on its own
		// after it's done sending events. Otherwise, proceed normally and shift the array here.

		if ( level == 0 ) {
			int end = types.length - 1;
			// Shift all array contents one index to the left
			System.arraycopy( types, index + 1, types, index, end - index );
			System.arraycopy( listeners, index + 1, listeners, index, end - index );

			index = end;
		}
		else {
			if ( level > 0 )
				level = -level;
		}
		// Either nullifies the tail of the array in case it was shifted to the left,
		// or removes the event-listener pair at the given index
		types[index] = 0;
		listeners[index] = null;
	}

	/**
	 * Unregisters the event-listener pair from the handler.
	 * 
	 * @param eventType
	 *            type of the event to be removed
	 * @param listener
	 *            listener to be removed
	 */
	public void unhook( int eventType, SLListener listener )
	{
		if ( types == null )
			return;
		for ( int i = 0; i < types.length; i++ ) {
			if ( types[i] == eventType && listeners[i] == listener ) {
				remove( i );
				break;
			}
		}
	}

	/**
	 * Completely unregisters the listener from the event handler, essentially forgetting it.
	 * 
	 * @param listener
	 *            listener to be removed
	 */
	public void unhook( SLListener listener )
	{
		if ( listeners == null )
			return;
		for ( int i = 0; i < listeners.length; i++ ) {
			if ( listeners[i] == listener )
				remove( i );
		}
	}

	/**
	 * Nullifies all arrays.
	 */
	public void dispose()
	{
		if ( types == null )
			return;

		for ( int i = 0; i < types.length; i++ ) {
			types[i] = 0;
			listeners[i] = null;
		}
		types = null;
		listeners = null;
	}
}
